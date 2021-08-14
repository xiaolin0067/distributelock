package com.zlin.redissonlock.controller;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * https://redisson.org/
 * @author zlin
 * @date 20210814
 */
@Slf4j
@RestController
public class RedissonController {

    @Autowired
    private RedissonClient redisson;

    @RequestMapping("redissonLock")
    public String redissonLock() {
        log.info("进入了方法");
        RLock lock = redisson.getLock("order");
        try {
            lock.lock(30, TimeUnit.SECONDS);
            log.info("进入了锁");
            Thread.sleep(15000);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
            log.info("释放了锁");
        }
        log.info("方法执行完成");
        return "执行完成";
    }

}
