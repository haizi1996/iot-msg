package com.hailin.iot.common.compression;

import java.io.IOException;

/**
 * 压缩接口
 * @author hailin
 */
public interface Compression {


    byte[] compress(byte[] buffer) throws IOException;

    /**
     * decompress
     */
    byte[] decompress(byte[] buffer) throws IOException;
}
