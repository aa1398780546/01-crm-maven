package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.dao.CustomerDao;
import com.bjpowernode.crm.workbench.domain.Customer;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.service.CustomerService;

import java.util.List;
import java.util.Map;

/**
 * @author 林哥哥
 */
public class CustomerServiceImpl implements CustomerService {

    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);


    @Override
    public PaginationVO<Customer> customerList(Map<String, Object> map) {

        System.out.println("进入到查询客户列表的操作CustomerServiceImpl-customerList");

        int total = customerDao.getTotalByCondition(map);
        List<Customer> dataList = customerDao.getClueListByCondition(map);

        PaginationVO<Customer> vo = new PaginationVO<Customer>() ;
        vo.setDataList(dataList);
        vo.setTotal(total);

        return vo;
    }


}
