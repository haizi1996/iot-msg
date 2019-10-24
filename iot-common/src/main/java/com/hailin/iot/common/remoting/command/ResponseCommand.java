package com.hailin.iot.common.remoting.command;


import com.hailin.iot.common.remoting.ResponseStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseCommand {

    private ResponseStatus responseStatus;
}
