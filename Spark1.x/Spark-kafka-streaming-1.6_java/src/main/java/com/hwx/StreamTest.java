package com.hwx;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkEnv;
import org.apache.spark.TaskContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

import java.util.*;


public class StreamTest
{
    public static void main( String[] args )
    {

        SparkConf conf = new SparkConf().setAppName("StreamTest");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaStreamingContext jssc = new JavaStreamingContext(sc,  Durations.seconds(Integer.parseInt(args[2])));

        Map<String, Object> kafkaParams = new HashMap<String, Object>();
        kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, args[0]);
        kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, "GroupStreamTest");
        kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        Set<String> topicsSet = new HashSet<String>(Arrays.asList(args[1]));
        JavaInputDStream<ConsumerRecord<Object, Object>> stream =  KafkaUtils.createDirectStream(
            jssc, LocationStrategies.PreferConsistent(), ConsumerStrategies.Subscribe(topicsSet,kafkaParams));

        JavaDStream<String> lines = stream.map(new Function<ConsumerRecord<Object,Object>, String>() {

            public String call(ConsumerRecord<Object, Object> objectObjectConsumerRecord) throws Exception {
                return objectObjectConsumerRecord.value().toString();
            }
        });

        lines.foreachRDD(new VoidFunction<JavaRDD<String>>() {
            public void call(JavaRDD<String> stringJavaRDD) throws Exception {
                stringJavaRDD.foreachPartition(new VoidFunction<Iterator<String>>() {
                    public void call(Iterator<String> stringIterator) throws Exception {
                        TaskContext tc = TaskContext.get();
                        System.out.println("Executor id : " + SparkEnv.get().executorId() + "  Task ID:  " + tc.taskAttemptId());

                    }
                });

            }
        });

        lines.print();
        // Start the computation
        jssc.start();
        jssc.awaitTermination();



    }
}
