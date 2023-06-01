package com.biuqu.boot.service;

import com.biuqu.model.GlobalConfig;

import java.util.List;
import java.util.Map;

/**
 * 聚合的全局配置查询服务
 *
 * @author BiuQu
 * @date 2023/2/18 13:12
 */
public interface AssemblyConfService
{
    /**
     * 获取客户端接口列表(urlId和url映射关系)
     *
     * @return 客户端接口列表
     */
    Map<String, String> getClientUrl();

    /**
     * 获取渠道接口列表(channelId和url映射关系)
     *
     * @return 渠道接口列表
     */
    Map<String, String> getChannelUrl();

    /**
     * 获取指定渠道的字典配置集合
     *
     * @param config 指定渠道的字典配置key(主要基于channelId来查)
     * @return 渠道的字典配置集合
     */
    Map<String, String> getChannelDict(GlobalConfig config);

    /**
     * 获取指定接口id的客户端配置集合
     *
     * @param config 指定接口id的配置信息
     * @return 客户端配置集合
     */
    List<GlobalConfig> getClientConf(GlobalConfig config);

    /**
     * 获取指定接口id的渠道配置集合
     *
     * @param config 指定接口id的配置信息
     * @return 渠道配置集合
     */
    List<GlobalConfig> getChannelConf(GlobalConfig config);
}
