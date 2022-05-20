package com.sdut.covid19.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sdut.covid19.mapper.AdminMapper;
import com.sdut.covid19.pojo.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 小王造轮子
 * @description 常用工具类
 * @date 2022/5/15
 */
@Component
public class UsualUtils {

    @Autowired
    private AdminMapper adminMapper;

    /**
     * 生成6位的随机码（管理员的唯一标识）
     * @return
     */
    public String generateIDCode() {
        char[] arr = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57,//从0到9的数字
                65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90,//从A到Z的数字
                97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122//从a到z的数字
        };
        int i = 1;
        String code = "";
        while(!checkCode(code)){
            while (i++ <= 6) { //循环六次，得到六位数的验证码
                code += arr[(int) (Math.random() * 61)];
            }
        }
        return code;
    }

    /**
     * 检查数据库中是否已经有该code
     * @param code
     * @return
     */
    private boolean checkCode(String code) {
        if (code.equals("")){
            return false;
        }

        if (adminMapper.selectCount(new QueryWrapper<Admin>().eq("id", code)) > 0) {
            return false;
        }
        return true;
    }

}
