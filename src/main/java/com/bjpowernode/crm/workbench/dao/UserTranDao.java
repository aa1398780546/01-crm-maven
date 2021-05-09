package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

/**
 * @author 林哥哥
 */
public interface UserTranDao {

    int getTotalByCondition(Map<String, Object> map);

    List<Tran> getActivityListByCondition(Map<String, Object> map);
}
