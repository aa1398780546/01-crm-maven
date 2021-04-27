package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Contacts;
import com.bjpowernode.crm.workbench.domain.Customer;

import java.util.Map;

/**
 * @author 林哥哥
 */
public interface ContactsService {

    PaginationVO<Contacts> contactList(Map<String, Object> map);

}
