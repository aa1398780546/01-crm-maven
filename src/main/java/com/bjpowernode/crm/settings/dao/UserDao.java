package com.bjpowernode.crm.settings.dao;

import com.bjpowernode.crm.settings.domain.User;

import java.util.List;
import java.util.Map;

/**
 * @author 林哥哥
 */
public interface UserDao {

    User login(Map<String, String> map);

    List<User> getUserList();

}
