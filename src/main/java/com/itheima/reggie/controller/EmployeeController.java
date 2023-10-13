package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.imp.EmployeeServiceImp;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import jdk.nashorn.internal.ir.RuntimeNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeServiceImp employeeService;


    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, HttpServletResponse response, @RequestBody Employee employee){
        //密码加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //查数据库有无username
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        if (emp == null){
            return R.error("登录失败");
        }
        if (!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }
        if (emp.getStatus() == 0){
            return R.error("账号已禁用");
        }
        //登录成功，将用户id放进session中
        HttpSession session = request.getSession();
        session.setAttribute("employee",emp.getId());


        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }
    /**
     * 新增员工
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee, HttpServletRequest request){
        //设置初始密码为123456,但是要md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        //获取当前登录用户
 //       long empId = (long) request.getSession().getAttribute("employee");
 //       employee.setCreateUser(empId);
 //       employee.setUpdateUser(empId);
        employeeService.save(employee);
        return R.success("新增员工成功");

    }

    /**
     *员工分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<Employee>> page(int page,int pageSize,String name){
        //构造分页构造器
        Page<Employee> pageInfo = new Page<>(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息，包括状态修改
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee,HttpServletRequest request){
        //获取当前登录的用户id
    //    Long id = (Long) request.getSession().getAttribute("employee");
      //  employee.setUpdateUser(id);
    //    employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if (employee != null){
            return R.success(employee);
        }
        return R.error("没查到对应员工信息");
    }
}
