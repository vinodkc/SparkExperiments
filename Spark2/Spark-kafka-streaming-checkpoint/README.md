# Spark-kafka-streaming

1)Create a topic
 kafka-topics.sh --create --zookeeper mycluter:2181 --replication-factor 2 --partitions 3 --topic test1
2) mvn clean package
3) Run following command from binary folder (please provide kafka server url:port, topicname, batch interval in seconds)

[spark@c320-node2 streamtest]$ /usr/hdp/current/spark2-client/bin/spark-submit --master yarn --class com.hwx.SparkStreamingCheckPointDemo --jars /usr/hdp/current/kafka-broker/libs/kafka-clients-0.10.1.2.6.4.0-91.jar,./spark-streaming-kafka-0-10_2.11-2.2.0.jar  ./Spark-kafka-streaming-checkpoint-1.0.jar mycluter:6667 test1 5 /tmp/chk1
4) Produce some data
./kafka-console-producer.sh --broker-list mycluter:6667 --topic test1

5) View printed messages from spark 
