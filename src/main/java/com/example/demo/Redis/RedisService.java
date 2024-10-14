package com.example.demo.Redis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void saveData(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void incKey(String key) {
        redisTemplate.opsForValue().increment(key);
    }

    public Object getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void saveDataWithExpiration(String key, Object value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    public boolean deleteData(String key) {
        return redisTemplate.delete(key);
    }

    public void saveTimestamp(String key, long timestamp) {
        redisTemplate.opsForZSet().add(key, timestamp, timestamp);
    }

    public Set<Object> getTimestamps(String key, long start, long end) {
        return redisTemplate.opsForZSet().rangeByScore(key, start, end);
    }

    public void removeOldTimestamps(String key, long timestamp) {
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, timestamp);
    }

    public void setExpiration(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }


}
