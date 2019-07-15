package com.vkc

import org.apache.spark.sql.SparkSession


  object SparkHiveDemo3 {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().appName("SparkHiveDemo3").enableHiveSupport().getOrCreate()

    println("*********start**********")
    spark.sql(args(0)).show(false)
    println("*********End**********")



  }
}
