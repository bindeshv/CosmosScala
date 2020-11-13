package models 

object BlogFactory {

  def getBlogs(): List[Blog] = {
    val blogList = List(
       Blog(
        "B1" + System.currentTimeMillis,
        "The Carbon FootPrint of AI",
        Author("Will", "Buchanan"),
        "october 26,2020"
        , "https://devblogs.microsoft.com/sustainable-software/the-carbon-footprint-of-ai/",
        Array[String]("AI", "Intelligence"),
        "AI"
      ),

       Blog(
        "B2" + System.currentTimeMillis,
        "Understanding the difference between point reads and queries in Azure Cosmos DB",
        Author("Tim", "Sander"),
        "october 22,2020",
        "https://devblogs.microsoft.com/cosmosdb/point-reads-versus-queries/",
        Array[String]("Core(SQL), Azure CosmosDB"),
        "CosmosDB"
      ),

       Blog(
        "B3" + System.currentTimeMillis,
        "Azure Cosmos DB Repository .NET SDK v.1.0.4",
        Author("David", "Pine"),
        "October 7th,2020",
        "https://devblogs.microsoft.com/cosmosdb/azure-cosmos-db-repository-net-sdk-v-1-0-4/",
        Array[String](".NET, AppDev, Azure CosmosDB"),
        "CosmosDB"
      )

    )

    blogList
  }
}
