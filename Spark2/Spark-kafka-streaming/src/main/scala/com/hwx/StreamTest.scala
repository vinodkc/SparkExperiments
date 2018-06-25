package com.hwx

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}


object StreamTest extends App {

  val conf = new SparkConf().setAppName("StreamTest")

  import org.apache.kafka.common.serialization.StringDeserializer
  import org.apache.spark.streaming.kafka010._
  import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
  import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> args(0),
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> "StreamTestGroup",
    "auto.offset.reset" -> "latest",
    "enable.auto.commit" -> (false: java.lang.Boolean)
  )
  val topics = Array(args(1))
  val streamingContext = new StreamingContext(conf, Seconds(Integer.parseInt(args(2))))
  val stream = KafkaUtils.createDirectStream[String, String](
    streamingContext,
    PreferConsistent,
    Subscribe[String, String](topics, kafkaParams)
  )

  stream.map(record => (record.key, record.value)).print()


  stream.foreachRDD { rdd =>
    val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges


    stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
  }

  // Start the context
  streamingContext.start()
  streamingContext.awaitTermination()

}

