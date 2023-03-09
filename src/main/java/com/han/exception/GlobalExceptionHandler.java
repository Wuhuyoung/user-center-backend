package com.han.exception;

import com.han.common.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.han.common.ErrorCode.SYSTEM_ERROR;

/**
 * 全局异常捕获器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse handleBusinessException(BusinessException e) {
        log.error("BusinessException:" + e.getMessage(), e);
        return new BaseResponse(e.getCode(), null, e.getMessage(), e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse handleRuntimeException(RuntimeException e) {
        log.error("RuntimeException", e);
        return new BaseResponse(SYSTEM_ERROR.getCode(), null, e.getMessage(), "");
    }
}
