package com.biuqu.boot.aop;

import com.biuqu.annotation.DisableSecurityAnn;
import com.biuqu.aop.BaseAop;
import com.biuqu.hsm.BizHsmFacade;
import com.biuqu.model.BaseSecurity;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 加密机的安全切面
 *
 * @author BiuQu
 * @date 2023/2/1 16:44
 */
@Component
@Aspect
public class EncSecurityAop extends BaseAop
{
    @Before(BEFORE_PATTERN)
    @Override
    public void before(JoinPoint joinPoint)
    {
        super.before(joinPoint);
    }

    @AfterReturning(value = AFTER_PATTERN, returning = "result")
    @Override
    public void after(JoinPoint joinPoint, Object result)
    {
        super.after(joinPoint, result);
    }

    @Override
    protected void doBefore(Method method, Object[] args)
    {
        List<BaseSecurity> models = getModels(method, args, DisableSecurityAnn.class);
        this.bizHsm.before(models);
    }

    @Override
    protected void doAfter(Method method, Object[] args, Object result)
    {
        List<BaseSecurity> models = getModels(method, result, DisableSecurityAnn.class);
        this.bizHsm.after(models);
    }

    /**
     * 获取业务模型对象
     *
     * @param method      反射对应的执行方法
     * @param result      参数对象
     * @param ignoreClazz 方法上的待忽略注解
     * @return 从反射方法中提取的参数对象
     */
    private List<BaseSecurity> getModels(Method method, Object result, Class<? extends Annotation> ignoreClazz)
    {
        List<BaseSecurity> models = Lists.newArrayList();
        Annotation ignoreAnn = method.getAnnotation(ignoreClazz);
        if (null != ignoreAnn)
        {
            return models;
        }

        if (result instanceof List)
        {
            List<?> list = (List)result;
            for (Object model : list)
            {
                if (model instanceof BaseSecurity)
                {
                    models.add((BaseSecurity)model);
                }
            }
        }
        else if (result instanceof BaseSecurity)
        {
            models.add((BaseSecurity)result);
        }
        return models;
    }

    /**
     * 获取业务模型对象
     *
     * @param method      反射对应的执行方法
     * @param args        方法上的参数列表
     * @param ignoreClazz 方法上的待忽略注解
     * @return 从反射方法中提取参数模型对象
     */
    private List<BaseSecurity> getModels(Method method, Object[] args, Class<? extends Annotation> ignoreClazz)
    {
        List<BaseSecurity> models = Lists.newArrayList();
        if (ArrayUtils.isEmpty(args))
        {
            return models;
        }

        models.addAll(getModels(method, args[0], ignoreClazz));
        return models;
    }

    /**
     * 启用安全策略的注解
     */
    private static final String ENABLE_SECURITY = "@annotation(com.biuqu.annotation.EnableSecurityAnn) && ";

    /**
     * 需要在dao的get方法
     */
    private static final String GET_DAO = "(execution (* com.biuqu.boot.dao.*.*SecDao.*get*(..)))";

    /**
     * 需要在dao的batchGet方法
     */
    private static final String BATCH_GET_DAO = "(execution (* com.biuqu.boot.dao.*.*SecDao.*batch*(..)))";

    /**
     * 需要在dao的add方法
     */
    private static final String ADD_DAO = "(execution (* com.biuqu.boot.dao.*.*SecDao.*add*(..)))";

    /**
     * 需要在dao的update方法
     */
    private static final String UPDATE_DAO = "(execution (* com.biuqu.boot.dao.*.*SecDao.*update*(..)))";

    /**
     * 需要在service的get方法
     */
    private static final String GET_SVC =
        ENABLE_SECURITY + "(execution (* com.biuqu.service.BaseBizService+.*get*(..)))";

    /**
     * 需要在service的batchGet方法
     */
    private static final String BATCH_GET_SVC =
        ENABLE_SECURITY + "(execution (* com.biuqu.service.BaseBizService+.*batch*(..)))";

    /**
     * 需要在service的add方法
     */
    private static final String ADD_SVC =
        ENABLE_SECURITY + "(execution (* com.biuqu.service.BaseBizService+.*add*(..)))";

    /**
     * 需要在service的update方法
     */
    private static final String UPDATE_SVC =
        ENABLE_SECURITY + "(execution (* com.biuqu.service.BaseBizService+.*update*(..)))";

    /**
     * 所有方法的前面做拦截的表达式(dao/service的add/update方法)
     */
    private static final String BEFORE_PATTERN = ADD_DAO + "||" + UPDATE_DAO + "||" + ADD_SVC + "||" + UPDATE_SVC;

    /**
     * 所有方法的后面做拦截的表达式(dao/service的get/batchGet方法)
     */
    private static final String AFTER_PATTERN = GET_DAO + "||" + BATCH_GET_DAO + "||" + GET_SVC + "||" + BATCH_GET_SVC;

    /**
     * 注入业务安全管理器
     */
    @Autowired
    private BizHsmFacade bizHsm;
}
