package com.hailin.iot.route.model;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

/**
 * 用户model
 * @author hailin
 */
@Getter
@Setter
public class User {

    private Long id;

    private String userName ;

    private String password;

    private DateTime createTime;

    private DateTime updateTime;

    private Integer status;
}
