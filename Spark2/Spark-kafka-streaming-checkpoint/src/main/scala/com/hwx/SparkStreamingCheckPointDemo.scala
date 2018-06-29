package com.hwx

import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.{Seconds, StreamingContext}


object SparkStreamingCheckPointDemo extends App {

  def createStreamingContext(
         kafkaParams: Map[String, Object],
         topicNames: String,
         batchInterval: Int,
         checkpointDir: String)() : StreamingContext  = {

    val ssc = new StreamingContext(new SparkConf, Seconds(batchInterval))



    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](Array(topicNames), kafkaParams))

    //Try you processing here
    stream.map(record => (record.key, record.value)).print()

    //after data processing checkpoint the stream
    ssc.checkpoint(checkpointDir)
    ssc
  }

  if(args.length < 4) {
    println("Usage: SparkStreamingCheckPointDemo <bootstrap.servers> <topicnames> <batchinterval> <checkpointdir>")
  } else {
    val conf = new SparkConf().setAppName("SparkStreamingCheckPointDemo")

    import org.apache.kafka.common.serialization.StringDeserializer


    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> args(0),
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "SparkStreamingCheckPointDemoGroup",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    val topics = args(1)
    val batchInterval = Integer.parseInt(args(2))
    // we'll checkpoint to avoid replaying the whole kafka log in case of failure
    val checkpointDir = args(3)


    val streamingContext = StreamingContext.getOrCreate(checkpointDir,
      createStreamingContext(kafkaParams, topics, batchInterval, checkpointDir))

    // Start the context
    streamingContext.start()
    streamingContext.awaitTermination()

  }


}

