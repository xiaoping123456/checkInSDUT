package com.sdut.covid19.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sdut.covid19.mapper.AdminMapper;
import com.sdut.covid19.pojo.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @Author: badwei
 * @Date: 2022/4/30 17:17
 * @Description:
 */
@EnableWebSecurity // 开启WebSecurity模式
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AdminMapper adminMapper;

    //授权
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 定制请求的授权规则
        // 首页所有人可以访问
        http.authorizeRequests()
                .antMatchers("/super/**").hasRole("super")
                .antMatchers("/super/generateIDCode").permitAll()
                .antMatchers("/list/**").hasAnyRole("super","manager")
                .antMatchers("/goSubmit/**").permitAll()
                .antMatchers("/success").permitAll()
                .antMatchers("/import").permitAll()
                .anyRequest().permitAll();

        //禁用缓存
        http.headers().cacheControl();
        //关闭csrf
        http.csrf().disable();

        // 开启自定义配置的登录功能
        http.formLogin().successHandler(new MyAuthenticationSuccessHandler());


        //开启注销功能,注销成功跳到首页
        http.logout().logoutSuccessUrl("/login");
    }

    //认证
    //密码编码
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
//                .withUser("jsjxy").password(new BCryptPasswordEncoder().encode("123456")).roles("manager");
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }

    /**
     * UserDetailService的实现类，重写了loadUserByUsername()方法
     */
    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        return username -> {
            Admin admin = adminMapper.selectOne(new QueryWrapper<Admin>().eq("username",username));
            if (null!=admin){
                return admin;
            }
            throw new UsernameNotFoundException("用户名或密码不正确");
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}