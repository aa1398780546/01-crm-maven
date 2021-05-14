package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.dao.UserTranDao;
import com.bjpowernode.crm.workbench.dao.UserTranHistoryDao;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.UserTranService;

import java.util.List;
import java.util.Map;

/**
 * @author 林哥哥
 */
public class UserTranServiceImpl implements UserTranService {

    private UserTranDao userTranDao = SqlSessionUtil.getSqlSession().getMapper(UserTranDao.class);
    private UserTranHistoryDao userTranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(UserTranHistoryDao.class);


    @Override
    public PaginationVO<Tran> pageListById(Map<String, Object> map) {

        int total = userTranDao.getTotalByCondition(map);
        List<Tran> dataList = userTranDao.getActivityListByCondition(map);

        PaginationVO<Tran> vo = new PaginationVO<Tran>() ;
        vo.setDataList(dataList);
        vo.setTotal(total);

        return vo;
    }

    @Override
    public List<TranHistory> getHistoryByTranId(String tranId) {

        List<TranHistory> historyList = userTranHistoryDao.getHistoryByTranId(tranId);

        return historyList;
    }


}

