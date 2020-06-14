package com.vkc.kafka.consumer

import java.time.Duration
import java.util
import java.util.Properties

import com.twitter.bijection.avro.GenericAvroCodecs
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}

abstract class AbstractKafkaAdConsumer(consumerGroup: String,
                                       bootStrapServers: String,
                                       topicName: String,
                                       avroSchemaFile: String) {

  protected def printRecord(genericRecord: GenericData.Record):Unit

  private val createKafkaProperties: Properties = {
    val props: Properties = new Properties()
    props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers)
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringDeserializer")
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.ByteArrayDeserializer")
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    props
  }

  def consumeRecords() = {

    val props = createKafkaProperties
    val consumer = new KafkaConsumer[String, Array[Byte]](props)
    //Kafka ConsumerDemo subscribes list of topics here.
    consumer.subscribe(util.Arrays.asList(topicName))
    println("Subscribed to topic " + topicName)

    val parser = new Schema.Parser
    val file = Thread.currentThread.getContextClassLoader.getResourceAsStream(avroSchemaFile)
    val schema = parser.parse(file)
    val recordInjection = GenericAvroCodecs.toBinary[GenericData.Record](schema)

    while (true) {
      val records = consumer.poll(Duration.ofSeconds(1))
      records.forEach(record => {
        val genericRecord = recordInjection.invert(record.value).get
        //template method
        printRecord(genericRecord)

      })
    }
  }
}
