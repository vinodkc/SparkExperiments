package com.vkc.spark

import java.nio.file.{Files, Paths}

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.avro._
import org.apache.spark.sql.functions.expr

object SparkStructuredStreamingDemo {
  def main(args: Array[String]): Unit = {
    if (args.length < 3) {
      println("Usage: SparkStructuredStreamingDemo  <bootstrapservers>   impressions clicks")
//change to json and test
    } else {


        val spark = SparkSession.builder().master("local").appName("SparkSecureKafkaStructuredStreamingDemo").getOrCreate()
      import spark.implicits._

      val bootstrapServers = args(0)
      val impressionsTopic = args(1)

      val jsonImpressionSchema: String = getAvroSchemaAsJson("./avro/Impressions.avsc")
      println(jsonImpressionSchema)

      val impressionsWithWatermark = spark
        .readStream
        .format("kafka")
        .option("kafka.bootstrap.servers", bootstrapServers)
        .option("subscribe", impressionsTopic)
        .load()
        .select(from_avro('value, jsonImpressionSchema) as 'impression)
        .selectExpr("impression.adId AS impressionAdId", "impression.impressionTime")
        .withWatermark("impressionTime", "10 seconds ")

      val clicksTopic = args(2)
      val jsonClickSchema: String = getAvroSchemaAsJson("./avro/Clicks.avsc")
      println(jsonClickSchema)

      val clicksWithWatermark = spark
        .readStream
        .format("kafka")
        .option("kafka.bootstrap.servers", bootstrapServers)
        .option("subscribe", clicksTopic)
        .load().
        select(from_avro('value, jsonClickSchema) as 'click)
        .selectExpr("click.adId AS clickAdId", "click.clickTime")
        .withWatermark("clickTime", "20 seconds")

      val joinDs = impressionsWithWatermark.join(
        clicksWithWatermark,
        expr(
          """clickAdId = impressionAdId
            |AND clickTime >= impressionTime
            |AND clickTime <= impressionTime + interval 1 minutes""".stripMargin
        )
      )
      val query = joinDs.writeStream.format("console")
      query.start().awaitTermination()
    }
  }

  private def getAvroSchemaAsJson(avroSchemFileName: String) = {
    val path = Thread.currentThread.getContextClassLoader.getResource(avroSchemFileName).getPath
    new String(Files.readAllBytes(Paths.get(path)))
  }
}
