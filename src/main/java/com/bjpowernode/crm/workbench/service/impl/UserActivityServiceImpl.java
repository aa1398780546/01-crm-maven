package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.dao.ActivityDao;
import com.bjpowernode.crm.workbench.dao.UserActivityDao;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.service.UserActivityService;

import java.util.List;
import java.util.Map;

/**
 * @author 林哥哥
 */
public class UserActivityServiceImpl implements UserActivityService {

    private UserActivityDao userActivityDao = SqlSessionUtil.getSqlSession().getMapper(UserActivityDao.class);

    @Override
    public PaginationVO<Activity> pageListById(Map<String, Object> map) {

        int total = userActivityDao.getTotalByCondition(map);
        List<Activity> dataList = userActivityDao.getActivityListByCondition(map);

        PaginationVO<Activity> vo = new PaginationVO<Activity>() ;
        vo.setDataList(dataList);
        vo.setTotal(total);

        return vo;
    }
}
