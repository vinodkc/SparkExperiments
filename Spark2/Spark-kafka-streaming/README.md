# Spark-kafka-streaming

1)Create a topic
 kafka-topics.sh --create --zookeeper mycluterip:2181 --replication-factor 2 --partitions 3 --topic test1
2) mvn clean package
3) Run following command from binary folder (please provide kafka server url:port, topicname, batch interval in seconds)

/usr/hdp/current/spark2-client/bin/spark-submit --master yarn --driver-memory 512m --executor-memory 512m --num-executors 3 --jars ./spark-streaming-kafka-0-10_2.11-2.1.0.jar,/usr/hdp/current/kafka-broker/libs/kafka-clients-0.10.1.2.6.2.0-205.jar --class com.hwx.StreamTest ./StreamTest-1.0.jar <kafka bootstrap server>:<port> <yourtopicname> <batchinterval in seconds>

Eg : /usr/hdp/current/spark2-client/bin/spark-submit --master yarn --driver-memory 512m --executor-memory 512m --num-executors 3 --jars ./spark-streaming-kafka-0-10_2.11-2.1.0.jar,/usr/hdp/current/kafka-broker/libs/kafka-clients-0.10.1.2.6.2.0-205.jar --class com.hwx.StreamTest ./StreamTest-1.0.jar mycluterip:6667 testtopic1 60

4) Produce some data
./kafka-console-producer.sh --broker-list mycluterip:6667 --topic test1 

5) View printed messages from spark 
