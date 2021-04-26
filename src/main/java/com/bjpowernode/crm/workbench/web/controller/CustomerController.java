package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Customer;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.service.CustomerService;
import com.bjpowernode.crm.workbench.service.TranService;
import com.bjpowernode.crm.workbench.service.impl.CustomerServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.TranServiceImpl;

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
public class CustomerController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到客户控制器");

        String path = request.getServletPath();

        if ("/workbench/customer/customerList.do".equals(path)) {
            customerList(request,response);
        } else if ("/workbench/customer/xx.do".equals(path)) {

        }

    }

    private void customerList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到查询顾客列表的操作（结合条件查询+分页查询）");

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

        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());

        PaginationVO<Customer> vo = cs.customerList(map);

        PrintJson.printJsonObj(response,vo);

    }


}
