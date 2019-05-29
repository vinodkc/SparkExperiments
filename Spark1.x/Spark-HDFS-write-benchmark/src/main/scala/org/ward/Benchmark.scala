package org.ward

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import java.lang.System.{currentTimeMillis => _time}
import org.apache.log4j.LogManager

object Benchmark {
  def main(args: Array[String]){
    
    def profile[R](code: => R, t: Long = _time) = (code, _time - t)

    val nFiles = args(0).toInt
    val fSize = args(1).toInt //bytes
    val repeat = args(2).toInt
    val outputTempPath = "_benchmark_out"

    // set config
    val sc = new SparkContext(
        new SparkConf().setAppName("Benchmark").set("spark.hadoop.dfs.replication", "1")
    )
    sc.hadoopConfiguration.set("mapred.output.compress", "false")
      
    val log = LogManager.getRootLogger
    val fs = FileSystem.get(new Configuration(true))

    // create RDD
    val a = sc.parallelize(1 until nFiles + 1, nFiles)
    val b = a.map( i => {
      "0" * fSize
    })

    // force calculation on RDD
    b.count()

    
    // write to HDFS
    var totalTimeW = 0L

    for (i <- 1 to repeat) {
      //make sure dir is empty
      fs.delete(new Path(outputTempPath), true)
      val (junk, timeW) = profile {
        b.saveAsTextFile(outputTempPath)
      }
      log.info("\nABenchmark: Pass " + i + " Aggregate Throughput : " + (nFiles * fSize.toLong)/(timeW/1000.toFloat) + " Bytes per second")
      totalTimeW += timeW
    }

    log.info("\n\nBenchmark: Total volume         : " + (repeat * nFiles.toLong * fSize) + " Bytes")
    log.info("\nBenchmark: Total write time     : " + (totalTimeW/1000.toFloat) + " s")
    log.info("\nABenchmark: Aggregate Throughput : " + ((repeat * nFiles * fSize.toLong)/125000000)/(totalTimeW/1000.toFloat) + " Gigabit per second\n")
  }
}
