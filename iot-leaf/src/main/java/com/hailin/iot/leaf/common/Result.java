package com.hailin.iot.leaf.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Result {

    private long id ;

    private Status status;
}
