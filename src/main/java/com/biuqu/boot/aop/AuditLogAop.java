package com.biuqu.boot.aop;

import com.biuqu.log.annotation.AuditLogAnn;
import com.biuqu.log.annotation.AuditLogModuleAnn;
import com.biuqu.log.model.BaseLog;
import com.biuqu.log.model.LogAopParam;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 审计日志的切面
 * <br>
 *
 * @date: 2019/9/2 18:17
 * @author: BiuQu
 * @since: JDK 1.8
 */
@Component
@Aspect
@Slf4j
public class AuditLogAop extends BaseLogAop
{
    @Around("@annotation(org.springframework.web.bind.annotation.PostMapping)&&@annotation(com.biuqu.log.annotation.AuditLogAnn)")
    @Override
    public Object around(ProceedingJoinPoint joinPoint)
    {
        return super.around(joinPoint);
    }

    @Override
    protected BaseLog getLog(boolean isSdk, LogAopParam param, Method method)
    {
        if (!isSdk)
        {
            AuditLogAnn targetAnn = method.getAnnotation(AuditLogAnn.class);
            AuditLogModuleAnn moduleAnn = method.getDeclaringClass().getAnnotation(AuditLogModuleAnn.class);
            return targetAnn.mapper().getLog(targetAnn, moduleAnn, param);
        }
        return null;
    }
}
