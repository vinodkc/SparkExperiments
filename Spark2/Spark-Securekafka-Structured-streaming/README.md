# Spark-Securekafka- Hive warehouse connector streaming  Example

**Project setup**

1) cd ./SparkExperiments/Spark2/Spark-Securekafka-Structured-streaming

   mvn package
   
   copy following jars into a dir in gateway node
   SparkSecureKafkaStructuredStreaming-Demo-1.0.jar from target dir
   
   download spark-sql-kafka-0-10_2.11-2.3.1.jar and kafka-clients-0.10.0.1.jar from maven central

**Create a topic**
```
./kafka-topics.sh --create --zookeeper c220-node2.squadron-labs.com:2181 --replication-factor 2 --partitions 3 --topic sensortopic
```



```
ll
-rw-r--r-- 1 spark hadoop  14490 Jul  9 06:00 SparkSecureKafkaStructuredStreaming-Demo-1.0.jar
-rw-r--r-- 1 spark hadoop 746207 Jul  9 06:30 kafka-clients-0.10.0.1.jar  //Important, you must use version 0.10.0.1 jar
-rw-r--r-- 1 spark hadoop    220 Jul  8 11:34 kafka_client_jaas.conf
-rw-r--r-- 1 spark hadoop 417309 Jul  8 11:13 spark-sql-kafka-0-10_2.11-2.3.1.jar
-r-------- 1 spark hadoop    298 Jul  8 11:29 spark.headless.keytab
```

**Start spark structured streaming application**

```
spark-submit --master yarn --deploy-mode cluster   --conf spark.executor.extraJavaOptions="-Djava.security.auth.login.config=kafka_client_jaas.conf" --conf spark.driver.extraJavaOptions="-Djava.security.auth.login.config=kafka_client_jaas.conf" --files ./kafka_client_jaas.conf,./spark.headless.keytab  --jars ./spark-sql-kafka-0-10_2.11-2.3.1.jar,./kafka-clients-0.10.0.1.jar --conf spark.sql.shuffle.partitions=3  --class com.vkc.SparkSecureKafkaStructuredStreamingDemo ./SparkSecureKafkaStructuredStreaming-Demo-1.0.jar c220-node2.squadron-labs.com:6667 SASL_PLAINTEXT sourcetopic
```

**Start producing some data**

```
/usr/hdp/current/kafka-broker/bin/kafka-console-producer.sh --broker-list c220-node2.squadron-labs.com:6667    --topic sourcetopic --producer-property security.protocol=SASL_PLAINTEXT
1461756862000,"SID1",500.0
1461756862000,"SID1",500.0
1461756862000,"SID1",500.0
1461756862000,"SID1",500.0
1461756862000,"SID1",500.0
1461756862000,"SID1",200.0
1461756862000,"SID1",500.0
1461756862000,"SID1",500.0
1461756862000,"SID1",500.0
1461756862000,"SID1",500.0
1461756862000,"SID1",500.0
1461756862000,"SID1",50.0
1461756862000,"SID1",500.0
1461756862000,"SID1",500.0
1461756862000,"SID1",500.0
1461756862000,"SID1",500.0
1461756862000,"SID1",5
1461756862000,"SID1",50
1461756862000,"SID1",500.0
```

**Spark application console output**


-------------------------------------------
Batch: 4
-------------------------------------------
+------------------------------------------+--------+----------+
|window                                    |sensorId|avg(value)|
+------------------------------------------+--------+----------+
|[2016-04-27 11:34:20, 2016-04-27 11:34:30]|"SID1"  |387.5     |
+------------------------------------------+--------+----------+

-------------------------------------------
Batch: 5
-------------------------------------------
+------------------------------------------+--------+----------+
|window                                    |sensorId|avg(value)|
+------------------------------------------+--------+----------+
|[2016-04-27 11:34:20, 2016-04-27 11:34:30]|"SID1"  |410.0     |
+------------------------------------------+--------+----------+
