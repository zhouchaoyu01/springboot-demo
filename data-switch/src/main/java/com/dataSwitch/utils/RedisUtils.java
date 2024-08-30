package com.dataSwitch.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redis工具类
 * Created by sunlei on 2020/2/26.
 */
@Component
public class RedisUtils {

    private static Log logger = LogFactory.getLog(RedisUtils.class);

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 写入缓存
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value){
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            logger.error("RedisUtils set occurs error.",e);
            logger.error("key is :["+key+"]");
        }
        return result;
    }
    /**
     * 写入缓存设置时效时间
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value, Long expireTime , TimeUnit timeUnit){
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, timeUnit);
            result = true;
        } catch (Exception e) {
            logger.error("RedisUtils set occurs error.",e);
        }
        return result;
    }

    /**
     * 写入缓存(分布式锁)
     * @param key
     * @param value
     * @return
     */
    public boolean setNx(final String key, Object value, Long expireTime , TimeUnit timeUnit){
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            result =operations.setIfAbsent(key, value, expireTime, timeUnit);
        } catch (Exception e) {
            logger.error("RedisUtils setNx occurs error.",e);
        }
        return result;
    }

    /**
     * 写入缓存(分布式锁)
     * @param key
     * @param value
     * @return
     */
    public boolean setNx(final String key, Object value){
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            result =operations.setIfAbsent(key, value);
        } catch (Exception e) {
            logger.error("RedisUtils setNx occurs error.",e);
        }
        return result;
    }

    /**
     * 批量删除对应的value
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }
    /**
     * 批量删除key
     * @param pattern
     */
    public void removePattern(final String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0){
            redisTemplate.delete(keys);
        }
    }
    /**
     * 删除对应的value
     * @param key
     */
    public void remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }
    /**
     * 判断缓存中是否有对应的value
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }
    /**
     * 读取缓存
     * @param key
     * @return
     */
    public Object get(final String key) {
        Object result = null;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result;
    }
    /**
     * 哈希 添加
     * @param key
     * @param hashKey
     * @param value
     */
    public void hmSet(String key, Object hashKey, Object value){
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        hash.put(key,hashKey,value);
    }
    /**
     * 哈希获取数据
     * @param key
     * @param hashKey
     * @return
     */
    public Object hmGet(String key, Object hashKey){
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.get(key,hashKey);
    }
    /**
     * 哈希批量获取数据
     * @param key
     * @param fields
     * @return
     */
    public List<Object> hmGet(String key, List<Object> fields) {
        HashOperations<String, Object, Object>  hash = redisTemplate.opsForHash();
        List<Object> result = hash.multiGet(key, fields);
        return result;
    }
    /**
     * 列表添加
     * @param k
     * @param v
     */
    public void lPush(String k, Object v){
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.rightPush(k,v);
    }
    /**
     * 列表获取
     * @param k
     * @param l
     * @param l1
     * @return
     */
    public List<Object> lRange(String k, long l, long l1){
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.range(k,l,l1);
    }
    /**
     * 集合添加
     * @param key
     * @param value
     */
    public void add(String key, Object value){
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        set.add(key,value);
    }
    /**
     * 集合获取
     * @param key
     * @return
     */
    public Set<Object> setMembers(String key){
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        return set.members(key);
    }

    /**
     * 有序集合添加
     * @param key
     * @param value
     * @param score
     */
    public void zAdd(String key, Object value, double score){
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        zset.add(key,value,score);
    }

    /**
     * 有序集合获取
     * @param key
     * @param scoure
     * @param scoure1
     * @param offset
     * @param count
     * @return
     */
    public Set<Object> rangeByScore(String key, double scoure, double scoure1, long offset, long count){
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        return zset.rangeByScore(key, scoure, scoure1, offset, count);
    }

    /**
     * 有序集合删除元素
     * @param key
     * @param value
     * @return
     */
    public long zrem(String key, Object value){
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        return zset.remove(key, value);
    }
}
