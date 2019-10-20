package com.hailin.iot.common.remoting;

import org.apache.commons.lang3.StringUtils;

import java.lang.ref.SoftReference;
import java.util.Objects;
import java.util.Properties;

/**
 * RPC地址解析器
 * @author hailin
 */
public class RpcAddressParser implements  RemotingAddressParser{

    @Override
    public Url parse(String url) {
        if (StringUtils.isBlank(url)){
            throw new IllegalArgumentException("Illegal format address string [" + url
                    + "], should not be blank! ");
        }
        Url parseUrl = this.tryGet(url);
        if (Objects.nonNull(parseUrl)){
            return parseUrl;
        }
        String ip = null , port = null ;

        Properties properties = null;
        int size = url.length() , pos = 0;

        for (int i = 0 ;  i < size; i ++ ){
            if (COLON == url.charAt(i)){
                ip = url.substring(pos , i);
                pos = i;
                if (i == size - 1){
                    throw new IllegalArgumentException();
                }
                break;
            }
            if (i == size - 1){
                throw new IllegalArgumentException();
            }
        }
        for (int i = pos; i < size; ++i) {
            if (QUES == url.charAt(i)) {
                port = url.substring(pos + 1, i);
                pos = i;
                if (i == size - 1) {
                    // should not end with QUES
                    throw new IllegalArgumentException("Illegal format address string [" + url
                            + "], should not end with QUES[?]! ");
                }
                break;
            }
            // end without a QUES
            if (i == size - 1) {
                port = url.substring(pos + 1, i + 1);
                pos = size;
            }
        }
        if (pos < (size - 1)) {
            properties = new Properties();
            while (pos < (size - 1)) {
                String key = null;
                String value = null;
                for (int i = pos; i < size; ++i) {
                    if (EQUAL == url.charAt(i)) {
                        key = url.substring(pos + 1, i);
                        pos = i;
                        if (i == size - 1) {
                            // should not end with EQUAL
                            throw new IllegalArgumentException(
                                    "Illegal format address string [" + url
                                            + "], should not end with EQUAL[=]! ");
                        }
                        break;
                    }
                    if (i == size - 1) {
                        // must have one EQUAL
                        throw new IllegalArgumentException("Illegal format address string [" + url
                                + "], must have one EQUAL[=]! ");
                    }
                }
                for (int i = pos; i < size; ++i) {
                    if (AND == url.charAt(i)) {
                        value = url.substring(pos + 1, i);
                        pos = i;
                        if (i == size - 1) {
                            // should not end with AND
                            throw new IllegalArgumentException("Illegal format address string ["
                                    + url
                                    + "], should not end with AND[&]! ");
                        }
                        break;
                    }
                    // end without more AND
                    if (i == size - 1) {
                        value = url.substring(pos + 1, i + 1);
                        pos = size;
                    }
                }
                properties.put(key, value);
            }
        }
        parseUrl = new Url(url, ip, Integer.parseInt(port), properties);
        this.initUrlArgs(parseUrl);
        Url.parsedUrls.put(url, new SoftReference<Url>(parseUrl));
        return parseUrl;
    }

    @Override
    public String parseUniqueKey(String url) {
        return null;
    }

    @Override
    public String parseProperty(String url, String propkey) {
        return null;
    }

    @Override
    public void initUrlArgs(Url url) {

    }

    private Url tryGet(String url) {
        SoftReference<Url> softRef = Url.parsedUrls.get(url);
        return (null == softRef) ? null : softRef.get();
    }
}
