# SparkHWCDemo

1) mvn clean package
2) Copy  Spark-HWC/target/Spark-HWC-test-1.0.jar into gateway node
3) Run Spark-submit command
eg : 
```js
spark-submit  --keytab hr-user1.keytab --principal  hr-user1@COELAB.CLOUDERA.COM --master yarn --deploy-mode cluster --class com.cloudera.SparkHWCDemo  --conf spark.datasource.hive.warehouse.load.staging.dir=/tmp --conf spark.datasource.hive.warehouse.metastoreUri=thrift://c220-node3.coelab.cloudera.com:9083 --conf spark.hadoop.hive.llap.daemon.service.hosts=@llap0 --conf spark.jars=./lib/hive-warehouse-connector-assembly-1.0.0.3.1.5.0-152_dev.jar --conf spark.sql.hive.hiveserver2.jdbc.url.principal=hive/_HOST@COELAB.CLOUDERA.COM   --conf spark.security.credentials.hiveserver2.enabled=true --conf spark.sql.hive.hiveserver2.jdbc.url="jdbc:hive2://c220-node2.coelab.cloudera.com:2181,c220-node3.coelab.cloudera.com:2181,c220-node4.coelab.cloudera.com:2181/;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2-interactive" --conf spark.sql.hive.zookeeper.quorum="c220-node2.coelab.cloudera.com:2181,c220-node3.coelab.cloudera.com:2181,c220-node4.coelab.cloudera.com:2181"  ./lib/Spark-HWC-test-1.0.jar employee employee2
```