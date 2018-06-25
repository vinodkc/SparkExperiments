# Spark-Securekafka-streaming

Yarn Client mode
/usr/hdp/current/spark2-client/bin/spark-submit --master yarn  --conf spark.driver.extraJavaOptions="auth.login.config=kafka_client_jaas.conf" --conf spark.executor.extraJavaOptions="-Djava.security.auth.login.config=kafka_client_jaas.conf"  --jars /usr/hdp/current/kafka-broker/libs/kafka-clients-1.0.0.2.6.5.0-292.jar,/tmp/streamtest/spark-streaming-kafka-0-10_2.11-2.3.0.jar  --files /tmp/streamtest/consumer-user.keytab#consumer-user.keytab,/tmp/streamtest/kafka_client_jaas.conf#kafka_client_jaas.conf --class com.hwx.SparkSecureKafkaDemo /tmp/streamtest/StreamTest-1.0.jar  hdp265secure3.openstacklocal:6667 PLAINTEXTSASL testacl 5


Yarn Cluster mode

/usr/hdp/current/spark2-client/bin/spark-submit --master yarn-cluster   --conf spark.driver.extraJavaOptions="auth.login.config=kafka_client_jaas.conf" --conf spark.executor.extraJavaOptions="-Djava.security.auth.login.config=kafka_client_jaas.conf"  --jars /usr/hdp/current/kafka-broker/libs/kafka-clients-1.0.0.2.6.5.0-292.jar,/tmp/streamtest/spark-streaming-kafka-0-10_2.11-2.3.0.jar  --files /tmp/streamtest/consumer-user.keytab#consumer-user.keytab,/tmp/streamtest/kafka_client_jaas.conf#kafka_client_jaas.conf --class com.hwx.SparkSecureKafkaDemo /tmp/streamtest/StreamTest-1.0.jar  hdp265secure3.openstacklocal:6667 PLAINTEXTSASL testacl 5
eg:

cat kafka_client_jaas.conf

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