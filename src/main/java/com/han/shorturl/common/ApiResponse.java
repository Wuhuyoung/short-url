package com.han.shorturl.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author han
 * @date 2023/12/6 10:27
 */
@Data
public class ApiResponse<T> implements Serializable {

    public static final int SUCCESS_CODE = 200;

    private int code;

    private String msg;

    private T data;

    public ApiResponse() {

    }

    public ApiResponse(T data) {
        this.data = data;
    }

    public ApiResponse(String msg) {
        this.msg = msg;
    }

    public ApiResponse(int code, String msg, T date) {
        this.code = code;
        this.msg = msg;
        this.data = date;
    }

    public static <T> ApiResponse<T> ok() {
        return new ApiResponse<>();
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(data);
    }


}
