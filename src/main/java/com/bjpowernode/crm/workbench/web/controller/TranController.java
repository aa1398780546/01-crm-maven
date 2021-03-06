package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.*;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.ClueService;
import com.bjpowernode.crm.workbench.service.CustomerService;
import com.bjpowernode.crm.workbench.service.TranService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.ClueServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.CustomerServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.TranServiceImpl;

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
public class TranController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到交易控制器");

        String path = request.getServletPath();

        if ("/workbench/transaction/tranList.do".equals(path)) {
            tranList(request,response);
        }else if ("/workbench/transaction/getUserList.do".equals(path)) {
            getUserList(request,response);
        }else if ("/workbench/transaction/getCustomerName.do".equals(path)) {
            getCustomerName(request,response);
        }else if ("/workbench/transaction/detail.do".equals(path)) {
            detail(request,response);
        }else if ("/workbench/transaction/save.do".equals(path)) {
            save(request,response);
        }else if ("/workbench/transaction/getHistoryByTranId.do".equals(path)) {
            getHistoryByTranId(request,response);
        }else if ("/workbench/transaction/changeStage.do".equals(path)) {
            changeStage(request,response);
        }else if ("/workbench/transaction/getCharts.do".equals(path)) {
            getCharts(request,response);
        }else if ("/workbench/transaction/delete.do".equals(path)) {
            delete(request,response);
        }


    }

    private void delete(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行交易列表的删除操作");

        String ids[] = request.getParameterValues("id");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        boolean flag = ts.delete(ids);

        PrintJson.printJsonFlag(response, flag);
    }

    //Echarts统计图
    private void getCharts(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("取得交易阶段数量统计图表的数据");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        /*

            业务层为我们返回
                total
                dataList

                通过map打包以上两项进行返回


         */
        Map<String,Object> map = ts.getCharts();

        PrintJson.printJsonObj(response, map);
    }

    //点击图标后修改数据库中的state等信息，同时新建一个交易历史表。
    private void changeStage(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行改变阶段的操作");

        String id = request.getParameter("id");
        String stage = request.getParameter("stage");
        System.out.println("stage========="+stage);
        String money = request.getParameter("money");
        String expectedDate = request.getParameter("expectedDate");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User)request.getSession().getAttribute("user")).getName();

        Tran t = new Tran();
        t.setId(id);
        t.setStage(stage);
        t.setMoney(money);
        t.setExpectedDate(expectedDate);
        t.setEditBy(editBy);
        t.setEditTime(editTime);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        boolean flag = ts.changeStage(t);

        Map<String,String> pMap = (Map<String,String>)this.getServletContext().getAttribute("pMap");
        t.setPossibility(pMap.get(stage));

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("success", flag);
        map.put("t", t);

        PrintJson.printJsonObj(response, map);
    }

    //根据tranId获取历史阶段信息列表
    private void getHistoryByTranId(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("根据tranId获取历史阶段信息列表");

        String tranId = request.getParameter("tranId");

        System.out.println("tranId=========="+tranId);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        List<TranHistory> historyList = ts.getHistoryByTranId(tranId);

        for(TranHistory t : historyList){

            //获得阶段stage
            String stage = t.getStage();
            //获得在监听器中放入application的Map
            Map<String,String> pMap = (Map<String,String>)this.getServletContext().getAttribute("pMap");
            //获得可能性
            String possibility = pMap.get(stage);
            //将可能性放入到每一个History中
            t.setPossibility(possibility);
        }

        PrintJson.printJsonObj(response,historyList);

    }

    //接收表单传输过来的参数，重定向回到index页面
    private void save(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("接收表单传输过来的参数，重定向回到index页面");

        String id = UUIDUtil.getUUID();
        String clueId = UUIDUtil.generateShortUuid();
        String owner = request.getParameter("owner");
        String tranName = request.getParameter("tranName");
        String money = request.getParameter("money");
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String fullname = request.getParameter("fullname");
        String appellation = "先生";
        String email = request.getParameter("email");
        String mphone = request.getParameter("mphone");
        String address = request.getParameter("address");
        String Rfullname = request.getParameter("Rfullname");
        String Rappellation = "女士";
        String Rmphone = request.getParameter("Rmphone");
        String Raddress = request.getParameter("Raddress");
        String expectedDate = request.getParameter("expectedDate");
        String receivedDate = request.getParameter("receivedDate");
        String editBy = request.getParameter("editBy");
        String editTime = request.getParameter("editTime");
        String description = request.getParameter("description");
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();

        System.out.println("=============1.创建交易订单表START========================");
        Tran t = new Tran();
        t.setId(clueId);
        t.setClueId(clueId);
        t.setOwner(owner);
        t.setTranName(tranName);
        t.setMoney(money);
        t.setStage(stage);
        t.setType(type);
        t.setFullname(fullname);
        t.setAppellation(appellation);
        t.setEmail(email);
        t.setMphone(mphone);
        t.setAddress(address);
        t.setRfullname(Rfullname);
        t.setRappellation(Rappellation);
        t.setRmphone(Rmphone);
        t.setRaddress(Raddress);
        t.setExpectedDate(expectedDate);
        t.setReceivedDate(receivedDate);
        t.setEditBy(editBy);
        t.setEditTime(editTime);
        t.setDescription(description);
        t.setCreateBy(createBy);
        t.setCreateTime(createTime);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean flag = ts.save(t);
        System.out.println("===========1.创建交易订单表END=============================");


        System.out.println("===========2.1创建联系人表START============================");
        //phone用来当作客户的初始密码123456
        String phone = "123456";
        Clue c = new Clue();
        c.setId(clueId);
        c.setFullname(fullname);
        c.setAppellation(appellation);
        c.setOwner(owner);
        c.setEmail(email);
        c.setPhone(phone);
        c.setMphone(mphone);
        c.setAddress(address);
        c.setCreateTime(createTime);
        c.setCreateBy(createBy);

        ClueService clueService = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        Boolean clueFlag = clueService.save(c);

        System.out.println("==============2.2添加普通客户表User信息=====================");
        String loginAct = clueId;
        String loginPwd = phone;
        String name = fullname;
        String expireTime ="2022-04-18 21:50:05";
        String lockState ="0";
        String allowIps ="192.168.1.1,192.168.1.2,127.0.0.1";
        loginPwd = MD5Util.getMD5(loginPwd);

        User user = new User();
        user.setId(clueId);
        user.setLoginAct(loginAct);
        user.setName(name);
        user.setLoginPwd(loginPwd);
        user.setEmail(email);
        user.setExpireTime(expireTime);
        user.setLockState(lockState);
        user.setAllowIps(allowIps);
        user.setCreateTime(createTime);
        user.setCreateBy(createBy);

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        Integer num = us.save(user);
        System.out.println("==============2.2添加普通客户表User信息结束==================");

        System.out.println("===========2.创建联系人表END===============================");


        System.out.println("===========3.创建货物表START==============================");

        Activity a = new Activity();
        a.setId(clueId);
        a.setOwner(owner);
        a.setName(tranName);
        a.setStartDate(expectedDate);
        a.setEndDate(receivedDate);
        a.setCost(money);
        a.setDescription(description);
        a.setCreateTime(createTime);
        a.setCreateBy(createBy);
        a.setClueId(clueId);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        Boolean ActivityFlag = as.save(a);
        System.out.println("===========3.创建货物表END================================");

        if(flag){
            //如果添加交易成功，跳转到列表页
            response.sendRedirect(request.getContextPath() + "/workbench/transaction/index.jsp");
        }

    }

    //根据id查询跳转到详细信息页所需要展示的内容。
    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

        System.out.println("根据id查询所有信息");

        String id = request.getParameter("id");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        Tran tran = ts.detail(id);

        String stage = tran.getStage();
        //获得在监听器中放入application的Map
        Map<String,String> pMap = (Map<String,String>)this.getServletContext().getAttribute("pMap");
        //获得可能性
        String possibility = pMap.get(stage);

        //直接在实体类中加多一个
        tran.setPossibility(possibility);
//
        request.setAttribute("t",tran);
        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request,response);

    }

    //根据用户输入的name来模糊查询name的完整名称。
    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("取得 客户名称列表（按照客户名称进行模糊查询）");

        String name = request.getParameter("name");

        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());

        List<String> sList = cs.getCustomerName(name);

        PrintJson.printJsonObj(response, sList);

    }

    //获得所有用户列表，为创建交易的所有者插入数据。
    private void getUserList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());

        List<User> userList = userService.getUserList();

        request.setAttribute("userList",userList);

        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request,response);

    }

    //获取所有的交易列表
    private void tranList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到查询交易列表的操作（结合条件查询+分页查询）");

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

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        PaginationVO<Tran> vo = ts.tranList(map);

        PrintJson.printJsonObj(response,vo);

    }


}
