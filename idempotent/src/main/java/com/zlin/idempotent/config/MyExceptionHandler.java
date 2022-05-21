package com.zlin.idempotent.config;

import com.zlin.idempotent.model.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author zlin
 * @date 20220521
 */
@ControllerAdvice
public class MyExceptionHandler {

    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    public Result runtimeExceptionHandler(RuntimeException exception){
        return Result.errorMsg(exception.getMessage());
    }

}
