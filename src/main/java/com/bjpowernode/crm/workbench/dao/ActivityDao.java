package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

/**
 * @author 林哥哥
 */
public interface ActivityDao {

    int save(Activity a);

    int getTotalByCondition(Map<String,Object> map);

    List<Activity> getActivityListByCondition(Map<String,Object> map);

    int delete(String[] ids);

    Activity getById(String id);

    int update(Activity a);

    Activity detail(String id);

    List<Activity> getActivityListById(String clueId);

    List<Activity> getActivityListByNameAndNotByClueId(Map<String, String> map);
}
