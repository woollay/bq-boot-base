package com.biuqu.boot.configure;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis启动配置
 *
 * @author BiuQu
 * @date 2023/1/27 22:54
 */
@Configuration
@MapperScan(basePackages = {"com.biuqu.boot.dao"})
public class MyBatisConfigurer
{
}
