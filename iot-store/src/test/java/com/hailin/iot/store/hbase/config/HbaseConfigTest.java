package com.hailin.iot.store.hbase.config;

import org.apache.hadoop.hbase.client.Result;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;


@SpringBootTest
@RunWith(SpringRunner.class)
public class HbaseConfigTest {

    @Autowired
    private HbaseTemplate hbaseTemplate;

    @Test
    public void getByRow() {
        String res = hbaseTemplate.get("test" , "row1" , new RowMapper<String>(){
            @Override
            public String mapRow(Result result, int i) throws Exception {
                return result.toString();
            }
        });
        System.out.println("----->");
        System.out.println(res);
    }
}