package com.hailin.iot.store.hbase.config;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;


@SpringBootTest
@RunWith(SpringRunner.class)
public class HbaseConfigTest {

    @Autowired
    private HbaseTemplate hbaseTemplate;

    @Test
    public void getByRow() {

        Scan scan = new Scan();
        scan.setLimit(1);
        scan.withStartRow("row3".getBytes() , false);

        List<String> res = hbaseTemplate.find("test" , scan , new RowMapper<String>(){
            @Override
            public String mapRow(Result result, int i) throws Exception {
                return result.toString();
            }
        });
        System.out.println("----->");
        res.forEach(System.out::println);
    }
}