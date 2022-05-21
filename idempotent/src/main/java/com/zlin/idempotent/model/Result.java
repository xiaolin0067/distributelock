package com.zlin.idempotent.model;

/**
 * @author zlin
 * @date 20220521
 */
public class Result {

    /**
     * 响应业务状态
     */
    private Integer status;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 响应中的数据
     */
    private Object data;

    public static Result build(Integer status, String msg, Object data) {
        return new Result(status, msg, data);
    }

    public static Result ok(Object data) {
        return new Result(data);
    }

    public static Result ok() {
        return new Result(null);
    }

    public static Result errorMsg(String msg) {
        return new Result(500, msg, null);
    }

    public static Result errorUserTicket(String msg) {
        return new Result(557, msg, null);
    }

    public static Result errorMap(Object data) {
        return new Result(501, "error", data);
    }

    public static Result errorTokenMsg(String msg) {
        return new Result(502, msg, null);
    }

    public static Result errorException(String msg) {
        return new Result(555, msg, null);
    }

    public static Result errorUserQQ(String msg) {
        return new Result(556, msg, null);
    }

    public Result() {

    }

    public Result(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public Result(Object data) {
        this.status = 200;
        this.msg = "OK";
        this.data = data;
    }

    public Boolean isOK() {
        return this.status == 200;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
