package com.biuqu.boot.aop;

import com.biuqu.aop.BaseAop;
import com.biuqu.constants.Const;
import com.biuqu.errcode.ErrCodeEnum;
import com.biuqu.exception.CommonException;
import com.biuqu.log.model.BaseAuditLog;
import com.biuqu.log.model.BaseLog;
import com.biuqu.log.model.ClientLog;
import com.biuqu.log.model.LogAopParam;
import com.biuqu.log.service.LogFacade;
import com.biuqu.log.utils.IpUtil;
import com.biuqu.model.GlobalDict;
import com.biuqu.model.JwtToken;
import com.biuqu.model.ResultCode;
import com.biuqu.service.BaseBizService;
import com.biuqu.utils.IdUtil;
import com.biuqu.utils.JwtUtil;
import com.biuqu.utils.UrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 日志的切面基类
 * <br>
 *
 * @date: 2019/9/2 18:17
 * @author: BiuQu
 * @since: JDK 1.8
 */
@Slf4j
public abstract class BaseLogAop extends BaseAop
{
    @Override
    protected Object doAround(ProceedingJoinPoint joinPoint)
    {
        try
        {
            return super.doAround(joinPoint);
        }
        catch (Exception e)
        {
            log.error("failed to add audit log.", e);
            ResultCode<?> resultCode;
            if (e instanceof CommonException)
            {
                resultCode = ResultCode.error(((CommonException)e).getErrCode().getCode());
            }
            else
            {
                resultCode = ResultCode.error(ErrCodeEnum.SERVER_ERROR.getCode());
            }

            Object obj = doAroundBefore(getMethod(joinPoint), joinPoint.getArgs());
            BaseLog baseLog;
            if (obj instanceof BaseAuditLog)
            {
                baseLog = (BaseAuditLog)obj;
            }
            else
            {
                baseLog = (ClientLog)obj;
            }
            this.doAroundAfter(baseLog, null);
            return resultCode;
        }
    }

    @Override
    protected Object doAroundBefore(Method method, Object[] args)
    {
        LogAopParam param = new LogAopParam();
        param.setCurTime(System.currentTimeMillis());

        HttpServletRequest req = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        param.setClientIp(IpUtil.getReqIp(req));
        param.setUrl(UrlUtil.shortUrl(req.getRequestURI()));

        JwtToken jwtToken = JwtUtil.getJwtToken(req.getHeader(HttpHeaders.AUTHORIZATION));
        if (null != jwtToken && !StringUtils.isEmpty(jwtToken.toClientId()))
        {
            String userId = jwtToken.toClientId();
            if (StringUtils.isEmpty(userId))
            {
                return null;
            }
            param.setUserId(userId);
            String reqId = req.getHeader(Const.HTTP_HEADERS_REQUEST);
            if (StringUtils.isEmpty(reqId))
            {
                reqId = IdUtil.uuid();
            }
            param.setRepId(reqId);

            GlobalDict dict = new GlobalDict();
            dict.setValue(param.getUrl());
            GlobalDict urlDict = dictService.get(dict.toDict());
            String urlId = StringUtils.EMPTY;
            if (null != urlDict && !StringUtils.isEmpty(urlDict.getKey()))
            {
                urlId = urlDict.getKey();
            }
            param.setUrlId(urlId);

            return getLog(jwtToken.isSdk(), param, method);
        }
        return null;
    }

    @Override
    protected void doAroundAfter(Object param, Object result)
    {
        if (!(param instanceof BaseLog))
        {
            log.info("no need to save audit log.");
            return;
        }

        BaseLog baseLog = (BaseLog)param;
        //保存成功的结果
        logFacade.saveLog(baseLog);
    }

    /**
     * 获取日志对象
     *
     * @param isSdk  true表示为接口调用
     * @param param  切面参数(主要是切面的用户信息)
     * @param method 方法对象
     * @return 日志对象
     */
    protected abstract BaseLog getLog(boolean isSdk, LogAopParam param, Method method);

    /**
     * 注入字典服务
     */
    @Autowired
    private BaseBizService<GlobalDict> dictService;

    /**
     * 注入日志门面
     */
    @Autowired
    private LogFacade logFacade;
}
