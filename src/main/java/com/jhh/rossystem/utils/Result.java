package com.jhh.rossystem.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Result<T> {

    private Integer status;

    private String msg;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer count;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static <T> Result<T> ok() {
        return ok("成功");
    }

    public static <T> Result<T> ok(T data) {
        return ok("成功", data);
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
        return result(code, msg, data);
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
        return result(code, msg, data);
    }


    public static <T> Result<T> result(int code, String msg, T data) {
        Result<T> result = new Result<T>();
        result.setStatus(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> page(Integer count, T data) {
        Result<T> result = new Result<>();
        result.setStatus(0);
        result.setMsg("成功");
        result.setData(data);
        result.setCount(count);
        return result;
    }

}
