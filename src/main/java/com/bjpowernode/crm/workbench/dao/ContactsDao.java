package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.Contacts;
import com.bjpowernode.crm.workbench.domain.Customer;

import java.util.List;
import java.util.Map;

public interface ContactsDao {

    int save(Contacts con);

    int getTotalByCondition(Map<String, Object> map);

    List<Contacts> getClueListByCondition(Map<String, Object> map);

}
