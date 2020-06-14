package com.vkc.kafka.producer

import com.vkc.avro.{Clicks, Impressions}

import scala.collection.mutable
class EventGenerator(val adCount: Int) {
  private val random = new scala.util.Random
  private val adIds = (1 to adCount).map(r => "AdId-%d".format(r)).toArray

  def getNextImpression: Impressions = {
    val adId = adIds(random.nextInt(adCount))
    new Impressions(adId,System.currentTimeMillis)
  }

  def getNextClick: Clicks = {
    val adId = adIds(random.nextInt(adCount))
    new Clicks(adId,System.currentTimeMillis)
  }

}
