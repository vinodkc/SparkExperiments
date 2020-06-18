package com.vkc.kafka.consumer.app

import com.vkc.kafka.consumer.AdImpressionsConsumer
import org.apache.avro.generic.GenericData

object AdImpressionsConsumerApp {

  def printRecord(genericRecord: GenericData.Record):Unit =
    println(genericRecord.get("adId"), genericRecord.get("impressionTime"))

  def main(args: Array[String]): Unit = {
    if (args.length < 3) {
      println("Usage : com.vkc.kafka.consumer.AdImpressionsConsumerApp <ConsumerGroupname> <bootstrapservers> impressions" )
      System.exit(0)
    }
    val consumerGroup = args(0)
    val bootStrapServers = args(1)
    val topicImpression = args(2)
    val avroImpressionFile = "./avro/Impressions.avsc"

    new AdImpressionsConsumer(consumerGroup, bootStrapServers,topicImpression, avroImpressionFile).consumeRecords()

  }


}
