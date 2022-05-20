package com.sdut.covid19.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdut.covid19.mapper.AdminMapper;
import com.sdut.covid19.mapper.DepartmentMapper;
import com.sdut.covid19.mapper.PersonMapper;
import com.sdut.covid19.mapper.UserMapper;
import com.sdut.covid19.pojo.Admin;
import com.sdut.covid19.pojo.Department;
import com.sdut.covid19.pojo.Person;
import com.sdut.covid19.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 小王造轮子
 * @description wyp
 * @date 2022/5/15
 */
@Controller
public class UserController2 {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DepartmentMapper departmentMapper;
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private PersonMapper personMapper;

    /**
     * 获取 该部门 今天的入校详情
     * @param department
     * @param currentPage
     * @param size
     * @return
     * @throws ParseException
     */
    @GetMapping("/getCurrentDayList")
    @ResponseBody
    public Map<String,Object> getCurrentDayList(@RequestParam(value = "department",required = true)String department,
                                 @RequestParam(value = "currentPage",defaultValue = "1")Integer currentPage,
                                 @RequestParam(value = "size",defaultValue = "10")Integer size) throws ParseException {
        // 设置日期格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 获取当前时间
        long currentTimestamp = System.currentTimeMillis();
        String currentTime = df.format(currentTimestamp);
        // 获取当天16:00:00的时间，转换成时间戳形式
        String time = currentTime.split(" ")[0] + " 16:00:00";
        long today = df.parse(time).getTime();

        long dayTime = 24 * 60 * 60 * 1000; // 24小时的时间戳
        long yesterday = today - dayTime;   // 昨天16:00:00的时间戳
        long tomorrow = today + dayTime;    // 明天16:00:00的时间戳

        IPage page = new Page(currentPage,size);
        IPage<User> userIPage = null;
        if (currentTimestamp < today){
            userIPage = userMapper.getSubmittedUsersInCurrentDayInOneDep(page,department,yesterday,today);
        }
        else if (currentTimestamp > today){
            userIPage = userMapper.getSubmittedUsersInCurrentDayInOneDep(page,department,today,tomorrow);
        }
        List<User> userList = userIPage.getRecords();
        Map<String,Object> res = new HashMap<>();
        res.put("data",userList);
        res.put("total",userIPage.getTotal());
        return res;
    }

    /**
     * 跳转到 今日审核未通过详情页
     * @param dep
     * @param model
     * @return
     * @throws ParseException
     */
    @GetMapping("/filedToday/{dep}")
    public String toFiledToday(@PathVariable("dep")String dep, Model model) throws ParseException {
        model.addAttribute("adminId",dep);

        QueryWrapper<Department> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("abbreviation",dep);
        Department department = departmentMapper.selectOne(wrapper1);
        model.addAttribute("department",department);

        String nickName = departmentMapper.getNickNameByDepartmentName(dep);
        model.addAttribute("nickName",nickName);

        // 设置日期格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 获取当前时间
        long currentTimestamp = System.currentTimeMillis();
        String currentTime = df.format(currentTimestamp);
        // 获取当天16:00:00的时间，转换成时间戳形式
        String time = currentTime.split(" ")[0] + " 16:00:00";
        long today = df.parse(time).getTime();

        long dayTime = 24 * 60 * 60 * 1000; // 24小时的时间戳
        long yesterday = today - dayTime;   // 昨天16:00:00的时间戳
        long tomorrow = today + dayTime;    // 明天16:00:00的时间戳

        Integer failedCount = 0;
        if (currentTimestamp < today){
            failedCount = userMapper.getFailedCountInCurrentDayInOneDep(dep,yesterday,today);
        }
        else if (currentTimestamp > today){
            failedCount = userMapper.getFailedCountInCurrentDayInOneDep(dep,today ,tomorrow);
        }
        model.addAttribute("failedCount",failedCount);

        return "filedInToday";
    }

    /**
     * 获取 该部门 今日审核未通过的用户
     * @param department
     * @param currentPage
     * @param size
     * @return
     */
    @GetMapping("/getFailedTodayList")
    @ResponseBody
    public Map<String,Object> getFailedTodayList(@RequestParam(value = "department",required = true)String department,
                                                 @RequestParam(value = "currentPage",defaultValue = "1")Integer currentPage,
                                                 @RequestParam(value = "size",defaultValue = "10")Integer size) throws ParseException {
        //设置日期格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 获取当前时间
        long currentTimestamp = System.currentTimeMillis();
        String currentTime = df.format(currentTimestamp);
        // 获取当天16:00:00的时间，转换成时间戳形式
        String time = currentTime.split(" ")[0] + " 16:00:00";
        long today = df.parse(time).getTime();

        long dayTime = 24 * 60 * 60 * 1000; // 24小时的时间戳
        long yesterday = today - dayTime;   // 昨天16:00:00的时间戳
        long tomorrow = today + dayTime;    // 明天16:00:00的时间戳

        IPage page = new Page(currentPage,size);
        IPage userPage = null;
        if (currentTimestamp < today){
            userPage = userMapper.getFailedUsersInCurrentDayInOneDep(page,department,yesterday,today);
        }
        else if (currentTimestamp > today){
            userPage = userMapper.getFailedUsersInCurrentDayInOneDep(page,department,today,tomorrow);
        }
        List<User> userList = userPage.getRecords();
        Map<String,Object> res = new HashMap<>();
        res.put("data",userList);
        res.put("total",userPage.getTotal());
        return res;
    }

    /**
     * 跳转到名单管理页
     * @param dep
     * @param model
     * @return
     */
    @GetMapping("/goListManager/{dep}")
    public String goListManager(@PathVariable("dep")String dep,Model model){
        //获取部门信息
//        model.addAttribute("department",
//                departmentMapper.selectOne(new QueryWrapper<Department>().eq("abbreviation",dep)));
        //获取该部门管理员信息
        Admin admin = adminMapper.selectOne(new QueryWrapper<Admin>().eq("id", dep));
        model.addAttribute("admin", admin);
        model.addAttribute("adminId",admin.getId());
        model.addAttribute("nickName",admin.getNickName());


        return "listManager";
    }

    /**
     * 跳转到 今日未提交页
     * @param dep
     * @param model
     * @return
     */
    @GetMapping("/goNotSubmitInToday/{dep}")
    public String goNotSubmitInToday(@PathVariable("dep")String dep,Model model) throws ParseException {
        //获取部门信息
//        model.addAttribute("department",
//                departmentMapper.selectOne(new QueryWrapper<Department>().eq("abbreviation",dep)));
        //获取该部门管理员信息
        Admin admin = adminMapper.selectOne(new QueryWrapper<Admin>().eq("id", dep));
        model.addAttribute("admin", admin);
        model.addAttribute("adminId",admin.getId());
        model.addAttribute("nickName",admin.getNickName());

        // 设置日期格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 获取当前时间
        long currentTimestamp = System.currentTimeMillis();
        String currentTime = df.format(currentTimestamp);
        // 获取当天16:00:00的时间，转换成时间戳形式
        String time = currentTime.split(" ")[0] + " 16:00:00";
        long today = df.parse(time).getTime();

        long dayTime = 24 * 60 * 60 * 1000; // 24小时的时间戳
        long yesterday = today - dayTime;   // 昨天16:00:00的时间戳
        long tomorrow = today + dayTime;    // 明天16:00:00的时间戳

        //获取 该部门总人数 今日未提交人数，已提交人数，审核未通过人数
        //获取总人数
        Long total = personMapper.selectCount(new QueryWrapper<Person>().eq("department_id",dep));
        model.addAttribute("total",total);
        //已提交人数
        Integer submittedCount = 0;
        //未通过人数
        Integer failedCount = 0;
        if (currentTimestamp < today){
            submittedCount = userMapper.getSubmittedCountInCurrentDayInOneDep(dep,yesterday,today);
            failedCount = userMapper.getFailedCountInCurrentDayInOneDep(dep,yesterday,today);
        }
        else if (currentTimestamp > today){
            submittedCount = userMapper.getSubmittedCountInCurrentDayInOneDep(dep,today,tomorrow);
            failedCount = userMapper.getFailedCountInCurrentDayInOneDep(dep,today,tomorrow);
        }
        model.addAttribute("submittedCount",submittedCount);
        model.addAttribute("failedCount",failedCount);
        //未提交人数
        Integer noSubmittedCount = Math.toIntExact(total - submittedCount);
        model.addAttribute("noSubmittedCount",noSubmittedCount);


        return "notSubmitInToday";
    }

    /**
     * 获取今日未提交的人员名单
     * @return
     */
    @GetMapping("/getNotSubmitPersonInToday")
    @ResponseBody
    public Map<String,Object> getNotSubmitPersonInToday(@RequestParam(value = "department",required = true)String department,
                                                  @RequestParam(value = "currentPage",defaultValue = "1")Integer currentPage,
                                                  @RequestParam(value = "size",defaultValue = "10")Integer size) throws ParseException{

        // 设置日期格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 获取当前时间
        long currentTimestamp = System.currentTimeMillis();
        String currentTime = df.format(currentTimestamp);
        // 获取当天16:00:00的时间，转换成时间戳形式
        String time = currentTime.split(" ")[0] + " 16:00:00";
        long today = df.parse(time).getTime();

        long dayTime = 24 * 60 * 60 * 1000; // 24小时的时间戳
        long yesterday = today - dayTime;   // 昨天16:00:00的时间戳
        long tomorrow = today + dayTime;    // 明天16:00:00的时间戳

        IPage page = new Page(currentPage,size);
        IPage<User> personPage = null;
        if (currentTimestamp < today){
            personPage = personMapper.getNotSubmitInCurrentDayInOneDep(page,department,yesterday,today);
        }
        else if (currentTimestamp > today){
            personPage = personMapper.getNotSubmitInCurrentDayInOneDep(page,department,today,tomorrow);
        }
        List<User> userList = personPage.getRecords();
        Map<String,Object> res = new HashMap<>();
        res.put("data",userList);
        res.put("total",personPage.getTotal());
        return res;
    }

}
