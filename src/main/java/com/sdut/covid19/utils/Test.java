package com.sdut.covid19.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;


/**
 * @author 小王造轮子
 * @description
 * @date 2022/5/21
 */
public class Test {

    public static void main(String[] args) throws ParseException {
        String time = "2022.06.05 172620";
        String time2 = time.replace(" ", "");
        StringBuffer buffer = new StringBuffer(time2);
        buffer.insert(10," ");
        buffer.insert(13,":");
        buffer.insert(16,":");
        System.out.println(buffer.toString());
    }

}
