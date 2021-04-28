package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;

import java.util.List;
import java.util.Map;

/**
 * @author 林哥哥
 */
public interface TranService {

    PaginationVO<Tran> tranList(Map<String,Object> map);

    Tran detail(String id);

    boolean save(Tran t, String customerName);

    List<TranHistory> getHistoryByTranId(String tranId);

    boolean changeStage(Tran t);

    Map<String, Object> getCharts();
}
