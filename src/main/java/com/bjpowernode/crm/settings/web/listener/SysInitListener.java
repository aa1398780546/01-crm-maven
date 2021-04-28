package com.bjpowernode.crm.settings.web.listener;

import com.bjpowernode.crm.settings.domain.DicType;
import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.service.DicService;
import com.bjpowernode.crm.settings.service.impl.DicServiceImpl;
import com.bjpowernode.crm.utils.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

/**
 * @author 林哥哥
 */
public class SysInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
//        System.out.println("application正在被调用!");
        System.out.println("服务器缓存处理数据字典开始");

        //获取到全局作用域对象
        ServletContext application = event.getServletContext();

        //创建业务层对象
        DicService ds = (DicService) ServiceFactory.getService(new DicServiceImpl());
        //调用业务层的方法,将七个分好类的List放入到Map中。
        Map<String, List<DicValue>> map = ds.getAll();
        //set保存的都是map集合的所有key值。
        Set<String> set = map.keySet();
        //遍历key值获取到value值，把一对key和value保存的全局作用域中。
        for (String key:set){
            application.setAttribute(key,map.get(key));
            //application.setAttribute("appellation",appellationList);
            //application.setAttribute("clueState",clueStateList);
            //application.setAttribute("returnPriority",returnPriorityList);
            //application.setAttribute("returnState",returnStateList);
            //application.setAttribute("source",sourceList);
            //application.setAttribute("stage",stageList);
            //application.setAttribute("transactionType",transactionTypeList);
        }

        System.out.println("数据字典处理结束！");

        //------------------------------------------------------------------------

        //数据字典处理完毕后，处理Stage2Possibility.properties文件
        /*

            处理Stage2Possibility.properties文件步骤：
                解析该文件，将该属性文件中的键值对关系处理成为java中键值对关系（map）

                Map<String(阶段stage),String(可能性possibility)> pMap = ....
                pMap.put("01资质审查",10);
                pMap.put("02需求分析",25);
                pMap.put("07...",...);

                pMap保存值之后，放在服务器缓存中
                application.setAttribute("pMap",pMap);

         */

        //解析properties文件

        Map<String,String> pMap = new HashMap<String,String>();

        ResourceBundle rb = ResourceBundle.getBundle("Stage2Possibility");

        Enumeration<String> e = rb.getKeys();

        while (e.hasMoreElements()){

            //阶段
            String key = e.nextElement();
            //可能性
            String value = rb.getString(key);

            pMap.put(key, value);

        }

        //将pMap保存到服务器缓存中
        application.setAttribute("pMap", pMap);


    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("application正在被销毁！");
    }

}
