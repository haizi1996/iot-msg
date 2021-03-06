
create database if not exists iot ;

create table `user` (
    `id` bigint primary key ,
    username varchar (30) comment '用户名',
    password varchar (60) comment '密码',
    create_time datetime comment '创建时间',
    update_time datetime comment '修改时间',
    status int comment default 1 comment '状态'
)engine = innodb default character set = utf8;
