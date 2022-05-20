package com.sdut.covid19.pojo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sdut.covid19.config.CustomAuthorityDeserializer;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author 小王造轮子
 * @description 管理员
 * @date 2022/5/15
 */
@Data
public class Admin implements Serializable,UserDetails {

    /**
     * 唯一标识码
     */
    private String id;
    /**
     * 归属单位名称
     */
    private String departmentName;
    /**
     * 单位管理员姓名
     */
    private String nickName;
    /**
     * 登录用户名
     */
    private String username;
    /**
     * 登录密码
     */
    private String password;
    /**
     * 手机号码
     */
    private String phone;
    /**
     * 角色  manager,super
     */
    private String role;


    @Override
    @JsonDeserialize(using = CustomAuthorityDeserializer.class) //反序列化
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
