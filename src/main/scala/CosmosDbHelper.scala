import com.azure.cosmos.{CosmosAsyncClient, CosmosAsyncDatabase, CosmosClientBuilder}
import org.slf4j.{Logger, LoggerFactory}
import reactor.core.publisher.Mono
import com.azure.cosmos.CosmosAsyncContainer
import com.azure.cosmos.CosmosException
import com.azure.cosmos.models.CosmosContainerProperties
import com.azure.cosmos.models.ThroughputProperties
import reactor.core.publisher.Flux
import collection.JavaConverters._
import com.azure.cosmos.models.CosmosItemRequestOptions
import com.azure.cosmos.models.CosmosQueryRequestOptions
import com.azure.cosmos.util.CosmosPagedFlux
import models._
import com.azure.cosmos.models.CosmosContainerResponse

class CosmosDbHelper {
  private val databaseName = "BlogsDb"
  private val containerName = "devBlogs"
  private val logger: Logger = LoggerFactory.getLogger("CosmosDbHelper")
  private var asyncClient: Option[CosmosAsyncClient] = Option(null)
  private var databaseAsync: Option[CosmosAsyncDatabase] = Option(null) 
  private var containerAsync: Option[CosmosAsyncContainer] = Option(null) 

  def connectToDatabase() = {
      try {
           asyncClient = Option(new CosmosClientBuilder()
                    .endpoint(AccountSettings.HOST)
                    .key(AccountSettings.MASTER_KEY)
                    .contentResponseOnWriteEnabled(true)
                    .directMode()
                    .buildAsyncClient()
                    )
      }catch {
        case e:CosmosException => logger.error(s"CosmosDB Exception while connecting: ${e.getMessage()}")
        case e: Throwable => logger.error(s"Exception while connecting:${e.getMessage}")
      }
      this
  }

  def createDatabaseIfNotExists() = {
    logger.info("Create database {} if not exists.", databaseName);
    try{
      for(dbClient <- asyncClient) {
           dbClient.createDatabaseIfNotExists(databaseName).flatMap(cosmosDbResp => {
              databaseAsync = Option(dbClient.getDatabase(cosmosDbResp.getProperties().getId()))
              Mono.empty()
           }).block;
      }  
    }catch {
      case e: Exception => println(s"Exception creating database ${e.getMessage()}")
    }
    this
  }

  def createContainerIfNotExists() = {
    logger.info("checking if container exists or creating one")
    for(dbClient <- databaseAsync) {
      val containerProperties = new CosmosContainerProperties(containerName, "/product")
      val containerResp = dbClient.createContainerIfNotExists(
                                  containerProperties,
                                  ThroughputProperties.createManualThroughput(400)
                                  )
                                  .flatMap(resp => {
                                      containerAsync = Option(dbClient.getContainer(resp.getProperties().getId()))
                                      Mono.empty()
                                    }
                                  )
                                  .block()                                          
    }
    this
  }

  def createBlogEntries() = {
    val blogListFlux = Flux.fromIterable(BlogFactory.getBlogs().asJava)
    logger.info("Creating blog entries")
    val creationOptions = new CosmosItemRequestOptions();
    for(container <- containerAsync) {
      {
        val totalCharge: Double = blogListFlux.flatMap(blog => {
          logger.warn(s"creating entry with ${blog.id}")
          container.createItem(blog)
        })//flux of item request response 
        .flatMap(itemResponse => {
          logger.info(s"created item with request charge of ${itemResponse.getRequestCharge()} within duration ${itemResponse.getDuration()}")
          Mono.just(itemResponse.getRequestCharge())
        })
        .reduce(0.0, 
                (charge_n: Double, charge_nplus1: Double) => charge_n + charge_nplus1
        )
        .block()
        logger.info(s"Created items with total request charge of ${totalCharge}")
      }
    } 
    this
  }

  def queryItems() = {
    logger.info("querying items..")
    for(container <- containerAsync) {
      val sql = "SELECT * From dev_blogs d where d.product='CosmosDB'"
        logger.info(s"querying items..with sql:${sql}")
        val queryOptions = new CosmosQueryRequestOptions
        queryOptions.setQueryMetricsEnabled(true)
        val queryPagedFlux: CosmosPagedFlux[Blog] = container.queryItems(sql,queryOptions,classOf[Blog])
        queryPagedFlux.byPage(5).flatMap(
          feedResponse => {
            val results = feedResponse.getResults()
            logger.info(s"got page results ${results.size()} for RUs ${feedResponse.getRequestCharge()}")
            results.forEach(item => {
              logger.info(s"blog post ${item.title} by ${item.author.firstName} on product ${item.product}")
            })
            Mono.empty()
          }
        ).blockLast()
    }
    this
  }
  def shutdown() = {
    asyncClient.map(client => client.close())
  }
}
//companion 
object CosmosDbHelper {
  def apply() = {
    new CosmosDbHelper
  }
}