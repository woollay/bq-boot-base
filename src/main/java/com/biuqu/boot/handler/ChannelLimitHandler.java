package com.biuqu.boot.handler;

import com.biuqu.boot.model.AccessLimit;
import com.biuqu.model.BaseBiz;
import com.biuqu.model.LimitConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 渠道限流处理器
 *
 * @author BiuQu
 * @date 2023/2/1 20:55
 */
@Component
public class ChannelLimitHandler
{
    /**
     * 限流执行方法
     *
     * @param method 切面拦截的方法对象
     * @param args   方法参数
     * @return true表示限流, false表示不限流
     */
    public boolean limit(Method method, Object[] args)
    {
        if (null == args || args.length <= 0)
        {
            return false;
        }

        Object element = args[0];
        if (element instanceof BaseBiz)
        {
            BaseBiz biz = (BaseBiz)element;
            if (StringUtils.isEmpty(biz.getChannelId()) || StringUtils.isEmpty(biz.getUrlId()))
            {
                return false;
            }
            AccessLimit model = new AccessLimit();

            model.setAccessId(biz.getUrlId());
            model.setUrlId(biz.getChannelId());
            model.setConfig(LimitConfig.channelConf());
            return limitHandler.limit(model);
        }

        return false;
    }

    /**
     * 注入接入限流服务(封装了qps限流和最大调用量限流)
     */
    @Autowired
    private LimitHandler limitHandler;
}
