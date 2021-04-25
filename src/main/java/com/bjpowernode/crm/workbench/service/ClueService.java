package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;

import java.util.List;
import java.util.Map;

/**
 * @author 林哥哥
 */
public interface ClueService {
    Boolean save(Clue c);

    Clue detail(String id);

    PaginationVO<Clue> pageList(Map<String, Object> map);

}
