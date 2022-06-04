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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long parse = sdf.parse("2022-01-01").getTime();
        long time = sdf.parse("2037-12-31").getTime();
        System.out.println(parse);
        System.out.println(time);
    }

}
