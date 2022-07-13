package com.vkc.spark.sources.datasourcev2.inmemory_withpartition;

import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.connector.read.InputPartition;
import org.apache.spark.sql.connector.read.PartitionReader;
import org.apache.spark.sql.connector.read.PartitionReaderFactory;
// Objects in executor
public class SimplePartitionReaderFactory implements PartitionReaderFactory {
    @Override
    public PartitionReader<InternalRow> createReader(InputPartition partition) {

        return new SimplePartitionReader((SimplePartition)partition);
    }
}
