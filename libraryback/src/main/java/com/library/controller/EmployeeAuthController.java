package com.library.controller;

import com.library.common.Result;
import com.library.entity.Employee;
import com.library.entity.Permission;
import com.library.service.EmployeeService;
import com.library.util.JwtUtil;
import com.library.util.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/employee/auth")
public class EmployeeAuthController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(
            @RequestParam @NotBlank(message = "用户名不能为空") String username,
            @RequestParam @NotBlank(message = "密码不能为空") String password) {
        
        log.info("员工登录请求: {}", username);
        
        Employee employee = employeeService.getByUsername(username);
        if (employee == null) {
            return Result.error("用户名或密码错误");
        }
        
        if (employee.getStatus() == null || employee.getStatus() != 1) {
            return Result.error("账号已被禁用");
        }
        
        if (!PasswordUtil.matches(password, employee.getPassword())) {
            return Result.error("用户名或密码错误");
        }
        
        String token = JwtUtil.generateToken(employee.getId(), employee.getUsername(), 2);
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("employeeId", employee.getId());
        result.put("username", employee.getUsername());
        result.put("name", employee.getName());
        result.put("departmentId", employee.getDepartmentId());
        result.put("type", "employee");
        
        log.info("员工登录成功: {}, 部门: {}", username, employee.getDepartmentId());
        return Result.success("登录成功", result);
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> getEmployeeInfo(@RequestAttribute Long userId) {
        Employee employee = employeeService.getById(userId);
        if (employee != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("id", employee.getId());
            result.put("name", employee.getName());
            result.put("username", employee.getUsername());
            result.put("employeeNo", employee.getEmployeeNo());
            result.put("departmentId", employee.getDepartmentId());
            result.put("phone", employee.getPhone());
            result.put("email", employee.getEmail());
            result.put("position", employee.getPosition());
            result.put("type", "employee");
            return Result.success(result);
        }
        return Result.error("员工不存在");
    }

    @GetMapping("/permissions")
    public Result<List<Permission>> getDepartmentPermissions(@RequestAttribute Long userId) {
        Employee employee = employeeService.getById(userId);
        if (employee == null) {
            return Result.error("员工不存在");
        }
        
        if (employee.getDepartmentId() == null) {
            return Result.error("员工未分配部门");
        }
        
        List<Permission> permissions = employeeService.getPermissionsByDepartmentId(employee.getDepartmentId());
        return Result.success(permissions);
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestAttribute Long userId) {
        log.info("员工登出: {}", userId);
        return Result.success("登出成功");
    }
}
