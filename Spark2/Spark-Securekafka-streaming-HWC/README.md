# Spark-Securekafka-streaming Example

1) In hive create a table : messages

CREATE TABLE messages (partitionnumber string, offset Int, data String) STORED AS ORC TBLPROPERTIES ('transactional'='true');

2) Submit application Yarn Client mode

/usr/hdp/current/spark2-client/bin/spark-submit --master yarn --conf spark.security.credentials.hiveserver2.enabled=false  --conf spark.driver.extraJavaOptions="-Djava.security.auth.login.config=kafka_client_jaas.conf" --conf spark.executor.extraJavaOptions="-Djava.security.auth.login.config=kafka_client_jaas.conf"  --jars /usr/hdp/current/hive_warehouse_connector/hive-warehouse-connector-assembly-1.0.0.3.1.0.0-78.jar,/usr/hdp/current/kafka-broker/libs/kafka-clients-2.0.0.3.1.0.0-78.jar,./SparkSecureKafkaHWC-Demo-1.0.jar,./spark-streaming-kafka-0-10_2.11-2.3.2.jar  --files ./kafka_client_jaas.conf,./spark.headless.keytab  --class com.hwx.SparkSecureKafkaHWCDemo ./SparkSecureKafkaHWC-Demo-1.0.jar  c220-node2.squadron-labs.com:6667 SASL_PLAINTEXT sourcetopic 5 SparkSecureKafkaHWCDemo-Client

OR

Submit application Yarn cluster mode

/usr/hdp/current/spark2-client/bin/spark-submit --master yarn --deploy-mode cluster --conf spark.security.credentials.hiveserver2.enabled=false  --conf spark.driver.extraJavaOptions="-Djava.security.auth.login.config=kafka_client_jaas.conf" --conf spark.executor.extraJavaOptions="-Djava.security.auth.login.config=kafka_client_jaas.conf"  --jars /usr/hdp/current/hive_warehouse_connector/hive-warehouse-connector-assembly-1.0.0.3.1.0.0-78.jar,/usr/hdp/current/kafka-broker/libs/kafka-clients-2.0.0.3.1.0.0-78.jar,./SparkSecureKafkaHWC-Demo-1.0.jar,./spark-streaming-kafka-0-10_2.11-2.3.2.jar  --files ./kafka_client_jaas.conf,./spark.headless.keytab  --class com.hwx.SparkSecureKafkaHWCDemo ./SparkSecureKafkaHWC-Demo-1.0.jar  c220-node2.squadron-labs.com:6667 SASL_PLAINTEXT sourcetopic 5 SparkSecureKafkaHWCDemo-Client


cat kafka_client_jaas.conf
```
KafkaClient {
  com.sun.security.auth.module.Krb5LoginModule required
  doNotPrompt=false
  useTicketCache=false
  principal="consumer-user@VKC.COM"
  useKeyTab=true
  serviceName="kafka"
  keyTab="consumer-user.keytab"
  client=true;
};
```


3) Produce messages into sourcetopic

/usr/hdp/current/kafka-broker/bin/kafka-console-producer.sh --broker-list c220-node2.squadron-labs.com:6667    --topic sourcetopic --producer-property security.protocol=SASL_PLAINTEXT

4) In hive run below query to view the messages from kafka
select * from messages;

5) Run below command to view the total number of files and folders created for table 'messages'

hdfs dfs -ls -R  /warehouse/tablespace/managed/hive/messages

Note: You may see many small files

6) To Combine small data files , run below command

 hdfs dfs -ls -R  /warehouse/tablespace/managed/hive/messages | wc -l
 note down the result
 Inhive, run
 ALTER TABLE messages CONCATENATE;

 hdfs dfs -ls -R  /warehouse/tablespace/managed/hive/messages | wc -l
  note down the result, it will be less than initial result
