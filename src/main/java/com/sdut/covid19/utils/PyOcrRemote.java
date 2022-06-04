package com.sdut.covid19.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @Author: badwei
 * @Date: 2022/5/3 11:54
 * @Description:
 */
@Component
public class PyOcrRemote {

    public String getOcrInfo(int k, String path){
        RestTemplate restTemplate=new RestTemplate();
//        String path = "D:/imgfiles/001.jpg";
        System.out.println("请求ocr");
        String url = "http://127.0.0.1:9092/ocr/" + k + "/" + path;
        String result = restTemplate.getForObject(url,String.class);
        System.out.println(result);
        return result;
    }
}
