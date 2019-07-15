package com.vkc

import org.apache.spark.sql.SparkSession


object SparkHDFSDemo3 {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().appName("SparkHiveDemo3").enableHiveSupport().getOrCreate()

    println("*********start**********")
    spark.read.textFile(args(0)).show(false)
    println("*********End**********")



  }
}
