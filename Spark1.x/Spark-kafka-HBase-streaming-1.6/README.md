Prerequisites 
-----------------
clone : https://github.com/hortonworks-spark/skc

`cd skc`

`mvn clean install`

then `cd Spark-kafka-HBase-streaming-1.6`
`mvn clean package`


# SparkKafkaHBaseDemo
Files 
```
[spark@c320-node2 kafkahbaseexample_spark1.6]$ ll
total 2568
-rw-r--r-- 1 spark hadoop   16186 Jan  8 09:59 Spark-kafka-HBase-streaming-1.6-1.0.jar
-rw-r--r-- 1 spark hadoop     235 Jan  8 05:23 kafka_user_jaas.conf
-rw------- 1 spark hadoop     148 Jan  8 05:23 kafkahbase-sparkuser.keytab
-rw------- 1 spark hadoop     148 Jan  8 05:23 kafkahbase-user.keytab
-rwxr--r-- 1 spark hadoop     241 Jan  8 05:23 runProducer.sh
-rw-r--r-- 1 spark hadoop     493 Jan  8 05:23 sensordatalite.csv
-rw-r--r-- 1 spark hadoop  202580 Jan  8 08:28 spark-kafka-0-10-connector_2.10-1.0.1.jar
```

1) Create HBase table
```
create 'sensor', {NAME=>'data'}, {NAME=>'alert'}, {NAME=>'stats'}
```
2) In Ranger - HBase policy give access to 'kafkahbase-sparkuser' user


3) Create a topic and give access to user 'kafkahbase-user'
Note: Spark and Kafka user should be same

```
./kafka-topics.sh --create --zookeeper c320-node2.squadron-labs.com:2181 --replication-factor 2 --partitions 3 --topic sensordata
```
4) Create this script file

cat ./runProducer.sh
```
#!/bin/bash

for (( ; ; ))
do

   cat sensordatalite.csv | /usr/hdp/current/kafka-broker/bin/kafka-console-producer.sh --broker-list c320-node3.squadron-labs.com:6667  --topic sensordata --security-protocol SASL_PLAINTEXT
   sleep 20
done
```


5) Sample Data file

cat sensordatalite.csv
```
COHUTTA,3/10/14,1:01,10.27,1.73,881,1.56,85,1.94
COHUTTA,3/10/14,1:02,9.67,1.731,882,0.52,87,1.79
COHUTTA,3/10/14,1:03,10.47,1.732,882,1.7,92,0.66
COHUTTA,3/10/14,1:05,9.56,1.734,883,1.35,99,0.68
COHUTTA,3/10/14,1:06,9.74,1.736,884,1.27,92,0.73
COHUTTA,3/10/14,1:08,10.44,1.737,885,1.34,93,1.54
COHUTTA,3/10/14,1:09,9.83,1.738,885,0.06,76,1.44
COHUTTA,3/10/14,1:11,10.49,1.739,886,1.51,81,1.83
COHUTTA,3/10/14,1:12,9.79,1.739,886,1.74,82,1.91
COHUTTA,3/10/14,1:13,10.02,1.739,886,1.24,86,1.79
```
6) Produce data
```
./runProducer.sh
```
7) In another terminal

7.1) Copy hbase-site.xml to /etc/spark/conf/  or
```
ln -s /etc/hbase/conf/hbase-site.xml /etc/spark2/conf/hbase-site.xml
```

7.2) In /etc/spark/conf/spark-env.sh, add the following:

```
export SPARK_CLASSPATH=/usr/hdp/current/hbase-client/lib/hbase-common.jar:/usr/hdp/current/hbase-client/lib/hbase-client.jar:/usr/hdp/current/hbase-client/lib/hbase-server.jar:/usr/hdp/current/hbase-client/lib/hbase-protocol.jar:/usr/hdp/current/hbase-client/lib/guava-12.0.1.jar:/usr/hdp/current/hbase-client/lib/htrace-core-3.1.0-incubating.jar
```

7.3) Run

```/usr/hdp/current/spark-client/bin/spark-submit --master yarn --deploy-mode cluster --keytab ./kafkahbase-sparkuser.keytab --principal kafkahbase-user@HWX.COM  --class com.hwx.SparkSecureKafkaHBaseStreaming --jars /usr/hdp/current/kafka-broker/libs/kafka-clients-1.0.0.2.6.5.0-292.jar,./spark-kafka-0-10-connector_2.10-1.0.1.jar,/usr/hdp/current/hbase-client/lib/hbase-client.jar,/usr/hdp/current/hbase-client/lib/hbase-common.jar,/usr/hdp/current/hbase-client/lib/hbase-server.jar,/usr/hdp/current/hbase-client/lib/guava-12.0.1.jar,/usr/hdp/current/hbase-client/lib/hbase-protocol.jar,/usr/hdp/current/hbase-client/lib/htrace-core-3.1.0-incubating.jar --conf spark.driver.extraJavaOptions="-Djava.security.auth.login.config=kafka_user_jaas.conf" --conf spark.executor.extraJavaOptions="-Djava.security.auth.login.config=kafka_user_jaas.conf" --files ./kafka_user_jaas.conf,./kafkahbase-user.keytab,/usr/hdp/current/spark-client/conf/hbase-site.xml ./Spark-kafka-HBase-streaming-1.6-1.0.jar  c320-node3.squadron-labs.com:6667 sensordata 10 SASL_PLAINTEXT sensor ```

Note: kafkahbase-sparkuser.keytab is a copy of kafkahbase-user.keytab : A hack to pass  --keytab
