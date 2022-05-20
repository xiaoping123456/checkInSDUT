package com.sdut.covid19.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sdut.covid19.mapper.AdminMapper;
import com.sdut.covid19.pojo.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring5.context.SpringContextUtils;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author 小王造轮子
 * @description   登录成功处理器
 * @date 2022/5/19
 */
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        AdminMapper adminMapper = (AdminMapper) SpringContextUtil.getBean("adminMapper");
        Admin admin = adminMapper.selectOne(new QueryWrapper<Admin>().eq("username", username));
        if (admin.getRole().equals("ROLE_super")){
            response.sendRedirect("/super");
        }else if (admin.getRole().equals("ROLE_manager")){
            response.sendRedirect("/list/"+admin.getId());
        }
    }
}
