package com.hwx

import org.apache.kafka.clients.CommonClientConfigs
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}


object SparkSecureSSLKafkaDemo extends App {

  if(args.length < 5) {
    println("Usage: SparkSecureSSLKafkaDemo  <bootstrapservers>  <SecurityProtocol> <topicname> <batchinterval> <consumergroupname> <ssl truststore filename> <ssl truststore password>")

  } else {
    val conf = new SparkConf().setAppName("SparkSecureSSLKafkaDemo")

    import org.apache.kafka.common.serialization.StringDeserializer
    import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
    import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
    import org.apache.spark.streaming.kafka010._

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> args(0),
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> args(4),
      "ssl.truststore.location" -> args(5),
      "ssl.truststore.password" -> args(6),
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean),
      CommonClientConfigs.SECURITY_PROTOCOL_CONFIG -> args(1)
    )

    val topics = Array(args(2))
    val streamingContext = new StreamingContext(conf, Seconds(Integer.parseInt(args(3))))
    val stream = KafkaUtils.createDirectStream[String, String](
      streamingContext,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    //Try your transformations
    stream.map(record => (record.key, record.value)).print()

    //Commit offsets of already processed messages
    stream.foreachRDD { rdd =>
      val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
    }

    // Start the context
    streamingContext.start()
    streamingContext.awaitTermination()
  }



}

