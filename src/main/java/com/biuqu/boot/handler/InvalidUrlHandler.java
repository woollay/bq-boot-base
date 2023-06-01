package com.biuqu.boot.handler;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 无效的URL拦截器
 *
 * @author BiuQu
 * @date 2023/1/3 19:50
 */
public class InvalidUrlHandler implements HandlerInterceptor
{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
    {
        return false;
    }
}
