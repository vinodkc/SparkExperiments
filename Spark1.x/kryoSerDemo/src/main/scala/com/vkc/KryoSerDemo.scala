package com.vkc

import com.esotericsoftware.kryo.Kryo
import com.vkc.KryoSerDemo.Person
import org.apache.spark.serializer.KryoRegistrator
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}

class MyKryoRegistrator extends KryoRegistrator {
  override def registerClasses(kryo: Kryo) {
    kryo.register(classOf[Person])
    kryo.register(classOf[Range])
  }
}
object KryoSerDemo extends App {

  case class Person(name: String, age: Long)




  val conf = new SparkConf().setAppName("KryoSerDemo")
  conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
  conf.set("spark.kryo.registrationRequired", "true")
  conf.set("spark.kryo.registrator", "com.vkc.MyKryoRegistrator")
  val sc = new SparkContext(conf)


  val limit = Integer.parseInt(args(1))

  val distData = sc.parallelize(1 to limit).map(i => Person("Name-"+i, i*10) ).persist(StorageLevel.MEMORY_ONLY_SER)
  println(distData.count());
  println(distData.count());
  println(distData.count());


  distData.saveAsObjectFile("/tmp/"+args(0))
}
