-- auto-generated definition
create table tag
(
    id         bigint auto_increment
        primary key,
    tagName    varchar(255)                       null comment '标签名称',
    userId     varchar(256)                       null comment '用户 id',
    parentId   bigint                             null comment '父标签',
    isParent   tinyint                            null comment '0-是父标签, 1-不是父标签',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 null comment '是否删除',
    constraint uniIndex_tagName
        unique (tagName)
)
    comment '标签表';

create index idx_userId
    on tag (userId);


