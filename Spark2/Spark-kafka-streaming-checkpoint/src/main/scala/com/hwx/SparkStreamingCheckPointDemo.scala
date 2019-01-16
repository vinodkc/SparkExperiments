package com.hwx

import org.apache.kafka.clients.CommonClientConfigs
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.{Seconds, StreamingContext}


object SparkStreamingCheckPointDemo  {

  var testingflag = false
  var testingflag2 = true
  var testingInt = 10




  def main(args: Array[String]): Unit = {



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

      println("In main testingflag"+ testingflag)
      println("In Main  testingflag2"+ testingflag2)
      println("In Main  testingInt"+ testingInt)
      val streamingContext = StreamingContext.getOrCreate(checkpointDir,
        createStreamingContext(kafkaParams, topics, batchInterval, checkpointDir))

      // Start the context
      streamingContext.start()
      streamingContext.awaitTermination()

    }

  }

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

    println("Starting stream with testingflag "+ testingflag)
    println("Starting stream with testingflag2"+ testingflag2)

    //Try your processing here
    stream.foreachRDD({ rdd =>
      if (!rdd.isEmpty()) {
        val messages = rdd.map { consumerRecord => consumerRecord.value }.collect()

        if(messages.exists(_ == "true")) {
          testingflag = true
          testingflag2 = false
          testingInt = 20;

        }
        messages.foreach(print(_))
        println("***************testingflag******************"+ testingflag)
        println("***************testingflag2******************"+ testingflag2)
        println("***************testingInt******************"+ testingInt)
      }
    })

    stream.foreachRDD { rdd =>
    val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges


    stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
  }
    //after data processing checkpoint the stream
    ssc.checkpoint(checkpointDir)
    ssc
  }

}
