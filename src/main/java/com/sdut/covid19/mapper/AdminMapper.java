package com.sdut.covid19.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sdut.covid19.pojo.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface AdminMapper extends BaseMapper<Admin> {

}
