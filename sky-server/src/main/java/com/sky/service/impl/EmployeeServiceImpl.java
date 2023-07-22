package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 对用户输入的密码进行加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        // 密码比对
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     * @param employeeDTO
     */
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        // 将employeeDTO中的属性值拷贝到employee中
        BeanUtils.copyProperties(employeeDTO,employee);
        // 设置账号状态为启用
        employee.setStatus(StatusConstant.ENABLE);
        // 设置默认密码
        String password = DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes());
        employee.setPassword(password);
        // 设置创建时间和更新时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        // 设置创建人和更新人
        Long userId = BaseContext.getCurrentId();
        employee.setCreateUser(userId);
        employee.setUpdateUser(userId);
        employeeMapper.insert(employee);
    }

    /**
     * 分页查询员工列表
     * @param employeePageQueryDTO
     * @return
     */
    public PageResult pageQUERY(EmployeePageQueryDTO employeePageQueryDTO) {
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return pageResult;
    }

    /**
     * 更新员工状态
     * @param status
     * @param id
     */
    public void updateStatus(Integer status, Long id) {
        employeeMapper.updateStatus(status,id);
    }

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    public Employee getById(Long id) {
        Employee employee = employeeMapper.selectById(id);
        employee.setPassword("******");
        return employee;
    }

    /**
     * 修改员工
     * @param employeeDTO
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        // 将employeeDTO中的属性值拷贝到employee中
        BeanUtils.copyProperties(employeeDTO,employee);
        // 设置更新时间
        employee.setUpdateTime(LocalDateTime.now());
        // 设置更新人
        Long userId = BaseContext.getCurrentId();
        employee.setUpdateUser(userId);
        employeeMapper.update(employee);
    }

    /**
     * 修改密码
     * @param passwordEditDTO
     */
    public void editPassword(PasswordEditDTO passwordEditDTO) {
        // 获取用户的id
        Long empId = BaseContext.getCurrentId();
        // 根据id查询用户
        Employee employee = employeeMapper.selectById(empId);
        // 获取用户输入的原密码
        String oldPassword = passwordEditDTO.getOldPassword();
        // 对用户输入的原密码进行加密
        oldPassword = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        // 判断用户输入的原密码是否正确
        if (!oldPassword.equals(employee.getPassword())) {
            // 原密码不正确
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
        // 获取用户输入的新密码
        String newPassword = passwordEditDTO.getNewPassword();
        // 对用户输入的新密码进行加密
        newPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes());
        // 修改密码
        employee.setPassword(newPassword);
        employeeMapper.updatePassword(employee);
    }
}
