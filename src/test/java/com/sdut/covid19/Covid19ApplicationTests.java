package com.sdut.covid19;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdut.covid19.mapper.DepartmentMapper;
import com.sdut.covid19.mapper.PersonMapper;
import com.sdut.covid19.mapper.UserMapper;
import com.sdut.covid19.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SpringBootTest
class Covid19ApplicationTests {

    @Autowired
    UserMapper userMapper;
    @Autowired
    private DepartmentMapper departmentMapper;
    @Autowired
    private PersonMapper personMapper;

    @Test
    void contextLoads() {
        IPage<User> page = new Page(1,10);
        System.out.println(personMapper.getNotSubmitInCurrentDayInOneDep(page,"jsjxy",Long.parseLong("1652025508927")
                ,Long.parseLong("1652604151791")).getRecords());

    }
    @Test
    void getCurrentTime(){
        System.out.println(System.currentTimeMillis());
    }

    @Test
    void test(){
        System.out.println(userMapper.selectById(Long.parseLong("1522899545019609000")));
    }

}
