package com.vkc.spark.sources.datasourcev2.inmemory;

import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.catalyst.expressions.GenericInternalRow;
import org.apache.spark.sql.connector.read.PartitionReader;
import org.apache.spark.unsafe.types.UTF8String;

import java.io.IOException;

public class SimplePartitionReader implements PartitionReader<InternalRow> {

    String[] values = {"1","2","3", "4", "5", "6"};
    int index = 0;
    @Override
    public boolean next() throws IOException {
        return index < values.length;
    }

    @Override
    public InternalRow get() {
        String value = values[index];
        UTF8String[] str = {UTF8String.fromString(value)};
        InternalRow row =  new GenericInternalRow(str);
        ++index;
        return row;
    }

    @Override
    public void close() throws IOException {

    }
}
