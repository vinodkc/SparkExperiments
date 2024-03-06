package com.vkc.spark.sources.datasourcev2.inmemory_read_write.write;

import com.vkc.spark.sources.datasourcev2.inmemory_read_write.write.SimpleBatchWrite;
import org.apache.spark.sql.connector.write.BatchWrite;
import org.apache.spark.sql.connector.write.Write;
import org.apache.spark.sql.connector.write.WriteBuilder;

public class SimpleWriteBuilder implements WriteBuilder {
    @Override
    public Write build() {
        return new SimpleWrite();
    }
}
