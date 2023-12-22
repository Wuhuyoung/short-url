package com.han.shorturl.utils;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author han
 * @date 2023/12/6 10:54
 */
@Component
public class RedisClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(RedisClientUtil.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static int EXPIRE_PERIOD_MIS = 60 * 1000 * 60 * 2;

    public static long TOKEN_EXPIRES_SECOND = 60 * 60 * 24 * 7;

    public static long INDEX_FRESH_SECOND = 60 * 30;

    public static long RANK_FRESH_SECOND = 60 * 60 * 24;

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, String value) {
        boolean result = false;
        try {
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 写入缓存设置时效时间
     *
     * @param key
     * @param value
     * @param expireTime 秒为单位
     * @return
     */
    public boolean set(final String key, String value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(key, value);
            stringRedisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存，计数器
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Integer value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Object, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 设置key的过期时间
     *
     * @param key
     * @param time
     * @return
     */
    public boolean expire(String key, long time) {
        boolean result = false;
        try {
            if (time > 0) {
                stringRedisTemplate.expire(key, time, TimeUnit.SECONDS);
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 追加内容
     *
     * @param key
     * @param value
     * @return
     */
    public boolean append(final String key, String value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.append(key, value);
            stringRedisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 批量删除对应的value
     *
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 批量删除key
     *
     * @param pattern
     */
    public void removePattern(final String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }


    /**
     * 删除对应的value
     *
     * @param key
     */
    public void remove(final String key) {
        boolean flag = exists(key);
        if (flag) {
            stringRedisTemplate.delete(key);
        }
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public String get(final String key) {
        String result = null;
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        result = operations.get(key);
        return result;
    }


    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public <T> T getByKey(final String key) {
        Object result = null;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return (T) result;
    }

    /**
     * 哈希 添加
     *
     * @param key
     * @param hashKey
     * @param value
     */
    public void hmSet(String key, Object hashKey, Object value) {
        HashOperations<String, Object, Object> hash = stringRedisTemplate.opsForHash();
        hash.put(key, hashKey, value);
    }

    /**
     * 哈希 添加
     *
     * @param key
     * @param hashKey
     * @param value
     */
    public void hmSet(String key, Object hashKey, Object value, Long expireTime) {
        HashOperations<String, Object, Object> hash = stringRedisTemplate.opsForHash();
        hash.put(key, hashKey, value);
        stringRedisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 哈希获取数据
     *
     * @param key
     * @param hashKey
     * @return
     */
    public Object hmGet(String key, Object hashKey) {
        HashOperations<String, Object, Object> hash = stringRedisTemplate.opsForHash();
        return hash.get(key, hashKey);
    }

    /**
     * 哈希获取数据
     *
     * @param key
     * @return
     */
    public Map<Object, Object> hmGetAll(String key) {
        HashOperations<String, Object, Object> hash = stringRedisTemplate.opsForHash();
        return hash.entries(key);
    }

    /**
     * 列表添加
     *
     * @param k
     * @param v
     */
    public void lPush(String k, Object v) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.leftPush(k, v);
    }

    /**
     * 列表获取
     *
     * @param k
     * @param l
     * @param l1
     * @return
     */
    public List<Object> lRange(String k, long l, long l1) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.range(k, l, l1);
    }

    /**
     * 集合添加
     *
     * @param key
     * @param value
     */
    public void add(String key, Object value) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        set.add(key, value);
    }

    public boolean sismember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 集合获取
     *
     * @param key
     * @return
     */
    public Set<Object> setMembers(String key) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        return set.members(key);
    }

    /**
     * 有序集合添加
     *
     * @param key
     * @param value
     * @param scoure
     */
    public void zAdd(String key, Object value, double scoure) {
        ZSetOperations<String, Object> set = redisTemplate.opsForZSet();
        set.add(key, value, scoure);
    }

    /**
     * 有序集合获取
     *
     * @param key
     * @param scoure
     * @param scoure1
     * @return
     */
    public Set<Object> rangeByScore(String key, double scoure, double scoure1) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        return zset.rangeByScore(key, scoure, scoure1);
    }

    /**
     * 哈希 删除
     *
     * @param key
     * @param hashKey
     */
    public void hmDel(String key, Object hashKey) {
        HashOperations<String, Object, Object> hash = stringRedisTemplate.opsForHash();
        hash.delete(key, hashKey);
    }

    public synchronized boolean lock(String lockKey, long lockTime) {
        boolean flag = (boolean) redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisSerializer serializer = new StringRedisSerializer();
                Boolean success = connection.setNX(serializer.serialize(lockKey), serializer.serialize(String.valueOf(lockTime)));
                connection.close();
                return success;
            }
        });
        if (flag) {
            logger.info("开始设置过期时间 lockKey：{} lockTime:{}", lockKey, lockTime);
            //设置超时时间，释放内存
            redisTemplate.expire(lockKey, lockTime, TimeUnit.MILLISECONDS);
        }
        return flag;
    }


    /**
     * @param lockKey 键
     * @param num     数量
     * @param unit    SECOND 秒， MINUTE 分， HOUR 时， DAY 天
     */
    public boolean lock(String lockKey, long num, long unit) {
        long milliseconds = num * unit;
        return lock(lockKey, milliseconds);
    }

    public synchronized void unlock(String lockKey) {
        try {
            this.remove(lockKey);
            logger.info("解锁成功 lockKey:{}", lockKey);
        } catch (Exception e) {
            logger.error("e:{}", e);
        }
    }

    public synchronized boolean unlock(String lockKey, long lockTime) {
        try {
            String expireTime = this.get(lockKey);
            if (expireTime != null && (System.currentTimeMillis() - Long.parseLong(expireTime)) > lockTime) {
                this.remove(lockKey);
                logger.info("解锁成功 lockKey:{}", lockKey);
                return true;
            } else {
                Thread.sleep(new Random().nextInt(1000));
            }
        } catch (Exception e) {
            logger.error("e:{}", e);
            return false;
        }
        return false;
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public boolean setNXStr(final String key, final String value) {
        Object obj = null;
        try {
            obj = redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    StringRedisSerializer serializer = new StringRedisSerializer();
                    Boolean success = connection.setNX(serializer.serialize(key), serializer.serialize(value));
                    connection.close();
                    return success;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj != null ? (Boolean) obj : false;
    }

    public void deleteByPrex(String prex) {
        String key = prex + "*";
        Set<String> keys = redisTemplate.keys(key);
        redisTemplate.delete(keys);
    }

    public void deleteBySuffix(String suffix) {
        Set<String> keys = redisTemplate.keys("*" + suffix);
        redisTemplate.delete(keys);
    }

    public void deleteByKeys(String... keys) {
        redisTemplate.delete(Arrays.asList(keys));
    }

    public void incr(String key) {
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.increment(key, 1);
    }

    public void incr(String key, long ttl) {
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.increment(key, 1);
        stringRedisTemplate.expire(key, ttl, TimeUnit.SECONDS);
    }

    /**
     * 返回 增加后的数
     *
     * @param key
     * @param incrNum 增加num
     * @return
     */
    public Long incrReturn(String key, Integer incrNum, Long ttl) {
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        Long num = operations.increment(key, incrNum);
        stringRedisTemplate.expire(key, ttl, TimeUnit.SECONDS);
        return num;
    }

    /**
     * 返回 增加后的数不设置过期时间
     *
     * @param key
     * @param incrNum 增加num
     * @return
     */
    public Long incrReturn(String key, Integer incrNum) {
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        Long num = operations.increment(key, incrNum);
        return num;
    }

    /**
     * 获取key超时时间
     */
    public long ttl(String key) {
        try {
            return (Long) redisTemplate.execute((RedisCallback) connection -> {
                StringRedisSerializer serializer = new StringRedisSerializer();
                return connection.ttl(serializer.serialize(key));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 在一个原子时间内，执行以下两个动作：
     * <p>
     * 将列表 source 中的最后一个元素(尾元素)弹出，并返回给客户端。
     * 将 source 弹出的元素插入到列表 destination ，作为 destination 列表的的头元素。
     * 如果 source 不存在，值 nil 被返回，并且不执行其他动作。
     * 如果 source 和 destination 相同，则列表中的表尾元素被移动到表头，并返回该元素，可以把这种特殊情况视作列表的旋转(rotation)操作。
     *
     * @param source
     * @param target
     * @return 被弹出的元素
     */
    public Object rplp(String source, String target) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.rightPopAndLeftPush(source, target);
    }

    /**
     * 查询队列长度
     *
     * @param key
     * @return
     */
    public long llen(String key) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.size(key);
    }

    public void srem(String key, String val) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        set.remove(key, val);
    }

    public List<String> getByPrex(String prex) {
        String key = prex + "*";
        Set<String> keys = redisTemplate.keys(key);
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        List<String> list = operations.multiGet(keys);
        return list;
    }

    public List<Map.Entry> getMapByPrex(String prex) {
        String key = prex + "*";
        Set<String> keys = redisTemplate.keys(key);
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        List<Map.Entry> entries = Lists.newArrayList();
        keys.forEach(k -> {
            Map.Entry entry = new Map.Entry() {
                @Override
                public Object getKey() {
                    return k.replace(prex, "");
                }

                @Override
                public Object getValue() {
                    return operations.get(k);
                }

                @Override
                public Object setValue(Object value) {
                    return null;
                }
            };

            entries.add(entry);
        });
        return entries;
    }

    public Set<String> keys(String prex) {
        return redisTemplate.keys(prex);
    }
}
