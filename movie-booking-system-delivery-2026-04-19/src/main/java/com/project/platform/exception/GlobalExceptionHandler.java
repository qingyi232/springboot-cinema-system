package com.project.platform.exception;

import com.project.platform.vo.ResponseVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常拦截
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = CustomException.class)
    @ResponseBody
    public ResponseEntity CustomExceptionHandler(CustomException e) {
        return new ResponseEntity<>(ResponseVO.fail(e.getHttpStatus().value(), e.getMessage()), e.getHttpStatus());
    }
}
