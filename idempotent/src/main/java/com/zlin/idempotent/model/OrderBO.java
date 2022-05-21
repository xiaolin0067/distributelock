package com.zlin.idempotent.model;

import lombok.Data;

/**
 * @author zlin
 * @date 20220521
 */
@Data
public class OrderBO {

    private Integer orderStatus;

    private String receiverName;

    private String receiverMobile;

    private Integer orderAmount;
}