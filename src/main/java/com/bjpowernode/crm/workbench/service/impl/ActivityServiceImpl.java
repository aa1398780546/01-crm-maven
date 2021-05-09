package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.dao.ActivityDao;
import com.bjpowernode.crm.workbench.dao.ActivityRemarkDao;
import com.bjpowernode.crm.workbench.service.ActivityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 林哥哥
 */
public class ActivityServiceImpl implements ActivityService {

    private ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    private ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public Boolean save(Activity a) {

        System.out.println("进入到货物管理列表：ActivityServiceImpl-save");

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

        int total = activityDao.getTotalByCondition(map);
        List<Activity> dataList = activityDao.getActivityListByCondition(map);

        PaginationVO<Activity> vo = new PaginationVO<Activity>() ;
        vo.setDataList(dataList);
        vo.setTotal(total);

        return vo;
    }

    @Override
    public boolean delete(String[] ids) {

        boolean flag = true;

        //查询出需要删除的备注的数量
        int count1 = activityRemarkDao.getCountByAids(ids);

        //删除备注，返回受到影响的条数（实际删除的数量）
        int count2 = activityRemarkDao.deleteByAids(ids);

        if(count1!=count2){

            flag = false;

        }

        //删除市场活动
        int count3 = activityDao.delete(ids);
        if(count3!=ids.length){

            flag = false;

        }

        return flag;
    }

    @Override
    public Map<String, Object> getUserListAndActivity(String id) {

        //取uList
        List<User> uList = userDao.getUserList();

        //取a
        Activity a = activityDao.getById(id);

        //将uList和a打包到map中
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("uList", uList);
        map.put("a", a);

        //返回map就可以了
        return map;
    }

    @Override
    public boolean update(Activity a) {

        boolean flag = true;

        int count = activityDao.update(a);

        if(count!=1){

            flag = false;

        }

        return flag;
    }

    @Override
    public Activity detail(String id) {
        Activity a = activityDao.detail(id);
        return a;
    }

    @Override
    public List<ActivityRemark> getRemarkListByAid(String activityId) {
        List<ActivityRemark> arList = activityRemarkDao.getRemarkListByAid(activityId);
        return arList;
    }

    @Override
    public boolean deleteRemark(String id) {

        boolean flag = true;

        int count = activityRemarkDao.deleteById(id);

        if(count!=1){
            flag = false;
        }

        return flag;
    }

    @Override
    public boolean saveRemark(ActivityRemark ar) {

        boolean flag = true;

        int count = activityRemarkDao.saveRemark(ar);

        if(count!=1){
            flag = false;
        }

        return flag;
    }

    @Override
    public boolean updateRemark(ActivityRemark ar) {

        boolean flag = true;

        int count = activityRemarkDao.updateRemark(ar);

        if(count!=1){
            flag = false;
        }

        return flag;
    }

    @Override
    public List<Activity> getActivityListById(String clueId) {

        List<Activity> activityList = activityDao.getActivityListById(clueId);

        return activityList;
    }

    @Override
    public List<Activity> getActivityListByNameAndNotByClueId(Map<String, String> map) {

        List<Activity> aList = activityDao.getActivityListByNameAndNotByClueId(map);

        return aList;

    }

    @Override
    public List<Activity> getActivityByName(String aname) {

        List<Activity> activityList = activityDao.getActivityByName(aname);

        return activityList;

    }
}
