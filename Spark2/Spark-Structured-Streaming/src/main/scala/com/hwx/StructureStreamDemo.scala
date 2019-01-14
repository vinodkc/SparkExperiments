package com.hwx

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{explode, split}

object StructureStreamDemo extends App {

  val spark = SparkSession.builder.appName("StructureStreamDemo").getOrCreate()
  import spark.implicits._
  val lines = spark.readStream.format("socket").option("host", "c420-node2.squadron-labs.com").option("port", 3503).load()
  val words = lines.withColumn("word", explode(split($"value", " ")))

   val query = words.writeStream.outputMode("append").format("parquet").option("checkpointLocation", "/tmp/ss_checkpointLocation").option("path", "/tmp/ss_fileout").start()
   query.awaitTermination()
}

