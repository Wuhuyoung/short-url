# 短链项目介绍

## 背景

1. **什么是短链**

   短链即短链接，是将原始长网址压缩而成的短网址。

2. **短链原理**

   通过短链找到原始链接（长链接），然后重定向到原始链接地址即可。

3. **为什么需要短链**

   可为短信预留更多的内容空间。比如微博、唯品会等网站都在使用短链。

## 技术栈

- SpringBoot
- Redis
- 通用 mapper
- Guava

## 核心职责

- 通过原始链接生成唯一短链地址。
- 短链地址重定向到原始链接地址。

## 架构图

以下是该项目的架构图：

![短链项目架构图](sql/%E7%9F%AD%E9%93%BE%E6%9E%B6%E6%9E%84%E5%9B%BE.png)
## 数据库表设计

```sql
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
```


## 问题

- **如何保证短链唯一性？**

  本项目生成唯一短链的方法为**HASH + 六十二进制表示法**，六十二进制是使用常用的0-9、a-z、A-Z，也就是10个数字 + 26个小写 + 26个大写 = 62位。7位长度62进制可以表示的范围是 62^7 = 3,521,614,606,208 (合计3.5万亿)，完全足够使用。

- **如何判断是否发生 HASH 冲突？**

  可以使用关系型数据库中的唯一索引来判断，本项目为了提高性能采用的是布隆过滤器来判断是否存在 HASH 冲突。本项目使用 Redis 实现布隆过滤器，参考：https://blog.csdn.net/wuhuayangs/article/details/121830094

- **如何处理 HASH 冲突？**

  如果发生 HASH 冲突，就在生成的字符串后面再随机拼一个字符串。如果还是发生冲突，就再在后面拼接一个字符串。

- **如何解决布隆过滤器误判的情况？**

  本项目在布隆过滤器之后，还增加了数据库校验。默认布隆过滤器中提示存在的短链地址，都要去数据库中再次检验一遍。