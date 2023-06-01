package com.biuqu.boot.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * dao接口
 *
 * @author BiuQu
 * @date 2023/1/29 19:01
 */
public interface Dao<T>
{
    /**
     * 插入数据
     *
     * @param model 业务参数模型
     * @return 插入成功的数量(一般不关注)
     */
    int add(@Param("model") T model);

    /**
     * 查询数据
     *
     * @param model 业务参数模型
     * @return 获取单个业务模型数据
     */
    T get(@Param("model") T model);

    /**
     * 查询批量数据
     *
     * @param model 业务参数模型
     * @return 获取多个业务模型数据
     */
    List<T> getBatch(@Param("model") T model);

    /**
     * 查询批量数据
     *
     * @param batch 多个业务参数模型
     * @return 获取多个业务模型数据
     */
    List<T> batchGet(@Param("batch") List<T> batch);

    /**
     * 更新数据
     *
     * @param model 业务参数模型
     * @return 变更的业务模型数据的数量
     */
    int update(@Param("model") T model);

    /**
     * 删除数据
     *
     * @param model 业务参数模型
     * @return 变更的业务模型数据的数量
     */
    int delete(@Param("model") T model);
}
