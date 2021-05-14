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

    int getTotalByCondition(Map<String, Object> map);

    List<User> getClueListByCondition(Map<String, Object> map);

    int save(User user);
}
