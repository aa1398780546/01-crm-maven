package com.bjpowernode.crm.settings.service;

import com.bjpowernode.crm.exception.LoginException;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.vo.PaginationVO;

import java.util.List;
import java.util.Map;

/**
 * @author 林哥哥
 */
public interface UserService {

    Map<String,Object> login(String loginAct, String loginPwd, String ip) throws LoginException;

    List<User> getUserList();

    PaginationVO<User> pageList(Map<String, Object> map);

}
