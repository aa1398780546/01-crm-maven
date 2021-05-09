package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Activity;

import java.util.Map;

/**
 * @author 林哥哥
 */
public interface UserActivityService {

    PaginationVO<Activity> pageListById(Map<String, Object> map);

}
