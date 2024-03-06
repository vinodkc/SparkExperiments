package com.vkc.spark.sources.datasourcev2.inmemory_read_write.write;

import org.apache.spark.sql.connector.write.BatchWrite;
import org.apache.spark.sql.connector.write.DataWriterFactory;
import org.apache.spark.sql.connector.write.PhysicalWriteInfo;
import org.apache.spark.sql.connector.write.WriterCommitMessage;

public class SimpleBatchWrite implements BatchWrite {
    @Override
    public DataWriterFactory createBatchWriterFactory(PhysicalWriteInfo info) {
        return new SimpleDataWriterFactory();
    }

    //  after completion of all tasks,  commit from Driver
    @Override
    public void commit(WriterCommitMessage[] messages) {

    }
   // abort job
    @Override
    public void abort(WriterCommitMessage[] messages) {

    }
}
