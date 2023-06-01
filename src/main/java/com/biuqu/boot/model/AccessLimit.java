package com.biuqu.boot.model;

import com.biuqu.model.LimitConfig;
import lombok.Data;

/**
 * 限流模型
 *
 * @author BiuQu
 * @date 2023/2/1 20:56
 */
@Data
public class AccessLimit
{
    /**
     * 生成全局配置对象
     * <p>
     * 客户端限流时,clientId为userId,urlId为客户端访问我们接口的url的id
     * 渠道限流时,clientId为channelId,urlId为客户端访问我们接口的url的id
     */
    public void setConfig(LimitConfig config)
    {
        this.config = config;
        this.config.setClientId(this.accessId);
        this.config.setUrlId(this.urlId);
    }

    /**
     * 唯一的访问id(客户端限流时为clientId，渠道限流时为channelId)
     */
    private String accessId;

    /**
     * 限流的url id
     */
    private String urlId;

    /**
     * 限流配置对象
     */
    private LimitConfig config;
}
