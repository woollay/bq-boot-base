package com.biuqu.boot.configure;

import com.biuqu.boot.constants.BootConst;
import com.biuqu.constants.Const;
import com.biuqu.json.JsonMappers;
import com.biuqu.model.GlobalDict;
import com.biuqu.service.BaseBizService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

/**
 * SpringMvc配置
 *
 * @author BiuQu
 * @date 2023/1/28 21:08
 */
@Slf4j
@Configuration
public class WebMvcConfigurer extends BaseWebConfigurer
{
    /**
     * actuator健康检查的自定义类型
     */
    private static final String ACTUATOR_TYPE = "vnd.spring-boot.actuator.v3+json";
    /**
     * 字典服务
     */
    @Resource(name = BootConst.GLOBAL_DICT_SVC)
    private BaseBizService<GlobalDict> dictService;
    /**
     * 限流handler
     */
    @Resource(name = BootConst.CLIENT_LIMIT_SVC)
    private HandlerInterceptor limitHandler;
    /**
     * 是否驼峰式json(默认支持)
     */
    @Value("${bq.json.snake-case:true}")
    private boolean snakeCase;
    /**
     * 直接拦截的url
     */
    @Value("${bq.web.invalid-urls:''}")
    private String invalidUrls;
    /**
     * 直接拦截的url
     */
    @Value("${bq.limit.enabled:false}")
    private boolean limitEnabled;

    @Override
    protected void addInterceptors(InterceptorRegistry registry)
    {
        super.addInterceptors(registry);

        //默认关闭限流,只有业务微服务才需要打开
        if (!limitEnabled)
        {
            log.info("disabled limit access.");
            return;
        }

        //添加一个限流器，仅拦截特定的url
        InterceptorRegistration registration = registry.addInterceptor(limitHandler);

        GlobalDict param = new GlobalDict().toDict();
        List<GlobalDict> batchDict = dictService.getBatch(param);

        Set<String> urls = Sets.newHashSet();
        if (!CollectionUtils.isEmpty(batchDict))
        {
            for (GlobalDict dict : batchDict)
            {
                urls.add(dict.getValue());
            }
        }
        registration.addPathPatterns(Lists.newArrayList(urls));
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        registry.addResourceHandler("/assets/**").addResourceLocations("classpath:/static/assets");
        super.addResourceHandlers(registry);
    }

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters)
    {
        for (int i = 0; i < converters.size(); i++)
        {
            HttpMessageConverter<?> converter = converters.get(i);
            if (converter instanceof MappingJackson2HttpMessageConverter)
            {
                ObjectMapper mapper = JsonMappers.getMapper(this.snakeCase);
                MappingJackson2HttpMessageConverter conv = new MappingJackson2HttpMessageConverter();
                conv.setObjectMapper(mapper);

                //默认返回的Jackson对应的Rest服务的ContentType为:'application/json;charset=UTF-8'
                List<MediaType> types = Lists.newArrayList();
                MediaType utf8Type = new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8);
                types.add(utf8Type);

                //兼容支持actuator健康检查
                MediaType actuatorType = new MediaType(MediaType.APPLICATION_JSON.getType(), ACTUATOR_TYPE);
                types.add(actuatorType);

                conv.setSupportedMediaTypes(types);
                //把旧的转换器替换成新的转换器
                converters.set(i, conv);
                break;
            }
        }
    }

    @Override
    protected Set<String> getInvalidPatterns()
    {
        Set<String> urls = null;
        if (!StringUtils.isEmpty(this.invalidUrls))
        {
            urls = Sets.newHashSet(StringUtils.split(this.invalidUrls, Const.SPLIT));
        }
        return urls;
    }
}
