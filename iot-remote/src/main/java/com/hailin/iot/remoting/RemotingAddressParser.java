package com.hailin.iot.remoting;

/**
 * 远程地址解析 接口
 * @author zhanghailin
 */
public interface RemotingAddressParser {

    char COLON = ':';

    char EQUAL = '=';

    char AND   = '&';

    char QUES  = '?';

    Url parse(String url);

    String parseUniqueKey(String url);

    String parseProperty(String url , String propkey);

    void initUrlArgs(Url url);
}
