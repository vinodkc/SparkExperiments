package com.vkc.spark.sources.datasourcev2.inmemory;

import org.apache.spark.sql.connector.catalog.SupportsRead;
import org.apache.spark.sql.connector.catalog.Table;
import org.apache.spark.sql.connector.catalog.TableCapability;
import org.apache.spark.sql.connector.read.ScanBuilder;
import org.apache.spark.sql.types.*;
import org.apache.spark.sql.util.CaseInsensitiveStringMap;

import java.util.HashSet;
import java.util.Set;


public class SimpleBatchTable implements Table , SupportsRead {
    @Override
    public String name() {
        return this.getClass().toString();
    }

    @Override
    public StructType schema() {
         return new StructType(
                 new StructField[] {
                         new StructField("value", DataTypes.StringType, false, Metadata.empty())});
    }

    @Override
    public Set<TableCapability> capabilities() {
        Set<TableCapability> set = new HashSet<>();
        set.add(TableCapability.BATCH_READ);
        return set;
    }

    @Override
    public ScanBuilder newScanBuilder(CaseInsensitiveStringMap options) {
        return new SimpleScanBuilder();
    }
}
