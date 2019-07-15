
Build the project
-----------------
mvn package

Copy the jar file "Spark-HiveDemo3-1.0.jar" into spark client node

Run below command
```
/usr/hdp/current/spark2-client/bin/spark-submit --master yarn   --class com.vkc.SparkHiveDemo3 ./Spark-HiveDemo3-1.0.jar "select * from <fillyourtablename>"
```
Eg:
```
/usr/hdp/current/spark2-client/bin/spark-submit --master yarn --deploy-mode cluster   --class com.vkc.SparkHiveDemo3 ./Spark-HiveDemo3-1.0.jar "select * from employee"
```
