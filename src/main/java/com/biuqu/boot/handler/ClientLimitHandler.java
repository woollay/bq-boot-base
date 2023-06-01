package com.biuqu.boot.handler;

import com.biuqu.boot.constants.BootConst;
import com.biuqu.boot.model.AccessLimit;
import com.biuqu.boot.service.AssemblyConfService;
import com.biuqu.boot.utils.ResponseUtil;
import com.biuqu.errcode.ErrCodeEnum;
import com.biuqu.model.JwtToken;
import com.biuqu.model.LimitConfig;
import com.biuqu.model.ResultCode;
import com.biuqu.utils.JsonUtil;
import com.biuqu.utils.JwtUtil;
import com.biuqu.utils.UrlUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 客户端调用限流
 *
 * @author BiuQu
 * @date 2023/1/28 22:37
 */
@Component(BootConst.CLIENT_LIMIT_SVC)
public class ClientLimitHandler implements HandlerInterceptor
{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        JwtToken token = JwtUtil.getJwtToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        if (null == token || StringUtils.isEmpty(token.toClientId()))
        {
            //没有token,不做限流控制
            return true;
        }

        Map<String, String> urls = MapUtils.invertMap(assemblyConfService.getClientUrl());
        String urlId = urls.get(UrlUtil.shortUrl(request.getRequestURI()));
        if (StringUtils.isEmpty(urlId))
        {
            //没有配置限流
            return true;
        }
        AccessLimit model = new AccessLimit();
        model.setUrlId(urlId);
        model.setAccessId(token.toClientId());
        model.setConfig(LimitConfig.clientConf());

        boolean needLimit = limitHandler.limit(model);
        if (needLimit)
        {
            ResultCode<?> resultCode = ResultCode.error(ErrCodeEnum.LIMIT_ERROR.getCode());
            ResponseUtil.writeErrorBody(response, JsonUtil.toJson(resultCode, snakeCase));
            return false;
        }
        return true;
    }

    /**
     * 是否驼峰式json(默认支持)
     */
    @Value("${bq.json.snake-case:true}")
    private boolean snakeCase;

    /**
     * 注入包装的限流服务
     */
    @Autowired
    private LimitHandler limitHandler;

    /**
     * 配置的聚合服务
     */
    @Autowired
    private AssemblyConfService assemblyConfService;
}
