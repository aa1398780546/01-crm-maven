package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.damain.Activity;

import java.util.Map;

/**
 * @author 林哥哥
 */
public interface ActivityService {

    Boolean save(Activity a);

    PaginationVO<Activity> pageList(Map<String, Object> map);
}
