package com.itheima.reggie.filter;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.imp.EmployeeServiceImp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")   //urlPatterns拦截哪些url
@Slf4j
public class LoginCheckFilter implements Filter {

    //路径匹配器,支持通配符
    public static final AntPathMatcher PATH_MATCHER= new AntPathMatcher();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
//        Employee employee = (Employee) request.getSession().getAttribute("employee");
        String requestURI = request.getRequestURI();   //  /backend/index.html
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",  //发送短信放行
                "/user/login"  //登录放行
        };
        boolean check = check(urls,requestURI);
        //如果需要放行
        if (check){
            filterChain.doFilter(request,response);
            return;
        }


        //如果已经登录,放行
        if (request.getSession().getAttribute("employee") != null){
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }
        //移动端登录校验
        if (request.getSession().getAttribute("user") != null){
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }


        //未登录
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }

    @Override
    public void destroy() {

    }

    /**
     *检查本次请求是否需要放行
     * @param requestURI
     * @param urls
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
