package com.biuqu.boot.service.impl;

import com.biuqu.boot.service.AssemblyConfService;
import com.biuqu.model.GlobalConfig;
import com.biuqu.model.GlobalDict;
import com.biuqu.service.BaseBizService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 全局聚合的查询配置服务实现
 * <p>
 * 简化dict和config的查询
 *
 * @author BiuQu
 * @date 2023/2/18 13:28
 */
@Service
public class AssemblyConfServiceImpl implements AssemblyConfService
{
    @Override
    public Map<String, String> getClientUrl()
    {
        GlobalDict dictParam = new GlobalDict().toDict();
        List<GlobalDict> batchDict = dictService.getBatch(dictParam);
        return getUrlMap(batchDict);
    }

    @Override
    public Map<String, String> getChannelUrl()
    {
        GlobalDict dictParam = GlobalDict.toChannelUrl();
        List<GlobalDict> batchDict = dictService.getBatch(dictParam);
        return getUrlMap(batchDict);
    }

    @Override
    public Map<String, String> getChannelDict(GlobalConfig confParam)
    {
        GlobalDict dictParam = new GlobalDict();
        //查询渠道配置GlobalConfig时,GlobalConfig的clientId为urlId,GlobalConfig的urlId为channelId
        dictParam.setType(confParam.getUrlId());

        Map<String, String> dictMap = Maps.newHashMap();
        List<GlobalDict> batchDict = dictService.getBatch(dictParam);
        for (GlobalDict dict : batchDict)
        {
            dictMap.put(dict.getKey(), dict.getValue());
        }
        return dictMap;
    }

    @Override
    public List<GlobalConfig> getClientConf(GlobalConfig confParam)
    {
        GlobalDict dictParam = GlobalDict.toClientDict();
        dictParam.setKey(confParam.getUrlId());
        return getConf(confParam, dictParam);
    }

    @Override
    public List<GlobalConfig> getChannelConf(GlobalConfig confParam)
    {
        GlobalDict dictParam = GlobalDict.toChannelDict();
        //查询渠道配置GlobalConfig时,GlobalConfig的clientId为urlId,GlobalConfig的urlId为channelId
        dictParam.setKey(confParam.getUrlId());
        return getConf(confParam, dictParam);
    }

    /**
     * 获取全局参数配置集合
     * <p>
     * 包括接口/渠道的参数配置
     *
     * @param confParam 全局配置参数
     * @param dictParam 字典配置参数
     * @return 全局参数配置集合
     */
    private List<GlobalConfig> getConf(GlobalConfig confParam, GlobalDict dictParam)
    {
        List<GlobalDict> batchDict = dictService.getBatch(dictParam);
        List<GlobalConfig> confParams = Lists.newArrayList();
        for (GlobalDict dict : batchDict)
        {
            GlobalConfig param = new GlobalConfig();
            param.setClientId(confParam.getClientId());
            param.setUrlId(confParam.getUrlId());
            param.setSvcId(dict.getValue());

            confParams.add(param);
        }

        return confService.batchGet(confParams);
    }

    /**
     * 获取接口/渠道的接口映射关系
     *
     * @param batchDict 查询到的结果集合
     * @return 接口映射关系(key为urlId, value为url)
     */
    private Map<String, String> getUrlMap(List<GlobalDict> batchDict)
    {
        Map<String, String> urlMap = Maps.newHashMap();
        for (GlobalDict dict : batchDict)
        {
            urlMap.put(dict.getKey(), dict.getValue());
        }
        return urlMap;
    }

    /**
     * 注入全局字典服务
     */
    @Autowired
    private BaseBizService<GlobalDict> dictService;

    /**
     * 注入全局配置服务
     */
    @Autowired
    private BaseBizService<GlobalConfig> confService;
}
