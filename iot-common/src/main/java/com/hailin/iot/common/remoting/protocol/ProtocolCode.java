package com.hailin.iot.common.remoting.protocol;

import lombok.ToString;

import java.util.Arrays;

/**
 * 协议的代号
 * @author hailin
 */
@ToString
public class ProtocolCode {

    private byte[] protocols;

    public ProtocolCode(byte... protocols) {
        this.protocols = protocols;
    }

    public static ProtocolCode fromBytes(byte... protocols){
        return new ProtocolCode(protocols);
    }

    public byte getFirstByte() {
        return this.protocols[0];
    }

    public int length() {
        return this.protocols.length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProtocolCode that = (ProtocolCode) o;
        return Arrays.equals(protocols, that.protocols);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(protocols);
    }
}
