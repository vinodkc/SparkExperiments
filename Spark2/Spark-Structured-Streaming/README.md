# Spark-Structured-streaming

1) mvn package

2) start netcat on another terminal

```
nc -kl 3503
```

3) run spark submit
```
/usr/hdp/current/spark2-client/bin/spark-submit --master yarn --deploy-mode cluster  --class com.hwx.StructureStreamDemo ./StructureStreamDemo-1.0.jar
```
4) Input some date on netcat terminal

5) Output will be saved in /tmp/ss_fileout


Note: In StructureStreamDemo.scala, ensure to change the option 'host' to your  netcat FQDN
```
option("host", "c420-node2.squadron-labs.com")
```