package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.dao.ContactsDao;
import com.bjpowernode.crm.workbench.domain.Contacts;
import com.bjpowernode.crm.workbench.domain.Customer;
import com.bjpowernode.crm.workbench.service.ContactsService;

import java.util.List;
import java.util.Map;

/**
 * @author 林哥哥
 */
public class ContactsServiceImpl implements ContactsService {

    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);


    @Override
    public PaginationVO<Contacts> contactList(Map<String, Object> map) {

        System.out.println("进入到查询客户列表的操作ContactsServiceImpl-contactsList");

        int total = contactsDao.getTotalByCondition(map);
        List<Contacts> dataList = contactsDao.getClueListByCondition(map);

        PaginationVO<Contacts> vo = new PaginationVO<Contacts>() ;
        vo.setDataList(dataList);
        vo.setTotal(total);

        return vo;

    }

}
