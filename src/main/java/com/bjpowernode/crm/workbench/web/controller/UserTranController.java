package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.service.TranService;
import com.bjpowernode.crm.workbench.service.UserActivityService;
import com.bjpowernode.crm.workbench.service.UserTranService;
import com.bjpowernode.crm.workbench.service.impl.TranServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.UserActivityServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.UserTranServiceImpl;

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
public class UserTranController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String path = request.getServletPath();

        if("/workbench/transaction/getTranListById.do".equals(path)){
            getUserListById(request,response);
        }

    }

    private void getUserListById(HttpServletRequest request, HttpServletResponse response) {

        String id = request.getParameter("id");
        System.out.println("ID-===============:"+ id);

        //页码,计算略过的记录数，但是数据库不需要这个
        Integer pageNo = Integer.valueOf(request.getParameter("pageNo"));
        //每页展现的记录数，计算略过的记录数，数据库需要这个
        Integer pageSize = Integer.valueOf(request.getParameter("pageSize"));
        //计算略过的记录数
        int skipCount = (pageNo-1)*pageSize;

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);
        map.put("id",id);

        UserTranService uts = (UserTranService) ServiceFactory.getService(new UserTranServiceImpl());

        PaginationVO<Tran> vo = uts.pageListById(map);
        PrintJson.printJsonObj(response,vo);

    }

}
