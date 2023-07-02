package com.biuqu.boot.web;

import com.biuqu.boot.constants.BootConst;
import com.biuqu.boot.service.RestService;
import com.biuqu.log.annotation.AuditLogAnn;
import com.biuqu.log.annotation.ClientLogAnn;
import com.biuqu.model.BaseBiz;
import com.biuqu.model.BaseBizInner;
import com.biuqu.model.ResultCode;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;

/**
 * 抽象的业务Rest服务
 *
 * @param <O> 业务出参模型(outer)
 * @param <T> 业务标准模型
 * @param <I> 业务的入参模型(inner)
 * @author BiuQu
 * @date 2023/2/4 16:16
 */
@Slf4j
public class BaseBizController<O, T extends BaseBiz<O>, I extends BaseBizInner<T>>
{
    /**
     * 获取批量结果(适用于接口调用)
     *
     * @param inner 入参业务模型
     * @return 批量出参模型
     */
    @ClientLogAnn
    public ResultCode<List<O>> batchExecute(I inner)
    {
        return getService().batchExecute(inner.toModel());
    }

    /**
     * 获取单个结果(适用于接口调用)
     *
     * @param inner 业务入参模型
     * @return 业务结果模型
     */
    @ClientLogAnn
    public ResultCode<O> execute(I inner)
    {
        return getService().execute(inner.toModel());
    }

    /**
     * 插入数据
     * <p>
     * TODO 注意：@AuditLogAnn注解需要重新加在实现的RestController上(补充审计日志的完整信息)
     *
     * @param inner 业务入参数模型
     * @return 插入成功的数量(一般不关注)
     */
    @AuditLogAnn
    public ResultCode<O> add(I inner)
    {
        return getService().add(inner.toModel());
    }

    /**
     * 查询数据
     * <p>
     * TODO 注意：@AuditLogAnn注解需要重新加在实现的RestController上(补充审计日志的完整信息)
     *
     * @param inner 业务入参数模型
     * @return 获取单个业务模型数据
     */
    @AuditLogAnn
    public ResultCode<O> get(I inner)
    {
        return getService().get(inner.toModel());
    }

    /**
     * 查询批量数据
     * <p>
     * TODO 注意：@AuditLogAnn注解需要重新加在实现的RestController上(补充审计日志的完整信息)
     *
     * @param inner 业务入参数模型
     * @return 获取多个业务模型数据
     */
    @AuditLogAnn
    public ResultCode<List<O>> getBatch(I inner)
    {
        return getService().getBatch(inner.toModel());
    }

    /**
     * 更新数据
     * <p>
     * TODO 注意：@AuditLogAnn注解需要重新加在实现的RestController上(补充审计日志的完整信息)
     *
     * @param inner 业务入参数模型
     * @return 变更的业务模型数据的数量
     */
    @AuditLogAnn
    public ResultCode<O> update(I inner)
    {
        return getService().update(inner.toModel());
    }

    /**
     * 删除数据
     * <p>
     * TODO 注意：@AuditLogAnn注解需要重新加在实现的RestController上(补充审计日志的完整信息)
     *
     * @param inner 业务入参数模型
     * @return 变更的业务模型数据的数量
     */
    @AuditLogAnn
    public ResultCode<O> delete(I inner)
    {
        return getService().delete(inner.toModel());
    }

    /**
     * 获取服务(支持覆写)
     *
     * @return 注入的服务对象
     */
    protected RestService<O, T> getService()
    {
        return service;
    }

    /**
     * 注入标准的业务服务
     */
    @Resource(name = BootConst.DEFAULT_REST_SVC)
    private RestService<O, T> service;
}
