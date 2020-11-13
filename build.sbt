name := "ScalaCosmosQuery"

version := "0.1"

scalaVersion := "2.12.12"

libraryDependencies ++= Seq(
    "com.azure" % "azure-cosmos" % "4.7.0",
      "org.slf4j" % "slf4j-jdk14" % "1.7.25",
      "org.apache.commons" % "commons-lang3" % "3.10"
)
