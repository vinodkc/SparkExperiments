
1) Create two topics `impressions` and `clicks`

./kafka-topics.sh --create --zookeeper < zkurl >:2181 --replication-factor 3 --partitions 3 --topic impressions
./kafka-topics.sh --create --zookeeper < zkurl >:2181 --replication-factor 3 --partitions 3 --topic clicks

2) Start Producers

com.vkc.kafka.producer.ImpressionSimulator <brokerlist> impressions <<number of messages>>

com.vkc.kafka.producer.AdClickSimulator <Number of Ads> <bootstrapservers> impressions  <number of impressions messages>  clicks <number of clicks messages>"

eg: 
java com.vkc.kafka.producer.AdClickSimulator 100 localhost:9092 impressions  300  clicks 500





**Result**


20/06/18 15:25:16 WARN NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
{"namespace": "com.vkc.avro",
  "type" : "record",
  "name" : "Impressions",
  "fields" : [
   {"name": "adId", "type": "string"},
  {
    "name" : "impressionTime",
    "type" : {
      "type" : "long",
      "logicalType" : "timestamp-millis"
    }
  }]
}
{"namespace": "com.vkc.avro",
  "type" : "record",
  "name" : "Clicks",
  "fields" : [
   {"name": "adId", "type": "string"},
  {
    "name" : "clickTime",
    "type" : {
      "type" : "long",
      "logicalType" : "timestamp-millis"
    }
  }]
}
-------------------------------------------
Batch: 0
-------------------------------------------
+--------------+--------------+---------+---------+
|impressionAdId|impressionTime|clickAdId|clickTime|
+--------------+--------------+---------+---------+
+--------------+--------------+---------+---------+

-------------------------------------------
Batch: 1
-------------------------------------------
+--------------+--------------------+---------+--------------------+
|impressionAdId|      impressionTime|clickAdId|           clickTime|
+--------------+--------------------+---------+--------------------+
|       AdId-10|2020-06-18 15:25:...|  AdId-10|2020-06-18 15:25:...|
|       AdId-10|2020-06-18 15:25:...|  AdId-10|2020-06-18 15:25:...|
|       AdId-10|2020-06-18 15:25:...|  AdId-10|2020-06-18 15:25:...|
|       AdId-10|2020-06-18 15:25:...|  AdId-10|2020-06-18 15:26:...|
|       AdId-10|2020-06-18 15:25:...|  AdId-10|2020-06-18 15:25:...|
|       AdId-10|2020-06-18 15:25:...|  AdId-10|2020-06-18 15:25:...|
|       AdId-10|2020-06-18 15:25:...|  AdId-10|2020-06-18 15:26:...|
|       AdId-10|2020-06-18 15:25:...|  AdId-10|2020-06-18 15:25:...|
|        AdId-3|2020-06-18 15:25:...|   AdId-3|2020-06-18 15:25:...|
|        AdId-3|2020-06-18 15:25:...|   AdId-3|2020-06-18 15:26:...|
|        AdId-3|2020-06-18 15:25:...|   AdId-3|2020-06-18 15:26:...|
|        AdId-3|2020-06-18 15:25:...|   AdId-3|2020-06-18 15:26:...|
|        AdId-3|2020-06-18 15:25:...|   AdId-3|2020-06-18 15:25:...|
|        AdId-3|2020-06-18 15:25:...|   AdId-3|2020-06-18 15:26:...|
|        AdId-3|2020-06-18 15:25:...|   AdId-3|2020-06-18 15:26:...|
|        AdId-3|2020-06-18 15:25:...|   AdId-3|2020-06-18 15:25:...|
|        AdId-3|2020-06-18 15:25:...|   AdId-3|2020-06-18 15:25:...|
|        AdId-3|2020-06-18 15:25:...|   AdId-3|2020-06-18 15:25:...|
|        AdId-3|2020-06-18 15:25:...|   AdId-3|2020-06-18 15:26:...|
|        AdId-2|2020-06-18 15:25:...|   AdId-2|2020-06-18 15:25:...|
+--------------+--------------------+---------+--------------------+
only showing top 20 rows

-------------------------------------------
Batch: 2
-------------------------------------------
+--------------+--------------------+---------+--------------------+
|impressionAdId|      impressionTime|clickAdId|           clickTime|
+--------------+--------------------+---------+--------------------+
|        AdId-3|2020-06-18 15:27:...|   AdId-3|2020-06-18 15:27:...|
|        AdId-3|2020-06-18 15:27:...|   AdId-3|2020-06-18 15:27:...|
|        AdId-4|2020-06-18 15:25:...|   AdId-4|2020-06-18 15:26:...|
|        AdId-4|2020-06-18 15:27:...|   AdId-4|2020-06-18 15:27:...|
|        AdId-4|2020-06-18 15:27:...|   AdId-4|2020-06-18 15:27:...|
|        AdId-4|2020-06-18 15:25:...|   AdId-4|2020-06-18 15:26:...|
|        AdId-4|2020-06-18 15:25:...|   AdId-4|2020-06-18 15:26:...|
|        AdId-9|2020-06-18 15:27:...|   AdId-9|2020-06-18 15:27:...|
|        AdId-9|2020-06-18 15:27:...|   AdId-9|2020-06-18 15:27:...|
|        AdId-7|2020-06-18 15:27:...|   AdId-7|2020-06-18 15:27:...|
|        AdId-7|2020-06-18 15:27:...|   AdId-7|2020-06-18 15:27:...|
|        AdId-6|2020-06-18 15:25:...|   AdId-6|2020-06-18 15:26:...|
|        AdId-6|2020-06-18 15:25:...|   AdId-6|2020-06-18 15:26:...|
|        AdId-6|2020-06-18 15:25:...|   AdId-6|2020-06-18 15:26:...|
|        AdId-6|2020-06-18 15:25:...|   AdId-6|2020-06-18 15:26:...|
|        AdId-6|2020-06-18 15:27:...|   AdId-6|2020-06-18 15:27:...|
+--------------+--------------------+---------+--------------------+
