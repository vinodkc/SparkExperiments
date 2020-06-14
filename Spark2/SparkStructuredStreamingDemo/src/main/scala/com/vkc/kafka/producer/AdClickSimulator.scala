package com.vkc.kafka.producer

import java.util.Properties

import com.twitter.bijection.avro.GenericAvroCodecs
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object AdClickSimulator {

  def getImpressionRecord(schema: Schema, generator: EventGenerator): GenericData.Record = {
    val avroRecord = new GenericData.Record(schema)
    val impression = generator.getNextImpression
    avroRecord.put("adId", impression.getAdId)
    avroRecord.put("impressionTime", impression.getImpressionTime)
    println(avroRecord)
    avroRecord
  }

  def getClickRecord(schema: Schema, generator: EventGenerator): GenericData.Record = {
    val avroRecord = new GenericData.Record(schema)
    val click = generator.getNextClick
    avroRecord.put("adId", click.getAdId)
    avroRecord.put("clickTime", click.getClickTime)
    println(avroRecord)
    avroRecord
  }

  def main(args: Array[String]): Unit = {
    if (args.length < 5) {
      println("Usage : com.vkc.kafka.producer.AdClickSimulator <Number of Ads> <bootstrapservers> impressions  <number of impressions messages>  clicks <number of clicks messages>" )
      System.exit(0)
    }
    val numAds = args(0).toInt
    val brokerList = args(1);
    val topicImpressions = args(2)
    val numMsgsImpressions = args(3).toInt
    val topicClicks = args(4)
    val numMsgsClicks = args(5).toInt
    val avroClicksFile = "./avro/Clicks.avsc"
    val avroImpressionFile = "./avro/Impressions.avsc"

    val generator = new EventGenerator(numAds)
    def produceImpressions = produceMessages(brokerList, topicImpressions, numMsgsImpressions, avroImpressionFile, 100, getImpressionRecord, generator)
    def produceClicks = produceMessages(brokerList, topicClicks, numMsgsClicks, avroClicksFile, 500, getClickRecord, generator)

    val executor = java.util.concurrent.Executors.newFixedThreadPool(2)
    List(new Runnable {
      override def run(): Unit =  produceImpressions
    }, new Runnable {
      override def run(): Unit = produceClicks
    }).foreach(r => {
      executor.execute(r)
    })
    executor.shutdown
  }

  private def produceMessages(brokerList: String,
                              topicName: String,
                              numMsgs: Int,
                              avroFile: String,
                              interval: Int,
                              getRecord: ((Schema, EventGenerator) => GenericData.Record),
                              generator: EventGenerator) = {
    val props: Properties = new Properties()
    props.put("bootstrap.servers", brokerList)
    props.put("key.serializer",
      "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer",
      "org.apache.kafka.common.serialization.ByteArraySerializer")
    props.put("acks", "all")
    val parser = new Schema.Parser
    val schema = parser.parse(Thread.currentThread.getContextClassLoader.getResourceAsStream(avroFile))

    val producer = new KafkaProducer[String, Array[Byte]](props)
    val recordInjection = GenericAvroCodecs.toBinary[GenericData.Record](schema)


    Range(1, numMsgs).foreach { x =>
      Thread.sleep(interval)
      val bytes: Array[Byte] = recordInjection.apply(getRecord(schema, generator))
      producer.send(new ProducerRecord[String, Array[Byte]](topicName, bytes))
    }
    println("Message sent successfully")
    producer.close
  }
}
