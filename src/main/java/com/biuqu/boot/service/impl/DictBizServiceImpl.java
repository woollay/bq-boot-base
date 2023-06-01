package com.biuqu.boot.service.impl;

import com.biuqu.boot.constants.BootConst;
import com.biuqu.boot.dao.BizDao;
import com.biuqu.model.GlobalDict;
import com.biuqu.service.BaseBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 带缓存的字典服务
 * <p>
 * 支持正查和反查
 *
 * @author BiuQu
 * @date 2023/1/29 20:51
 */
@Service(BootConst.GLOBAL_DICT_SVC)
public class DictBizServiceImpl extends BaseBizService<GlobalDict>
{
    @Override
    protected GlobalDict queryByKey(String key)
    {
        return dao.get(GlobalDict.toDict(key));
    }

    @Override
    protected List<GlobalDict> queryBatchByKey(String key)
    {
        GlobalDict dict = new GlobalDict();
        dict.setType(key);
        return dao.getBatch(dict);
    }

    /**
     * 注入抽象dao
     */
    @Autowired
    private BizDao<GlobalDict> dao;
}
