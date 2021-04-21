package com.bjpowernode.crm.workbench.dao;

/**
 * @author 林哥哥
 */
public interface ActivityRemarkDao {

    int getCountByAids(String[] ids);

    int deleteByAids(String[] ids);

}
