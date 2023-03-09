package com.han.exception;

import com.han.common.ErrorCode;

/**
 * 自定义异常类
 */
public class BusinessException extends RuntimeException {
    //继承RuntimeException后，在代码中抛出异常时，不用在方法上throws或者try catch捕获
    private final int code;
    private final String description; //定义成final的原因是初始化时就直接赋值，后面不用再set修改

    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
