package com.vkc

import java.lang
import java.util.Properties

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.{CanCommitOffsets, HasOffsetRanges, _}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object StreamDataProcessor {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("StreamTest")
    val bootstrapservers = args(0)
    val kafkaconsumerParams = Map[String, Object](
      "bootstrap.servers" -> bootstrapservers,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "StreamTestGroup",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: lang.Boolean))
    val sourceTopics = Array(args(1))
    val destTopic = args(2)
    val streamingContext = new StreamingContext(conf, Seconds(Integer.parseInt(args(3))))
    val stream = KafkaUtils.createDirectStream[String, String](
      streamingContext,
      PreferConsistent,
      Subscribe[String, String](sourceTopics, kafkaconsumerParams)
    )

    val kafkaProducer: Broadcast[SparkKafkaProducer[String, String]] = {
      val kafkaProducerConfig = {
        val p = new Properties()
        p.setProperty("bootstrap.servers", bootstrapservers)
        p.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        p.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        p
      }
      //create Kafka producer in driver and broadcast to executor
      val producer = SparkKafkaProducer[String, String](kafkaProducerConfig)
      streamingContext.sparkContext.broadcast(producer)
    }

    val bc_destTopic = streamingContext.sparkContext.broadcast(destTopic)

    //find the frequencies of words in each batch
    stream.flatMap(record => record.value.split(" ").filter(_.length > 0).map((_, 1))).
      reduceByKey((v1: Int, v2: Int) => v1 + v2).
      foreachRDD(rdd =>
        if(rdd.isEmpty() == false) {
          rdd.foreachPartition { partition =>
            val topic = bc_destTopic.value
            partition.map { record: (String, Int) =>
              kafkaProducer.value.send(topic, record._1, record._1 + ":" + record._2)
            }.foreach(recordMetadata => recordMetadata.get())
          }
        }
      )
    //after successful processing of messages in current batch commit offsets to kafka
    commitOffests(stream)

    // Start the context
    streamingContext.start()
    streamingContext.awaitTermination()
  }

  private def commitOffests(stream: InputDStream[ConsumerRecord[String, String]]) = {
    stream.foreachRDD { rdd =>
      val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
    }
  }
}

