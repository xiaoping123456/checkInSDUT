package com.sdut.covid19.controller;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdut.covid19.mapper.PersonMapper;
import com.sdut.covid19.pojo.Person;
import com.sdut.covid19.service.IPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 小王造轮子
 * @description
 * @date 2022/5/16
 */
@RestController
public class PersonController {

    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private IPersonService personService;

    /**
     * 下载excel模板文件
     * @return
     */
    @PostMapping("/downloadTemplate")
    public String downloadTemplate(){
        JSONObject res = new JSONObject();
        return "http://43.138.66.201:8080/home/template/template.xlsx";
//        return "C:\\Users\\xiaoping\\Desktop\\template.xlsx";
    }

    /**
     * 导入excel表
     * @param file
     * @param dep
     * @return
     */
    @PostMapping("/import")
    public Object importEmployee(@RequestPart("file") MultipartFile file,@RequestPart("dep")String dep){
        JSONObject res = new JSONObject();
        ImportParams params = new ImportParams();
        try {
            List<Person> personList = ExcelImportUtil.importExcel(file.getInputStream(), Person.class, params);
            personList.forEach(person -> {
                person.setDepartmentId(dep);
            });
            if (personService.saveBatch(personList)){
                res.put("code",200);
            }else{
                res.put("code",500);
            }
        } catch (Exception e) {
            res.put("code",500);
            System.out.println(e);
        }
        return res;
    }

    /**
     * 获取某个部门的所有成员
     * @return
     */
    @GetMapping("/getAllPersonInDep")
    public Map<String,Object> getAllPersonInDep(@RequestParam(value = "dep",required = true)String dep,
                                                @RequestParam(value = "currentPage",defaultValue = "1")Integer currentPage,
                                                @RequestParam(value = "size",defaultValue = "10")Integer size){
        Page<Person> page = new Page<>(currentPage,size);
        Page<Person> personPage = personMapper.selectPage(page, new QueryWrapper<Person>().eq("department_id", dep));
        Map<String, Object> res = new HashMap<>();
        res.put("data",personPage.getRecords());
        res.put("total",personPage.getTotal());
        return res;
    }

    /**
     * 添加成员
     * @param json
     * @return
     */
    @PostMapping("/addPerson")
    public boolean addPerson(@RequestBody String json){


        JSONObject params = JSONObject.parseObject(json);
        Person person = params.getObject("person",Person.class);
        String dep = params.getString("dep");

        if (personMapper.selectOne(new QueryWrapper<Person>().eq("name",person.getName())
                .eq("department_id",dep))!=null){
            return false;
        }

        person.setDepartmentId(dep);
        if (personMapper.insert(person)!=0){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 修改成员信息
     * @param person
     * @return
     */
    @PostMapping("/updatePerson")
    public boolean updatePerson(@RequestBody Person person){
        if (personMapper.updateById(person)!=0){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 删除某个成员
     * @param id
     * @return
     */
    @DeleteMapping("/deletePerson")
    public boolean deletePerson(@RequestParam("id")String id){
        if (personMapper.deleteById(id)!=0){
            return true;
        }else{
            return false;
        }
    }

}
