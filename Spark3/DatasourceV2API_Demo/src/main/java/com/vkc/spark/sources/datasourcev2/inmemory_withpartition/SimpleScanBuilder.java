package com.vkc.spark.sources.datasourcev2.inmemory_withpartition;

import org.apache.spark.sql.connector.read.Scan;
import org.apache.spark.sql.connector.read.ScanBuilder;

public class SimpleScanBuilder implements ScanBuilder {
    @Override
    public Scan build() {
        return new SimpleScan();
    }
}
