package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

/**
 * @author 林哥哥
 */
public interface TranService {

    PaginationVO<Tran> tranList(Map<String,Object> map);

}
