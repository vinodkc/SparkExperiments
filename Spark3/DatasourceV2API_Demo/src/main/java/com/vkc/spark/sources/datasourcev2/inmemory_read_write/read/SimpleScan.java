package com.vkc.spark.sources.datasourcev2.inmemory_read_write.read;

import org.apache.spark.sql.connector.read.Batch;
import org.apache.spark.sql.connector.read.InputPartition;
import org.apache.spark.sql.connector.read.PartitionReaderFactory;
import org.apache.spark.sql.connector.read.Scan;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

// Driver side
public class SimpleScan implements Scan, Batch {
    @Override
    public Batch toBatch() {
        return this;
    }

    @Override
    public StructType readSchema() {
        return new StructType(
                new StructField[] {
                        new StructField("col1", DataTypes.StringType, false, Metadata.empty()),
                        new StructField("col2", DataTypes.StringType, false, Metadata.empty()),
                        new StructField("col3", DataTypes.IntegerType, false, Metadata.empty())
                });
    }

    @Override
    public InputPartition[] planInputPartitions() {
        return new InputPartition[] {
                new SimplePartition(0, 4),
                new SimplePartition(5, 9),
        };
    }

    @Override
    public PartitionReaderFactory createReaderFactory() {
        return new SimplePartitionReaderFactory();
    }
}
