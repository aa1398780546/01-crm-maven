package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;

import java.util.List;
import java.util.Map;

/**
 * @author 林哥哥
 */
public interface UserTranService {
    PaginationVO<Tran> pageListById(Map<String, Object> map);

    List<TranHistory> getHistoryByTranId(String tranId);
}
