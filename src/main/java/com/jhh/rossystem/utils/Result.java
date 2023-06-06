package com.jhh.rossystem.utils;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Result<T> {

    private boolean success;

    private Integer code;

    private String msg;

    private Integer count;

    private T data;

    public static <T> Result<T> ok() {
        return ok("");
    }

    public static <T> Result<T> ok(T data) {
        return ok("", data);
    }

    public static <T> Result<T> ok(String msg) {
        return ok(msg, null);
    }

    public static <T> Result<T> ok(String msg, T data) {
        return ok(0, msg, data);
    }


    public static <T> Result<T> ok(int code, String msg) {
        return ok(code, msg, null);
    }

    public static <T> Result<T> ok(int code, String msg, T data) {
        return result(code, msg, data, true);
    }

    public static <T> Result<T> fail() {
        return fail("失败");
    }

    public static <T> Result<T> fail(String msg) {
        return fail(msg, null);
    }

    public static <T> Result<T> fail(String msg, T data) {
        return fail(1, msg, data);
    }

    public static <T> Result<T> fail(int code, String msg) {
        return fail(code, msg, null);
    }

    public static <T> Result<T> fail(int code, String msg, T data) {
        return result(code, msg, data, false);
    }

    public static <T> Result<T> auto(boolean b) {
        return auto(b, "成功", "失败");
    }

    public static <T> Result<T> auto(boolean b, String successMsg, String failMsg) {
        if (b) {
            return ok(successMsg);
        } else {
            return fail(failMsg);
        }
    }

    public static <T> Result<T> result(int code, String msg, T data, boolean success) {
        Result<T> result = new Result<T>();
        result.setCode(code);
        result.setMsg(msg);
        result.setSuccess(success);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> page(Integer count, T data) {
        Result<T> result = new Result<>();
        result.setCode(0);
        result.setMsg("成功");
        result.setSuccess(true);
        result.setData(data);
        result.setCount(count);
        return result;
    }


    public static <T> Result<T> page( T data) {
        Result<T> result = new Result<>();
        result.setCode(0);
        result.setMsg("");
//        result.setSuccess(true);
        result.setData(data);
//        result.setCount(count);
        return result;
    }

}
