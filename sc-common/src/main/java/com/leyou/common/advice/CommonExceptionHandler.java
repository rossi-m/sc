package com.leyou.common.advice;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.ExceptionResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice       //通用异常处理
public class CommonExceptionHandler {
//exceptionHandler:用于捕获Controller中抛出的指定类型的异常，从而达到不同类型的异常区别处理的目的
@ExceptionHandler(LyException.class)
public ResponseEntity<ExceptionResult> handleException(LyException e){
    ExceptionEnum em = e.getExceptionEnums();
    return ResponseEntity.status(em.getCode()).body(new ExceptionResult(em));

}
}
