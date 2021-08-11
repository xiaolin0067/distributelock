package com.zlin.distributelock.controller;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author zlin
 * @date 20210811
 */
@Slf4j
@RestController
public class RedisLockController {

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("redisLock")
    public String redisLock() {
        log.info("进入了方法");
        String key = "redisKey";
        String value = UUID.randomUUID().toString();

        RedisCallback<Boolean> callback = redisConnection -> {
            // 设置NX
            RedisStringCommands.SetOption setOption = RedisStringCommands.SetOption.ifAbsent();
            Expiration expiration = Expiration.seconds(30);
            byte[] redisKey = redisTemplate.getKeySerializer().serialize(key);
            byte[] redisValue = redisTemplate.getValueSerializer().serialize(value);
            // 执行SETNX操作
            return redisConnection.set(redisKey, redisValue, expiration, setOption);

        };
        // 获取分布式锁
        Boolean lock = (Boolean) redisTemplate.execute(callback);
        if (lock) {
            log.info("进入了锁");
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                String luaScript = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then \n" +
                        "  return redis.call(\"del\",KEYS[1]) \n" +
                        "else \n" +
                        "  return 0\n" +
                        "end";
                RedisScript<Boolean> redisScript = RedisScript.of(luaScript, Boolean.class);
                List<String> keys = Arrays.asList(key);

                Boolean result = (Boolean) redisTemplate.execute(redisScript, keys, value);
                log.info("释放锁的结果：{}", result);
            }
        }
        log.info("方法执行完成");
        return "执行完成";
    }
}
