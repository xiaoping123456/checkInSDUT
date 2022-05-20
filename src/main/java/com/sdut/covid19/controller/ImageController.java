package com.sdut.covid19.controller;

import com.alibaba.fastjson.JSON;
import com.sdut.covid19.mapper.UserMapper;
import com.sdut.covid19.pojo.User;
import com.sdut.covid19.utils.PyOcrRemote;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;



/**
 * @Author: badwei
 * @Date: 2022/4/30 21:32
 * @Description:
 */
@Slf4j
@Controller
public class ImageController {

    @Autowired
    PyOcrRemote pyOcrRemote;

    @Autowired
    UserMapper userMapper;


    @Autowired
    ThreadPoolExecutor imageOcrThreadPoolExecutor;


    @RequestMapping("/upload")
    public String upload(@RequestParam("name") String name,
                        @RequestParam("healthImg") MultipartFile healthImg,
                        @RequestParam("itineraryImg") MultipartFile itineraryImg,
                        @RequestParam("covid19Img") MultipartFile covid19Img
                        ) throws IOException {

        String healthImgPath = "";
        String itineraryImgPath = "";
        String covid19ImgPath = "";
        long currentTimeMillis = System.currentTimeMillis();

        //获取当前工作目录(启动的jar包的位置)
        String property = System.getProperty("user.dir");

        //使用Thumbnails压缩图片并上传到指定位置
        if (!healthImg.isEmpty()) {
            String originalFilename = currentTimeMillis + "1.png";
//            healthImgPath = "D:/imgfiles/" + originalFilename; //win
            healthImgPath = property + "/img/" + originalFilename; //linux图片路径
//            healthImg.transferTo(new File(healthImgPath));
            Thumbnails.of(healthImg.getInputStream()).scale(1f).outputQuality(0.2f).toFile(healthImgPath);

        }

        if (!itineraryImg.isEmpty()) {
            String originalFilename = currentTimeMillis + "2.png";
//            itineraryImgPath = "D:/imgfiles/" + originalFilename; //win
            itineraryImgPath = property + "/img/" + originalFilename; //linux图片路径
//            itineraryImg.transferTo(new File(itineraryImgPath));
            Thumbnails.of(itineraryImg.getInputStream()).scale(1f).outputQuality(0.1f).toFile(itineraryImgPath);
        }
        if (!covid19Img.isEmpty()) {
            String originalFilename = currentTimeMillis + "3.png";
//            covid19ImgPath = "D:/imgfiles/" + originalFilename; //win
            covid19ImgPath = property + "/img/" + originalFilename; //linux图片路径
//            covid19Img.transferTo(new File(covid19ImgPath));
            Thumbnails.of(covid19Img.getInputStream()).scale(1f).outputQuality(0.1f).toFile(covid19ImgPath);
        }

        String finalHealthImgPath = healthImgPath;
        String finalItineraryImgPath = itineraryImgPath;
        String finalCovid19ImgPath = covid19ImgPath;
        this.imageOcrThreadPoolExecutor.submit(() -> {
            long l1 = System.currentTimeMillis();
            User user = new User();
            user.setName(name);
            user.setTimestamp(currentTimeMillis);
            Date date1 = new Date(currentTimeMillis);
            SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = formatter1.format(date1);
            user.setDayTime(dateString);
            user.setHealthImgPath(finalHealthImgPath);
            user.setItineraryImgPath(finalItineraryImgPath);
            user.setCovid19ImgPath(finalCovid19ImgPath);

            String healthImgOcrInfo = pyOcrRemote.getOcrInfo(1, finalHealthImgPath);
            log.info("healthImgOcrInfo:{}",healthImgOcrInfo);
            String[] healthList = healthImgOcrInfo.split("\\|");
            if ("0".equals(healthList[0])){
                user.setHealthInfo("识别异常");
            } else if ("1".equals(healthList[0])){
                user.setHealthInfo("绿码");
            } else if ("2".equals(healthList[0])){
                user.setHealthInfo("红码");
            }
            user.setHealthTime(healthList[1]);
            user.setHealthMessage(healthList[2]);
            user.setHealthName(healthList[3]);
            user.setHealthIdCard(healthList[4]);


            String itineraryImgOcrInfo = pyOcrRemote.getOcrInfo(2, finalItineraryImgPath);
            log.info("itineraryImgOcrInfo:{}",itineraryImgOcrInfo);
            String[] itineraryList = itineraryImgOcrInfo.split("\\|");
            if ("0".equals(itineraryList[0])){
                user.setItineraryInfo("识别异常");
            }else if ("1".equals(itineraryList[0])){
                user.setItineraryInfo(itineraryList[4]);
            }else if ("2".equals(itineraryList[0])){
                user.setItineraryInfo("行程码带*");
            }
            user.setItineraryPhone(itineraryList[1]);
            user.setItineraryTime(itineraryList[2]);
            user.setItineraryMessage(itineraryList[3]);


            String covid19ImgOcrInfo = pyOcrRemote.getOcrInfo(3, finalCovid19ImgPath);
            log.info("covid19ImgOcrInfo:{}",covid19ImgOcrInfo);
            String[] covid19List = covid19ImgOcrInfo.split("\\|");
            if ("0".equals(covid19List[0])){
                user.setCovid19Info("识别异常");
            }else if ("1".equals(covid19List[0])){
                user.setCovid19Info("阳性");
            }else if ("2".equals(covid19List[0])){
                // 设置日期格式
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                // 获取当前时间
                long currentTimestamp = new Date().getTime();
                String currentTime = df.format(currentTimestamp);
                // 获取当天16:00:00的时间，转换成时间戳形式
                String time = currentTime.split(" ")[0] + " 16:00:00";
                long today = 0;
                try {
                    today = df.parse(time).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                long dayTimestamp = 24 * 60 * 60 * 1000; // 24小时的时间戳
                long before_yesterday = today - dayTimestamp -dayTimestamp;   // 前天16:00:00的时间戳
                long covid19Time = 0;   // 核酸检测报告的时间

                try {
                    covid19Time = Long.parseLong(covid19List[6]);
                    if(covid19Time >= before_yesterday){
                        user.setCovid19Info("阴性核酸检测小于48小时");
                    }
                    else {
                        user.setCovid19Info("阴性核酸检测超过48小时");
                    }
                } catch (Exception e) {
                    user.setCovid19Info("识别异常");
                    e.printStackTrace();
                }

            }
            user.setCovid19Name(covid19List[1]);
            user.setCovid19IdCard(covid19List[2]);
            user.setCovid19Time(covid19List[3]);
            user.setCovid19Hospital(covid19List[4]);
            user.setCovid19Result(covid19List[5]);



            if ("绿码".equals(user.getHealthInfo()) &&
                    "行程码不带*".equals(user.getItineraryInfo()) &&
                    "阴性核酸检测小于48小时".equals(user.getCovid19Info())){
                user.setInfoOk(true);
            }else{
                user.setInfoOk(false);
            }
            log.info("user:{}",JSON.toJSONString(user));
            long l2 = System.currentTimeMillis();
            log.info("整个ocr耗时(秒):{}",(l2-l1)/1000);

            long dayTimeLong = user.getTimestamp(); //上传时间戳

            Date dayTime = new Date(dayTimeLong);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String dayStr = formatter.format(dayTime);
            String dayFourStr = dayStr + " 16:00:00";
            SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date parse = formatter2.parse(dayFourStr);
                long dayFourTimeLong = parse.getTime(); //上传当天的下午4点的时间戳
                long dayFourTimeLongBefore = dayFourTimeLong - 1000 * 60 * 60 * 24; //昨天下午4点的时间戳
                long dayFourTimeLongNext = dayFourTimeLong + 1000 * 60 * 60 * 24; //明天下午4点的时间戳

                if (dayTimeLong > dayFourTimeLong){ //下午4点之后上传，需要判断今天4点之后有没有数据，有就更新，没有就新增
                    Map<String, Object> map = new HashMap<>();
                    //自定义查询
                    map.put("name",name);
                    List<User> users = userMapper.selectByMap(map);
                    int k = 0;
                    for (User user1 : users) {
                        if (user1.getTimestamp()>dayFourTimeLong){ //今天4点之后
                            user.setId(user1.getId());
                            log.info("更新数据:{}",JSON.toJSONString(user));
                            userMapper.updateById(user);
                            k = 1;
                        }
                    }
                    if (k == 0){
                        log.info("新增数据:{}",JSON.toJSONString(user));
                        userMapper.insert(user);
                    }
                } else { //下午4点之前上传，需要判断昨天4点到今天4点之间有没有数据，有就更新，没有就新增
                    Map<String, Object> map = new HashMap<>();
                    //自定义查询
                    map.put("name",name);
                    List<User> users = userMapper.selectByMap(map);
                    int k = 0;
                    for (User user1 : users) {
                        if (user1.getTimestamp() > dayFourTimeLongBefore && user1.getTimestamp() < dayFourTimeLong){ //昨天4点到今天4点之间
                            user.setId(user1.getId());
                            log.info("更新数据:{}",JSON.toJSONString(user));
                            userMapper.updateById(user);
                            k = 1;
                        }
                    }
                    if (k == 0){
                        log.info("新增数据:{}",JSON.toJSONString(user));
                        userMapper.insert(user);
                    }

                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        return "success";

    }



}
