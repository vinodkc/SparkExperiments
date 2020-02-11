# Spark-Securekafka-Structured-streaming  Demo

**Project setup**

1) `cd ./SparkExperiments/Spark2/Spark-Securekafka-Structured-streaming`

   `mvn install`
   
**copy following jars into a dir into gateway node**
   
```
   target/SparkSecureKafkaStructuredStreaming-Demo-1.0.jar
   target/lib/spark-sql-kafka-0-10_2.11-2.3.2.3.1.4.0-315.jar
   target/lib/kafka-clients-2.0.0.3.1.4.0-315.jar
 ```

**Create a topic**
```
cd /usr/hdp/current/kafka-broker/bin/
./kafka-topics.sh --create --zookeeper c220-node2.squadron-labs.com:2181 --replication-factor 2 --partitions 3 --topic sensortopic
```



```
ll
-rw-r--r-- 1 spark hadoop   12107 Feb 11 06:44 SparkSecureKafkaStructuredStreaming-Demo-1.0.jar
-rw-r--r-- 1 spark hadoop 1894756 Feb 11 06:54 kafka-clients-2.0.0.3.1.4.0-315.jar
-rw-r--r-- 1 spark hadoop  417361 Feb 11 06:45 spark-sql-kafka-0-10_2.11-2.3.2.3.1.4.0-315.jar
```

**Start spark structured streaming application**

```
spark-submit --master yarn --deploy-mode client   --conf spark.executor.extraJavaOptions="-Djava.security.auth.login.config=kafka_client_jaas.conf" --conf spark.driver.extraJavaOptions="-Djava.security.auth.login.config=kafka_client_jaas.conf" --files ./kafka_client_jaas.conf,./spark.headless.keytab  --jars --jars ./kafka-clients-2.0.0.3.1.4.0-315.jar,./spark-sql-kafka-0-10_2.11-2.3.2.3.1.4.0-315.jar --conf spark.sql.shuffle.partitions=3  --class com.vkc.SparkSecureKafkaStructuredStreamingDemo ./SparkSecureKafkaStructuredStreaming-Demo-1.0.jar c220-node2.squadron-labs.com:6667 SASL_PLAINTEXT sensortopic
```

**Start producing some data**

```
/usr/hdp/current/kafka-broker/bin/kafka-console-producer.sh --broker-list c220-node2.squadron-labs.com:6667    --topic sensortopic --producer-property security.protocol=SASL_PLAINTEXT
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
