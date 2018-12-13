package com.hwx

import java.lang

import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.mapreduce.Job
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}



object SparkKafkaHBaseDemo2  {
  def main(args: Array[String]): Unit = {

    if(args.length < 5) {
      println("Usage: SparkKafkaHBaseDemo2  <bootstrapservers>  <SecurityProtocol> <topicname> <batchinterval> <consumergroupname> <hbaseTablename>")

    } else {

      val sparkConf = new SparkConf().setAppName("SparkSecureKafkaDemo")

      import org.apache.kafka.common.serialization.StringDeserializer
      import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
      import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
      import org.apache.spark.streaming.kafka010._

      val kafkaParams = Map[String, Object](
        "bootstrap.servers" -> args(0),
        "key.deserializer" -> classOf[StringDeserializer],
        "value.deserializer" -> classOf[StringDeserializer],
        "group.id" -> args(4),
        "auto.offset.reset" -> "latest",
        "enable.auto.commit" -> (false: lang.Boolean),
        CommonClientConfigs.SECURITY_PROTOCOL_CONFIG -> args(1)
      )

      val topics = Array(args(2))
      val streamingContext = new StreamingContext(sparkConf, Seconds(Integer.parseInt(args(3))))
      val stream = KafkaUtils.createDirectStream[String, String](
        streamingContext,
        PreferConsistent,
        Subscribe[String, String](topics, kafkaParams)
      )

      val tableName = args(5)
      val jobConf = getHBaseConf(tableName)


      //save to HBase Table
      stream.map(record => parseSensor(record.value)).foreachRDD { rdd =>
        if(!rdd.isEmpty()) {
          rdd.map(convertToPut).saveAsNewAPIHadoopDataset(jobConf)
        }
      }

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
