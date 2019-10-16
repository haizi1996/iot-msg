package com.hailin.iot.common.remoting.serialization;

import com.hailin.iot.common.exception.CodecException;

public interface Serializer {

    byte[] serialize(final Object obj) throws CodecException;

    <T> T deserialize(final byte[] data, String classOfT) throws CodecException;
}
