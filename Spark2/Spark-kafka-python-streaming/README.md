Spark-kafka-python-streaming
-----------------------------
This demo is created for pyspark console, same can be used in spark-submit too

1) Create a topic 
```
./kafka-topics.sh --create --zookeeper c220-node3.squadron-labs.com:2181 --replication-factor 2 --partitions 3 --topic sourcetopic
```

2) start pyspark
```
./bin/pyspark  --packages  org.apache.spark:spark-streaming-kafka-0-8_2.11:2.3.1
```
3) Paste below code on pyspark shell
```
from pyspark.streaming.kafka import KafkaUtils
from pyspark.streaming import StreamingContext
ssc = StreamingContext(sc,5)

topic = "sourcetopic"
brokers = "c220-node2.squadron-labs.com:6667"
kafkaStream = KafkaUtils.createDirectStream(ssc, [topic], {"metadata.broker.list": brokers})
lines = kafkaStream.map(lambda x: x[1])
counts = lines.flatMap(lambda line: line.split(" ")).map(lambda word: (word, 1)).reduceByKey(lambda a, b: a+b)
counts.pprint()
ssc.start()
ssc.awaitTermination()
```
4) Produce some messages in topic 'sourcetopic'
```
./kafka-console-producer.sh --broker-list c220-node2.squadron-labs.com:6667 --topic sourcetopic
```
5) processed messages will be shown on pyspark console
