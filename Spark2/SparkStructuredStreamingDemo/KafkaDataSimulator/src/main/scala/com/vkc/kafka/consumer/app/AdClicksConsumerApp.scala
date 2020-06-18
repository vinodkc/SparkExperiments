package com.vkc.kafka.consumer.app

import com.vkc.kafka.consumer.AdClicksConsumer
import org.apache.avro.generic.GenericData

object AdClicksConsumerApp {

  def printRecord(genericRecord: GenericData.Record):Unit =
    println(genericRecord.get("adId"), genericRecord.get("clickTime"))

  def main(args: Array[String]): Unit = {
    if (args.length < 3) {
      println("Usage : com.vkc.kafka.consumer.AdClicksConsumerApp <ConsumerGroupname> <bootstrapservers> clicks" )
      System.exit(0)
    }

    val consumerGroup = args(0)
    val bootStrapServers = args(1)
    val topicClicks = args(2)
    val avroClicksFile = "./avro/Clicks.avsc"

    new AdClicksConsumer(consumerGroup, bootStrapServers,topicClicks, avroClicksFile).consumeRecords()

  }

}
