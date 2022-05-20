package com.sdut.covid19.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdut.covid19.mapper.DepartmentMapper;
import com.sdut.covid19.mapper.PersonMapper;
import com.sdut.covid19.mapper.UserMapper;
import com.sdut.covid19.pojo.Department;
import com.sdut.covid19.pojo.Person;
import com.sdut.covid19.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@CrossOrigin
@Controller
public class UserController {


    @Autowired
    UserMapper userMapper;

    @Autowired
    DepartmentMapper departmentMapper;
    @Autowired
    private PersonMapper personMapper;

    /**
     * 展示 今日该部门 的所有人员检测结果
     * @param dep
     * @param model
     * @return
     * @throws ParseException
     */
    @RequestMapping("/list/{dep}")
    public String list(@PathVariable("dep") String dep,Model model) throws ParseException {
        model.addAttribute("adminId",dep);

        QueryWrapper<Department> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("abbreviation",dep);
        Department department = departmentMapper.selectOne(wrapper1);
        model.addAttribute("department",department);

        String nickName = departmentMapper.getNickNameByDepartmentName(dep);
        model.addAttribute("nickName",nickName);

        // 设置日期格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 获取当前时间
        long currentTimestamp = System.currentTimeMillis();
        String currentTime = df.format(currentTimestamp);
        // 获取当天16:00:00的时间，转换成时间戳形式
        String time = currentTime.split(" ")[0] + " 16:00:00";
        long today = df.parse(time).getTime();

        long dayTime = 24 * 60 * 60 * 1000; // 24小时的时间戳
        long yesterday = today - dayTime;   // 昨天16:00:00的时间戳
        long tomorrow = today + dayTime;    // 明天16:00:00的时间戳

        //获取 该部门总人数 今日未提交人数，已提交人数，审核未通过人数
        //获取总人数
        Long total = personMapper.selectCount(new QueryWrapper<Person>().eq("department_id",dep));
        model.addAttribute("total",total);
        //已提交人数
        Integer submittedCount = 0;
        //未通过人数
        Integer failedCount = 0;
        if (currentTimestamp < today){
            submittedCount = userMapper.getSubmittedCountInCurrentDayInOneDep(dep,yesterday,today);
            failedCount = userMapper.getFailedCountInCurrentDayInOneDep(dep,yesterday,today);
        }
        else if (currentTimestamp > today){
            submittedCount = userMapper.getSubmittedCountInCurrentDayInOneDep(dep,today,tomorrow);
            failedCount = userMapper.getFailedCountInCurrentDayInOneDep(dep,today,tomorrow);
        }
        model.addAttribute("submittedCount",submittedCount);
        model.addAttribute("failedCount",failedCount);
        //未提交人数
        Integer noSubmittedCount = Math.toIntExact(total - submittedCount);
        model.addAttribute("noSubmittedCount",noSubmittedCount);

        return "list";
    }

    // 进入详情页
    @RequestMapping("/list/{dep}/detail/{id}")
    public String detail(@PathVariable("dep") String dep,@PathVariable("id") long id, Model model){
        QueryWrapper<Department> wrapper = new QueryWrapper<>();
        wrapper.eq("abbreviation",dep);

        Department department = departmentMapper.selectOne(wrapper);
        model.addAttribute("department",department);

        String nickName = departmentMapper.getNickNameByDepartmentName(dep);
        model.addAttribute("nickName",nickName);

        User user = userMapper.selectById(id);
        model.addAttribute("user",user);
        return "detail";
    }

    // 对识别异常的进行修改
    @PostMapping("/update")
    public String update(@RequestParam("dep") String dep,
                         @RequestParam("id") long id,
                         @RequestParam(value = "ifcolor",required = false) String ifcolor,
                         @RequestParam(value = "ifstar",required = false) String ifstar,
                         @RequestParam(value = "if48",required = false) String if48,
                         Model model){
        User user = userMapper.selectById(id);
        if(ifcolor != null){
            user.setHealthInfo(ifcolor);
        }
        if(ifstar != null){
            user.setItineraryInfo(ifstar);
        }
        if(if48 != null){
            user.setCovid19Info(if48);
        }

        userMapper.updateById(user);
        QueryWrapper<Department> wrapper = new QueryWrapper<>();
        wrapper.eq("abbreviation",dep);

        Department department = departmentMapper.selectOne(wrapper);
        model.addAttribute("department",department);
        model.addAttribute("user",user);
        return "detail";
    }

    @RequestMapping("/goSubmit/{dep}")
    public String goSubmit(@PathVariable("dep") String dep,Model model){
        QueryWrapper<Department> wrapper = new QueryWrapper<>();
        wrapper.eq("abbreviation",dep);

        Department department = departmentMapper.selectOne(wrapper);
        model.addAttribute("department",department);
        return "submit";
    }

    /**
     * @Description: 分页查询返回数据
     */
    @RequestMapping("/list/query/{num}")
    @ResponseBody
    @CrossOrigin
    public IPage<User> queryUserListPage(@PathVariable("num") int num){

        IPage<User> userPage = new Page<>(num, 10);
        return userMapper.selectPage(userPage,null);
    }


    /**
     * @Description: 跳转到分页查询页面
     */
    @RequestMapping("/list/all/{dep}")
    public String queryAllList(@PathVariable("dep") String dep,Model model){
        QueryWrapper<Department> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("abbreviation",dep);

        Department department = departmentMapper.selectOne(wrapper1);
        model.addAttribute("department",department);
        return "alllist";
    }
}
