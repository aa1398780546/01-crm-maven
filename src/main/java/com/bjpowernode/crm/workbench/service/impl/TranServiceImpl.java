package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.dao.TranDao;
import com.bjpowernode.crm.workbench.dao.TranHistoryDao;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.service.TranService;

import java.util.List;
import java.util.Map;

/**
 * @author 林哥哥
 */
public class TranServiceImpl implements TranService {

    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);

    @Override
    public PaginationVO<Tran> tranList(Map<String,Object> map) {

        System.out.println("进入到查询线索信息列表的操作TranServiceImpl-tranList");

        int total = tranDao.getTotalByCondition(map);
        List<Tran> dataList = tranDao.getClueListByCondition(map);

        PaginationVO<Tran> vo = new PaginationVO<Tran>() ;
        vo.setDataList(dataList);
        vo.setTotal(total);

        return vo;
    }
}
