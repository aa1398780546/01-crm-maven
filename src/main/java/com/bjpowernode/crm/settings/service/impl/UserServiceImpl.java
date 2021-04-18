package com.bjpowernode.crm.settings.service.impl;

import com.bjpowernode.crm.exception.LoginException;
import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 林哥哥
 */
public class UserServiceImpl implements UserService {

    //使用动态代理获得userDao
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);


    /*
        拿到参数 loginAct,loginPwd,ip ,将其中的loginAct,loginPwd放到一个Map集合中
        调用UserDao的 login(Map<String,String>) 方法，返回一个User对象。
        分别对对象中的expireTime,lockState和ip地址进行验证，如果都符合要求，将User返回给UserServlet
     */
    @Override
    public User login(String loginAct, String loginPwd, String ip) throws LoginException {

        //将参数 loginAct 和 loginPwd 放到一个Map集合中
        Map<String, String> map = new HashMap<>();
        map.put("loginAct", loginAct);
        map.put("loginPwd", loginPwd);

        //调用 UserDao的login方法，将map作为参数传入,返回一个User对象。
        User user = userDao.login(map);
        System.out.println("====================================");
        System.out.println("UserServiceImpl = user的值为：" + user);

        if (user==null){
            System.out.println("Test。。。。。。。。。。。。。。");
            throw new LoginException("账号密码错误");
        }

        //对User对象的expireTime,lockState,ip进行判断，如果都符合要求，将User返回给UserServlet
        String expireTime = user.getExpireTime();
        String lockState = user.getLockState();
        String allowIps = user.getAllowIps();

        //通过自定义的工具类获取系统当前时间
        String nowTime = DateTimeUtil.getSysTime();

        if (expireTime.compareTo(nowTime)<0){
            throw new LoginException("账号已失效");
        }

        if ("0".equals(lockState)){
            throw new LoginException("账号未激活");
        }

        if (!allowIps.contains(ip)){
            throw new LoginException("无效的IP地址");
        }

    return user;

    }
}