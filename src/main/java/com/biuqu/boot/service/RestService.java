package com.biuqu.boot.service;

import com.biuqu.log.annotation.AuditLogAnn;
import com.biuqu.model.BaseBiz;
import com.biuqu.model.ResultCode;

import java.util.List;

/**
 * Rest接口对应的服务接口
 *
 * @author BiuQu
 * @date 2023/2/5 16:48
 */
public interface RestService<O, T extends BaseBiz<O>>
{
    /**
     * 获取批量结果(适用于接口调用)
     *
     * @param model 参业务模型
     * @return 批量出参模型结果对象
     */
    ResultCode<List<O>> batchExecute(T model);

    /**
     * 获取单个结果(适用于接口调用)
     *
     * @param model 业务参模型
     * @return 业务结果模型结果对象
     */
    ResultCode<O> execute(T model);

    /**
     * 插入数据
     *
     * @param model 业务参数模型
     * @return 插入成功的结果对象
     */
    @AuditLogAnn
    ResultCode<O> add(T model);

    /**
     * 查询数据
     *
     * @param model 业务参数模型
     * @return 获取单个业务模型数据结果对象
     */
    @AuditLogAnn
    ResultCode<O> get(T model);

    /**
     * 查询批量数据
     *
     * @param model 业务参数模型
     * @return 获取多个业务模型数据结果对象
     */
    ResultCode<List<O>> getBatch(T model);

    /**
     * 更新数据
     *
     * @param model 业务参数模型
     * @return 变更的业务模型数据的结果对象
     */
    ResultCode<O> update(T model);

    /**
     * 删除数据
     *
     * @param model 业务参数模型
     * @return 变更的业务模型数据的结果对象
     */
    ResultCode<O> delete(T model);
}
