package com.sdut.covid19.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdut.covid19.mapper.AdminMapper;
import com.sdut.covid19.mapper.PersonMapper;
import com.sdut.covid19.mapper.UserMapper;
import com.sdut.covid19.pojo.Admin;
import com.sdut.covid19.pojo.Person;
import com.sdut.covid19.pojo.User;
import com.sdut.covid19.utils.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 小王造轮子
 * @description 统计部分的controller
 * @date 2022/5/21
 */
@Controller
public class CountController {

    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PersonMapper personMapper;

    /**
     * 跳转到提交数据统计页
     *
     * @param dep
     * @param model
     * @return
     */
    @RequestMapping("/toSubmitDataCount/{dep}")
    public String toSubmitDataCount(@PathVariable("dep") String dep, Model model) {
        //获取该部门管理员信息
        Admin admin = adminMapper.selectOne(new QueryWrapper<Admin>().eq("id", dep));
        model.addAttribute("admin", admin);
        model.addAttribute("adminId", admin.getId());
        model.addAttribute("nickName", admin.getNickName());

        return "submitDataCount";
    }

    /**
     * 跳转到提交 到达和途径页
     *
     * @param dep
     * @param model
     * @return
     */
    @RequestMapping("/toWayData/{dep}")
    public String toWayData(@PathVariable("dep") String dep, Model model) {
        //获取该部门管理员信息
        Admin admin = adminMapper.selectOne(new QueryWrapper<Admin>().eq("id", dep));
        model.addAttribute("admin", admin);
        model.addAttribute("adminId", admin.getId());
        model.addAttribute("nickName", admin.getNickName());

        return "wayData";
    }

    /**
     * 获取最近21天的提交数据
     *
     * @param dep
     * @param currentPage
     * @param size
     * @return
     */
    @GetMapping("/get21DaysData")
    @ResponseBody
    public Map get21DaysData(@RequestParam("dep") String dep,
                             @RequestParam("currentPage") Integer currentPage,
                             @RequestParam("size") Integer size) {
        //获取当前日期和21天前的日期  以下午4点为每天的分隔线
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Long end = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_YEAR, -21);
        Long begin = calendar.getTimeInMillis();
        //获取该部门21天内的所有提交数据
        List<User> userList = userMapper.get21DaysData(dep, begin, end);

        //获取该部门所有成员
        IPage<Person> page = new Page<>(currentPage, size);
        IPage<Person> personIPage = personMapper.selectPage(page, new QueryWrapper<Person>().eq("department_id", dep));
        List<Person> personList = personIPage.getRecords();


        JSONObject res = new JSONObject();
        res.put("total", personIPage.getTotal());
        List list = new ArrayList();

        for (Person person : personList) {
            Map<String, Object> per = new HashMap<>();
            per.put("name", person.getName());
            String[] arr = new String[22];
            for (int i = 0; i < 22; i++) {
                arr[i] = " ";
            }
            Calendar ca = Calendar.getInstance();
            ca.add(Calendar.DAY_OF_MONTH, 1);
            ca.set(Calendar.HOUR_OF_DAY, 16);
            ca.set(Calendar.MINUTE, 0);
            ca.set(Calendar.SECOND, 0);
            Long endMs = ca.getTimeInMillis();
            for (int i = 21; i >= 1; i--) {
                for (User user : userList) {
                    if (person.getName().equals(user.getName())) {
                        if (user.getTimestamp() >= (endMs - 1000 * 60 * 60 * 24) && user.getTimestamp() <= endMs) {
                            arr[i] = "√";
                        }
                    }
                }
                endMs = endMs - 1000 * 60 * 60 * 24;
            }
            per.put("arr", arr);
            list.add(per);
        }
        res.put("data", list);

        //获取21天的时间列表
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd");
        List<String> dayList = new ArrayList<>();
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.DAY_OF_MONTH, -21);
        for (int i = 21; i >= 1; i--) {
            ca.add(Calendar.DAY_OF_MONTH, 1);
            Long endMs = ca.getTimeInMillis();
            dayList.add(simpleDateFormat.format(new Date(endMs)));
        }
        res.put("dayList", dayList);
        return res;
    }

    /**
     * 获取该部门最近21天的 途径地信息
     * 【到达和途径页 表格需要的信息】
     *
     * @param dep
     * @param currentPage
     * @param size
     * @return
     */
    @GetMapping("/get21DaysWayData")
    @ResponseBody
    public Map get21DaysWayData(@RequestParam("dep") String dep,
                                @RequestParam("currentPage") Integer currentPage,
                                @RequestParam("size") Integer size) {
        //获取当前日期和21天前的日期  以下午4点为每天的分隔线
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Long end = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_YEAR, -21);
        Long begin = calendar.getTimeInMillis();
        //获取该部门21天内的所有提交数据
        List<User> userList = userMapper.get21DaysData2(dep, begin, end);


        //获取该部门所有成员
        IPage<Person> page = new Page<>(currentPage, size);
        IPage<Person> personIPage = personMapper.selectPage(page, new QueryWrapper<Person>().eq("department_id", dep));
        List<Person> personList = personIPage.getRecords();

        JSONObject res = new JSONObject();
        res.put("total", personIPage.getTotal());

        List<Object> data = new ArrayList<>();
        //获取某个人的 最近21天途径地
        for (Person person : personList) {
            Map<String, Object> map = new HashMap<>();
            Set<String> waySet = new HashSet<>();
            List<String> imgUrlList = new ArrayList<>();
            for (User user : userList) {
                if (person.getName().equals(user.getName())) {
                    //添加途径地
                    if (!user.getItineraryInfo().contains("市")&&!user.getItineraryInfo().contains("区")){
                        continue;
                    }
                    List<String> ways = UserUtil.formatItineraryInfo(user.getItineraryInfo());
                    ways.forEach(s -> {
                        waySet.add(s);
                    });
                    //添加行程码url
                    imgUrlList.add(UserUtil.pathToUrl(user.getItineraryImgPath()));
                }
            }
            String allWay = String.join(",",waySet);
            map.put("name", person.getName());
            map.put("id",person.getId());
            map.put("ways", allWay);
            map.put("imgUrls", imgUrlList);
            data.add(map);
        }
        res.put("data", data);

        //获取最近21天起止日期
        SimpleDateFormat sdf = new SimpleDateFormat("MM.dd");
        Calendar ca = Calendar.getInstance();
        String endDay = sdf.format(new Date(ca.getTimeInMillis()));
        ca.add(Calendar.DAY_OF_MONTH, -20);
        String beginDay = sdf.format(new Date(ca.getTimeInMillis()));
        List<String> dayList = new ArrayList<>();
        dayList.add(beginDay);
        dayList.add(endDay);
        res.put("dayList", dayList);

        return res;
    }


}
