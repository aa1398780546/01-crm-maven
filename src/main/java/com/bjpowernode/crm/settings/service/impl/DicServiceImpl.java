package com.bjpowernode.crm.settings.service.impl;

import com.bjpowernode.crm.settings.dao.DicTypeDao;
import com.bjpowernode.crm.settings.dao.DicValueDao;
import com.bjpowernode.crm.settings.domain.DicType;
import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.service.DicService;
import com.bjpowernode.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author 北京动力节点
 */
public class DicServiceImpl implements DicService {

    private DicTypeDao dicTypeDao = SqlSessionUtil.getSqlSession().getMapper(DicTypeDao.class);
    private DicValueDao dicValueDao = SqlSessionUtil.getSqlSession().getMapper(DicValueDao.class);

    //获取所有分好类的TypeList
    public Map<String, List<DicValue>> getAll() {

        Map<String,List<DicValue>> map = new HashMap<String,List<DicValue>>();

        //调用DicDao接口的方法，要求返回所有的Code     select * from tbl_dic_type
        List<DicType> typeList = dicTypeDao.getTypeList();

        //通过遍历Code来获取所有的value
        for(DicType ty : typeList){

            //取得每一个类型的字典类型编码
            String code = ty.getCode();

            //根据每一个字典类型来取得字典值列表 select * from tbl_dic_value where typeCode=#{code} order by orderNo
            List<DicValue> valueList = dicValueDao.getListByCode(code);

            map.put(code+"List",valueList);
        }

        return map;

    }

}




















