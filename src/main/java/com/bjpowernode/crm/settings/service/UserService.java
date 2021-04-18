package com.bjpowernode.crm.settings.service;

import com.bjpowernode.crm.exception.LoginException;
import com.bjpowernode.crm.settings.domain.User;

/**
 * @author 林哥哥
 */
public interface UserService {
    User login(String loginAct, String loginPwd, String ip) throws LoginException;
}
