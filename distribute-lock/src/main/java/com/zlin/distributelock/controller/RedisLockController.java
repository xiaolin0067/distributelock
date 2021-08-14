package com.zlin.distributelock.controller;

import com.zlin.distributelock.lock.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        try (RedisLock redisLock = new RedisLock(redisTemplate, "redisKey", 20)) {
            if (redisLock.getLock()) {
                log.info("进入了锁");
                Thread.sleep(15000);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        log.info("方法执行完成");
        return "执行完成";
    }
}
