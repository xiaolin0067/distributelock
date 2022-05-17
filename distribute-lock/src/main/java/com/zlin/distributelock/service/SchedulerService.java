package com.zlin.distributelock.service;

import com.zlin.distributelock.lock.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author zlin
 * @date 20210814
 */
@Slf4j
@Service
public class SchedulerService {

    @Autowired
    private RedisTemplate redisTemplate;

//    @Scheduled(cron = "0/5 * * * * ?")
    public void scheduleTask() {
        try (RedisLock redisLock = new RedisLock(redisTemplate, "scheduleTaskKey", 30)) {
            if (!redisLock.getLock()) {
                log.info("未获取到锁.....");
                return;
            }
            log.info("定时任务开始执行。。。。");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
