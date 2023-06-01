package com.biuqu.boot.dao;

import com.biuqu.model.BaseSecurity;

/**
 * 绑定业务模型的dao
 *
 * @author BiuQu
 * @date 2023/1/29 19:13
 */
public interface BizDao<T extends BaseSecurity> extends Dao<T>
{
}
