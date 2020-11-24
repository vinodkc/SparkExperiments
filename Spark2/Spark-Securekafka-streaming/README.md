# Spark-Securekafka-streaming

1.1) Kinit as Kafka user 

1.2) create kafka topic 

```./kafka-topics.sh --create --zookeeper ip:2181 --replication-factor 3 --partitions 12 --topic sensortopic```

2) Give Produce & Consume  rights to user `spark-user1` on topic `sensortopic` . (Use Ranger or Kafka ACL)
 
 Kafka ACL Example 
 --------------
 a) Give producer acl
 
 ```
 ./kafka-acls.sh --authorizer-properties zookeeper.connect=ip:2181    --add --allow-principal User:spark-user1    --producer --topic sensortopic
```

 b) Give consumer acl 
 ```
 ./kafka-acls.sh --authorizer-properties zookeeper.connect=ip:2181  --add --allow-principal User:spark-user1 --consumer --topic sensortopic --group sensorGroup
```

3) From another host/terminal, login as `spark-user1` and produce data  
```
./kafka-console-producer.sh --broker-list ip:6667   --topic sensortopic --producer-property security.protocol=SASL_PLAINTEXT
```

4) From another host/terminal, login as `spark-user1` and try 4.1  or 4.2

# SASL_PLAINTEXT DEMO

4.1) Spark - Kafka SASL_PLAINTEXT DEMO

```
Usage: SparkSecureKafkaDemo  <bootstrapservers>  <SecurityProtocol> <topicname> <batchinterval> <consumergroupname>
```

Yarn Client mode

```
/usr/hdp/current/spark2-client/bin/spark-submit --master yarn --deploy-mode client --keytab ./spark-user1_forSpark.keytab --principal spark-user1@VKC.COM --conf spark.driver.extraJavaOptions="-Djava.security.auth.login.config=kafka_client_jaas.conf" --conf spark.executor.extraJavaOptions="-Djava.security.auth.login.config=kafka_client_jaas.conf"  --jars /usr/hdp/current/kafka-broker/libs/kafka-clients-*.jar,./spark-streaming-kafka-0-10_2.11-2.3.2.jar  --files ./spark-user1.keytab,./kafka_client_jaas.conf --class com.hwx.SparkSecureKafkaDemo ./SparkSecureKafkaDemo-1.0.jar  ip:6667 SASL_PLAINTEXT sensortopic 5 sensorGroup
```
you will see the result on console

Yarn Cluster mode
```
/usr/hdp/current/spark2-client/bin/spark-submit --master yarn --deploy-mode cluster --keytab ./spark-user1_forSpark.keytab --principal spark-user1@VKC.COM --conf spark.driver.extraJavaOptions="-Djava.security.auth.login.config=kafka_client_jaas.conf" --conf spark.executor.extraJavaOptions="-Djava.security.auth.login.config=kafka_client_jaas.conf"  --jars /usr/hdp/current/kafka-broker/libs/kafka-clients-*.jar,./spark-streaming-kafka-0-10_2.11-2.3.2.jar  --files ./spark-user1.keytab,./kafka_client_jaas.conf --class com.hwx.SparkSecureKafkaDemo ./SparkSecureKafkaDemo-1.0.jar  ip:6667 SASL_PLAINTEXT sensortopic 5 sensorGroup
```
you will see the result on application log

# SASL_SSL DEMO

4.2) Spark - Kafka SASL_PLAINTEXT DEMO


```Usage: SparkSecureSSLKafkaDemo  <bootstrapservers>  <SecurityProtocol> <topicname> <batchinterval> <consumergroupname> <ssl truststore filename> <ssl truststore password>```

Yarn Client mode

```
/usr/hdp/current/spark2-client/bin/spark-submit --master yarn --deploy-mode client --keytab ./spark-user1_forSpark.keytab --principal spark-user1@VKC.COM --conf spark.driver.extraJavaOptions="-Djava.security.auth.login.config=kafka_client_jaas.conf" --conf spark.executor.extraJavaOptions="-Djava.security.auth.login.config=kafka_client_jaas.conf"  --jars /usr/hdp/current/kafka-broker/libs/kafka-clients-*.jar,./spark-streaming-kafka-0-10_2.11-2.3.2.jar  --files ./spark-user1.keytab,./kafka_client_jaas.conf,./client.truststore.jks --class com.hwx.SparkSecureSSLKafkaDemo ./SparkSecureKafkaDemo-1.0.jar  ip:6668 SASL_SSL sensortopic 5 sensorGroup client.truststore.jks Hadoop@123
```
Yarn Cluster mode

```
/usr/hdp/current/spark2-client/bin/spark-submit --master yarn --deploy-mode cluster --keytab ./spark-user1_forSpark.keytab --principal spark-user1@VKC.COM --conf spark.driver.extraJavaOptions="-Djava.security.auth.login.config=kafka_client_jaas.conf" --conf spark.executor.extraJavaOptions="-Djava.security.auth.login.config=kafka_client_jaas.conf"  --jars /usr/hdp/current/kafka-broker/libs/kafka-clients-*.jar,./spark-streaming-kafka-0-10_2.11-2.3.2.jar  --files ./spark-user1.keytab,./kafka_client_jaas.conf,./client.truststore.jks --class com.hwx.SparkSecureSSLKafkaDemo ./SparkSecureKafkaDemo-1.0.jar  ip:6668 SASL_SSL sensortopic 5 sensorGroup client.truststore.jks Hadoop@123
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
   -rw-r--r-- 1 spark-user1 root    14661 Nov 24 07:18 SparkSecureKafkaDemo-1.0.jar
   -rw-r--r-- 1 spark-user1 hadoop    129 Nov 24 07:04 client-ssl.properties
   -rw-r--r-- 1 spark-user1 hadoop   1124 Nov 24 07:03 client.truststore.jks
   -rw-r--r-- 1 spark-user1 root      232 Nov 24 06:35 kafka_client_jaas.conf
   -rw-r--r-- 1 spark-user1 root   197823 Nov 24 06:35 spark-streaming-kafka-0-10_2.11-2.3.2.jar
   -rw------- 1 spark-user1 root      610 Nov 24 06:35 spark-user1.keytab
   -rw------- 1 spark-user1 root      610 Nov 24 06:35 spark-user1_forSpark.keytab
```
### Annexure*

SASL_SSL Kafka consle client demo 
```
cat home/spark-user1/spark-kafka/client-ssl.properties
group.id=sensorGroup
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


