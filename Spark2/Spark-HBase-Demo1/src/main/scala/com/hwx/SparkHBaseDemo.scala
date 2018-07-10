package com.hwx

import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.{TableInputFormat, TableOutputFormat}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.mapreduce.Job
import org.apache.spark.sql._


object SparkHBaseDemo extends App {

  val spark = SparkSession
    .builder()
    .appName("SparkHBaseDemo")
    .enableHiveSupport()
    .getOrCreate()
 /* val df = spark.read.json("file:///usr/hdp/current/spark2-client/examples/src/main/resources/people.json")
  df.write.format("orc").save("/tmp/demo.orc/")*/
  val data = spark.read.format("orc").load("/tmp/demo.orc/")

 /* data.printSchema
  data.show()*/

  val hconf = HBaseConfiguration.create()

  /*people-example
  COLUMN FAMILIES DESCRIPTION
  {NAME => 'personal', BLOOMFILTER => 'ROW', VERSIONS => '1', IN_MEMORY => 'false', KEEP_DELETED_CELLS => 'FALSE', DATA_BLOCK_ENCODING => 'NONE',
    TTL => 'FOREVER', COMPRESSION => 'NONE', MIN_VERSIONS => '0', BLOCKCACHE => 'true', BLOCKSIZE => '65536', REPLICATION_SCOPE => '0'}
*/
  val hbaseTable = "people-example"

/*  hconf.set(TableOutputFormat.OUTPUT_TABLE, hbaseTable)
  hconf.set(TableInputFormat.INPUT_TABLE, hbaseTable)
 // HBaseAdmin.checkHBaseAvailable(hconf)
  val htable = new HTable(hconf, hbaseTable)
  println("Hbase table connection established")
  println(htable.getTableDescriptor)

  println("Getting count of records in table now...")
  val hBaseRDD = spark.sparkContext.newAPIHadoopRDD(hconf, classOf[TableInputFormat],
    classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable],
    classOf[org.apache.hadoop.hbase.client.Result])
  println(hBaseRDD.count())
  println("Done!")*/

  data.printSchema

  val job = Job.getInstance(hconf)
  val jobConf = job.getConfiguration
  jobConf.set(TableOutputFormat.OUTPUT_TABLE, hbaseTable)
  job.setOutputFormatClass(classOf[TableOutputFormat[ImmutableBytesWritable]])

  println("Before hbasePuts")
  val hbasePuts= data.rdd.zipWithIndex().map{ r =>
    val row = r._1
    val key = r._2

    println("inside hbasePuts")

    val age = if(row.get(0) == null) "0" else { row.getLong(0).toString }
    val name = row.getString(1)

    println ("Key is :" + key, " Age :" + age + "  Name :" + name)
    val  put = new Put(Bytes.toBytes(key.toString))
    println("After Put")
    put.addColumn(Bytes.toBytes("personal"), Bytes.toBytes("age"),Bytes.toBytes(age));
    put.addColumn(Bytes.toBytes("personal"), Bytes.toBytes("name"),Bytes.toBytes(name));
    (new ImmutableBytesWritable(), put)
  }
  hbasePuts.saveAsNewAPIHadoopDataset(jobConf)
  println("after hbasePuts save")

}

