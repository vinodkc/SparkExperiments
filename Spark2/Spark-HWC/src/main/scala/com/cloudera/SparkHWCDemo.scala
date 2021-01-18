package com.cloudera

import com.hortonworks.hwc.HiveWarehouseSession
import org.apache.spark.sql.{SaveMode, SparkSession}

object SparkHWCDemo  {

  def main(args: Array[String]): Unit = {

    if (args.length < 2) {
      println("Missing arguments  <input tablename> <output tablename> ")
      System.exit(0);
    }
    val inputTable = args(0)
    val outputTable = args(1)
    val spark = SparkSession.builder.appName("SparkHWCDemo").getOrCreate()
    val hive = HiveWarehouseSession.session(spark).build()
    println("Selecting from " + inputTable)
    val inputTableDf = hive.executeQuery("select * from " + inputTable + " limit 100")
    inputTableDf.show(false)
    println("Saving " + inputTable + " to " + outputTable)
    inputTableDf.write
      .format(HiveWarehouseSession.HIVE_WAREHOUSE_CONNECTOR)
      .option("table", outputTable)
      .mode(SaveMode.Append)
      .save()
    println("Saved " + inputTable + " to " + outputTable)


  }


}

