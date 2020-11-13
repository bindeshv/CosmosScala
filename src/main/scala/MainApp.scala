import com.azure.cosmos.CosmosClientBuilder
import org.slf4j.LoggerFactory
import com.azure.cosmos.models.CosmosDatabaseResponse
import com.azure.cosmos.CosmosAsyncDatabase
import reactor.core.publisher.Mono
import org.apache.commons.lang3.StringUtils

object MainApp extends App {
    
    CosmosDbHelper()
    .connectToDatabase()
    .createDatabaseIfNotExists()
    .createContainerIfNotExists()
    .createBlogEntries()
    .queryItems()
    .shutdown()
    
  
}
