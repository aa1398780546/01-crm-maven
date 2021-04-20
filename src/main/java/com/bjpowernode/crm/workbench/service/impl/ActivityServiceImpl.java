package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.workbench.damain.Activity;
import com.bjpowernode.crm.workbench.dao.ActivityDao;
import com.bjpowernode.crm.workbench.service.ActivityService;

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
}
