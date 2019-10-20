package com.hailin.iot.common.remoting.protocol;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 协议的集中管理
 * @author hailin
 */
public class ProtocolManager {

    private static final ConcurrentHashMap<ProtocolCode , Protocol> protocols = new ConcurrentHashMap<>();

    public static Protocol getProtocol(ProtocolCode protocolCode){
        return protocols.get(protocolCode);
    }

    public static void registerProtocol(Protocol protocol, byte... protocolBytes){
        registerProtocol(protocol , ProtocolCode.fromBytes(protocolBytes));
    }

    private static void registerProtocol(Protocol protocol, ProtocolCode protocolCode) {
        if (Objects.isNull(protocol) || Objects.isNull(protocolCode)){
            throw new RuntimeException("Protocol: " + protocol + " and protocol code:"
                    + protocolCode + " should not be null!");
        }
        Protocol exists = protocols.putIfAbsent(protocolCode , protocol);
        if (exists != null) {
            throw new RuntimeException("Protocol for code: " + protocolCode + " already exists!");
        }
    }

    public static Protocol unRegisterProtocol(byte protocolCode) {
        return ProtocolManager.protocols.remove(ProtocolCode.fromBytes(protocolCode));
    }


}
