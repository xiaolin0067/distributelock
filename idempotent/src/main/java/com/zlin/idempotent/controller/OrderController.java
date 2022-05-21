package com.zlin.idempotent.controller;

import com.zlin.idempotent.config.ApiIdempotent;
import com.zlin.idempotent.dao.OrderMapper;
import com.zlin.idempotent.model.Order;
import com.zlin.idempotent.model.OrderBO;
import com.zlin.idempotent.model.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zlin
 * @date 20220521
 */
@RestController
@RequestMapping("order")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderController {

    private final OrderMapper orderMapper;

    @PostMapping
    public Result createOrder(@RequestBody OrderBO orderBo){
        doCreate(orderBo);
        return Result.ok();
    }

    @ApiIdempotent
    @PostMapping("createOrder2")
    public Result createOrder2(@RequestBody OrderBO orderBo){
        doCreate(orderBo);
        return Result.ok();
    }

    private void doCreate(OrderBO orderBo) {
        Order order = new Order();
        BeanUtils.copyProperties(orderBo, order);
        order.setOrderAmount(new BigDecimal(orderBo.getOrderAmount()));
        order.setCreateTime(new Date());
        order.setCreateUser("createUser");
        order.setUpdateTime(new Date());
        order.setUpdateUser("updateUser");
        orderMapper.insertSelective(order);
    }

}
