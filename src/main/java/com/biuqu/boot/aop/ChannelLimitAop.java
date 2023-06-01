package com.biuqu.boot.aop;

import com.biuqu.aop.BaseAop;
import com.biuqu.boot.handler.ChannelLimitHandler;
import com.biuqu.errcode.ErrCodeEnum;
import com.biuqu.exception.CommonException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 渠道限流切面
 *
 * @author BiuQu
 * @date 2023/2/1 21:29
 */
@Component
@Aspect
public class ChannelLimitAop extends BaseAop
{
    @Before("execution (* com.biuqu.boot.remote.RemoteService+.*invoke*(..))")
    @Override
    public void before(JoinPoint joinPoint)
    {
        super.before(joinPoint);
    }

    @Override
    protected void doBefore(Method method, Object[] args)
    {
        boolean isLimit = limitHandler.limit(method, args);
        if (isLimit)
        {
            throw new CommonException(ErrCodeEnum.LIMIT_ERROR.getCode());
        }
    }

    /**
     * 注入渠道限流
     */
    @Autowired
    private ChannelLimitHandler limitHandler;
}
