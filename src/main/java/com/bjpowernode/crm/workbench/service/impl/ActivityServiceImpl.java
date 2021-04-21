package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.damain.Activity;
import com.bjpowernode.crm.workbench.dao.ActivityDao;
import com.bjpowernode.crm.workbench.service.ActivityService;

import java.util.List;
import java.util.Map;

/**
 * @author 林哥哥
 */
public class ActivityServiceImpl implements ActivityService {

    private ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);

    @Override
    public Boolean save(Activity a) {

        System.out.println("进入到市场活动列表：ActivityServiceImpl-save");

        Boolean flag = true;

        //业务逻辑层，接收ActivityController传过来的Activity。
        int num = activityDao.save(a);

        if (num!=1){
           flag = false;
        }

        System.out.println("测试flag：" + flag);

        return flag;
    }

    @Override
    public PaginationVO<Activity> pageList(Map<String, Object> map) {
        System.out.println("进入到查询市场活动信息列表的操作ActivityServiceImpl-pageList");

        /*
            分析：将发送过来的Map传给ActivityDao
                 给ActivityController返回一个Vo，那这里需要创建一个VO,
                 VO有两个属性 int total   ， List<T> dataList
         */
//        Integer skipCount = (Integer) map.get("skipCount");
//        System.out.println("ActivityServiceImpl的skipCount " + skipCount);

        int total = activityDao.getTotalByCondition(map);
        List<Activity> dataList = activityDao.getActivityListByCondition(map);

        PaginationVO<Activity> vo = new PaginationVO<Activity>() ;
        vo.setDataList(dataList);
        vo.setTotal(total);

        return vo;
    }
}
