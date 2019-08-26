name := "Spark-kafkaIntegration"

version := "0.1"

scalaVersion := "2.11.8"

val sparkVersion = "2.3.2.3.1.0.0-78"

val spark_streaming_kafkaVersion = "2.3.2"

resolvers += "apache-snapshots" at "http://repository.apache.org/snapshots/"

resolvers += "Hortonworks Repository" at "http://repo.hortonworks.com/content/repositories/releases/"



libraryDependencies ++= Seq(
   "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
   "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
   "org.apache.spark" %% "spark-streaming" % sparkVersion % "provided",
   "org.apache.spark" % "spark-streaming-kafka-0-10_2.11" % spark_streaming_kafkaVersion
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}