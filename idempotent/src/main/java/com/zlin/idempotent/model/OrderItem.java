package com.zlin.idempotent.model;

import java.math.BigDecimal;
import java.util.Date;

public class OrderItem {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_item.id
     *
     * @mbg.generated Sat May 21 16:44:06 CST 2022
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_item.order_id
     *
     * @mbg.generated Sat May 21 16:44:06 CST 2022
     */
    private Integer orderId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_item.product_id
     *
     * @mbg.generated Sat May 21 16:44:06 CST 2022
     */
    private Integer productId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_item.purchase_price
     *
     * @mbg.generated Sat May 21 16:44:06 CST 2022
     */
    private BigDecimal purchasePrice;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_item.purchase_num
     *
     * @mbg.generated Sat May 21 16:44:06 CST 2022
     */
    private Integer purchaseNum;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_item.create_time
     *
     * @mbg.generated Sat May 21 16:44:06 CST 2022
     */
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_item.create_user
     *
     * @mbg.generated Sat May 21 16:44:06 CST 2022
     */
    private String createUser;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_item.update_time
     *
     * @mbg.generated Sat May 21 16:44:06 CST 2022
     */
    private Date updateTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_item.update_user
     *
     * @mbg.generated Sat May 21 16:44:06 CST 2022
     */
    private String updateUser;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_item.id
     *
     * @return the value of order_item.id
     *
     * @mbg.generated Sat May 21 16:44:06 CST 2022
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_item.id
     *
     * @param id the value for order_item.id
     *
     * @mbg.generated Sat May 21 16:44:06 CST 2022
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_item.order_id
     *
     * @return the value of order_item.order_id
     *
     * @mbg.generated Sat May 21 16:44:06 CST 2022
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_item.order_id
     *
     * @param orderId the value for order_item.order_id
     *
     * @mbg.generated Sat May 21 16:44:06 CST 2022
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_item.product_id
     *
     * @return the value of order_item.product_id
     *
     * @mbg.generated Sat May 21 16:44:06 CST 2022
     */
    public Integer getProductId() {
        return productId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_item.product_id
     *
     * @param productId the value for order_item.product_id
     *
     * @mbg.generated Sat May 21 16:44:06 CST 2022
     */
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_item.purchase_price
     *
     * @return the value of order_item.purchase_price
     *
     * @mbg.generated Sat May 21 16:44:06 CST 2022
     */
    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_item.purchase_price
     *
     * @param purchasePrice the value for order_item.purchase_price
     *
     * @mbg.generated Sat May 21 16:44:06 CST 2022
     */
    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_item.purchase_num
     *
     * @return the value of order_item.purchase_num
     *
     * @mbg.generated Sat May 21 16:44:06 CST 2022
     */
    public Integer getPurchaseNum() {
        return purchaseNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_item.purchase_num
     *
     * @param purchaseNum the value for order_item.purchase_num
     *
     * @mbg.generated Sat May 21 16:44:06 CST 2022
     */
    public void setPurchaseNum(Integer purchaseNum) {
        this.purchaseNum = purchaseNum;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_item.create_time
     *
     * @return the value of order_item.create_time
     *
     * @mbg.generated Sat May 21 16:44:06 CST 2022
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_item.create_time
     *
     * @param createTime the value for order_item.create_time
     *
     * @mbg.generated Sat May 21 16:44:06 CST 2022
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_item.create_user
     *
     * @return the value of order_item.create_user
     *
     * @mbg.generated Sat May 21 16:44:06 CST 2022
     */
    public String getCreateUser() {
        return createUser;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_item.create_user
     *
     * @param createUser the value for order_item.create_user
     *
     * @mbg.generated Sat May 21 16:44:06 CST 2022
     */
    public void setCreateUser(String createUser) {
        this.createUser = createUser == null ? null : createUser.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_item.update_time
     *
     * @return the value of order_item.update_time
     *
     * @mbg.generated Sat May 21 16:44:06 CST 2022
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_item.update_time
     *
     * @param updateTime the value for order_item.update_time
     *
     * @mbg.generated Sat May 21 16:44:06 CST 2022
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_item.update_user
     *
     * @return the value of order_item.update_user
     *
     * @mbg.generated Sat May 21 16:44:06 CST 2022
     */
    public String getUpdateUser() {
        return updateUser;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_item.update_user
     *
     * @param updateUser the value for order_item.update_user
     *
     * @mbg.generated Sat May 21 16:44:06 CST 2022
     */
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser == null ? null : updateUser.trim();
    }
}