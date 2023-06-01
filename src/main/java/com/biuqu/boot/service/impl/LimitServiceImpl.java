package com.biuqu.boot.service.impl;

import com.biuqu.boot.constants.CommonBootConst;
import com.biuqu.boot.service.LimitService;
import com.biuqu.boot.service.RedisService;
import com.biuqu.constants.Const;
import com.biuqu.model.LimitConfig;
import com.biuqu.utils.MathUtil;
import com.biuqu.utils.TimeUtil;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 限流服务实现
 *
 * @author BiuQu
 * @date 2023/1/30 21:46
 */
@Service
public class LimitServiceImpl implements LimitService
{
    @Override
    public boolean qpsLimit(LimitConfig config)
    {
        List<String> keys = Lists.newArrayList(config.toKey());
        Object[] params = new Object[Const.THREE];
        int i = 0;
        //限流大小
        params[i++] = MathUtil.getLong(config.getSvcValue(), qps);
        //限流单位时间(默认为1000,即秒,也可以支持更大时间粒度)
        params[i++] = MathUtil.getLong(config.getUnit(), qpsUnit);
        //当前时间
        params[i] = System.currentTimeMillis();

        Boolean result = redis.execute(CommonBootConst.QPS_REDIS_SCRIPT_SVC, Boolean.class, keys, params);
        return Boolean.TRUE.equals(result);
    }

    @Override
    public boolean maxLimit(LimitConfig config)
    {
        List<String> keys = Lists.newArrayList(config.toKey());
        Object[] params = new Object[Const.TWO];
        int i = 0;
        params[i++] = MathUtil.getLong(config.getSvcValue(), max);
        //计算过期时间为当天的00:00:00对应的毫秒+配置的阈值(默认支持天,也可以支持更大粒度,比如月)
        params[i] = TimeUtil.getTodayUtcMills() + MathUtil.getLong(config.getUnit(), maxUnit);

        Boolean result = redis.execute(CommonBootConst.MAX_REDIS_SCRIPT_SVC, Boolean.class, keys, params);
        return Boolean.TRUE.equals(result);
    }

    /**
     * 注入redis服务
     */
    @Autowired
    private RedisService redis;

    /**
     * qps默认值
     */
    @Value("${bq.limit.qps:100}")
    private long qps;

    /**
     * max限流值
     */
    @Value("${bq.limit.max:10000}")
    private long max;

    /**
     * qps粒度值(1秒)
     */
    @Value("${bq.limit.qpsUnit:1000}")
    private long qpsUnit;

    /**
     * max限流粒度值(1天)
     */
    @Value("${bq.limit.maxUnit:86400000}")
    private long maxUnit;
}
