package com.vkc

import org.apache.spark.internal.Logging

import org.apache.spark.sql.SparkSession

import java.util.concurrent.TimeUnit
import scala.util.{Try, Success, Failure}


object SparkExceptionTest extends Logging{

  def main(args: Array[String]): Unit = {

      val spark = SparkSession
        .builder
        .appName("SparkExceptionTest")
        .getOrCreate()

      val procReturnVal = Try {
        doDBIngest(spark)
      }
      procReturnVal match {
        case Success(value) => logInfo(s"Success: $value")
        case Failure(exception) => {
          logError(s"Failure: ${exception}")
          // Still this application will wait till the non daemon thread completes
          throw exception
        }
      }
  }

  def doDBIngest(spark: SparkSession): Boolean = {

    getStartTimesFromZookeeper()

    logInfo("doDBIngest finished")
    true
  }
  def getStartTimesFromZookeeper2(throwExceptionFlag: Boolean) : Boolean = {
    throw new RuntimeException("This is a deliberate exception from getStartTimesFromZookeeper to simulate Zookeeper connection error")
    true
  }

  def getStartTimesFromZookeeper() : Boolean = {
    //Simulate zookeeper internal threads and Runtime exception from one of the thread
    val zookeeper_fetcherthread1 = new Thread {
      override def run(): Unit = {
        logInfo("Thread 1 started, sleeping for 10 minutes...This is to simulate a Zookeeper connection hung")
        try {
          for (a <- 1 to 10) {
            logInfo("Thread 1 sleeping for 1 min")
            TimeUnit.MINUTES.sleep(1)
            logInfo("Thread 1 woke up!")
          }
          logInfo("Thread 1 Finished")

        } catch {
          case e: InterruptedException => logInfo("Thread 1 interrupted!")
        }
      }
    }
   // zookeeper_fetcherthread1.setDaemon(true) // "NOTE : This will solve the problem of application freeze"
    // Start  Zookeeper connection hung threads
    zookeeper_fetcherthread1.start()
    //simulating some other work
    TimeUnit.MINUTES.sleep(1)


    // Simulate runtimeException after interrupting active thread
    zookeeper_fetcherthread1.interrupt()
    throw new RuntimeException("This is a deliberate exception from getStartTimesFromZookeeper while another thread in hung state")

    true
  }
}
