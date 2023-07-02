package com.biuqu.boot.configure;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.biuqu.boot.utils.ResponseUtil;
import com.biuqu.errcode.ErrCodeEnum;
import com.biuqu.model.ResultCode;
import com.biuqu.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Alibaba Sentinel熔断降级配置(针对常规的SpringBoot/SpringCloud服务)
 *
 * @author BiuQu
 * @date 2023/6/29 19:20
 */
@Slf4j
@Configuration
public class SentinelWebConfigurer
{
    /**
     * 定义异常时的处理器(使用自定义的错误码)
     *
     * @return 熔断降级异常时的处理器
     */
    @Bean
    public BlockExceptionHandler blockSentinelHandler()
    {
        return (request, response, e) ->
        {
            log.error("limit block happened.", e);
            ResultCode<?> resultCode = ResultCode.error(ErrCodeEnum.LIMIT_ERROR.getCode());
            ResponseUtil.writeErrorBody(response, JsonUtil.toJson(resultCode, snakeCase));
        };
    }

    /**
     * 是否驼峰式json(默认支持)
     */
    @Value("${bq.json.snake-case:true}")
    private boolean snakeCase;
}
