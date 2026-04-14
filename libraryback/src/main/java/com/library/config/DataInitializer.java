package com.library.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.entity.*;
import com.library.mapper.*;
import com.library.util.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 数据初始化器
 * 在应用启动时初始化默认数据
 */
@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private DepartmentMapper departmentMapper;
    
    @Autowired
    private EmployeeMapper employeeMapper;
    
    @Autowired
    private ProcurementMapper procurementMapper;
    
    @Autowired
    private ProcurementItemMapper procurementItemMapper;
    
    @Autowired
    private AttendanceMapper attendanceMapper;

    @Override
    public void run(String... args) throws Exception {
        initAdminUser();
        initDepartments();
        initEmployees();
        initProcurements();
        initAttendanceData();
    }

    /**
     * 初始化管理员用户
     * 如果管理员用户不存在，则创建默认管理员
     */
    private void initAdminUser() {
        User existUser = userMapper.selectByUsername("admin");
        if (existUser == null) {
            log.info("初始化管理员用户...");
            
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(PasswordUtil.encode("admin123"));
            admin.setRealName("系统管理员");
            admin.setRole(1);
            admin.setStatus(1);
            
            userMapper.insert(admin);
            log.info("管理员用户初始化完成，用户名: admin, 密码: admin123");
        } else {
            log.info("管理员用户已存在，跳过初始化");
        }
    }
    
    private void initDepartments() {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        Long count = departmentMapper.selectCount(wrapper);
        if (count > 0) {
            log.info("部门数据已存在，跳过初始化");
            return;
        }
        
        log.info("初始化部门数据...");
        
        String[] deptNames = {"行政部", "采购部", "技术部", "财务部", "人事部"};
        for (int i = 0; i < deptNames.length; i++) {
            Department dept = new Department();
            dept.setName(deptNames[i]);
            dept.setCode("DEPT00" + (i + 1));
            dept.setSortOrder(i + 1);
            dept.setStatus(1);
            departmentMapper.insert(dept);
        }
        
        log.info("部门数据初始化完成");
    }
    
    private void initEmployees() {
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        Long count = employeeMapper.selectCount(wrapper);
        if (count > 0) {
            log.info("员工数据已存在，跳过初始化");
            return;
        }
        
        log.info("初始化员工数据...");
        
        String[][] employees = {
            {"张三", "EMP001", "1", "13800138001", "zhangsan@library.com", "主管"},
            {"李四", "EMP002", "2", "13800138002", "lisi@library.com", "采购专员"},
            {"王五", "EMP003", "2", "13800138003", "wangwu@library.com", "采购专员"},
            {"赵六", "EMP004", "3", "13800138004", "zhaoliu@library.com", "技术工程师"},
            {"钱七", "EMP005", "1", "13800138005", "qianqi@library.com", "行政专员"}
        };
        
        for (String[] emp : employees) {
            Employee employee = new Employee();
            employee.setName(emp[0]);
            employee.setEmployeeNo(emp[1]);
            employee.setDepartmentId(Long.parseLong(emp[2]));
            employee.setPhone(emp[3]);
            employee.setEmail(emp[4]);
            employee.setPosition(emp[5]);
            employee.setStatus(1);
            employeeMapper.insert(employee);
        }
        
        log.info("员工数据初始化完成");
    }
    
    private void initProcurements() {
        LambdaQueryWrapper<Procurement> wrapper = new LambdaQueryWrapper<>();
        Long count = procurementMapper.selectCount(wrapper);
        if (count > 0) {
            log.info("采购数据已存在，跳过初始化");
            return;
        }
        
        log.info("初始化采购数据...");
        
        Procurement procurement1 = new Procurement();
        procurement1.setBatchNo("CG202401001");
        procurement1.setPurchaser("李四");
        procurement1.setEmployeeId(2L);
        procurement1.setDepartmentId(2L);
        procurement1.setTotalPrice(new BigDecimal("5800.00"));
        procurement1.setStatus(1);
        procurement1.setRemark("2024年第一季度图书采购");
        procurementMapper.insert(procurement1);
        
        ProcurementItem item1 = new ProcurementItem();
        item1.setProcurementId(procurement1.getId());
        item1.setBookCategory("文学小说");
        item1.setBookName("百年孤独");
        item1.setIsbn("9787544253994");
        item1.setQuantity(50);
        item1.setUnitPrice(new BigDecimal("39.80"));
        item1.setTotalPrice(new BigDecimal("1990.00"));
        procurementItemMapper.insert(item1);
        
        ProcurementItem item2 = new ProcurementItem();
        item2.setProcurementId(procurement1.getId());
        item2.setBookCategory("科技技术");
        item2.setBookName("Java编程思想");
        item2.setIsbn("9787111213826");
        item2.setQuantity(30);
        item2.setUnitPrice(new BigDecimal("109.00"));
        item2.setTotalPrice(new BigDecimal("3270.00"));
        procurementItemMapper.insert(item2);
        
        ProcurementItem item3 = new ProcurementItem();
        item3.setProcurementId(procurement1.getId());
        item3.setBookCategory("历史人文");
        item3.setBookName("人类简史");
        item3.setIsbn("9787508647357");
        item3.setQuantity(15);
        item3.setUnitPrice(new BigDecimal("36.00"));
        item3.setTotalPrice(new BigDecimal("540.00"));
        procurementItemMapper.insert(item3);
        
        Procurement procurement2 = new Procurement();
        procurement2.setBatchNo("CG202402001");
        procurement2.setPurchaser("王五");
        procurement2.setEmployeeId(3L);
        procurement2.setDepartmentId(2L);
        procurement2.setTotalPrice(new BigDecimal("3200.00"));
        procurement2.setStatus(0);
        procurement2.setRemark("儿童读物专项采购");
        procurementMapper.insert(procurement2);
        
        ProcurementItem item4 = new ProcurementItem();
        item4.setProcurementId(procurement2.getId());
        item4.setBookCategory("教育教材");
        item4.setBookName("安徒生童话");
        item4.setIsbn("9787532748907");
        item4.setQuantity(40);
        item4.setUnitPrice(new BigDecimal("25.00"));
        item4.setTotalPrice(new BigDecimal("1000.00"));
        procurementItemMapper.insert(item4);
        
        ProcurementItem item5 = new ProcurementItem();
        item5.setProcurementId(procurement2.getId());
        item5.setBookCategory("教育教材");
        item5.setBookName("格林童话");
        item5.setIsbn("9787020073191");
        item5.setQuantity(55);
        item5.setUnitPrice(new BigDecimal("40.00"));
        item5.setTotalPrice(new BigDecimal("2200.00"));
        procurementItemMapper.insert(item5);
        
        log.info("采购数据初始化完成");
    }
    
    private void initAttendanceData() {
        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
        Long count = attendanceMapper.selectCount(wrapper);
        if (count > 0) {
            log.info("考勤数据已存在，跳过初始化");
            return;
        }
        
        log.info("初始化考勤数据...");
        
        LocalDate baseDate = LocalDate.now().minusDays(10);
        
        for (int i = 0; i < 7; i++) {
            LocalDate date = baseDate.plusDays(i);
            if (date.getDayOfWeek().getValue() > 5) {
                continue;
            }
            
            for (long empId = 1; empId <= 5; empId++) {
                Attendance attendance = new Attendance();
                attendance.setEmployeeId(empId);
                attendance.setAttendanceDate(date);
                attendance.setCheckInTime(LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 
                    8 + (int)(Math.random() * 2), (int)(Math.random() * 30)));
                attendance.setCheckOutTime(LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 
                    18, (int)(Math.random() * 30)));
                attendance.setStatus(0);
                attendance.setDeviceNo("FACE00" + ((i % 3) + 1));
                attendance.setRemark("人脸设备采集");
                attendanceMapper.insert(attendance);
            }
        }
        
        log.info("考勤数据初始化完成");
    }
}
