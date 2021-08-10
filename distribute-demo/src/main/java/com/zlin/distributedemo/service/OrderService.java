package com.zlin.distributedemo.service;

import com.zlin.distributedemo.dao.OrderItemMapper;
import com.zlin.distributedemo.dao.OrderMapper;
import com.zlin.distributedemo.dao.ProductMapper;
import com.zlin.distributedemo.model.Order;
import com.zlin.distributedemo.model.OrderItem;
import com.zlin.distributedemo.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Resource;

/**
 * @author zlin
 * @date 20210808
 */
@Service
@Slf4j
public class OrderService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderItemMapper orderItemMapper;
    @Resource
    private ProductMapper productMapper;
    //购买商品id
    private int purchaseProductId = 100100;
    //购买商品数量
    private int purchaseProductNum = 1;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private TransactionDefinition transactionDefinition;

    private Lock lock = new ReentrantLock();

    /**
     * private Object object = new Object();
     * synchronized(this){} = synchronized(obj){} 都是锁住当前对象
     * synchronized(OrderService.class){}则是锁住整个类，类只有一个，而上面锁住对象可能有多个仍可能造成并发
     */
//    @Transactional(rollbackFor = Exception.class)
    public Integer createOrder() throws Exception {

        Product product = null;
        lock.lock();
        try {
            TransactionStatus transaction = platformTransactionManager.getTransaction(transactionDefinition);
            product = productMapper.selectByPrimaryKey(purchaseProductId);
            if (product == null) {
                platformTransactionManager.rollback(transaction);
                throw new RuntimeException("购买商品ID：" + purchaseProductId + "不存在");
            }
            Integer currentCount = product.getCount();
            if (purchaseProductNum > currentCount) {
                platformTransactionManager.rollback(transaction);
                throw new RuntimeException("商品ID：" + purchaseProductId + "仅剩" + currentCount + "件，无法购买");
            }
            Integer leftCount = currentCount - purchaseProductNum;
            product.setCount(leftCount);
            product.setUpdateTime(new Date());
            product.setUpdateUser("xxx");
            productMapper.updateByPrimaryKeySelective(product);
            platformTransactionManager.commit(transaction);
        }finally {
            lock.unlock();
        }

        TransactionStatus transaction = platformTransactionManager.getTransaction(transactionDefinition);
        Order order = new Order();
        order.setOrderAmount(product.getPrice().multiply(new BigDecimal(purchaseProductNum)));
        order.setOrderStatus(1);//待处理
        order.setReceiverName("xxx");
        order.setReceiverMobile("13311112222");
        order.setCreateTime(new Date());
        order.setCreateUser("xxx");
        order.setUpdateTime(new Date());
        order.setUpdateUser("xxx");
        orderMapper.insertSelective(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(order.getId());
        orderItem.setProductId(product.getId());
        orderItem.setPurchasePrice(product.getPrice());
        orderItem.setPurchaseNum(purchaseProductNum);
        orderItem.setCreateUser("xxx");
        orderItem.setCreateTime(new Date());
        orderItem.setUpdateTime(new Date());
        orderItem.setUpdateUser("xxx");
        orderItemMapper.insertSelective(orderItem);
        platformTransactionManager.commit(transaction);
        return order.getId();
    }


}
