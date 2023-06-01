package com.biuqu.boot.web;

import com.biuqu.errcode.ErrCodeEnum;
import com.biuqu.exception.CommonException;
import com.biuqu.model.ResultCode;
import com.biuqu.utils.JsonUtil;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 服务降级Rest
 *
 * @author BiuQu
 * @date 2023/2/12 10:46
 */
@Slf4j
public abstract class BaseServletFallbackController<T, O>
{
    /**
     * 规则降级
     *
     * @param model 业务模型
     * @param e     业务或其它运行异常
     * @return 降级后的结果对象
     */
    protected ResultCode<O> circuitFallback(T model, Throwable e)
    {
        if (e instanceof CallNotPermittedException)
        {
            String name = ((CallNotPermittedException)e).getCausingCircuitBreakerName();
            CircuitBreaker circuitBreaker = circuitRegistry.circuitBreaker(name);
            CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
            log.error("Circuit[{}]'s metrics:{},with param:{}", name, JsonUtil.toJson(metrics), JsonUtil.toJson(model));
        }
        return fallback(e.getCause());
    }

    /**
     * 服务降级处理业务逻辑
     *
     * @param e 服务异常
     * @return 标准的结果对象
     */
    protected ResultCode<O> fallback(Throwable e)
    {
        ServletRequestAttributes servletAttrs = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest req = servletAttrs.getRequest();
        String url = req.getRequestURI();
        String errCode = ErrCodeEnum.SERVER_ERROR.getCode();
        if (e instanceof CommonException)
        {
            errCode = ((CommonException)e).getErrCode().getCode();
        }
        ResultCode<O> resultCode = ResultCode.error(errCode);
        log.error("[{}]circuit breaker result:{},with exception:{}", url, JsonUtil.toJson(resultCode), e);
        return resultCode;
    }

    /**
     * 注入断路策略
     */
    @Autowired
    private CircuitBreakerRegistry circuitRegistry;
}
