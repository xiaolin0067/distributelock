package com.zlim.distributedemo;

import com.zlin.distributedemo.DistributeDemoApp;
import com.zlin.distributedemo.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zlin
 * @date 20210808
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DistributeDemoApp.class)
public class DistributeDemoAppTests {

    @Autowired
    private OrderService orderService;

    @Test
    public void concurrentOrder() throws InterruptedException {
        //闭锁（Latch）：一种同步方法，可以延迟线程的进度直到线程到达某个终点状态。
        CountDownLatch cdl = new CountDownLatch(5);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(5);

        ExecutorService es = Executors.newFixedThreadPool(5);
        for (int i =0;i<5;i++){
            es.execute(()->{
                try {
                    // 线程在此处等待，直到达到指定线程数都达到此await()之后才会统一释放，线程同时执行，保证并发效果
                    cyclicBarrier.await();
                    Integer orderId = orderService.createOrder();
                    System.out.println("订单id："+orderId);
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    cdl.countDown();
                }
            });
        }
        // 主线程在此处等待上面的线程都执行完成，都执行了countDown()之后才会继续往下执行。
        // 否则主线程执行先执行完成会关闭数据库连接，导致上面的线程执行失败
        cdl.await();
        es.shutdown();
    }

}
