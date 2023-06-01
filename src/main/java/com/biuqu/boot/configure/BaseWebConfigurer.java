package com.biuqu.boot.configure;

import com.biuqu.boot.handler.InvalidUrlHandler;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import java.util.Set;

/**
 * 基础的web配置(预留排除不需要拦截的URL)
 *
 * @author BiuQu
 * @date 2023/1/3 19:45
 */
public abstract class BaseWebConfigurer extends DelegatingWebMvcConfiguration
{
    @Override
    protected void addInterceptors(InterceptorRegistry registry)
    {
        super.addInterceptors(registry);

        if (!CollectionUtils.isEmpty(getInvalidPatterns()))
        {
            InterceptorRegistration registration = registry.addInterceptor(new InvalidUrlHandler());
            String[] invalidPatterns = getInvalidPatterns().toArray(new String[] {});
            registration.addPathPatterns(invalidPatterns);
        }
    }

    /**
     * 获取不可用的URL列表
     *
     * @return 不可用的URL列表
     */
    protected abstract Set<String> getInvalidPatterns();
}
