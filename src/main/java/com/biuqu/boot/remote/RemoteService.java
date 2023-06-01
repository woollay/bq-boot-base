package com.biuqu.boot.remote;

import com.biuqu.model.BaseBiz;
import com.biuqu.model.ResultCode;

import java.util.List;

/**
 * 远程Rest调用接口
 *
 * @param <T> 远程调用后解析成我们需要的标准模型
 * @author BiuQu
 * @date 2023/2/1 22:44
 */
public interface RemoteService<O, T extends BaseBiz<O>>
{
    /**
     * 发起远程调用
     *
     * @param model 调用渠道使用的参数模型
     * @return 系统内部使用的标准业务模型
     */
    ResultCode<O> invoke(T model);

    /**
     * 发起远程调用
     *
     * @param model 调用渠道使用的参数模型
     * @return 系统内部使用的带有结果状态的标准业务模型
     */
    ResultCode<List<O>> invokeBatch(T model);
}
