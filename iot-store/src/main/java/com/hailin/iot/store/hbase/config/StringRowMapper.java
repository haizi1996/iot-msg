package com.hailin.iot.store.hbase.config;

import org.apache.hadoop.hbase.client.Result;
import org.springframework.data.hadoop.hbase.RowMapper;

public class StringRowMapper implements RowMapper<String> {

    @Override
    public String mapRow(Result result, int i) throws Exception {
        return result.getValue("cf".getBytes() , "a".getBytes() ).toString();
    }
}
