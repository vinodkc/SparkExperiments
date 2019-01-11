
Build the project
-----------------
mvn package

Copy the jar file "Spark-HiveDemo-1.0.jar" into spark client node

Run below command
```
/usr/hdp/current/spark2-client/bin/spark-submit --master yarn   --class com.vkc.SparkHiveDemo ./Spark-HiveDemo-1.0.jar <localpathofdatafile>
```
Eg:
```
/usr/hdp/current/spark2-client/bin/spark-submit --master yarn   --class com.vkc.SparkHiveDemo ./Spark-HiveDemo-1.0.jar /tmp/inputdata.csv
```
Note: input path localpathofdatafile is optional, if not passed, sample datafile from resource directory (Spark-HiveDemo/src/main/resources/data.csv)will be used

Eg:
```
/usr/hdp/current/spark2-client/bin/spark-submit --master yarn   --class com.vkc.SparkHiveDemo ./Spark-HiveDemo-1.0.jar
```


Follow this schema if you wish to create dala file.

employee table

```
+-----------+---------+-------+
|   col_name|data_type|comment|
+-----------+---------+-------+
|        eid|      int|   null|
|       name|   string|   null|
|     salary|   string|   null|
|destination|   string|   null|
+-----------+---------+-------+
```

Sample data format
```
1201,Gopal,45000,Technical manager
1202,Manisha,45000,Proof reader
1203,Masthanvali,40000,Technical writer
1204,Kiran,40000,Hr Admin
1205,Kranthi,30000,Op Admin
```