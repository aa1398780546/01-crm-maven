package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.damain.Activity;

import java.util.List;
import java.util.Map;

/**
 * @author 林哥哥
 */
public interface ActivityDao {

    int save(Activity a);

    int getTotalByCondition(Map<String,Object> map);

    List<Activity> getActivityListByCondition(Map<String,Object> map);

}
