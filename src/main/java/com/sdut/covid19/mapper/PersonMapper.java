package com.sdut.covid19.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sdut.covid19.pojo.Person;
import com.sdut.covid19.pojo.User;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonMapper extends BaseMapper<Person> {

    /**
     * 获取今日未提交的人员
     * @param page
     * @param dep
     * @param begin
     * @param end
     * @return
     */
    @Select("SELECT\n" +
            "\t* \n" +
            "FROM\n" +
            "\tperson \n" +
            "WHERE\n" +
            "\tperson.department_id = #{dep} \n" +
            "\tAND NOT EXISTS (\n" +
            "\tSELECT\n" +
            "\t\t* \n" +
            "\tFROM\n" +
            "\t\t`user` \n" +
            "\tWHERE\n" +
            "\t\t`user`.`name` = person.`name` \n" +
            "\tAND ( `user`.`timestamp` BETWEEN #{begin} AND #{end} ) \n" +
            "\t)")
    public IPage<Person> getNotSubmitInCurrentDayInOneDep(IPage<User> page, String dep, long begin, long end);

}
