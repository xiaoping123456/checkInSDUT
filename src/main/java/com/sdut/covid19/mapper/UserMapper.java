package com.sdut.covid19.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sdut.covid19.pojo.Person;
import com.sdut.covid19.pojo.User;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper extends BaseMapper<User> {

    /**
     * 获取某个部门当日 提交的信息User
     * @return
     */
    @Select("SELECT `user`.*\n" +
            "FROM\n" +
            "\t`user`,\n" +
            "\tperson \n" +
            "WHERE\n" +
            "\t`user`.`name` = person.`name` \n" +
            "\tAND person.department_id =#{dep} \n" +
            "\tAND \n" +
            "\t(`user`.`timestamp` BETWEEN #{begin} AND #{end}) ORDER BY `timestamp` desc")
    public IPage<User> getSubmittedUsersInCurrentDayInOneDep(IPage<User> page, String dep, long begin, long end);

    /**
     * 获取某个部门当日 已提交的人数
     * @param dep
     * @param begin
     * @param end
     * @return
     */
    @Select("SELECT COUNT(`user`.name)\n" +
            "FROM\n" +
            "\t`user`,\n" +
            "\tperson \n" +
            "WHERE\n" +
            "\t`user`.`name` = person.`name` \n" +
            "\tAND person.department_id =#{dep} \n" +
            "\tAND \n" +
            "\t(`user`.`timestamp` BETWEEN #{begin} AND #{end}) ORDER BY `timestamp` desc")
    public Integer getSubmittedCountInCurrentDayInOneDep(String dep,long begin,long end);

    /**
     * 获取该部门今日 审核未通过的成员信息
     * @param dep
     * @param begin
     * @param end
     * @return
     */
    @Select("SELECT user.*\n" +
            "FROM\n" +
            "\t`user`,\n" +
            "\tperson \n" +
            "WHERE\n" +
            "\t`user`.`name` = person.`name` \n" +
            "\tAND person.department_id=#{dep} \n" +
            "\tAND user.info_ok=1\n" +
            "\tAND (`user`.`timestamp` BETWEEN #{begin} AND #{end})\n" +
            "\tORDER BY `timestamp` desc")
    public IPage<User> getFailedUsersInCurrentDayInOneDep(IPage<User>page,String dep,long begin,long end);

    /**
     * 获取该部门今日 审核未通过的人数
     * @param dep
     * @param begin
     * @param end
     * @return
     */
    @Select("SELECT COUNT(`user`.name)\n" +
            "FROM\n" +
            "\t`user`,\n" +
            "\tperson \n" +
            "WHERE\n" +
            "\t`user`.`name` = person.`name` \n" +
            "\tAND person.department_id=#{dep} \n" +
            "\tAND user.info_ok=1\n" +
            "\tAND (`user`.`timestamp` BETWEEN #{begin} AND #{end})\n" +
            "\tORDER BY `timestamp` desc")
    public Integer getFailedCountInCurrentDayInOneDep(String dep,long begin,long end);


}
