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
import com.bjpowernode.crm.workbench.domain.Tran;
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
        }else if ("/workbench/clue/unbund.do".equals(path)) {
            unbund(request, response);
        }else if ("/workbench/clue/getActivityListByNameAndNotByClueId.do".equals(path)) {
            getActivityListByNameAndNotByClueId(request, response);
        }else if ("/workbench/clue/relationActivityById.do".equals(path)) {
            relationActivityById(request, response);
        }else if ("/workbench/clue/getActivityByName.do".equals(path)) {
            getActivityByName(request, response);
        }else if ("/workbench/clue/convert.do".equals(path)) {
            convert(request, response);
        }
    }

    //执行线索转换的操作
    private void convert(HttpServletRequest request, HttpServletResponse response) throws IOException {

        System.out.println("执行线索转换的操作");

        String clueId = request.getParameter("clueId");

        //接收是否需要创建交易的标记
        String flag = request.getParameter("flag");

        String createBy = ((User)request.getSession().getAttribute("user")).getName();

        Tran t = null;

        //如果需要创建交易
        if("a".equals(flag)){

            t = new Tran();

            //接收交易表单中的参数
            String money = request.getParameter("money");
            String name = request.getParameter("name");
            String expectedDate = request.getParameter("expectedDate");
            String stage = request.getParameter("stage");
            String activityId = request.getParameter("activityId");
            String id = UUIDUtil.getUUID();
            String createTime = DateTimeUtil.getSysTime();

            t.setId(id);
            t.setMoney(money);
            t.setName(name);
            t.setExpectedDate(expectedDate);
            t.setStage(stage);
            t.setActivityId(activityId);
            t.setCreateBy(createBy);
            t.setCreateTime(createTime);

        }

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        /*
            为业务层传递的参数：

            1.必须传递的参数clueId，有了这个clueId之后我们才知道要转换哪条记录
            2.必须传递的参数t，因为在线索转换的过程中，有可能会临时创建一笔交易（业务层接收的t也有可能是个null）

         */
        boolean flag1 = cs.convert(clueId,t,createBy);

        if(flag1){
            //重定向302，因为不是Ajax请求。
            response.sendRedirect(request.getContextPath()+"/workbench/clue/index.jsp");

        }
    }

    //为Clue-convert.jsp的搜索框，搜索市场活动
    private void getActivityByName(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("根据用户输入的值查询市场活动");

        String aname = request.getParameter("aname");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> activityList = as.getActivityByName(aname);

        PrintJson.printJsonObj(response,activityList);

    }

    //为Clue-detail.jsp的关联市场活动进行关联
    private void relationActivityById(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行市场活动的关联操作");

        String cid = request.getParameter("cid");
        String aids[] = request.getParameterValues("aid");

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        boolean flag = cs.relationActivityById(cid,aids);

        PrintJson.printJsonFlag(response, flag);

    }

    //获取Clue-detail.jsp的关联市场活动需要的内容
    private void getActivityListByNameAndNotByClueId(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("查询市场活动列表（根据名称模糊查+排除掉已经关联指定线索的列表）");

        String aname = request.getParameter("aname");
        String clueId = request.getParameter("clueId");

        Map<String,String> map = new HashMap<String,String>();
        map.put("aname", aname);
        map.put("clueId", clueId);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        List<Activity> aList = as.getActivityListByNameAndNotByClueId(map);

        PrintJson.printJsonObj(response, aList);
    }

    //删除Clue-detail.jsp市场活动列表
    private void unbund(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到ClueController-unbund");

        String id = request.getParameter("id");

        System.out.println("id,,,,,,:"+id);

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        Boolean flag = cs.unbund(id);

        PrintJson.printJsonObj(response,flag);


    }

    //获取Clue-detail.jsp所需要的市场活动列表
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

    //获取从Clue-index.jsp打开detail.jsp所需要的信息,request.setAttribute("c",clue);
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

    //保存Clue-index.jsp创建线索后的信息。
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

    //获取Clue-index.jsp的创建线索中所有者需要的用户信息。
    private void getUserList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("ClueController-getUserList正在执行");

        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());

        List<User> userList = userService.getUserList();

        PrintJson.printJsonObj(response,userList);

    }

}
