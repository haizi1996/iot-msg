package com.hailin.iot.leaf;

import com.hailin.iot.leaf.common.Result;

public interface IDGen {
    Result get(String key);
    boolean init();
}
