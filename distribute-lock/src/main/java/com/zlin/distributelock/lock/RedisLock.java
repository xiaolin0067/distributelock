package com.zlin.distributelock.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author zlin
 * @date 20210811
 */
@Slf4j
public class RedisLock implements AutoCloseable {

    private RedisTemplate redisTemplate;
    private String key;
    private String value;
    /**
     * 过期时间-单位秒
     */
    private int expireTime;

    public RedisLock(RedisTemplate redisTemplate, String key, int expireTime) {
        this.redisTemplate = redisTemplate;
        this.key = key;
        this.expireTime = expireTime;
        this.value = UUID.randomUUID().toString();
    }

    public boolean getLock() {
        RedisCallback<Boolean> callback = redisConnection -> {
            // 设置NX
            RedisStringCommands.SetOption setOption = RedisStringCommands.SetOption.ifAbsent();
            Expiration expiration = Expiration.seconds(expireTime);
            byte[] redisKey = redisTemplate.getKeySerializer().serialize(key);
            byte[] redisValue = redisTemplate.getValueSerializer().serialize(value);
            // 执行SETNX操作
            return redisConnection.set(redisKey, redisValue, expiration, setOption);

        };
        // 获取分布式锁
        return (Boolean) redisTemplate.execute(callback);
    }

    public boolean unLock() {
        String luaScript = "if redis.call('get',KEYS[1]) == ARGV[1] then \n" +
                "  return redis.call('del',KEYS[1]) \n" +
                "else \n" +
                "  return 0\n" +
                "end";
        RedisScript<Boolean> redisScript = RedisScript.of(luaScript, Boolean.class);
        List<String> keys = Arrays.asList(key);
        boolean result = (Boolean) redisTemplate.execute(redisScript, keys, value);
        log.info("释放锁的结果：{}", result);
        return  result;
    }

    @Override
    public void close() throws Exception {
        unLock();
    }
}
