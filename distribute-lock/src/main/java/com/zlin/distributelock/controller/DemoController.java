package com.zlin.distributelock.controller;

import com.zlin.distributelock.dao.DistributeLockMapper;
import com.zlin.distributelock.model.DistributeLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zlin
 * @date 20210810
 */
@Slf4j
@RestController
public class DemoController {

    @Autowired
    private DistributeLockMapper distributeLockMapper;

    /**
     * 基于数据库悲观锁的分布式锁
     * 优点：简单方便、易于理解与操作
     * 缺点：并发量大时对数据库压力大
     * 建议：作为锁的数据库与业务数据库分开
     */
    @RequestMapping("singleLock")
    @Transactional(rollbackFor = Exception.class)
    public String singleLock() {
        log.info("进入了方法");

        DistributeLock distributeLock = distributeLockMapper.selectForUpdateByCode("demo");
        if (distributeLock == null) {
            throw new RuntimeException("找不到分布式锁");
        }
        log.info("进入了锁");
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "执行完成！";
    }

}
