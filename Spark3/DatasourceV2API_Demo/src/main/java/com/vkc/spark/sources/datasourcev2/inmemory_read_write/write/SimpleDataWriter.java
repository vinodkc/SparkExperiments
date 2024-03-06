package com.vkc.spark.sources.datasourcev2.inmemory_read_write.write;

import org.apache.spark.internal.io.FileCommitProtocol;
import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.connector.write.DataWriter;
import org.apache.spark.sql.connector.write.WriterCommitMessage;
import org.apache.spark.sql.execution.datasources.WriteTaskResult;

import java.io.IOException;

public class SimpleDataWriter implements DataWriter<InternalRow> {
    @Override
    public void write(InternalRow record) throws IOException {
        String col1 =  record.getString(0);
        String col2 =  record.getString(1);
        int col3 =  record.getInt(2);
        System.out.println(String.format("%s | %s | %d", col1, col2, col3));
    }

    @Override
    public WriterCommitMessage commit() throws IOException {
        return new WriteTaskResult (new FileCommitProtocol.TaskCommitMessage(null), null);
    }

    @Override
    public void abort() throws IOException {

    }

    @Override
    public void close() throws IOException {

    }
}
