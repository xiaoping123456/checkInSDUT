package com.sdut.covid19.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdut.covid19.mapper.AdminMapper;
import com.sdut.covid19.mapper.DepartmentMapper;
import com.sdut.covid19.pojo.Admin;
import com.sdut.covid19.pojo.Department;
import com.sdut.covid19.utils.UsualUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 小王造轮子
 * @description 管理员controller
 * @date 2022/5/15
 */
@Controller
public class AdminController {

    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private UsualUtils usualUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DepartmentMapper departmentMapper;

    @GetMapping("/super")
    public String toSuper(){
        return "super";
    }

    @GetMapping("/super/add")
    public String toAddAdmin(){
        return "addAdmin";
    }

    @GetMapping("/super/generateIDCode")
    @ResponseBody
    public String generateIDCode(){
        //生成6位随机验证码
        String code = usualUtils.generateIDCode();
        return code;
    }

    /**
     * 添加管理员
     * 添加后跳转到该
     * @param admin
     * @return
     */
    @PostMapping("/super/addAdmin")
    public String addAdmin(Admin admin){
        if (adminMapper.selectOne(new QueryWrapper<Admin>().eq("username",admin.getUsername()))!=null){
            return "该登录用户名已存在";
        }

        admin.setRole("ROLE_manager");
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        if (adminMapper.insert(admin)!=0){
            //添加部门 department表
            Department department = new Department();
            department.setAbbreviation(admin.getId());
            department.setDepartName(admin.getDepartmentName());
            departmentMapper.insert(department);
            return "redirect:/goSubmit/"+department.getAbbreviation();
        }else{
            return "添加失败";
        }
    }

    @GetMapping("/getAdmins")
    @ResponseBody
    public Map<String,Object> getAdmins(@RequestParam(value = "currentPage",defaultValue = "1")Integer currentPage,
                                        @RequestParam(value = "size",defaultValue = "10")Integer size){
        IPage<Admin> page = new Page<>(currentPage,size);
        IPage<Admin> adminIPage = adminMapper.selectPage(page, new QueryWrapper<Admin>().ne("role","ROLE_super"));
        Map<String,Object> res = new HashMap<>();
        res.put("data",adminIPage.getRecords());
        res.put("total",adminIPage.getTotal());
        return res;
    }

    @DeleteMapping("/super/deleteAdmin")
    @ResponseBody
    public boolean deleteAdmin(@RequestParam("id")String id){
        if (adminMapper.deleteById(id)!=0){
            return true;
        }else{
            return false;
        }
    }

    @PostMapping("/super/updateAdmin")
    @ResponseBody
    public boolean updateAdmin(@RequestBody Admin admin){
        System.out.println(admin);
        if(adminMapper.updateById(admin)!=0){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 重置某个admin的密码
     * @return
     */
    @PostMapping("/super/resetPassword")
    @ResponseBody
    public boolean resetPassword(@RequestBody String json){
        JSONObject params = JSONObject.parseObject(json);
        String id = params.getString("id");
        String newPassword = params.getString("newPassword");
        Admin admin = new Admin();
        admin.setId(id);
        admin.setPassword(passwordEncoder.encode(newPassword));
        if (adminMapper.updateById(admin)!=0){
            return true;
        }
        return false;
    }


}
