package com.biuqu.boot.handler.impl;

import com.biuqu.boot.handler.LimitHandler;
import com.biuqu.boot.model.AccessLimit;
import com.biuqu.boot.service.LimitService;
import com.biuqu.model.GlobalConfig;
import com.biuqu.model.LimitConfig;
import com.biuqu.service.BaseBizService;
import com.biuqu.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 真正的接入限流实现(包装了原子限流服务)
 * <p>
 * 包括客户端接入限流和渠道接入限流
 *
 * @author BiuQu
 * @date 2023/2/1 21:02
 */
@Slf4j
@Service
public class LimitHandlerImpl implements LimitHandler
{
    @Override
    public boolean limit(AccessLimit model)
    {
        LimitConfig limitConf = model.getConfig();

        LimitConfig maxConfig = getLimit(limitConf, limitConf.toMaxDbKey(), limitConf.toMaxDbUnitKey());
        if (!maxConfig.isEmpty())
        {
            boolean maxResult = limitService.maxLimit(maxConfig);
            if (maxResult)
            {
                log.error("[{}]reach max limit:{}.", JsonUtil.toJson(model), JsonUtil.toJson(maxConfig));
                return true;
            }
        }

        LimitConfig qpsConfig = getLimit(limitConf, limitConf.toQpsDbKey(), limitConf.toQpsDbUnitKey());
        if (!qpsConfig.isEmpty())
        {
            boolean qpsResult = limitService.qpsLimit(qpsConfig);
            if (qpsResult)
            {
                log.error("[{}]reach qps limit:{}.", JsonUtil.toJson(model), JsonUtil.toJson(maxConfig));
                return true;
            }
        }
        return false;
    }

    /**
     * 获取限流配置
     *
     * @param param        限流参数
     * @param limitKey     限流的key(client.limit.qps)
     * @param limitUnitKey 限流的阈值key(client.limit.qps.unit)
     * @return 限流对象
     */
    private LimitConfig getLimit(LimitConfig param, String limitKey, String limitUnitKey)
    {
        param.setSvcId(limitKey);
        LimitConfig resultConf = param.rebuild();

        //1.查询出限流的值并设置至结果配置对象
        GlobalConfig config = confService.get(param);
        if (!config.isEmpty())
        {
            resultConf.setSvcValue(config.getSvcValue());

            param.setSvcId(limitUnitKey);
            //2.查询限流的时间单位并设置至结果配置对象
            GlobalConfig unitConf = confService.get(param);
            String unit = null;
            if (!unitConf.isEmpty())
            {
                unit = unitConf.getSvcValue();
            }
            resultConf.setUnit(unit);
            return resultConf;
        }
        return resultConf;
    }

    /**
     * 注入全局配置服务
     */
    @Autowired
    private BaseBizService<GlobalConfig> confService;

    /**
     * 注入原子限流服务
     */
    @Autowired
    private LimitService limitService;
}
