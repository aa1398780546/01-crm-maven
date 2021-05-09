package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.service.UserActivityService;
import com.bjpowernode.crm.workbench.service.impl.UserActivityServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 林哥哥
 */
public class UserActivityController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到客户物流管理控制器");

        String path = request.getServletPath();

        if("/workbench/activity/getUserListById.do".equals(path)){
            getUserListById(request,response);
        }

    }

    //获取客户自己的货物信息
    private void getUserListById(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到查询物流管理信息列表的操作（结合条件查询+分页查询）");

        String id = request.getParameter("id");
        System.out.println("ID-===============:"+ id);

        //页码,计算略过的记录数，但是数据库不需要这个
        Integer pageNo = Integer.valueOf(request.getParameter("pageNo"));
        //每页展现的记录数，计算略过的记录数，数据库需要这个
        Integer pageSize = Integer.valueOf(request.getParameter("pageSize"));
        //计算略过的记录数
        int skipCount = (pageNo-1)*pageSize;
        System.out.println("SkipCount:   " + skipCount);

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);
        map.put("id",id);

        UserActivityService uas = (UserActivityService) ServiceFactory.getService(new UserActivityServiceImpl());


        PaginationVO<Activity> vo = uas.pageListById(map);
        PrintJson.printJsonObj(response,vo);
    }
}
