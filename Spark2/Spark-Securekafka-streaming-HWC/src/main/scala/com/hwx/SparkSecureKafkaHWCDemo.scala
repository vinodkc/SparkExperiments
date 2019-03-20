package com.hwx

import com.hortonworks.hwc.HiveWarehouseSession
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkSecureKafkaHWCDemo extends App {

  if(args.length < 5) {
    println("Usage: SparkSecureKafkaHWCDemo  <bootstrapservers>  <SecurityProtocol> <topicname> <batchinterval> <consumergroupname>")

  } else {
    import org.apache.kafka.common.serialization.StringDeserializer
    import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
    import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
    import org.apache.spark.streaming.kafka010._

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> args(0),
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" ->  args(4),
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean),
      "security.protocol" -> args(1)
    )

    val conf = new SparkConf().setAppName("SparkSecureKafkaDemo")
    val streamingContext = new StreamingContext(conf, Seconds(Integer.parseInt(args(3))))

    val topics = Array(args(2))
    val stream = KafkaUtils.createDirectStream[String, String](
      streamingContext,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )
    val spark = SparkSession.builder.appName("Simple Application").getOrCreate()
    val hive = HiveWarehouseSession.session(spark).build()

    import spark.implicits._
    //Try your transformations
    stream.map(record => (record.partition(),record.offset(), record.value)).foreachRDD( rdd =>
      if (!rdd.isEmpty()) {
        val df = rdd.toDF().withColumnRenamed("_1", "partitionnumber")
          .withColumnRenamed("_2", "offset")
          .withColumnRenamed("_3", "data")

          df.write
          .format(HiveWarehouseSession.DATAFRAME_TO_STREAM).option("table", "messages")
          .save()
        //Commit the offsets
        val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
        stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
      }
    )

    // Start the context
    streamingContext.start()
    streamingContext.awaitTermination()
  }



}

