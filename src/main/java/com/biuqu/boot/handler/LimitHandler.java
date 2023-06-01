package com.biuqu.boot.handler;

import com.biuqu.boot.model.AccessLimit;

/**
 * 接入限流服务(封装了最大调用量和qps限流)
 *
 * @author BiuQu
 * @date 2023/2/1 20:59
 */
public interface LimitHandler
{
    /**
     * 基于限流模型的限流实现
     *
     * @param model 限流模型
     * @return true表示需要限流
     */
    boolean limit(AccessLimit model);
}
