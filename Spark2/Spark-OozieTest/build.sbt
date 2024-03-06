name := "Spark-OozieTest"

version := "0.1"

scalaVersion := "2.11.8"

val sparkVersion = "2.4.8.7.1.8.0-801"

resolvers += "apache-snapshots" at "http://repository.apache.org/snapshots/"

// resolvers += "Hortonworks Repository" at "http://repo.hortonworks.com/content/repositories/releases/"
resolvers += "Cloudera Repository" at "https://repository.cloudera.com/repository/cloudera-repos/"



libraryDependencies ++= Seq(
   "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
   "org.apache.spark" %% "spark-sql" % sparkVersion % "provided"
)
/*

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}*/
