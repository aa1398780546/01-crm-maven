package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.damain.Activity;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 林哥哥
 */
public class ActivityController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到市场活动控制器");

        String path = request.getServletPath();

        if ("/workbench/activity/getUserList.do".equals(path)){
            getUserList(request,response);
        }else if ("/workbench/activity/save.do".equals(path)){
            save(request,response);
        }else if ("/workbench/activity/pageList.do".equals(path)){
            pageList(request,response);
        }else if ("/workbench/activity/delete.do".equals(path)){
            delete(request,response);
        }else if ("/workbench/activity/getUserListAndActivity.do".equals(path)){
            getUserListAndActivity(request,response);
        }else if ("/workbench/activity/update.do".equals(path)){
            update(request,response);
        }

    }

    //打开修改市场活动的模态窗口，添加完信息后，点击修改按钮。
    private void update(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行市场活动修改操作");

        String id = request.getParameter("id");
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        //修改时间：当前系统时间
        String editTime = DateTimeUtil.getSysTime();
        //修改人：当前登录用户
        String editBy = ((User)request.getSession().getAttribute("user")).getName();

        Activity a = new Activity();
        a.setId(id);
        a.setCost(cost);
        a.setStartDate(startDate);
        a.setOwner(owner);
        a.setName(name);
        a.setEndDate(endDate);
        a.setDescription(description);
        a.setEditBy(editBy);
        a.setEditTime(editTime);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        boolean flag = as.update(a);

        PrintJson.printJsonFlag(response, flag);
    }

    //点击修改，所有者信息根据用户列表展现。
    private void getUserListAndActivity(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到查询用户信息列表和根据市场活动id查询单条记录的操作");

        String id = request.getParameter("id");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        /*

            总结：
                controller调用service的方法，返回值应该是什么
                你得想一想前端要什么，就要从service层取什么

            前端需要的，管业务层去要
            uList
            a

            以上两项信息，复用率不高，我们选择使用map打包这两项信息即可
            map

         */
        Map<String,Object> map = as.getUserListAndActivity(id);

        PrintJson.printJsonObj(response, map);
    }

    //删除选中的市场活动信息
    private void delete(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行市场活动的删除操作");

        String ids[] = request.getParameterValues("id");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        boolean flag = as.delete(ids);

        PrintJson.printJsonFlag(response, flag);

    }

    //查询市场活动的详细信息
    private void pageList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到查询市场活动信息列表的操作（结合条件查询+分页查询）");

         /*
                计算出略过的记录数
                为什么要记录这个 因为数据库的分页 select * form student limit 10,5
                表示的意思是从略过第10个，从第11个开始数，数5个。
         */
        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        //页码,计算略过的记录数，但是数据库不需要这个
        Integer pageNo = Integer.valueOf(request.getParameter("pageNo"));
        //每页展现的记录数，计算略过的记录数，数据库需要这个
        Integer pageSize = Integer.valueOf(request.getParameter("pageSize"));
        //计算略过的记录数
        int skipCount = (pageNo-1)*pageSize;
//        System.out.println("pageNo:   " + pageNo);
        System.out.println("SkipCount:   " + skipCount);

        //分析一波，我们拿到了这么多参数，很明显不能直接把所有的参数一股脑的丢到需要调用的方法里面，
        //那么在没有一个实体类相对应的情况下，要怎么才能将这些参数打包呢？
        //使用VO？ 不行，VO是使用在给浏览器传值的，使用的地方不正确。
        //调用map传参
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);
//         skipCount = (Integer) map.get("skipCount");
//        System.out.println("map取下来的skipCount" + skipCount);

        //使用动态代理的方法，创建ActivityService的实例对象。
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        //思考返回什么，前端需要什么，我们就去问业务曾拿什么。
        /*
               前端要：
                    市场活动列表
                    查询的总条数

                    业务层拿到了以上两项信息之后，如果做返回
                    map     使用率低用这个
                    vo      使用率高用这个

                假设使用map：
                    map
                    map.put("dataList":dataList)
                    map.put("total":total)
                    PrintJson map -> Json
                    {"total":100,"dataList":[{市场活动1},{2},{3}]}

                假设使用Vo：
                    PaginationVO<T>
                        private int total;
                        private List<T> dataList;

                    PaginationVO<Activity> vo = new PaginationVo<>;
                    vo.setTotal(total);
                    vo.setDataList(dataList);
                    PrintJson vo -> json
                    {"total":100,"dataList":[{市场活动1},{2},{3}]}

         */
        PaginationVO<Activity> vo = as.pageList(map);
        PrintJson.printJsonObj(response,vo);
    }

    //打开创建市场活动的模态窗口，添加完信息后，点击保存按钮。
    private void save(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到添加市场活动列表save");

        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate  = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        //创建时间：当前系统时间
        String createTime = DateTimeUtil.getSysTime();
        //创建人：当前登录用户
        String createBy = ((User)request.getSession().getAttribute("user")).getName();

        Activity a = new Activity();
        a.setId(id);
        a.setOwner(owner);
        a.setName(name);
        a.setStartDate(startDate);
        a.setEndDate(endDate);
        a.setCost(cost);
        a.setDescription(description);
        a.setCreateTime(createTime);
        a.setCreateBy(createBy);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        Boolean flag = as.save(a);

        PrintJson.printJsonObj(response,flag);

    }

    //点击创建，所有者信息根据用户列表展现。
    private void getUserList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到获取用户信息列表getUserList");

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        List<User> userList = us.getUserList();

        PrintJson.printJsonObj(response,userList);


    }
}
