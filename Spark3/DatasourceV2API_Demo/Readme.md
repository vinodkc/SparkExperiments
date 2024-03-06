

 ./bin/spark-shell --jars ~/Spark3/DatasourceV2API_Demo/target/DatasourceV2API_Demo-1.0-SNAPSHOT.jar
 
 val simpleDf = spark.read.format("com.vkc.spark.sources.datasourcev2.inmemory_withpartition").load.show
 
 ```
val simpleDf = spark.read.format("com.vkc.spark.sources.datasourcev2.inmemory_withpartition").load
simpleDf.show
```
 ```
     +----+----+----+
     |col1|col2|col3|
     +----+----+----+
     |   1|   A|   1|
     |   2|   B|   2|
     |   3|   C|   3|
     |   4|   D|   4|
     |   5|   E|   5|
     |   6|   F|   6|
     |   7|   G|   7|
     |   8|   H|   8|
     |   9|   I|   9|
     |  10|   J|  10|
     +----+----+----+
```

Simlulatting save to console :)
simpleDf.write.format("com.vkc.spark.sources.datasourcev2.inmemory_read_write").mode("Append").save()