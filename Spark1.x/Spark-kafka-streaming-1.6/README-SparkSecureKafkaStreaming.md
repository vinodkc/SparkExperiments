Secure Spark 1.6.3  Kafka streaming demo

Version Info:
```
    spark.version: 1.6.3

    kafka.version : 1.0.0.2.6.5.0-292

    spark-kafka-0-10-connector_2.10:1.0.1
```

1) List of files
```
-rw-r--r-- 1 spark hadoop  14M Oct 12 13:46 Spark-kafka-streaming-1.6-1.0.jar
-rw-r--r-- 1 spark hadoop  144 Oct 12 13:55 consumer-user.keytab
-rw-r--r-- 1 spark hadoop  231 Oct 12 15:53 kafka-clent-keytab-jaas.conf
-rw------- 1 spark hadoop  144 Oct 12 13:55 producer-user.keytab
-rw-r--r-- 1 spark hadoop 198K Oct 12 14:35 spark-kafka-0-10-connector_2.10-1.0.1.jar
```

2) [spark@c320-node3]$ cat kafka-clent-keytab-jaas.conf
   ```
   KafkaClient {
    com.sun.security.auth.module.Krb5LoginModule required
    doNotPrompt=true
    useTicketCache=false
    principal="consumer-user@HWX.COM"
    useKeyTab=true
    serviceName="kafka"
    keyTab="./consumer-user.keytab"
    client=true;
   };
```
3) Spark Streaming submit :
```
 /usr/hdp/current/spark-client/bin/spark-submit --master yarn --class com.hwx.SparkSecureKafkaStreaming --files ./consumer-user.keytab,./kafka-clent-keytab-jaas.conf --jars  ./spark-kafka-0-10-connector_2.10-1.0.1.jar,/usr/hdp/current/kafka-broker/libs/kafka-clients-1.0.0.2.6.5.0-292.jar --conf "spark.driver.extraJavaOptions=-Djava.security.auth.login.config=./kafka-clent-keytab-jaas.conf" --conf "spark.executor.extraJavaOptions=-Djava.security.auth.login.config=./kafka-clent-keytab-jaas.conf"  ./Spark-kafka-streaming-1.6-1.0.jar  c320-node2.squadron-labs.com:6667 secureTopic 10  SASL_PLAINTEXT
```
 4) Produce some data to secureTopic
 /usr/hdp/current/kafka-broker/bin/kafka-console-consumer.sh  --bootstrap-server  c320-node2.squadron-labs.com:2181 --topic secureTopic --security-protocol SASL_PLAINTEXT

Result: Will print the messages on console