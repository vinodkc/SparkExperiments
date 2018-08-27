# Spark-Kafka-Streaming-Producer

1) Create two topics
sourcetopic And  desttopic

2) mvn clean package

3) Copy the application jar and spark-streaming-kafka-0-10_2.11-2.3.0.jar into cluster node ,
 Run following command from binary folder (please provide kafka server url:port, source topicname destination topicname, batch interval in seconds)
```
/usr/hdp/current/spark2-client/bin/spark-submit  --class com.vkc.StreamDataProcessor    --jars ./spark-streaming-kafka-0-10_2.11-2.3.0.jar,/usr/hdp/current/kafka-broker/libs/kafka-clients-1.0.0.2.6.5.0-292.jar  --master yarn ./Spark-Kafka-Streaming-Producer-1.0.jar  c220-node2.squadron-labs.com:6667 sourcetopic desttopic 5
```
4) Produce some data to sourcetopic
```
./kafka-console-producer.sh --broker-list -node2.squadron-labs.com:6667 --topic sourcetopic
```
5) View desttopic messages
```
./kafka-console-consumer.sh --bootstrap-serv c220-node2.squadron-labs.com:6667 --topic desttopic
```
