package com.biuqu.boot.remote.impl;

import com.biuqu.boot.constants.BootConst;
import com.biuqu.boot.remote.BaseRemoteService;
import com.biuqu.model.BaseBiz;
import org.springframework.stereotype.Service;

/**
 * 抽象的远程服务调用
 *
 * @author BiuQu
 * @date 2023/2/1 23:10
 */
@Service(BootConst.DEFAULT_REMOTE_SVC)
public class RemoteServiceImpl<O, T extends BaseBiz<O>> extends BaseRemoteService<O, T>
{
}
