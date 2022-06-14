package com.sdut.covid19.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdut.covid19.mapper.DepartmentMapper;
import com.sdut.covid19.mapper.UserMapper;
import com.sdut.covid19.pojo.Department;
import com.sdut.covid19.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 杨敬鸿
 */
@Controller
public class UserController3 {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DepartmentMapper departmentMapper;

    @RequestMapping("/list/historyData/{dep}")
    public String toHistoryPage(@PathVariable("dep")String dep,Model model){
        model.addAttribute("adminId",dep);
        QueryWrapper<Department> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("abbreviation",dep);
        Department department = departmentMapper.selectOne(wrapper1);
        model.addAttribute("department",department);
        String nickName = departmentMapper.getNickNameByDepartmentName(dep);
        model.addAttribute("nickName",nickName);




        return "historyList";
    }

    @RequestMapping("/list/historyData/query/{dep}")
    public String queryHistoryData(Model model,
                                   @RequestParam("date1") String date1,
                                   @RequestParam("date2") String date2,
                                   @PathVariable("dep")String dep){
        System.out.println(date1);
        System.out.println(date2);
        model.addAttribute("date1",date1);
        model.addAttribute("date2",date2);
        List<User> res =  userMapper.selectList(new QueryWrapper<User>().eq("department_id",dep));
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 转换时间 timestamp -> date -> string
        List<User> users = new ArrayList();
        for (User user : res) {
            long t = Long.valueOf(user.getTimestamp());
            String date = sdf.format(new Date(t));
            user.setDayTime(date);
            // 时间比对
            String time = user.getDayTime();
            if(date1.compareTo(time)<0&&date2.compareTo(time)>0){
                users.add(user);
            }
        }
        model.addAttribute("users",users);
        // 如果结果为空 返回提示
        if(users.isEmpty()){
            model.addAttribute("msg","这个时间段没有数据");
        }
        else{
            model.addAttribute("msg","");
        }
        return "historyList";
    }

    /**
     * 获取该部门所有的提交数据  指定时间端的
     * @param dep
     * @param beginDate
     * @param endDate
     * @param currentPage
     * @param size
     * @return
     */
    @GetMapping("/getHistoryData")
    @ResponseBody
    public Map<String,Object> getHistoryData(@RequestParam("dep")String dep,
                                             @RequestParam(value = "beginDate",defaultValue = "1640966400000")String beginDate,
                                             @RequestParam(value = "endDate",defaultValue = "2145801600000")String endDate,
                                             @RequestParam(value = "currentPage",defaultValue = "1")Integer currentPage,
                                             @RequestParam(value = "size",defaultValue = "10")Integer size){
        JSONObject res = new JSONObject();
        try {
            Long begin = Long.parseLong(beginDate) + 60*60*16*1000;
            Long end = Long.parseLong(endDate) + 60*60*(16+24)*1000;
            System.out.println(new Date(begin));
            System.out.println(new Date(end));
            IPage<User> page = new Page<>(currentPage,size);
            QueryWrapper<User> wrapper = new QueryWrapper<User>().eq("department_id", dep).between("timestamp", begin, end);
            IPage<User> userIPage = userMapper.selectPage(page, wrapper);
            res.put("code",200);
            res.put("data",userIPage.getRecords());
            res.put("total",userIPage.getTotal());
        } catch (Exception e) {
            System.out.println(e);
            res.put("code",500);
            res.put("message","查询失败");
        }
        return res;
    }


    /*
     * 函数名：getUserFun1
     * 作用：在数据库返回的所有用户中找出符合时间条件的用户
     * 规则：今天16点-明天16点 为今天
     * */
    public List<String> getUsersFun1(String date1, String date2) throws ParseException {
        // 转换时间格式_将前端传来的yyyy-MM-dd格式的日期 转换成yyyy-MM-dd HH:mm:ss格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date t1 = sdf.parse(date1);
        Date t2 = sdf.parse(date2);
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(t1);
        calendar2.setTime(t2);
        long timeInMillis1 = calendar1.getTimeInMillis();
        long timeInMillis2 = calendar2.getTimeInMillis();
        // 今天的00:00开始加40个小时即为明天的16:00
        timeInMillis1+=16*60*60*1000;
        timeInMillis2+=40*60*60*1000;
        calendar1.setTimeInMillis(timeInMillis1);
        calendar2.setTimeInMillis(timeInMillis2);
        t1 = calendar1.getTime();
        t2 = calendar2.getTime();
        String newDate1 = sdff.format(t1);
        String newDate2 = sdff.format(t2);
        List<String> listRes = new ArrayList<>();
        listRes.add(newDate1);
        listRes.add(newDate2);
        return listRes;
    }


    @RequestMapping("/list/visualization/{dep}")
    public String toVisualization(@PathVariable("dep")String dep,Model model) throws ParseException {
        model.addAttribute("adminId",dep);

        QueryWrapper<Department> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("abbreviation",dep);
        Department department = departmentMapper.selectOne(wrapper1);
        model.addAttribute("department",department);

        String nickName = departmentMapper.getNickNameByDepartmentName(dep);
        model.addAttribute("nickName",nickName);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        Date time22 = calendar.getTime();
        long timeInMillis = calendar.getTimeInMillis();
        timeInMillis-=14*24*60*60*1000;
        calendar.setTimeInMillis(timeInMillis);
        Date time11 = calendar.getTime();
        String date2 = sdf.format(time22);
        String date1 = sdf.format(time11);

        // 设置当前选中的时间
        // 设置当前选中的时间
        model.addAttribute("t1",date1);
        model.addAttribute("t2",date2);
        // 查询符合时间段中的所有用户提交
        List<User> res =  userMapper.selectList(new QueryWrapper<User>().eq("department_id",dep));
        // 转换时间格式
        List<String> newDates = getUsersFun1(date1,date2);
        String newDate1 = newDates.get(0);
        String newDate2 = newDates.get(1);
        // 求出符合时间的要求的用户
        List<User> users = new ArrayList<>();
        for (User user : res) {
            String dayTime = user.getDayTime();
            if(dayTime.compareTo(newDate1)>=0&&dayTime.compareTo(newDate2)<=0)
                users.add(user);
        }
        // 提示用户选中的时间段没有数据
        if(users.isEmpty()) model.addAttribute("msg","这个时间段没有数据");
        else model.addAttribute("msg","");

        // 柱状图传值
        int arr[] = new int[24];
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH");
        for (User user : users) {
            long t = Long.valueOf(user.getTimestamp());
            String date = sdf1.format(new Date(t));
            int x = Integer.valueOf(date);
            arr[x]++;
        }
        String title1Str = date1+"至"+date2+"各时间段提交人次展示";
        model.addAttribute("title1Str",title1Str);
        model.addAttribute("arr",arr);

        // 折线图传值 使用HashMap统计每一天的提交次数
        Calendar c1 = Calendar.getInstance();
        c1.setTime(sdf.parse(date1));
        long time1 = c1.getTimeInMillis();
        Calendar c2= Calendar.getInstance();
        c2.setTime(sdf.parse(date2));
        long time2 = c2.getTimeInMillis();
        List<String> dates = new ArrayList<>();
        for(long i = time1;i<=time2;i+=24*60*60*1000){
            dates.add(sdf.format(new Date(i)));
        }
        // 利用HashMap计算所有符合条件提交次数
        Map<String,Integer> map = new HashMap();
        for (User user : res) {
            String key = user.getDayTime();
            key = sdf.format(sdff.parse(key));
            if(map.containsKey(key)){
                map.put(key,map.get(key)+1);
            }
            else map.put(key,1);
        }
        int datesArr[] = new int[dates.size()];
        int k = 0;
        for (String date : dates) {
            if(map.containsKey(date))datesArr[k++]=map.get(date);
            else datesArr[k++] = 0;
        }
        model.addAttribute("dates",dates);
        model.addAttribute("datesArr",datesArr);
        String title2Str = date1+"至"+date2+"提交人次变化情况";
        model.addAttribute("title2Str",title2Str);

        // 地图
        // 存储每个省的人次——用于中国地图传值
        Map<String, Integer> chinaMap = new HashMap();
        for (User user : users) {
            String target = user.getItineraryInfo();
            target = target.trim();
            // 用正则提取每个省 并放入map中
            Pattern pattern1 = Pattern.compile("(^|,)(.*?)(市|省|区).*?");
            Matcher matcher1 = pattern1.matcher(target);
            while (matcher1.find()) {
                String startArr[] = {"新疆维吾尔自治", "西藏自治", "宁夏回族自治", "广西壮族自治", "香港特别行政", "澳门特别行政"};
                String endArr[] = {"新疆", "西藏", "宁夏", "广西", "香港", "澳门"};
                String s = matcher1.group(2);
                for (int i = 0; i < endArr.length; i++) {
                    if (s.equals(startArr[i])) {
                        s = endArr[i];
                        break;
                    }
                }
                if (chinaMap.containsKey(s))
                    chinaMap.put(s, chinaMap.get(s) + 1);
                else chinaMap.put(s, 1);
            }
        }
        int maxValue = 1;
        for(String s:chinaMap.keySet()){
            maxValue=Math.max(maxValue,chinaMap.get(s));
        }
        model.addAttribute("maxValue",maxValue+100);
        Object obj = JSONArray.toJSON(chinaMap);
        String chinaJson = obj.toString();
        model.addAttribute("chinaJson",chinaJson);
        return "visualization";
    }

    @RequestMapping("/list/visualization/query/{dep}")
    public String showVisualization(
            Model model, @RequestParam("date1") String date1, @RequestParam("date2") String date2,
            @PathVariable("dep")String dep) throws ParseException {

        model.addAttribute("adminId",dep);
        QueryWrapper<Department> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("abbreviation",dep);
        Department department = departmentMapper.selectOne(wrapper1);
        model.addAttribute("department",department);
        String nickName = departmentMapper.getNickNameByDepartmentName(dep);
        model.addAttribute("nickName",nickName);

        // 设置当前选中的时间
        model.addAttribute("t1",date1);
        model.addAttribute("t2",date2);
        // 查询符合时间段中的所有用户提交
        List<User> res =  userMapper.selectList(new QueryWrapper<User>().eq("department_id",dep));
        // 转换时间格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 转换时间格式
        List<String> newDates = getUsersFun1(date1,date2);
        String newDate1 = newDates.get(0);
        String newDate2 = newDates.get(1);
        // 求出符合时间的要求的用户
        List<User> users = new ArrayList<>();
        for (User user : res) {
            String dayTime = user.getDayTime();
            if(dayTime.compareTo(newDate1)>=0&&dayTime.compareTo(newDate2)<=0)
                users.add(user);
        }
        // 提示用户选中的时间段没有数据
        if(users.isEmpty()) model.addAttribute("msg","这个时间段没有数据");
        else model.addAttribute("msg","");

        // 柱状图传值
        int arr[] = new int[24];
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH");
        for (User user : users) {
            long t = Long.valueOf(user.getTimestamp());
            String date = sdf1.format(new Date(t));
            int x = Integer.valueOf(date);
            arr[x]++;
        }
        String title1Str = date1+"至"+date2+"各时间段提交人次展示";
        model.addAttribute("title1Str",title1Str);
        model.addAttribute("arr",arr);

        // 折线图传值 使用HashMap统计每一天的提交次数
        Calendar c1 = Calendar.getInstance();
        c1.setTime(sdf.parse(date1));
        long time1 = c1.getTimeInMillis();
        Calendar c2= Calendar.getInstance();
        c2.setTime(sdf.parse(date2));
        long time2 = c2.getTimeInMillis();
        List<String> dates = new ArrayList<>();
        for(long i = time1;i<=time2;i+=24*60*60*1000){
            dates.add(sdf.format(new Date(i)));
        }
        // 利用HashMap计算所有符合条件提交次数
        Map<String,Integer> map = new HashMap();
        for (User user : res) {
            String key = user.getDayTime();
            key = sdf.format(sdff.parse(key));
            if(map.containsKey(key)){
                map.put(key,map.get(key)+1);
            }
            else map.put(key,1);
        }
        int datesArr[] = new int[dates.size()];
        int k = 0;
        for (String date : dates) {
            if(map.containsKey(date))datesArr[k++]=map.get(date);
            else datesArr[k++] = 0;
        }
        model.addAttribute("dates",dates);
        model.addAttribute("datesArr",datesArr);
        String title2Str = date1+"至"+date2+"提交人次变化情况";
        model.addAttribute("title2Str",title2Str);

        // 地图
        // 存储每个省的人次——用于中国地图传值
        Map<String, Integer> chinaMap = new HashMap();
        for (User user : users) {
            String target = user.getItineraryInfo();
            target = target.trim();
            // 用正则提取每个省 并放入map中
            Pattern pattern1 = Pattern.compile("(^|,)(.*?)(市|省|区).*?");
            Matcher matcher1 = pattern1.matcher(target);
            while (matcher1.find()) {
                String startArr[] = {"新疆维吾尔自治", "西藏自治", "宁夏回族自治", "广西壮族自治", "香港特别行政", "澳门特别行政"};
                String endArr[] = {"新疆", "西藏", "宁夏", "广西", "香港", "澳门"};
                String s = matcher1.group(2);
                for (int i = 0; i < endArr.length; i++) {
                    if (s.equals(startArr[i])) {
                        s = endArr[i];
                        break;
                    }
                }
                if (chinaMap.containsKey(s))
                    chinaMap.put(s, chinaMap.get(s) + 1);
                else chinaMap.put(s, 1);
            }
        }
        int maxValue = 1;
        for(String s:chinaMap.keySet()){
            maxValue=Math.max(maxValue,chinaMap.get(s));
        }
        model.addAttribute("maxValue",maxValue+100);
        Object obj = JSONArray.toJSON(chinaMap);
        String chinaJson = obj.toString();
        model.addAttribute("chinaJson",chinaJson);
        return "visualization";
    }

    // 中国地图具体到每一个省的数据，使用ajax请求处理
    @RequestMapping("/list/visualization/query/citydata")
    @ResponseBody
    public String showProvinceData(String selectedCity, Model model, String date1, String date2){
        List<User> res =  userMapper.selectList(null);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 转换时间 timestamp -> date -> string
        List<User> users = new ArrayList();
        for (User user : res) {
            long t = Long.valueOf(user.getTimestamp());
            String date = sdf.format(new Date(t));
            user.setDayTime(date);
            // 时间比对
            String time = user.getDayTime();
            if(date1.compareTo(time)<0&&date2.compareTo(time)>0){
                users.add(user);
            }
        }
        // 存储每个省中的每个城市的人次——用于每个省的每个地区传值
        Map<String, HashMap<String, Integer>> provinceMap = new HashMap();
        for (User user : users) {
            String target = user.getItineraryInfo();
            target = target.trim();
            Pattern pattern2 = Pattern.compile("(^|,)(.{2,5})(自治区|省)(.*?市).*?");
            Matcher matcher2 = pattern2.matcher(target);
            while (matcher2.find()) {
                String province = matcher2.group(2);
                String city = matcher2.group(4);
                String startArr[] = {"新疆维吾尔", "西藏自治", "宁夏回族", "广西壮族",};
                String endArr[] = {"新疆", "西藏", "宁夏", "广西",};
                for (int i = 0; i < endArr.length; i++) {
                    if (province.equals(startArr[i])) {
                        province = endArr[i];
                        break;
                    }
                }
                HashMap<String, Integer> map1;
                if (provinceMap.containsKey(province)) {
                    map1 = provinceMap.get(province);
                    if(map1.containsKey(city))map1.put(city,map1.get(city)+1);
                    else map1.put(city,1);
                }
                else {
                    map1 = new HashMap();
                    if(map1.containsKey(city))map1.put(city,map1.get(city)+1);
                    else map1.put(city,1);
                }
                provinceMap.put(province,map1);
            }
        }
        Map<String,Integer> cityMap = new HashMap<>();
        for (String s : provinceMap.keySet()) {
            if(s.equals(selectedCity))
            cityMap = provinceMap.get(s);
        }
        String cityJson = JSON.toJSONString(cityMap);
        return cityJson;
    }

}
