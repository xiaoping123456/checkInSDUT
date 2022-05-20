package com.sdut.covid19.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Department {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String abbreviation;    // 部门缩写
    private String departName;  // 部门全称
}
