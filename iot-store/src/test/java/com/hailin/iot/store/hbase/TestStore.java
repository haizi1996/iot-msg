package com.hailin.iot.store.hbase;

import org.apache.hadoop.hbase.TableName;
import org.junit.Test;

import java.io.IOException;


public class TestStore {




    @Test
    public void test() throws IOException {

        TableName[] tableNames = new HbaseUtils("localhost").getTableList();
        System.out.println(tableNames);
    }
}
