package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.dao.CustomerDao;
import com.bjpowernode.crm.workbench.dao.TranDao;
import com.bjpowernode.crm.workbench.dao.TranHistoryDao;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Customer;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.TranService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 林哥哥
 */
public class TranServiceImpl implements TranService {

    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    @Override
    public PaginationVO<Tran> tranList(Map<String,Object> map) {

        System.out.println("进入到查询线索信息列表的操作TranServiceImpl-tranList");

        int total = tranDao.getTotalByCondition(map);
        List<Tran> dataList = tranDao.getClueListByCondition(map);

        PaginationVO<Tran> vo = new PaginationVO<Tran>() ;
        vo.setDataList(dataList);
        vo.setTotal(total);

        return vo;
    }

    @Override
    public Tran detail(String id) {

        System.out.println("TranServiceImpl-----detail------");

        Tran tran = tranDao.detail(id);

        return tran;
    }

    @Override
    public boolean save(Tran t) {

        boolean flag = true;

        //添加交易
        int count2 = tranDao.save(t);
        if(count2!=1){
            flag = false;
        }

        //添加交易历史
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setTranId(t.getId());
        th.setStage(t.getStage());
        th.setMoney(t.getMoney());
        th.setExpectedDate(t.getExpectedDate());
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setCreateBy(t.getCreateBy());
        int count3 = tranHistoryDao.save(th);
        if(count3!=1){
            flag = false;
        }

        return flag;

    }

    @Override
    public List<TranHistory> getHistoryByTranId(String tranId) {

        List<TranHistory> historyList = tranHistoryDao.getHistoryByTranId(tranId);

        return historyList;

    }

    @Override
    public boolean changeStage(Tran t) {

        boolean flag = true;
        //改变交易阶段
        int count1 = tranDao.changeStage(t);
        if(count1!=1){
            flag = false;
        }
        //交易阶段改变后，生成一条交易历史
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setCreateBy(t.getEditBy());
        th.setStage(t.getStage());
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setExpectedDate(t.getExpectedDate());
        th.setMoney(t.getMoney());
        th.setTranId(t.getId());
        //添加交易历史
        int count2 = tranHistoryDao.save(th);
        if(count2!=1){
            flag = false;
        }
        return flag;
    }

    @Override
    public Map<String, Object> getCharts() {

        //取得total
        int total = tranDao.getTotal();

        //取得dataList
        List<Map<String,Object>> dataList = tranDao.getCharts();

        //将total和dataList保存到map中
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("total", total);
        map.put("dataList", dataList);

        //返回map
        return map;
    }

    @Override
    public boolean delete(String[] ids) {

        boolean flag = true;

        //删除市场活动
        int count3 = tranHistoryDao.delete(ids);
        if(count3!=ids.length){

            flag = false;

        }

        return flag;
    }
}
