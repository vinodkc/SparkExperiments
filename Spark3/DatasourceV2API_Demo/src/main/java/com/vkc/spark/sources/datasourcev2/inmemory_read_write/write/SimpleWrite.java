package com.vkc.spark.sources.datasourcev2.inmemory_read_write.write;

import org.apache.spark.sql.connector.write.BatchWrite;
import org.apache.spark.sql.connector.write.Write;

public class SimpleWrite implements Write {
    @Override
    public BatchWrite toBatch() {
        return new SimpleBatchWrite();
    }
}
