package com.hailin.iot.store.hbase;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledHeapByteBuf;
import lombok.NoArgsConstructor;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.nio.ByteBuffer;


@NoArgsConstructor
public class HbaseUtils {

    /**
     * 构建出rowkey
     *
     * hash(session id) |  session id | 逆序消息id
     */
    public static byte[] buildRowKey(String sessionId , long messageId){

        return ByteBuffer.allocate(4).putInt(sessionId.hashCode())
                .putChar('|').put(sessionId.getBytes()).putChar('|').array();
    }




    Configuration config = HBaseConfiguration.create();
    Connection connection;
    Admin admin;

    //初始化提供  hbase ip 和 zk端口
    public HbaseUtils(String ip) throws IOException {
        this.config.set("hbase.zookeeper.quorum", ip);
        this.connection = ConnectionFactory.createConnection(this.config);
        admin=connection.getAdmin();
    }


    //创建表（没查重）
    public void createTable(String tablename, String[] family) throws IOException {

        TableName tn = TableName.valueOf(tablename);

        if (!admin.tableExists(tn)) {
            HTableDescriptor htd = new HTableDescriptor(tn);
            String[] var5 = family;
            int var6 = family.length;

            for (int var7 = 0; var7 < var6; ++var7) {
                String f = var5[var7];
                htd.addFamily(new HColumnDescriptor(f));
            }
            admin.createTable(htd);
            admin.close();
            //this.connection.close();  (再加关闭的函数)
            System.out.println("表：" + tablename + "创建成功");
        } else {
            System.out.println("表：" + tn + "已存在");
        }
    }

    //删除表
    public void deleteTable(String tablename) throws IOException {


        TableName tn = TableName.valueOf(tablename);
        try {
            if (admin.tableExists(tn)) {
                admin.disableTable(tn);
                admin.deleteTable(tn);
                System.out.println("删除成功");
            } else {

                System.out.println("删除失败 该表不存在");

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    //查询所有表
    public TableName[] getTableList() throws IOException {

        TableName[] tableNames = admin.listTableNames();
        for (TableName tn : tableNames
        ) {
            System.out.println(tn);

        }

        return tableNames;

    }


    //查询数据 by  rowkey
    public Result getByRowKey(String tablename, String rowkey) throws IOException {

        TableName tn = TableName.valueOf(tablename);

        if (admin.tableExists(tn)) {
            System.out.println(tn+"表存在");
            Table table = connection.getTable(tn);
            Result result = table.get(new Get(Bytes.toBytes(rowkey)));
            System.out.println("开始查询");
            for (Cell cell:result.listCells()
            ) {

                System.out.println("rowkey:"+rowkey);
                System.out.println("family:"+new String( CellUtil.cloneFamily( cell ) ));
                System.out.println("value:"+new String( CellUtil.cloneValue( cell ) ));

            }
            return result;
        } else {
            System.out.println("表不存在");
            return null;
        }


    }


}
