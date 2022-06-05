package com.sdut.covid19.utils;

import com.sdut.covid19.mapper.UserMapper;
import com.sdut.covid19.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * @author 小王造轮子
 * @description
 * @date 2022/5/23
 */
@Component
public class UserUtil {

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取最近21天的提交数据
     *
     * @param dep
     * @return
     */
    public List<User> get21DaysSubmitData(String dep) {
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
        return userList;
    }

    //行程码 行程信息转list
    public static List<String> formatItineraryInfo(String itineraryInfo) {
        List<String> itineraryList = Arrays.asList(itineraryInfo.split(","));
        return itineraryList;
    }

    //行程码 图片路径转url
    public static String pathToUrl(String path) {
        return "http://43.138.66.201:8080" + path;
    }

    /**
     * 提取itineraryInfo中的市级信息
     */
    public static List<String> getCityList(String info){
        String itineraryInfo = info.replace("，",",");
        List<String> itineraryList = Arrays.asList(itineraryInfo.split(","));
        List<String> cityList = new ArrayList<>();

        if (info.contains("*")){
            return cityList;
        }

        itineraryList.forEach(place->{
            if (place.equals("北京市")||place.equals("天津市")||place.equals("重庆市")||place.equals("上海市")){
                cityList.add(place);
            }else if (place.contains("自治区")){
                cityList.add(place.substring(place.indexOf("自治区")+3));
            }else if (place.contains("行政区")){
                cityList.add(place);
            }else {
                cityList.add(place.substring(place.indexOf("省")+1));
            }
        });
        return cityList;
    }

    //covid19_time 日期和小时之间加个空格
    public static String formatCovidTime(String time){
        StringBuffer time2 = new StringBuffer(time);
        time2.insert(10," ");
        return time2.toString();
    }

    //itinerary_time 格式字符串
    public static String formatItineraryTime(String time){
        String time2 = time.replace(" ", "");
        StringBuffer buffer = new StringBuffer(time2);
        buffer.insert(10," ");
        buffer.insert(13,":");
        buffer.insert(16,":");
        return buffer.toString();
    }


}
