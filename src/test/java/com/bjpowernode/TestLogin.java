package com.bjpowernode;

import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.MD5Util;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 林哥哥
 */
public class TestLogin {

    //验证能否返回user
    @Test
    public void TestLogin(){
        SqlSession sqlSession = SqlSessionUtil.getSqlSession();
        UserDao userDao = sqlSession.getMapper(UserDao.class);
        String loginAct = "zs";
        String loginPwd = "202cb962ac59075b964b07152d234b70";
        Map<String,String> map = new HashMap<>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);
        User user = userDao.login(map);
        System.out.println("User的值为：" + user);
    }


    //验证失效时间
    @Test
    public void TestExpireTime(){
        //失效时间
        String expireTime = "2021-04-16 20:01:01";
        //当前系统时间
        String  currentTime = DateTimeUtil.getSysTime();
        //二者之间进行相应的判断,结果分为 >0 <0 =0 三种情况，
        // 当结果>0，不管大多少，都是大，其他情况也是如此。
        int count = expireTime.compareTo(currentTime);
        System.out.println(count);
    }

    //验证锁定状态
    @Test
    public void TestLookState(){
        //锁定状态
        String lockState = "1";
        //对锁定状态进行判断
        if ("0".equals(lockState)){
            System.out.println("账号被锁定");
        }
    }

    //验证运行访问的Ip
    @Test
    public void TestAllowIps(){
        //ip地址
        String ip = "192.168.1.1";
        //允许访问的ip地址群
        String allowIps ="192.168.1.1,192.168.1.2";
        //进行判断
        if (allowIps.contains(ip)){
            System.out.println("有效的IP地址，允许访问系统");
        }else {
            System.out.println("无效的IP地址，无法访问系统");
        }
    }

    //对账号密码进行加密
    @Test
    public void TestEncrypt(){
        //明文密码
        String pwd = "123";
        //调用工具进行加密
        pwd = MD5Util.getMD5(pwd);
        //加密后的结果：202cb962ac59075b964b07152d234b70
        System.out.println(pwd);
    }

}
