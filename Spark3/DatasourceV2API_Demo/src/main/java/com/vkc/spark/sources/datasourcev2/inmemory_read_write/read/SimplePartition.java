package com.vkc.spark.sources.datasourcev2.inmemory_read_write.read;

import org.apache.spark.sql.connector.read.InputPartition;

public class SimplePartition implements InputPartition {
    private int start;
    private int end;

    public SimplePartition(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
