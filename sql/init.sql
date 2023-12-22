create schema shortUrl collate utf8mb4_bin;

-- auto-generated definition
create table url_map
(
    id        int auto_increment comment '主键'
        primary key,
    short_url varchar(200) default '' not null comment '短链接',
    long_url  varchar(500) default '' not null comment '长链接',
    constraint uk_short_url
        unique (short_url)
)
    comment '长链短链对应表';