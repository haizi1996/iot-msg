package com.hailin.iot.route.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * 用户model
 * @author hailin
 */
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class User {

    private Long id;

    private String userName ;

    private String password;

    private Date createTime;

    private Date updateTime;

    private Integer status;
}
