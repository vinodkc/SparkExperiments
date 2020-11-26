package com.vkc

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.window

object SparkSecureSSLKafkaStructuredStreamingDemo {

  def main(args: Array[String]): Unit = {
    if(args.length < 3) {
      println("Usage: SparkSecureSSLKafkaStructuredStreamingDemo  <bootstrapservers>  <SecurityProtocol> <sourcetopicname> <ssl truststore filename> <ssl truststore password>")

    } else {

      val spark = SparkSession.builder().appName("SparkSecureSSLKafkaStructuredStreamingDemo").getOrCreate()
      import spark.implicits._

      val bootstrapServers = args(0)
      val securityProtocol = args(1)
      val sourceTopic = args(2)

      //SSL confs
      val ssl_truststoreFile = args(3)
      val ssl_truststorePassword = args(4)

      val sensorStreamDs = spark.readStream.
        format("kafka").
        option("kafka.bootstrap.servers", bootstrapServers).
        option("kafka.security.protocol", securityProtocol).
        option("kafka.ssl.truststore.location", ssl_truststoreFile).
        option("kafka.ssl.truststore.password", ssl_truststorePassword).
        option("subscribe", sourceTopic).
        load().
        selectExpr("CAST(value AS STRING)").as[String]

      val sensorDs = sensorStreamDs.map(value=> {
        val columns = value.split(",")
        Sensor(new java.sql.Timestamp(columns(0).toLong), columns(1), columns(2).toDouble)
      }).groupBy(window($"time", "10 seconds"), $"sensorId").avg("value")

      val query = sensorDs.writeStream.format("console").option("truncate", "false").outputMode(org.apache.spark.sql.streaming.OutputMode.Complete())
      query.start().awaitTermination()
    }
  }
}
