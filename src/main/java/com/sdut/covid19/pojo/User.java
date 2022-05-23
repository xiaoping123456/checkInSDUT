package com.sdut.covid19.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;


/**
 * @Author: badwei
 * @Date: 2022/4/30 15:20
 * @Description: 用户信息
 */
@Data
public class User {
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id; //主键

    private String departmentId; //所属部门id

    private String name;
    private String unit; //单位，eg:计算机科学与技术学院
    private String phone;
    private String email;
    private String idCard;
    private Long timestamp; //上传时间戳
    private String dayTime; //2022.04.30 每天作区分的字段   上午10 - 下午4

    private String healthInfo; // 2红码or1绿码 or0异常
    private String itineraryInfo; // 0行程码带* or 1行程码不带*
    private String covid19Info; // 0默认异常，1为阳性，2阴性核酸检测超过48小时，3阴性核酸检测未超过48小时

    private boolean infoOk; //是否通过

    //健康码信息
    private String healthTime;
    private String healthName;
    private String healthIdCard; //身份证
    private String healthMessage; //eg:未见异常(绿码)
    private String healthImgPath; //图片路径

    //行程码信息
    private String itineraryTime;
    private String itineraryPhone; //手机号
    private String itineraryMessage; //eg:14天内到达或途径：山东省淄博市
    private String itineraryImgPath; //图片路径

    //核酸结果信息
    private String covid19Time;
    private String covid19Name;
    private String covid19Hospital; //医院
    private String covid19Result; //eg:阴性
    private String covid19IdCard; //身份证
    private String covid19ImgPath; //图片路径

}


