package com.sdut.covid19.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdut.covid19.mapper.PersonMapper;
import com.sdut.covid19.pojo.Person;
import com.sdut.covid19.service.IPersonService;
import org.springframework.stereotype.Service;

/**
 * @author 小王造轮子
 * @description
 * @date 2022/5/16
 */
@Service
public class IPersonServiceImpl extends ServiceImpl<PersonMapper, Person> implements IPersonService {
}
