package com.zlin.idempotent.enums;

/**
 * @author zlin
 * @date 20210606
 */
public enum CacheKey {

    /**
     * 分隔符
     */
    SEPARATOR(":"),
    /**
     * 首页轮播图
     */
    INDEX_CAROUSEL("index:carousel"),
    /**
     * 首页商品一级分类
     */
    INDEX_CATEGORIES("index:category"),
    /**
     * 首页商品二级分类
     */
    INDEX_SUB_CATEGORIES("index:categoryvo"),
    /**
     * 购物车
     */
    SHOP_CART("shopcart"),
    /**
     * 用户
     */
    USER("user"),
    /**
     * 用户Token
     */
    USER_TOKEN("user:token"),
    /**
     * 用户会话
     */
    USER_SESSION("user:session"),
    /**
     * 用户全局门票
     */
    USER_TICKET("user:ticket"),
    /**
     * 临时门票
     */
    USER_TMP_TICKET("user:tmp:ticket"),
    /**
     * 用户全局门票cookie的key
     */
    USER_TICKET_COOKIE("user_ticket"),
    /**
     * 订单token
     */
    ORDER_TOKEN("order_token"),
    /**
     * 订单token分布式锁的key
     */
    ORDER_TOKEN_LOCK("order_token_lock")
    ;

    public final String value;

    CacheKey(String value){
        this.value = value;
    }

    public String append(Object param) {
        return this.value + SEPARATOR.value + param;
    }
}
