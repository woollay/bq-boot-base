package com.biuqu.boot.remote;

import com.biuqu.boot.service.AssemblyConfService;
import com.biuqu.errcode.ErrCodeEnum;
import com.biuqu.http.CommonRestTemplate;
import com.biuqu.model.BaseBiz;
import com.biuqu.model.GlobalConfig;
import com.biuqu.model.GlobalDict;
import com.biuqu.model.ResultCode;
import com.biuqu.utils.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * 抽象的远程服务调用
 *
 * @author BiuQu
 * @date 2023/2/1 23:10
 */
public abstract class BaseRemoteService<O, T extends BaseBiz<O>> implements RemoteService<O, T>
{
    @Override
    public ResultCode<O> invoke(T model)
    {
        ResultCode<O> resultCode = null;
        try
        {
            //1.获取接口的配置状态(是否可用)
            boolean channelStatus = this.queryChannelStatus(model, GlobalDict.toChannelStatus().getKey());
            if (!channelStatus)
            {
                LOGGER.error("[{}]channel[{}] is not active.", model.getUrlId(), model.getChannelId());
                resultCode = ResultCode.error(ErrCodeEnum.CHANNEL_ERROR.getCode());
                return resultCode;
            }

            //2.获取接口是否是驼峰结构的参数
            boolean snake = this.queryChannelStatus(model, GlobalDict.toChannelSnake().getKey());

            //3.发起远程调用
            String resultJson = this.call(model, snake);

            if (StringUtils.isEmpty(resultJson))
            {
                LOGGER.error("no channel[{}] result found.", model.getUrlId());
                resultCode = ResultCode.error(ErrCodeEnum.SERVER_ERROR.getCode());
                return resultCode;
            }

            //4.转换成标准的结果模型
            resultCode = toModel(resultJson, model.toTypeRef(), snake);
            if (null == resultCode)
            {
                LOGGER.error("channel[{},{}]'s result has happened error.", model.getUrlId(), model.getUrlId());
                resultCode = ResultCode.error(ErrCodeEnum.CHANNEL_ERROR.getCode());
                return resultCode;
            }
        }
        finally
        {
            if (null != resultCode)
            {
                resultCode.setReqId(model.getReqId());
                resultCode.setRespId(model.getRespId());
                resultCode.setChannelId(model.getChannelId());
            }
        }
        return resultCode;
    }

    @Override
    public ResultCode<List<O>> invokeBatch(T model)
    {
        ResultCode<List<O>> resultCode = null;
        try
        {
            //1.获取接口的配置状态(是否可用)
            boolean channelStatus = this.queryChannelStatus(model, GlobalDict.toChannelStatus().getKey());
            if (!channelStatus)
            {
                LOGGER.error("[{}]channels[{}] is not active.", model.getUrlId(), model.getChannelId());
                resultCode = ResultCode.error(ErrCodeEnum.CHANNEL_ERROR.getCode());
                return resultCode;
            }

            //2.获取接口是否是驼峰结构的参数
            boolean snake = this.queryChannelStatus(model, GlobalDict.toChannelSnake().getKey());
            String resultJson = this.call(model, snake);

            if (StringUtils.isEmpty(resultJson))
            {
                LOGGER.error("no channel[{}] results found.", model.getUrlId());
                resultCode = ResultCode.error(ErrCodeEnum.SERVER_ERROR.getCode());
                return resultCode;
            }

            resultCode = toModels(resultJson, model.toTypeRefs(), snake);
            if (null == resultCode)
            {
                LOGGER.error("channel[{},{}]'s results have happened error.", model.getUrlId(), model.getUrlId());
                resultCode = ResultCode.error(ErrCodeEnum.CHANNEL_ERROR.getCode());
                return resultCode;
            }
        }
        finally
        {
            if (null != resultCode)
            {
                resultCode.setRespId(model.getRespId());
                resultCode.setChannelId(model.getChannelId());
            }
        }
        return resultCode;
    }

    /**
     * 真实的远程调用(可覆写)
     *
     * @param model 业务模型
     * @param snake 渠道是否驼峰转下划线方式
     * @return 结果json
     */
    protected String call(T model, boolean snake)
    {
        String channelUrl = this.getChannelUrl(model);
        if (StringUtils.isEmpty(channelUrl))
        {
            LOGGER.error("[{}]no channel[{}] url found.", model.getUrlId(), model.getChannelId());
            return null;
        }

        String paramJson = JsonUtil.toJson(model.toRemote(), snake);
        return this.restTemplate.invoke(channelUrl, null, paramJson);
    }

    /**
     * 查询渠道对应的url
     *
     * @param model 业务模型
     * @return 渠道的url
     */
    protected String getChannelUrl(T model)
    {
        Map<String, String> channelUrls = assemblyConfService.getChannelUrl();
        return channelUrls.get(model.getChannelId());
    }

    /**
     * 把结果转换成标准的带返回状态标记的业务模型(可覆写)
     *
     * @param json    返回结果json
     * @param typeRef 复杂类型的jackson转换适配器
     * @param snake   渠道是否驼峰转下划线方式
     * @return 带返回状态标记的业务模型
     */
    protected ResultCode<O> toModel(String json, TypeReference<ResultCode<O>> typeRef, boolean snake)
    {
        if (StringUtils.isEmpty(json))
        {
            return null;
        }
        return JsonUtil.toComplex(json, typeRef, snake);
    }

    /**
     * 把结果转换成标准的带返回状态标记的业务模型(可覆写)
     *
     * @param json     返回结果json
     * @param typeRefs 复杂类型的jackson转换适配器
     * @param snake    渠道是否驼峰转下划线方式
     * @return 带返回状态标记的业务模型
     */
    protected ResultCode<List<O>> toModels(String json, TypeReference<ResultCode<List<O>>> typeRefs, boolean snake)
    {
        if (StringUtils.isEmpty(json))
        {
            return null;
        }
        return JsonUtil.toComplex(json, typeRefs, snake);
    }

    /**
     * 查询渠道配置的特定状态
     *
     * @param model 业务模型
     * @param key   渠道状态参数
     * @return 渠道配置的布尔值
     */
    private boolean queryChannelStatus(T model, String key)
    {
        GlobalConfig config = new GlobalConfig();
        config.setUrlId(model.getChannelId());
        Map<String, String> dictMap = assemblyConfService.getChannelDict(config);
        String result = Boolean.FALSE.toString();
        if (dictMap.containsKey(key))
        {
            result = dictMap.get(key);
        }
        return Boolean.TRUE.toString().equalsIgnoreCase(result);
    }

    /**
     * 日志句柄
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseRemoteService.class);

    /**
     * 配置的聚合服务
     */
    @Autowired(required = false)
    private AssemblyConfService assemblyConfService;

    /**
     * 注入http请求
     */
    @Autowired(required = false)
    private CommonRestTemplate restTemplate;
}
