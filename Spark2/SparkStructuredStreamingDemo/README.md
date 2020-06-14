
1) Create two topics `impressions` and `clicks`

./kafka-topics.sh --create --zookeeper < zkurl >:2181 --replication-factor 3 --partitions 3 --topic impressions
./kafka-topics.sh --create --zookeeper < zkurl >:2181 --replication-factor 3 --partitions 3 --topic clicks

2) Start Producers

com.vkc.kafka.producer.ImpressionSimulator <brokerlist> impressions <<number of messages>>

com.vkc.kafka.producer.AdClickSimulator <Number of Ads> <bootstrapservers> impressions  <number of impressions messages>  clicks <number of clicks messages>"

eg: 
java com.vkc.kafka.producer.AdClickSimulator 10 localhost:9092 impressions  100  clicks 500
