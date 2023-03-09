package com.han.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 * @param <T>
 * @author han
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;
    private T data;
    private String message;
    private String description;

    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "", "");
    }

    public BaseResponse(int code, String msg) {
        this(code, null, msg, "");
    }
    public BaseResponse(int code, T data, String msg) {
        this(code, data, msg, "");
    }

    public static <T> BaseResponse<T> ok(T data) {
        return new BaseResponse<>(0, data, "success");
    }

    public static <T> BaseResponse<T> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
    }

    public static <T> BaseResponse<T> error(ErrorCode errorCode, String description) {
        return new BaseResponse<>(errorCode.getCode(), null, errorCode.getMessage(), description);
    }

    public static <T> BaseResponse<T> error(ErrorCode errorCode, String message, String description) {
        return new BaseResponse<>(errorCode.getCode(), null, message, description);
    }
}
