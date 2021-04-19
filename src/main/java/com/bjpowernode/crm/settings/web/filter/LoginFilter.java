package com.bjpowernode.crm.settings.web.filter;

import com.bjpowernode.crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author 林哥哥
 */
public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

        System.out.println("检测是否恶意登录LoginFilter");

        //如何进行验证？
        //如果用户登陆过网页，那么Session中将会有一个user对象
        //只要我们尝试去获取Session中的对象，如果有则进行过登录，如果没有就重定向登陆页面。
        //以前获取Session的方法，request.getSession().getAttribute();
        //但是以前的request是HttpServletRequest出来的，现在的是ServletRequest没有getSession方法。

        //使用强制类型转换 由 大-->小
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String path = request.getServletPath();

        //不应该被拦截的资源，自动放行请求
        if ("/login.jsp".equals(path) || "/settings/user/login.do".equals(path)){

            chain.doFilter(req,resp);

        }else {

            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            //如果user不为空，说明用户登陆过
            if (user!=null){
                chain.doFilter(req,resp);
                //没有登陆过
            }else {
            /*
               1.重定向的地址怎么写？
                    在实际的开发中，对于路劲的使用，无论操作的是前端还是后端，应该一律使用绝对路径。
                    关于转发和重定向的路径写法如下：
                       转发：
                            使用的是一种特殊的使用方式，这种绝对路径前面不加/项目名，这种绝对路劲也称之为“内部路径”
                            /login.jsp
                       重定向：
                            使用的是传统绝对路径的写法，前面必须以/项目名开头，后面跟具体的资源路径。
                            /crm/login.jsp

               2.为什么使用重定向，使用请求转发不行吗？
                    转发之后，路径会停留在老路径上，而不是跳转之后最新的资源路径
                    我们应该在为用户跳转到登陆页面的同时，将浏览器的地址栏自动设置为当前登录页的路径。
             */
            response.sendRedirect(request.getContextPath() + "/login.jsp");
//            response.sendRedirect("/crm/login.jsp");
//                response.sendRedirect("login.jsp");
            }

        }

    }

}
