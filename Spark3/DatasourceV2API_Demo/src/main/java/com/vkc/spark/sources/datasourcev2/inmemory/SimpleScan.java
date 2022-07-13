package com.vkc.spark.sources.datasourcev2.inmemory;

import org.apache.spark.sql.connector.read.Batch;
import org.apache.spark.sql.connector.read.InputPartition;
import org.apache.spark.sql.connector.read.PartitionReaderFactory;
import org.apache.spark.sql.connector.read.Scan;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

public class SimpleScan implements Scan, Batch {
    @Override
    public Batch toBatch() {
        return this;
    }

    @Override
    public StructType readSchema() {
        return  new StructType(
                new StructField[] {
                        new StructField("value", DataTypes.StringType, false, Metadata.empty())});
    }

    @Override
    public InputPartition[] planInputPartitions() {
        return new InputPartition[] {new SimplePartition()};
    }

    @Override
    public PartitionReaderFactory createReaderFactory() {
        return new SimplePartitionReaderFactory();
    }
}
