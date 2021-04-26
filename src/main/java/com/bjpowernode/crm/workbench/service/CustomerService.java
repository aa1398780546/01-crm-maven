package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Customer;

import java.util.Map;

/**
 * @author 林哥哥
 */
public interface CustomerService {

    PaginationVO<Customer> customerList(Map<String, Object> map);

}
