package com.biuqu.boot.service;

import com.biuqu.boot.constants.BootConst;
import com.biuqu.boot.remote.RemoteService;
import com.biuqu.errcode.ErrCodeEnum;
import com.biuqu.exception.CommonException;
import com.biuqu.model.BaseBiz;
import com.biuqu.model.GlobalConfig;
import com.biuqu.model.ResultCode;
import com.biuqu.service.BaseBizService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Rest业务处理服务
 *
 * @param <O> 业务出参模型(outer)
 * @param <T> 业务标准模型
 * @author BiuQu
 * @date 2023/2/4 16:10
 */
@Slf4j
public abstract class BaseRestService<O, T extends BaseBiz<O>> implements RestService<O, T>
{
    @Override
    public ResultCode<List<O>> batchExecute(T model)
    {
        if (null == model)
        {
            log.error("failed to get valid parameter.");
            return ResultCode.error(ErrCodeEnum.VALID_ERROR.getCode());
        }

        //1.添加客户/接口定制化的参数(比如业务阈值等)
        this.appendConfig(model);

        //2.发起远程调用
        return this.invokeBatchResult(model);
    }

    @Override
    public ResultCode<O> execute(T model)
    {
        if (null == model)
        {
            log.error("failed to get valid parameter.");
            return ResultCode.error(ErrCodeEnum.VALID_ERROR.getCode());
        }

        //1.添加客户/接口定制化的参数(比如业务阈值等)
        this.appendConfig(model);

        //2.发起远程调用
        return this.invokeResult(model);
    }

    @Override
    public ResultCode<O> add(T model)
    {
        int result = service.add(model);
        if (result > 0)
        {
            ResultCode.ok(null);
        }
        return ResultCode.error(ErrCodeEnum.ADD_ERROR.getCode());
    }

    @Override
    public ResultCode<O> get(T model)
    {
        T result = service.get(model);
        return ResultCode.ok(result.toModel());
    }

    @Override
    public ResultCode<List<O>> getBatch(T model)
    {
        List<T> batch = service.getBatch(model);
        List<O> results = Lists.newArrayList();
        for (T t : batch)
        {
            O result = t.toModel();
            if (null != result)
            {
                results.add(result);
            }
        }
        return ResultCode.ok(results);
    }

    @Override
    public ResultCode<O> update(T model)
    {
        int result = service.update(model);
        if (result > 0)
        {
            ResultCode.ok(null);
        }
        return ResultCode.error(ErrCodeEnum.UPDATE_ERROR.getCode());
    }

    @Override
    public ResultCode<O> delete(T model)
    {
        int result = service.delete(model);
        if (result > 0)
        {
            ResultCode.ok(null);
        }
        return ResultCode.error(ErrCodeEnum.DELETE_ERROR.getCode());
    }

    /**
     * 业务模型丰富上接口级的全局配置参数
     *
     * @param model 业务模型
     */
    protected void appendConfig(T model)
    {
        //1.查询出urlId
        Map<String, String> urls = MapUtils.invertMap(assemblyConfService.getClientUrl());
        String urlId = urls.get(model.getUrl());
        if (!StringUtils.isEmpty(urlId))
        {
            model.setUrlId(urlId);
        }

        //2.添加客户/接口定制化的参数(比如业务阈值等)
        GlobalConfig config = new GlobalConfig();
        config.setClientId(model.getUserId());
        config.setUrlId(model.getUrlId());
        List<GlobalConfig> configResults = assemblyConfService.getChannelConf(config);
        model.appendConf(configResults);
    }

    /**
     * 获取远程调用的结果(根据业务情况去覆写,可以不需要remote服务调用)
     *
     * @param model 业务模型
     */
    protected ResultCode<O> invokeResult(T model)
    {
        ResultCode<O> resultCode = ResultCode.error(ErrCodeEnum.SERVER_ERROR.getCode());
        try
        {
            resultCode = this.getRemoteService().invoke(model);
        }
        catch (CommonException e)
        {
            resultCode = ResultCode.error(e.getErrCode().getCode());
        }
        catch (Exception e)
        {
            log.error("unknown error in channel.", e);
        }
        finally
        {
            resultCode.setReqId(model.getReqId());
            resultCode.setCost(System.currentTimeMillis() - model.getStart());
        }
        return resultCode;
    }

    /**
     * 获取远程调用的结果(根据业务情况去覆写,可以不需要remote服务调用)
     *
     * @param model 业务模型
     */
    protected ResultCode<List<O>> invokeBatchResult(T model)
    {
        return this.getRemoteService().invokeBatch(model);
    }

    /**
     * 注入远程服务
     *
     * @return 远程服务
     */
    protected RemoteService<O, T> getRemoteService()
    {
        return remoteService;
    }

    /**
     * 注入业务服务
     */
    @Autowired(required = false)
    private BaseBizService<T> service;

    /**
     * 注入远程调用服务
     */
    @Resource(name = BootConst.DEFAULT_REMOTE_SVC)
    private RemoteService<O, T> remoteService;

    /**
     * 配置的聚合服务
     */
    @Autowired
    private AssemblyConfService assemblyConfService;
}
