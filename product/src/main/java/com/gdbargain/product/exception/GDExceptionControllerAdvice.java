package com.gdbargain.product.exception;

import com.gdbargain.common.exception.BizCodeEnum;
import com.gdbargain.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.Valid;
import java.util.HashMap;

/**
 * @author: shh
 * @createTime: 2023/1/1422:33
 */
//这个注解就是统一处理异常的，点进去，里面有@Component注解(说明是一个组件)
//    basePackages是告诉处理哪些controller出现的异常
//@ControllerAdvice(basePackages = "com.gdbargain.product.controller") //集中处理所有异常
//@ResponseBody //将返回值以json的方式返回出去

//RestControllerAdvice包含ControllerAdvice ResponseBody
@RestControllerAdvice(basePackages = "com.gdbargain.product.controller") //集中处理所有异常
@Slf4j //日志记录
public class GDExceptionControllerAdvice {
    //数据校验的异常都放在这里处理
    // @ExceptionHandler告诉SpringMVC这个handleValidException方法能处理什么异常
    //value = Exception.class, 表示所有异常都可以指定

    //处理单个类型的异常
//    @ExceptionHandler(value = Exception.class)  // MethodArgumentNotValidException.class
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e){
        log.error("数据校验出现问题{},异常类型{}", e.getMessage(),e.getClass());
        BindingResult bindingResult = e.getBindingResult();
        HashMap<String, String> map = new HashMap<>();
        bindingResult.getFieldErrors().forEach((i) -> {
            map.put(i.getField(), i.getDefaultMessage());
        });
        return R.error(BizCodeEnum.VALID_EXCEPTION.getCode(), BizCodeEnum.VALID_EXCEPTION.getMsg()).put("data", map);
//        return R.error(400, "数据校验出现问题:").put("data", map);
//        return R.error();
    }

    //处理任意类型的异常
    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable e){
        return R.error(BizCodeEnum.UNKNOWN_EXCEPTION.getCode(), BizCodeEnum.UNKNOWN_EXCEPTION.getMsg());
    }
}
