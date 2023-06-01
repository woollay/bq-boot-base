package com.biuqu.boot.aop;

import com.biuqu.log.model.BaseLog;
import com.biuqu.log.model.ClientLog;
import com.biuqu.log.model.LogAopParam;
import com.biuqu.model.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 客户端接口日志的切面
 * <br>
 *
 * @date: 2019/9/2 18:17
 * @author: BiuQu
 * @since: JDK 1.8
 */
@Component
@Aspect
@Slf4j
public class ClientLogAop extends BaseLogAop
{
    @Around("@annotation(org.springframework.web.bind.annotation.PostMapping)&&@annotation(com.biuqu.log.annotation.ClientLogAnn)")
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
            log.info("current audit request[{}/{}],no need to save client log.", param.getRepId(), param.getUrlId());
            return null;
        }
        ClientLog clientLog = new ClientLog();
        BeanUtils.copyProperties(param, clientLog);
        return clientLog;
    }

    @Override
    protected void doAroundAfter(Object param, Object result)
    {
        if (!(param instanceof ClientLog))
        {
            log.info("no need to save client log.");
            return;
        }

        ClientLog cLog = (ClientLog)param;
        if (result instanceof ResultCode)
        {
            ResultCode resultCode = (ResultCode)result;
            if (!StringUtils.isEmpty(resultCode.getReqId()))
            {
                cLog.setRepId(resultCode.getReqId());
            }
            cLog.setRespId(resultCode.getRespId());
            cLog.setCode(resultCode.getCode());
            cLog.setMsg(resultCode.getMsg());
            cLog.setChannelId(resultCode.getChannelId());
            cLog.setCost(System.currentTimeMillis() - cLog.getCurTime());
            log.info("[{}/{}]cost:{}/{}ms", cLog.getRepId(), cLog.getUrlId(), resultCode.getCost(), cLog.getCost());
        }

        super.doAroundAfter(param, result);
    }
}
