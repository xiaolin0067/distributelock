package com.zlin.redissonlock.controller;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author zlpang
 * @date 20220126
 */
@Slf4j
@RestController
public class RedissonBloomController {

    @Autowired
    private RedissonClient redisson;

    @GetMapping("bloomFilter")
    public Object testBloomFilter() {
        log.info("---------start-------------");
        RBloomFilter<String> bloomFilter = redisson.getBloomFilter("phoneList");
        bloomFilter.tryInit(100000000, 0.01);
        for (int i = 0; i < 1000000; i++) {
            String evtId = UUID.randomUUID().toString();
            if (!bloomFilter.contains(evtId)) {
                bloomFilter.add(evtId);
            }else {
                log.info("UUID生成可能重复：{}", evtId);
            }
        }
        for (int i = 0; i < 10000; i++) {
            String evtId = UUID.randomUUID().toString();
            if (bloomFilter.contains(evtId)) {
                log.info("bloomFilter contains: {}", evtId);
            }
        }
        return "over";
    }

}
