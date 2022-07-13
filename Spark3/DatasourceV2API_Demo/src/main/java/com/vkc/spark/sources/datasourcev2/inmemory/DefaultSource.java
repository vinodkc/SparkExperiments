package com.vkc.spark.sources.datasourcev2.inmemory;

import org.apache.spark.sql.connector.catalog.Table;
import org.apache.spark.sql.connector.catalog.TableProvider;
import org.apache.spark.sql.connector.expressions.Transform;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.util.CaseInsensitiveStringMap;

import java.util.Map;

public class DefaultSource implements TableProvider {
    @Override
    public StructType inferSchema(CaseInsensitiveStringMap options) {
        Transform[] transforms = {};
        return getTable(null,transforms , options.asCaseSensitiveMap()).schema();
    }

    @Override
    public Table getTable(StructType schema, Transform[] partitioning, Map<String, String> properties) {
        return new SimpleBatchTable();
    }
}
