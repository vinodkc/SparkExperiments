package com.vkc.spark.sources.datasourcev2.inmemory_read_write.write;

import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.connector.write.DataWriter;
import org.apache.spark.sql.connector.write.DataWriterFactory;

public class SimpleDataWriterFactory implements DataWriterFactory {
    @Override
    public DataWriter<InternalRow> createWriter(int partitionId, long taskId) {
        return new SimpleDataWriter();
    }
}
