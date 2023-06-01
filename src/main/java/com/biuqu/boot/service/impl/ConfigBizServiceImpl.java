package com.biuqu.boot.service.impl;

import com.biuqu.boot.constants.BootConst;
import com.biuqu.boot.dao.BizDao;
import com.biuqu.model.GlobalConfig;
import com.biuqu.service.BaseBizService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Iterator;
import java.util.List;

/**
 * 全局配置服务实现
 *
 * @author BiuQu
 * @date 2023/1/29 21:29
 */
@Service(BootConst.GLOBAL_CONF_SVC)
public class ConfigBizServiceImpl extends BaseBizService<GlobalConfig>
{
    @Override
    public GlobalConfig get(GlobalConfig model)
    {
        List<GlobalConfig> batch = this.getBatch(model);
        return getBest(batch);
    }

    @Override
    protected List<GlobalConfig> queryBatchByKey(String key)
    {
        return dao.getBatch(GlobalConfig.toBean(key));
    }

    @Override
    protected List<GlobalConfig> queryBatchByKeys(Iterable<? extends String> keys)
    {
        List<GlobalConfig> configs = Lists.newArrayList();
        for (Iterator<? extends String> iterator = keys.iterator(); iterator.hasNext(); )
        {
            String key = iterator.next();
            configs.add(GlobalConfig.toBean(key));
        }
        return dao.batchGet(configs);
    }

    @Override
    protected List<GlobalConfig> bestChoose(List<GlobalConfig> batch)
    {
        List<GlobalConfig> bestResults = Lists.newArrayList();
        GlobalConfig best = getBest(batch);
        if (!best.isEmpty())
        {
            bestResults.add(best);
        }
        return super.bestChoose(batch);
    }

    /**
     * 从匹配的4种组合数据下获取最佳的配置
     * <p>
     * 4种配列组合及其优选顺序(从高到低)：
     * 1.clientId和urlId都存在
     * 2.clientId存在,urlId不存在
     * 3.clientId不存在,urlId存在
     * 4.clientId和urlId都不存在
     *
     * @param batch 批量结果
     * @return 最佳结果
     */
    private GlobalConfig getBest(List<GlobalConfig> batch)
    {
        if (CollectionUtils.isEmpty(batch))
        {
            //没数据时返回空对象,避免数据库被击穿(此服务因为涉及接口限流，执行频率太高)
            return new GlobalConfig();
        }

        for (GlobalConfig config : batch)
        {
            if (!StringUtils.isEmpty(config.getClientId()) && !StringUtils.isEmpty(config.getUrlId()))
            {
                return config;
            }
        }

        for (GlobalConfig config : batch)
        {
            if (!StringUtils.isEmpty(config.getClientId()) && StringUtils.isEmpty(config.getUrlId()))
            {
                return config;
            }
        }

        for (GlobalConfig config : batch)
        {
            if (StringUtils.isEmpty(config.getClientId()) && !StringUtils.isEmpty(config.getUrlId()))
            {
                return config;
            }
        }
        return batch.get(0);
    }

    /**
     * 全局配置dao
     */
    @Autowired
    private BizDao<GlobalConfig> dao;
}
