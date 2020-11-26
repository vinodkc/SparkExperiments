# Spark-Secure kafka-Structured-streaming  Demo

**Project setup**

1) `cd ./SparkExperiments/Spark2/Spark-Securekafka-Structured-streaming`

   `mvn install`
   
**copy following jars into a dir into gateway node**
   
```
   target/SparkSecureKafkaStructuredStreaming-Demo-1.0.jar
   target/lib/spark-sql-kafka-0-10_2.11-2.3.2.3.1.4.0-315.jar
   target/lib/kafka-clients-2.0.0.3.1.4.0-315.jar
 ```

```
ll
-rw-r--r-- 1 spark hadoop   12107 Feb 11 06:44 SparkSecureKafkaStructuredStreaming-Demo-1.0.jar
-rw-r--r-- 1 spark hadoop 1894756 Feb 11 06:54 kafka-clients-2.0.0.3.1.4.0-315.jar
-rw-r--r-- 1 spark hadoop  417361 Feb 11 06:45 spark-sql-kafka-0-10_2.11-2.3.2.3.1.4.0-315.jar
```

1.1) Kinit as Kafka user 

1.2) create kafka topic 

```./kafka-topics.sh --create --zookeeper ip:2181 --replication-factor 3 --partitions 12 --topic sensortopic```

2) Give Produce & Consume  rights to user `spark-user1` on topic `sensortopic` and any consumer group name starting with name `spark-` . (Use Ranger or Kafka ACL)
 
 Kafka ACL Example 
 --------------
 a) Give producer acl
 
```
./kafka-acls.sh --authorizer-properties zookeeper.connect=c3543-node2:2181    --add --allow-principal User:spark-user1 --producer --topic sensortopic
```

 b) Give consumer acl to `sensortopic` and any consumer group name starting with name `spark-`
```
./kafka-acls.sh --authorizer-properties zookeeper.connect=c3543-node2:2181  --add --allow-principal User:spark-user1 --consumer --topic sensortopic --group 'spark-' --resource-pattern-type prefixed
```

3) From another host/terminal, login as `spark-user1` and produce data  
```
./kafka-console-producer.sh --broker-list ip:6667   --topic sensortopic --producer-property security.protocol=SASL_PLAINTEXT
```

Copy & paste below rows one row at a time (Don't copy all rows and paste, Kafka producer will fail to parse multiple rows at once)

```
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

4) From another host/terminal, login as `spark-user1` and try 4.1  or 4.2

# SASL_PLAINTEXT DEMO

4.1) Spark - Kafka SASL_PLAINTEXT DEMO

```
Usage: SparkSecureKafkaDemo  <bootstrapservers>  <SecurityProtocol> <topicname> <batchinterval> <consumergroupname>
```

Yarn Client mode

```
/usr/hdp/current/spark2-client/bin/spark-submit --master yarn --deploy-mode client   --keytab ./spark-user1_forSpark.keytab --principal spark-user1@COELAB.CLOUDERA.COM  --conf spark.executor.extraJavaOptions="-Djava.security.auth.login.config=kafka_client_jaas.conf" --conf spark.driver.extraJavaOptions="-Djava.security.auth.login.config=kafka_client_jaas.conf" --files ./kafka_client_jaas.conf,./spark-user1.keytab --jars ./kafka-clients-2.0.0.3.1.4.0-315.jar,./spark-sql-kafka-0-10_2.11-2.3.2.3.1.4.0-315.jar --conf spark.sql.shuffle.partitions=3  --class com.vkc.SparkSecureKafkaStructuredStreamingDemo ./SparkSecureKafkaStructuredStreaming-Demo-1.0.jar c3543-node2.coelab.cloudera.com:6667 SASL_PLAINTEXT sensortopic
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

Yarn Cluster mode
```
/usr/hdp/current/spark2-client/bin/spark-submit --master yarn --deploy-mode cluster   --keytab ./spark-user1_forSpark.keytab --principal spark-user1@VKC.COM --conf spark.executor.extraJavaOptions="-Djava.security.auth.login.config=kafka_client_jaas.conf" --conf spark.driver.extraJavaOptions="-Djava.security.auth.login.config=kafka_client_jaas.conf" --files ./kafka_client_jaas.conf,./spark-user1.keytab --jars ./kafka-clients-2.0.0.3.1.4.0-315.jar,./spark-sql-kafka-0-10_2.11-2.3.2.3.1.4.0-315.jar --conf spark.sql.shuffle.partitions=3  --class com.vkc.SparkSecureKafkaStructuredStreamingDemo ./SparkSecureKafkaStructuredStreaming-Demo-1.0.jar ip:6667 SASL_PLAINTEXT sensortopic
```
you will see the result on application log

# SASL_SSL DEMO

4.2) Spark - Kafka SASL_SSL DEMO

```Usage: SparkSecureSSLKafkaStructuredStreamingDemo  <bootstrapservers>  <SecurityProtocol> <sourcetopicname> <ssl truststore filename> <ssl truststore password>```

Yarn Client mode

```
/usr/hdp/current/spark2-client/bin/spark-submit --master yarn --deploy-mode client --keytab ./spark-user1_forSpark.keytab --principal spark-user1@VKC.COM --conf spark.executor.extraJavaOptions="-Djava.security.auth.login.config=kafka_client_jaas.conf" --conf spark.driver.extraJavaOptions="-Djava.security.auth.login.config=kafka_client_jaas.conf" --files ./client.truststore.jks,./kafka_client_jaas.conf,./spark-user1.keytab --jars ./kafka-clients-2.0.0.3.1.4.0-315.jar,./spark-sql-kafka-0-10_2.11-2.3.2.3.1.4.0-315.jar --conf spark.sql.shuffle.partitions=12  --class com.vkc.SparkSecureSSLKafkaStructuredStreamingDemo ./SparkSecureKafkaStructuredStreaming-Demo-1.0.jar ip:6668 SASL_SSL sensortopic client.truststore.jks Hadoop@123
```
Yarn Cluster mode

```
/usr/hdp/current/spark2-client/bin/spark-submit --master yarn --deploy-mode cluster --keytab ./spark-user1_forSpark.keytab --principal spark-user1@VKC.COM --conf spark.executor.extraJavaOptions="-Djava.security.auth.login.config=kafka_client_jaas.conf" --conf spark.driver.extraJavaOptions="-Djava.security.auth.login.config=kafka_client_jaas.conf" --files ./client.truststore.jks,./kafka_client_jaas.conf,./spark-user1.keytab --jars ./kafka-clients-2.0.0.3.1.4.0-315.jar,./spark-sql-kafka-0-10_2.11-2.3.2.3.1.4.0-315.jar --conf spark.sql.shuffle.partitions=12  --class com.vkc.SparkSecureSSLKafkaStructuredStreamingDemo ./SparkSecureKafkaStructuredStreaming-Demo-1.0.jar ip:6668 SASL_SSL sensortopic client.truststore.jks Hadoop@123
```

Note: spark-user1_forSpark.keytab is a copy of spark-user1.keytab ; A hack to support both --keytab & --files ./consumer-user.keytab

cat kafka_client_jaas.conf

```
cat kafka_client_jaas.conf
KafkaClient {
  com.sun.security.auth.module.Krb5LoginModule required
  doNotPrompt=false
  useTicketCache=false
  principal="spark-user1@VKC.COM"
  useKeyTab=true
  serviceName="kafka"
  keyTab="spark-user1.keytab";
};
```

Directory listing

```
-rw-r--r-- 1 spark-user1 hadoop  17434 Nov 26 11:27 SparkSecureKafkaStructuredStreaming-Demo-1.0.jar
-rw-r--r-- 1 spark-user1 hadoop  1894756 Nov 26 11:27 kafka-clients-2.0.0.3.1.4.0-315.jar
-rw-r--r-- 1 spark-user1 hadoop    131 Nov 26 10:56 client-ssl.properties
-rw-r--r-- 1 spark-user1 hadoop   1124 Nov 26 09:01 client.truststore.jks
-rw-r--r-- 1 spark-user1 hadoop    232 Nov 26 09:01 kafka_client_jaas.conf
-rw-r--r-- 1 spark-user1 hadoop 417361 Nov 26 10:30 spark-sql-kafka-0-10_2.11-2.3.2.3.1.4.0-315.jar
-rw------- 1 spark-user1 hadoop    610 Nov 26 09:01 spark-user1.keytab
-rw------- 1 spark-user1 hadoop    610 Nov 26 09:01 spark-user1_forSpark.keytab
```

### Annexure*

SASL_SSL Kafka consle client demo 

```
cat ./client-ssl.properties
group.id=spark-console
security.protocol=SASL_SSL
ssl.truststore.location=client.truststore.jks
ssl.truststore.password=Hadoop@123
```

SASL_SSL kafka consumber demo

```
./kafka-console-consumer.sh --bootstrap-server ip:6668   --topic sensortopic --consumer.config ./client-ssl.properties --from-beginning
```

SASL_SSL kafka producer demo

```
./kafka-console-producer.sh --broker-list ip:6668   --topic sensortopic --producer.config ./client-ssl.properties
```

