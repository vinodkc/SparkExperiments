package com.vkc

import org.apache.spark.sql.SparkSession


object SparkHiveDemo {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().master("local").appName("SparkHiveDemo").enableHiveSupport().getOrCreate()

    val fileName = if (args.length > 0) args(0) else getClass().getClassLoader().getResource("data.csv").getPath

    spark.sql("""CREATE TABLE IF NOT EXISTS employee ( eid int, name String, salary String, destination String)
                |COMMENT 'Employee details'
                |ROW FORMAT DELIMITED
                |FIELDS TERMINATED BY ','
                |LINES TERMINATED BY '\n'
                |STORED AS TEXTFILE""".stripMargin)

    println("Count before data load")

    spark.sql("SELECT count(*) FROM employee").show

    spark.sql(s"""LOAD DATA LOCAL INPATH '$fileName' INTO TABLE employee""")

    println("Count after data load")

    spark.sql("SELECT count(*) FROM employee").show

  }
}
