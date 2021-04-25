package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.ClueService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.ClueServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 林哥哥
 */
public class ClueController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到线索控制器");

        String path = request.getServletPath();

        if ("/workbench/clue/getUserList.do".equals(path)) {
           getUserList(request,response);
        } else if ("/workbench/clue/save.do".equals(path)) {
           save(request,response);
        } else if ("/workbench/clue/detail.do".equals(path)) {
           detail(request,response);
        } else if ("/workbench/clue/pageList.do".equals(path)){
            pageList(request,response);
        }else if ("/workbench/clue/showActivityList.do".equals(path)) {
            showActivityList(request, response);
        }
    }

    //获取Clue-detail.jsp市场活动列表
    private void showActivityList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入ClueController-showActivityList");

        String clueId = request.getParameter("clueId");

        System.out.println("clueId...:" + clueId);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        List<Activity> activityList = as.getActivityListById(clueId);

        PrintJson.printJsonObj(response,activityList);

    }

    //获取Clue-index.jsp分页列表所要的total和ActivityList
    private void pageList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到查询物流管理信息列表的操作（结合条件查询+分页查询）");

         /*
                计算出略过的记录数
                为什么要记录这个 因为数据库的分页 select * form student limit 10,5
                表示的意思是从略过第10个，从第11个开始数，数5个。
         */
        //页码,计算略过的记录数，但是数据库不需要这个
        Integer pageNo = Integer.valueOf(request.getParameter("pageNo"));
        //每页展现的记录数，计算略过的记录数，数据库需要这个
        Integer pageSize = Integer.valueOf(request.getParameter("pageSize"));
        System.out.println("pageSize:  "+ pageSize);
        //计算略过的记录数
        int skipCount = (pageNo-1)*pageSize;
//        System.out.println("pageNo:   " + pageNo);
        System.out.println("SkipCount:   " + skipCount);

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("pageSize",pageSize);
        map.put("skipCount",skipCount);

        //使用动态代理的方法，创建ActivityService的实例对象。
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        PaginationVO<Clue> vo = cs.pageList(map);

        PrintJson.printJsonObj(response,vo);

    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String id = request.getParameter("id");

        System.out.println("id=======" + id);

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        Clue clue = clueService.detail(id);

        //打开一个新的页面，不是使用局部刷新，选择重定向或者请求转发。
//        PrintJson.printJsonObj(response,clue);

        request.setAttribute("c",clue);
        request.getRequestDispatcher("/workbench/clue/detail.jsp").forward(request,response);

    }

    private void save(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("正在进入ClueController-save");

        //接收参数
        String id = UUIDUtil.getUUID();
        String fullname = request.getParameter("fullname");
        String appellation = request.getParameter("appellation");
        String owner = request.getParameter("owner");
        String company = request.getParameter("company");
        String job = request.getParameter("job");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String website = request.getParameter("website");
        String mphone = request.getParameter("mphone");
        String state = request.getParameter("state");
        String source = request.getParameter("source");
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        String address = request.getParameter("address");
        //创建时间：当前系统时间
        String createTime = DateTimeUtil.getSysTime();
        //创建人：当前登录用户
        String createBy = ((User)request.getSession().getAttribute("user")).getName();

        Clue c = new Clue();
        c.setId(id);
        c.setFullname(fullname);
        c.setAppellation(appellation);
        c.setOwner(owner);
        c.setCompany(company);
        c.setJob(job);
        c.setEmail(email);
        c.setPhone(phone);
        c.setWebsite(website);
        c.setMphone(mphone);
        c.setState(state);
        c.setSource(source);
        c.setDescription(description);
        c.setContactSummary(contactSummary);
        c.setNextContactTime(nextContactTime);
        c.setAddress(address);
        c.setCreateTime(createTime);
        c.setCreateBy(createBy);

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        Boolean flag = clueService.save(c);

        PrintJson.printJsonObj(response,flag);

    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("ClueController-getUserList正在执行");

        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());

        List<User> userList = userService.getUserList();

        PrintJson.printJsonObj(response,userList);

    }

}
