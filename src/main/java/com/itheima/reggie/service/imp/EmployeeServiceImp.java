package com.itheima.reggie.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.mapper.EmployeeMapper;
import com.itheima.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;
//public class ServiceImpl<M extends BaseMapper<T>, T> implements IService<T>
//public interface EmployeeMapper(M) extends BaseMapper<Employee(T)>
//public interface EmployeeService extends IService<Employee>

@Service
public class EmployeeServiceImp extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService{


}
