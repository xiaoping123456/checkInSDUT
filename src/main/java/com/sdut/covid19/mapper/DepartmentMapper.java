package com.sdut.covid19.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sdut.covid19.pojo.Department;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentMapper extends BaseMapper<Department> {

    /**
     * 获取某个部门的负责人姓名
     * @param id
     * @return
     */
    @Select("SELECT\n" +
            "\tadmin.nick_name \n" +
            "FROM\n" +
            "\t`admin`,\n" +
            "\tdepartment \n" +
            "WHERE\n" +
            "\tadmin.id = department.abbreviation \n" +
            "\tAND admin.id=#{id}")
    public String getNickNameByDepartmentName(String id);

}
