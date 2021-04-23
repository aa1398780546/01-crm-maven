package com.bjpowernode.crm.settings.web.listener;

import com.bjpowernode.crm.settings.domain.DicType;
import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.service.DicService;
import com.bjpowernode.crm.settings.service.impl.DicServiceImpl;
import com.bjpowernode.crm.utils.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        //调用业务层的方法
        Map<String, List<DicValue>> map = ds.getAll();
        //将map解析为全局作用域对象中保存的键值对。
        Set<String> set = map.keySet();
        for (String key:set){
            application.setAttribute(key,map.get(key));
        }

        System.out.println("数据字典处理结束！");


    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("application正在被销毁！");
    }

}
