# Spark-Kafka-Streaming-Producer

1) Create a  topic , test100


2) mvn clean package

3) Copy the application jar and spark-streaming-kafka-0-10_2.11-2.3.0.jar into cluster node ,
 Run following command from binary folder (please provide kafka server url:port, source topicname , batch interval in seconds)
```
/usr/hdp/current/spark2-client/bin/spark-submit --master yarn --class com.vkc.StreamDataProcessor  --jars /usr/hdp/current/kafka-broker/libs/kafka-clients-1.0.0.2.6.5.0-292.jar,./spark-streaming-kafka-0-10_2.11-2.3.0.jar ./Spark-Kafka-Streaming-HDFS-Sink-1.0.jar  c220-node4.squadron-labs.com:6667 test100  5
```
4) Produce some data to source topic
```
./kafka-console-producer.sh --broker-list -node2.squadron-labs.com:6667 --topic test100
```

5) Open spark-shell

```
spark.sql("""CREATE EXTERNAL TABLE word_table (word STRING, count INT, hour STRING) STORED AS PARQUET LOCATION '/tmp/wordfrequecies.parquet'""")
```

Run this to check the new record count for current wall clock  hour

```
spark.sql(s"select word, sum(count)  from word_table  where hour='${org.joda.time.DateTime.now().toString("yyyy-MM-dd-hh")}' group by word").show
```


