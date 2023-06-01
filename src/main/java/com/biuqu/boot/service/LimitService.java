package com.biuqu.boot.service;

import com.biuqu.model.LimitConfig;

/**
 * 原子限流服务
 *
 * @author BiuQu
 * @date 2023/1/30 21:43
 */
public interface LimitService
{
    /**
     * 基于QPS限流(与最大调用量的差异是QPS限流支持滑动时间窗)
     *
     * @param config 限流配置
     * @return true表示需要限流
     */
    boolean qpsLimit(LimitConfig config);

    /**
     * 基于最大调用量限流
     *
     * @param config 限流配置
     * @return true表示需要限流
     */
    boolean maxLimit(LimitConfig config);
}
