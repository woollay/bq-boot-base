package com.biuqu.boot.configure;

import com.biuqu.boot.model.MdcAccessLogValve;
import com.biuqu.log.dao.LogDao;
import com.biuqu.log.dao.impl.OpLogDaoImpl;
import com.biuqu.log.dao.impl.SecLogDaoImpl;
import com.biuqu.log.dao.impl.SysLogDaoImpl;
import com.biuqu.log.model.OperationLog;
import com.biuqu.log.model.SecurityLog;
import com.biuqu.log.model.SystemLog;
import com.biuqu.log.service.LogFacade;
import com.google.common.collect.Lists;
import org.apache.catalina.valves.AccessLogValve;
import org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * 日志配置服务
 *
 * @author BiuQu
 * @date 2023/2/3 22:39
 */
@Configuration
public class LogConfigurer
{
    /**
     * 在tomcat日志中实现trace id
     * <p>
     * 参考: https://www.appsloveworld.com/springboot/100/36/mdc-related-content-in-tomcat-access-logs
     *
     * @param env 运行环境变量
     * @return 定制的AccessLog工厂
     */
    @Bean
    public WebServerFactoryCustomizer<ConfigurableTomcatWebServerFactory> accessLog(Environment env)
    {
        return factory ->
        {
            final AccessLogValve valve = new MdcAccessLogValve();
            valve.setPattern(env.getProperty("server.tomcat.accesslog.pattern"));

            //直接覆盖原生的日志对象
            if (factory instanceof TomcatServletWebServerFactory)
            {
                TomcatServletWebServerFactory tsFactory = (TomcatServletWebServerFactory)factory;
                tsFactory.setEngineValves(Lists.newArrayList(valve));
            }
        };
    }

    @Bean
    public LogDao<SystemLog> sysLog()
    {
        return new SysLogDaoImpl();
    }

    @Bean
    public LogDao<OperationLog> opLog()
    {
        return new OpLogDaoImpl();
    }

    @Bean
    public LogDao<SecurityLog> secLog()
    {
        return new SecLogDaoImpl();
    }

    @Bean
    public LogFacade facade(LogDao<OperationLog> opDao, LogDao<SecurityLog> secDao, LogDao<SystemLog> sysDao)
    {
        //可以注入自定义的实现(比如把日志改到存储至数据库)
        return new LogFacade(opDao, secDao, sysDao);
    }
}
