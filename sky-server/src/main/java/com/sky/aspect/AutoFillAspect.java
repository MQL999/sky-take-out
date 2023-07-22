package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {
    }

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint jointPoint) {
        log.info("开始公共字段填充");
        // 获取操作类型
        MethodSignature signature = (MethodSignature) jointPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();
        // 获取参数
        Object[] args = jointPoint.getArgs();
        // 如果参数为空或者参数长度为0，直接返回
        if (args == null && args.length == 0) {
            return;
        }
        // 获取第一个参数
        Object arg = args[0];
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 获取当前用户id
        Long userId = BaseContext.getCurrentId();

        // 根据操作类型进行填充
        if (operationType == OperationType.INSERT) {
            try {
                Method setCreateTime = arg.getClass().getDeclaredMethod("setCreateTime", LocalDateTime.class);
                Method setUpdateTime = arg.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
                Method setCreateUser = arg.getClass().getDeclaredMethod("setCreateUser", Long.class);
                Method setUpdateUser = arg.getClass().getDeclaredMethod("setUpdateUser", Long.class);
                setCreateTime.invoke(arg, now);
                setUpdateTime.invoke(arg, now);
                setCreateUser.invoke(arg, userId);
                setUpdateUser.invoke(arg, userId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Method setUpdateTime = arg.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
                Method setUpdateUser = arg.getClass().getDeclaredMethod("setUpdateUser", Long.class);
                setUpdateTime.invoke(arg, now);
                setUpdateUser.invoke(arg, userId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
