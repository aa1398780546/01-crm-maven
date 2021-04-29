package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.MD5Util;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.service.ClueService;
import com.bjpowernode.crm.workbench.service.impl.ClueServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 林哥哥
 */
public class UserController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到用户控制器");
        //获取访问路径
        String path = request.getServletPath();
        System.out.println("访问路径：" + path);

        //根据访问路劲，执行对应的方法。
        if ("/settings/user/login.do".equals(path)) {
            login(request,response);
        } else if ("/settings/user/getUserList.do".equals(path)) {
            getUserList(request,response);
        }

    }

    //获取所有用户列表
    private void getUserList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到所有用户列表界面");

        //页码,计算略过的记录数，但是数据库不需要这个
        Integer pageNo = Integer.valueOf(request.getParameter("pageNo"));
        //每页展现的记录数，计算略过的记录数，数据库需要这个
        Integer pageSize = Integer.valueOf(request.getParameter("pageSize"));
        System.out.println("pageSize:  "+ pageSize);
        //计算略过的记录数
        int skipCount = (pageNo-1)*pageSize;
        System.out.println("SkipCount:   " + skipCount);

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("pageSize",pageSize);
        map.put("skipCount",skipCount);

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        PaginationVO<User> vo = us.pageList(map);

        PrintJson.printJsonObj(response,vo);



    }

    //这是根据登录页面进来的方法
    public void login(HttpServletRequest request,HttpServletResponse response){

        System.out.println("进入到验证登录操作");

        //获取到前端传过来的参数
        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");


        //接收浏览器的ip地址
        String ip = request.getRemoteAddr();

        //对账号密码进行加密
        //loginAct = MD5Util.getMD5(loginAct);
        loginPwd = MD5Util.getMD5(loginPwd);

        //普通实现类形态，不走事务，登录不走事务，但是在将来的业务里用可能需要扩充日志相关操作，接触到添加修改还是要走事务。
//        UserService us = new UserServiceImpl();
        //创建Service层对象UserServiceImpl
        //在未来业务层开发，统一使用代理类形态的接口对象
//        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        UserService us = new UserServiceImpl();


        try {

            //将参数发送到Service层的login方法，如果各种验证都通过将会返回一个User
            User user = us.login(loginAct,loginPwd,ip);

            //程序执行到这里说明登陆成功，业务逻辑层没有为controller抛出任何异常
            //将User对象保存到Session域对象中
            request.getSession().setAttribute("user",user);

            //调用工具类的方法，将 flag=true 的结果返回给浏览器。
            PrintJson.printJsonFlag(response,true);

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("看到这句话说明UserController捕捉到了异常。");
            /*
                {"success":false,"msg":?}
             */

            //程序一旦执行catch块的信息，说明业务层为我们验证失败，为controller抛出的异常，登录失败
            /*

                我们现在作为controller，需要为ajax请求提供多项信息

                可以有两种手段来处理：
                    （1）将多项信息打包成为map，将map解析为json串
                    （2）创建一个Vo
                            private boolean success;
                            private String msg;

                    如果对于展现的信息将来还会大量的使用，我们创建一个vo类，使用方便
                    如果对于展现的信息只有在这个需求中能够使用，我们使用map就可以了

             */
            //获取到msg=?
            String msg = e.getMessage();
            System.out.println("======================================");
            System.out.println("msg的值：" + msg);

            Map<String,Object> map = new HashMap<String,Object>();
            map.put("success",false);
            map.put("msg",msg);

            //将 flag=false,msg=? 的信息打包到map集合中返回给浏览器
            PrintJson.printJsonObj(response,map);
        }

    }

}
