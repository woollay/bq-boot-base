package com.biuqu.boot.service.impl;

import com.biuqu.boot.constants.BootConst;
import com.biuqu.boot.remote.RemoteService;
import com.biuqu.boot.service.BaseRestService;
import com.biuqu.model.BaseBiz;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Rest业务处理服务
 *
 * @param <O> 业务出参模型(outer)
 * @param <T> 业务标准模型
 * @author BiuQu
 * @date 2023/2/4 16:10
 */
@Slf4j
@Service(BootConst.DEFAULT_REST_SVC)
public class RestServiceImpl<O, T extends BaseBiz<O>> extends BaseRestService<O, T>
{
    /**
     * 注入远程服务
     *
     * @return 远程服务
     */
    @Override
    protected RemoteService<O, T> getRemoteService()
    {
        return remoteService;
    }

    /**
     * 注入远程调用服务
     */
    @Resource(name = BootConst.DEFAULT_REMOTE_SVC)
    private RemoteService<O, T> remoteService;
}
