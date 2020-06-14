package com.vkc.kafka.consumer
import org.apache.avro.generic.GenericData

class AdImpressionsConsumer(consumerGroup: String,
                            bootStrapServers: String,
                            topicName: String,
                            avroSchemaFile: String) extends  AbstractKafkaAdConsumer(
  consumerGroup,
  bootStrapServers,
  topicName,
  avroSchemaFile) {

  override protected def printRecord(genericRecord: GenericData.Record): Unit =
    println(genericRecord.get("adId"), genericRecord.get("impressionTime"))
}
