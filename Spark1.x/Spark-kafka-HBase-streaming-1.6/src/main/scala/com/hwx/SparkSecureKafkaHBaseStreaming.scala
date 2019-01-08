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

import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.mapreduce.Job
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable

object SparkSecureKafkaHBaseStreaming {

  def main(args: Array[String]): Unit = {
    if (args.length < 4) {
      println("Usage: SparkSecureKafkaHBaseStreaming <bootstrap.servers> <topicname> <batchinterval> <protocol>  <hbasetable>")
      System.exit(-1)
    }

    val topics = args(1)
    val batchInterval = Integer.parseInt(args(2))
    val protocol = args(3)
    val tableName = args(4)

    val kafkaParams = new mutable.HashMap[String, Object]()
    kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, args(0))
    kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, "SparkStreaming16CheckPointDemoGroup")
    kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer])
    kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer])
    kafkaParams.put("security.protocol", protocol)

    val conf = new SparkConf().setAppName("SparkSecureKafkaHBaseStreaming")

    val ssc = new StreamingContext(conf, Seconds(batchInterval))
    val stream = KafkaUtils.createDirectStream(
      ssc, LocationStrategies.PreferConsistent, ConsumerStrategies.Subscribe[String,String](Set(topics),kafkaParams))


    val jobConf = getHBaseConf(tableName)

    //save to HBase Table
    stream.map(record => parseSensor(record.value)).foreachRDD { rdd =>
      if(!rdd.isEmpty()) {
        rdd.map(convertToPut).saveAsNewAPIHadoopDataset(jobConf)
        println(s"saved data to HBase table $tableName")
      }
    }

    ssc.start()
    ssc.awaitTermination
  }

  private def getHBaseConf(tableName: String) = {
    val hconf = HBaseConfiguration.create()
    val job = Job.getInstance(hconf)
    val jobConf = job.getConfiguration
    jobConf.set(TableOutputFormat.OUTPUT_TABLE, tableName)
    job.setOutputFormatClass(classOf[TableOutputFormat[ImmutableBytesWritable]])
    jobConf
  }

  // function to parse line of sensor data into Sensor class
  def parseSensor(str: String): Sensor = {
    val p = str.split(",")
    Sensor(p(0), p(1), p(2), p(3).toDouble, p(4).toDouble, p(5).toDouble, p(6).toDouble, p(7).toDouble, p(8).toDouble)
  }

  //  Convert a row of sensor object data to an HBase put object
  def convertToPut(sensor: Sensor): (ImmutableBytesWritable, Put) = {
    val cfDataBytes = Bytes.toBytes("data")
    val cfAlertBytes = Bytes.toBytes("alert")
    val colHzBytes = Bytes.toBytes("hz")
    val colDispBytes = Bytes.toBytes("disp")
    val colFloBytes = Bytes.toBytes("flo")
    val colSedBytes = Bytes.toBytes("sedPPM")
    val colPsiBytes = Bytes.toBytes("psi")
    val colChlBytes = Bytes.toBytes("chlPPM")
    val dateTime = sensor.date + " " + sensor.time
    // create a composite row key: sensorid_date time
    val rowkey = sensor.resid + "_" + dateTime
    val put = new Put(Bytes.toBytes(rowkey))
    // add to column family data, column  data values to put object
    put.addColumn(cfDataBytes, colHzBytes, Bytes.toBytes(sensor.hz))
    put.addColumn(cfDataBytes, colDispBytes, Bytes.toBytes(sensor.disp))
    put.addColumn(cfDataBytes, colFloBytes, Bytes.toBytes(sensor.flo))
    put.addColumn(cfDataBytes, colSedBytes, Bytes.toBytes(sensor.sedPPM))
    put.addColumn(cfDataBytes, colPsiBytes, Bytes.toBytes(sensor.psi))
    put.addColumn(cfDataBytes, colChlBytes, Bytes.toBytes(sensor.chlPPM))
    return (new ImmutableBytesWritable(), put)
  }
}


case class Sensor(resid: String, date: String, time: String, hz: Double, disp: Double, flo: Double, sedPPM: Double, psi: Double, chlPPM: Double)
