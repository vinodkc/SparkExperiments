/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hwx

import java.io.Serializable

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable


object SparkStreaming16CheckPointDemo {

  class XrefFlagInner extends Serializable {

    @transient private var flag = 10 //date
    @transient private var xrefloaded = 10 // static data
    private var nonTransientFlag = 10

    def getFlag(): Int = flag

    def setFlag(value: Int): Unit = flag = value

    def getXrefloaded(): Int = xrefloaded

    def setXrefloaded(value: Int): Unit = xrefloaded = value

    def getNonTransientFlag(): Int = nonTransientFlag

    def setNonTransientFlag(value: Int): Unit = nonTransientFlag = value
  }


  def main(args: Array[String]): Unit = {
    if (args.length < 4) {
      println("Usage: SparkStreaming16CheckPointDemo <bootstrap.servers> <topicname> <batchinterval> <checkpointdir>")
      System.exit(-1)
    }

    var testingflag = 10
    val xrefflag = new XrefFlagInner
    xrefflag.setFlag(10)
    xrefflag.setXrefloaded(10)
    xrefflag.setNonTransientFlag(10)


    val kafkaParams = new mutable.HashMap[String, Object]()
    kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, args(0))
    kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, "SparkStreaming16CheckPointDemoGroup")
    kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer])
    kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer])



    val topics = args(1)
    val batchInterval = Integer.parseInt(args(2))
    val checkpointDir = args(3)

    def createStreamingContext(
         kafkaParams: mutable.HashMap[String, Object],
         topics: String,
         batchInterval: Int,
         checkpointDir: String)(): StreamingContext = {

      val conf = new SparkConf().setAppName("SparkStreaming16CheckPointDemo")

      val ssc = new StreamingContext(conf, Seconds(batchInterval))

      val stream = KafkaUtils.createDirectStream(
        ssc, LocationStrategies.PreferBrokers, ConsumerStrategies.Subscribe[String, String](Set(topics), kafkaParams))

      //Try your processing here
      stream.foreachRDD({ rdd =>
        if (!rdd.isEmpty()) {
          val messages = rdd.map { consumerRecord => consumerRecord.value }.collect()

          if (messages.exists(_ == "false")) {
            // set testingflag if message has the value 'true'
            testingflag = 20
            xrefflag.setFlag(20)
            xrefflag.setXrefloaded(20)
            xrefflag.setNonTransientFlag(20)
          }
          println("***************testingflag******************" + testingflag)
          println("***************xrefflag.getFlag******************" + xrefflag.getFlag())
          println("***************xrefflag.getXrefloaded******************" + xrefflag.getXrefloaded())
          println("***************xrefflag.getNonTransientFlag******************" + xrefflag.getNonTransientFlag())
        }
      })

      //after data processing checkpoint the stream
      ssc.checkpoint(checkpointDir)

      ssc
    }

    val streamingContext = StreamingContext.getOrCreate(checkpointDir,
      createStreamingContext(kafkaParams, topics, batchInterval, checkpointDir))

    streamingContext.start()
    streamingContext.awaitTermination()
  }
}
