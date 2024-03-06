package com.vkc.spark.sources.datasourcev2.inmemory_read_write.read;

import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.catalyst.expressions.GenericInternalRow;
import org.apache.spark.sql.connector.read.PartitionReader;
import org.apache.spark.unsafe.types.UTF8String;

import java.io.IOException;

public class SimplePartitionReader implements PartitionReader<InternalRow> {

    private String[] values1 = {"1","2","3", "4", "5", "6", "7", "8" , "9", "10"};
    private String[] values2 = {"A","B","C", "D", "E", "F", "G", "H" , "I", "J"};
    private int [] values3 = {1 ,2 ,3 ,4 ,5 ,6 ,7, 8, 9, 10};
    private int index;
    private int end;
    private SimplePartition partition;

    public SimplePartitionReader(SimplePartition partition) {
        this.index = partition.getStart();
        this.end = partition.getEnd();
    }

    @Override
    public boolean next() throws IOException {
        return index <= end;
    }

    @Override
    public InternalRow get() {
        String value1 = values1[index];
        String value2 = values2[index];
        int value3 = values3[index];
        Object[] str = {UTF8String.fromString(value1),
                UTF8String.fromString(value2),
                value3
        };
        InternalRow row =  new GenericInternalRow(str);
        ++index;
        return row;
    }

    @Override
    public void close() throws IOException {

    }
}
