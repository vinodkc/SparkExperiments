package com.vkc

import java.lang

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.{CanCommitOffsets, HasOffsetRanges, _}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object StreamDataProcessor2 {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("StreamToHDFS2")
    val bootstrapservers = args(0)
    val kafkaconsumerParams = Map[String, Object](
      "bootstrap.servers" -> bootstrapservers,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "StreamToHDFS2",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: lang.Boolean))
    val sourceTopics = Array(args(1))
    val streamingContext = new StreamingContext(conf, Seconds(Integer.parseInt(args(2))))
    val stream = KafkaUtils.createDirectStream[String, String](
      streamingContext,
      PreferConsistent,
      Subscribe[String, String](sourceTopics, kafkaconsumerParams)
    )

    val spark = SparkSession.builder.getOrCreate()

    import spark.implicits._
    //find the frequencies of words in each batch and save
    stream.flatMap(record => record.value.split(" ").filter(_.length > 0).map((_, 1))).
      reduceByKey((v1: Int, v2: Int) => v1 + v2).foreachRDD{
      rdd =>
        if (!rdd.isEmpty) {
          rdd.mapPartitions{ partition =>
              val currentHour = org.joda.time.DateTime.now().toString("yyyy-MM-dd-hh")
              partition.map(r => Record(r._1, r._2, currentHour))
            }
            .toDS.write.partitionBy("hour").format("parquet")
            .mode(SaveMode.Append)
            .save("/tmp/wordfrequecies.parquet")
        }
    }

    commitOffests(stream)

    // Start the context
    streamingContext.start()
    streamingContext.awaitTermination()
  }

  case class Record(word: String, count: Int, hour: String)

  private def commitOffests(stream: InputDStream[ConsumerRecord[String, String]]) = {
    stream.foreachRDD { rdd =>
      val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
    }
  }
}

