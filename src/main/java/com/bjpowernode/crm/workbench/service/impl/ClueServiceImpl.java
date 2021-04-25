package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.dao.ClueDao;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.service.ClueService;

import java.util.List;
import java.util.Map;

/**
 * @author 林哥哥
 */
public class ClueServiceImpl implements ClueService {

    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);

    @Override
    public Boolean save(Clue c) {

        Boolean flag = true;

        int num = clueDao.save(c);

        if (num!=1) {
            flag = false;
        }

        return flag;
    }

    @Override
    public Clue detail(String id) {

        Clue clue = clueDao.detail(id);

        return clue;
    }

    @Override
    public PaginationVO<Clue> pageList(Map<String, Object> map) {

        System.out.println("进入到查询线索信息列表的操作ClueServiceImpl-pageList");

        int total = clueDao.getTotalByCondition(map);
        List<Clue> dataList = clueDao.getClueListByCondition(map);

        PaginationVO<Clue> vo = new PaginationVO<Clue>() ;
        vo.setDataList(dataList);
        vo.setTotal(total);

        return vo;
    }

}
