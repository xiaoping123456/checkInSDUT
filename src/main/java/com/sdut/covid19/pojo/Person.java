package com.sdut.covid19.pojo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author 小王造轮子
 * @description 名单管理里的成员
 * @date 2022/5/16
 */
@Data
public class Person {

    @TableId(type = IdType.NONE)
    @Excel(name = "工号/学号")
    private String id;
    @Excel(name = "姓名")
    private String name;
    @Excel(name = "身份证号")
    private String idCard;
    @Excel(name = "系部/班级")
    private String departmentName;
    @Excel(name = "电话")
    private String telephone;
    @Excel(name = "手机1")
    private String phone1;
    @Excel(name = "手机2")
    private String phone2;

    private String departmentId;
}
