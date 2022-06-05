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
import com.sdut.covid19.pojo.dto.City;
import com.sdut.covid19.pojo.dto.LineStyle;
import com.sdut.covid19.pojo.dto.Way;
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
    @Autowired
    private UserUtil userUtil;

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
     * 获取最近14天的提交数据
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
        //获取当前日期和14天前的日期  以下午4点为每天的分隔线
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Long end = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_YEAR, -14);
        Long begin = calendar.getTimeInMillis();
        //获取该部门14天内的所有提交数据
        List<User> userList = userMapper.get21DaysData(dep, begin, end);

        //获取该部门所有成员
        IPage<Person> page = new Page<>(currentPage, size);
        IPage<Person> personIPage = personMapper.selectPage(page, new QueryWrapper<Person>().eq("department_id", dep));
        List<Person> personList = personIPage.getRecords();


        JSONObject res = new JSONObject();
        res.put("total", personIPage.getTotal());
        List list = new ArrayList();

        for (Person person : personList) {

            //per记录 该成员们的姓名和该成员14天内的提交情况
            Map<String, Object> per = new HashMap<>();
            per.put("name", person.getName());
            //arr记录该成员14天内的提交情况
            String[] arr = new String[15];
            for (int i = 0; i < 15; i++) {
                arr[i] = " ";
            }
            Calendar ca = Calendar.getInstance();
            ca.add(Calendar.DAY_OF_MONTH, 1);
            ca.set(Calendar.HOUR_OF_DAY, 16);
            ca.set(Calendar.MINUTE, 0);
            ca.set(Calendar.SECOND, 0);
            Long endMs = ca.getTimeInMillis();
            for (int i = 14; i >= 1; i--) {
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

        //获取14天的时间列表
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd");
        List<String> dayList = new ArrayList<>();
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.DAY_OF_MONTH, -14);
        for (int i = 14; i >= 1; i--) {
            ca.add(Calendar.DAY_OF_MONTH, 1);
            Long endMs = ca.getTimeInMillis();
            dayList.add(simpleDateFormat.format(new Date(endMs)));
        }
        res.put("dayList", dayList);
        return res;
    }

    /**
     * 获取该部门最近14天 提交数据页 散点图的信息
     */
    @GetMapping("/getScatterDataInSubmitted")
    @ResponseBody
    public JSONObject getScatterDataInSubmitted(@RequestParam("dep") String dep) {
        JSONObject res = new JSONObject();

        //获取当前日期和14天前的日期  以下午4点为每天的分隔线
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Long end = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_YEAR, -14);
        Long begin = calendar.getTimeInMillis();
        //获取该部门14天内的所有提交数据
        List<User> userList = userMapper.get21DaysData(dep, begin, end);

        //获取该部门的所有成员
        List<Person> personList = personMapper.selectList(new QueryWrapper<Person>().eq("department_id", dep));
        //存储每个成员的名字
        List<String> nameList = new ArrayList<>();
        //存储每个成员与日期的 提交对应关系
        List<List<String>> submitList = new ArrayList<>();
        for (Person person : personList) {
            nameList.add(person.getName());

            Calendar ca = Calendar.getInstance();
            ca.add(Calendar.DAY_OF_MONTH, 1);
            ca.set(Calendar.HOUR_OF_DAY, 16);
            ca.set(Calendar.MINUTE, 0);
            ca.set(Calendar.SECOND, 0);
            Long endMs = ca.getTimeInMillis();
            for (int i = 14; i >= 1; i--) {
                List<String> list = new ArrayList<>();
                for (User user : userList) {
                    if (person.getName().equals(user.getName())) {
                        if (user.getTimestamp() >= (endMs - 1000 * 60 * 60 * 24) && user.getTimestamp() <= endMs) {
                            list.add(person.getName());
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd");
                            String currentDay = simpleDateFormat.format(new Date(endMs - 1000 * 60 * 60 * 24));
                            list.add(currentDay);
                            continue;
                        }
                    }
                }
                endMs = endMs - 1000 * 60 * 60 * 24;
                if (list.size() > 0) {
                    submitList.add(list);
                }
            }
        }
        res.put("submitList", submitList);

        //获取14天的时间列表
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd");
        List<String> dayList = new ArrayList<>();
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.DAY_OF_MONTH, -14);
        for (int i = 14; i >= 1; i--) {
            ca.add(Calendar.DAY_OF_MONTH, 1);
            Long endMs = ca.getTimeInMillis();
            dayList.add(simpleDateFormat.format(new Date(endMs)));
        }
        res.put("dayList", dayList);

        res.put("nameList", nameList);
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
                    if (!user.getItineraryInfo().contains("市") && !user.getItineraryInfo().contains("区")) {
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
            String allWay = String.join(",", waySet);
            map.put("name", person.getName());
            map.put("id", person.getId());
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

    @GetMapping("/get21DaysWayDataForRelationMap")
    @ResponseBody
    public Object get21DaysWayDataForRelationMap(@RequestParam("dep") String dep) {
        //获取该部门21天的所有提交数据
        List<User> userList = userUtil.get21DaysSubmitData(dep);
        //记录每个城市出现的次数
        Map<String, Object> cityMap = new HashMap<>();
        //记录相邻城市关系
        List<Way> wayList = new ArrayList<>();

        for (User user : userList) {
            List<String> cityList = UserUtil.getCityList(user.getItineraryInfo());
            for (int i = 0; i < cityList.size(); i++) {
                //记录城市出现的次数
                if (!cityMap.containsKey(cityList.get(i))) {
                    cityMap.put(cityList.get(i), 1);
                } else {
                    cityMap.put(cityList.get(i), (Integer) cityMap.get(cityList.get(i)) + 1);
                }
                //记录相邻城市关系
                if (i <= cityList.size() - 2) {
                    Way way = new Way();
                    way.setSource(cityList.get(i));
                    way.setTarget(cityList.get(i + 1));
                    if (!wayList.contains(way)) {
                        way.setLineStyle(new LineStyle(1));
                        wayList.add(way);
                    } else {
                        int index = wayList.indexOf(way);
                        wayList.get(index).getLineStyle().setWidth(wayList.get(index).getLineStyle().getWidth() + 1);
                    }
                }
            }
        }
        JSONObject res = new JSONObject();
        res.put("wayList", wayList);
        //处理map
        List<City> cityList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : cityMap.entrySet()) {
            City city = new City(entry.getKey(), (Integer) entry.getValue() * 2 + 40,(Integer) entry.getValue());
            cityList.add(city);
        }
        res.put("cityList", cityList);
        return res;
    }

}
