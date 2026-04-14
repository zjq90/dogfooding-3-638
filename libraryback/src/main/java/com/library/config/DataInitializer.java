package com.library.config;

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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
    private BookPurchaseMapper bookPurchaseMapper;
    
    @Autowired
    private BookPurchaseItemMapper bookPurchaseItemMapper;
    
    @Autowired
    private AttendanceRecordMapper attendanceRecordMapper;
    
    @Autowired
    private FaceDeviceMapper faceDeviceMapper;

    @Override
    public void run(String... args) throws Exception {
        initAdminUser();
        initDepartments();
        initEmployees();
        initFaceDevices();
        initBookPurchases();
        initAttendanceRecords();
    }

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
        Long count = departmentMapper.selectCount(null);
        if (count > 0) {
            log.info("部门数据已存在，跳过初始化");
            return;
        }
        
        log.info("初始化部门数据...");
        
        Department tech = new Department();
        tech.setName("技术部");
        tech.setCode("TECH");
        tech.setDescription("负责技术研发");
        tech.setSortOrder(1);
        tech.setStatus(1);
        departmentMapper.insert(tech);
        
        Department market = new Department();
        market.setName("市场部");
        market.setCode("MARKET");
        market.setDescription("负责市场推广");
        market.setSortOrder(2);
        market.setStatus(1);
        departmentMapper.insert(market);
        
        Department hr = new Department();
        hr.setName("人事部");
        hr.setCode("HR");
        hr.setDescription("负责人力资源管理");
        hr.setSortOrder(3);
        hr.setStatus(1);
        departmentMapper.insert(hr);
        
        Department finance = new Department();
        finance.setName("财务部");
        finance.setCode("FINANCE");
        finance.setDescription("负责财务管理");
        finance.setSortOrder(4);
        finance.setStatus(1);
        departmentMapper.insert(finance);
        
        log.info("部门数据初始化完成");
    }
    
    private void initEmployees() {
        Long count = employeeMapper.selectCount(null);
        if (count > 0) {
            log.info("员工数据已存在，跳过初始化");
            return;
        }
        
        log.info("初始化员工数据...");
        
        createEmployee("张三", "EMP001", "emp001", "123456", 1L, "软件工程师", "13800138001", "zhangsan@library.com");
        createEmployee("李四", "EMP002", "emp002", "123456", 2L, "市场经理", "13800138002", "lisi@library.com");
        createEmployee("王五", "EMP003", "emp003", "123456", 1L, "前端工程师", "13800138003", "wangwu@library.com");
        createEmployee("赵六", "EMP004", "emp004", "123456", 3L, "人事专员", "13800138004", "zhaoliu@library.com");
        createEmployee("钱七", "EMP005", "emp005", "123456", 4L, "财务主管", "13800138005", "qianqi@library.com");
        
        log.info("员工数据初始化完成，默认密码: 123456");
    }
    
    private void createEmployee(String name, String employeeNo, String username, String password, Long deptId, String position, String phone, String email) {
        Employee emp = new Employee();
        emp.setName(name);
        emp.setEmployeeNo(employeeNo);
        emp.setUsername(username);
        emp.setPassword(PasswordUtil.encode(password));
        emp.setDepartmentId(deptId);
        emp.setPosition(position);
        emp.setPhone(phone);
        emp.setEmail(email);
        emp.setStatus(1);
        employeeMapper.insert(emp);
    }
    
    private void initFaceDevices() {
        Long count = faceDeviceMapper.selectCount(null);
        if (count > 0) {
            log.info("人脸设备数据已存在，跳过初始化");
            return;
        }
        
        log.info("初始化人脸设备数据...");
        
        FaceDevice dev1 = new FaceDevice();
        dev1.setDeviceNo("DEV001");
        dev1.setDeviceName("一楼大门考勤机");
        dev1.setLocation("一楼大厅入口");
        dev1.setStatus(1);
        dev1.setLastOnlineTime(LocalDateTime.now());
        faceDeviceMapper.insert(dev1);
        
        FaceDevice dev2 = new FaceDevice();
        dev2.setDeviceNo("DEV002");
        dev2.setDeviceName("二楼考勤机");
        dev2.setLocation("二楼办公区入口");
        dev2.setStatus(1);
        dev2.setLastOnlineTime(LocalDateTime.now());
        faceDeviceMapper.insert(dev2);
        
        FaceDevice dev3 = new FaceDevice();
        dev3.setDeviceNo("DEV003");
        dev3.setDeviceName("三楼考勤机");
        dev3.setLocation("三楼办公区入口");
        dev3.setStatus(1);
        dev3.setLastOnlineTime(LocalDateTime.now());
        faceDeviceMapper.insert(dev3);
        
        log.info("人脸设备数据初始化完成");
    }
    
    private void initBookPurchases() {
        Long count = bookPurchaseMapper.selectCount(null);
        if (count > 0) {
            log.info("采购数据已存在，跳过初始化");
            return;
        }
        
        log.info("初始化采购数据...");
        
        BookPurchase purchase1 = new BookPurchase();
        purchase1.setBatchNo("PO2024010001");
        purchase1.setPurchaserId(1L);
        purchase1.setPurchaserName("张三");
        purchase1.setTotalPrice(new BigDecimal("1500.00"));
        purchase1.setTotalQuantity(35);
        purchase1.setPurchaseDate(LocalDate.of(2024, 1, 15));
        purchase1.setStatus(1);
        purchase1.setRemark("第一批采购");
        bookPurchaseMapper.insert(purchase1);
        
        createPurchaseItem(purchase1.getId(), "计算机", "Java编程思想", 20, new BigDecimal("45.00"));
        createPurchaseItem(purchase1.getId(), "计算机", "Python入门到精通", 15, new BigDecimal("40.00"));
        
        BookPurchase purchase2 = new BookPurchase();
        purchase2.setBatchNo("PO2024010002");
        purchase2.setPurchaserId(2L);
        purchase2.setPurchaserName("李四");
        purchase2.setTotalPrice(new BigDecimal("2800.00"));
        purchase2.setTotalQuantity(80);
        purchase2.setPurchaseDate(LocalDate.of(2024, 1, 20));
        purchase2.setStatus(2);
        purchase2.setRemark("第二批采购");
        bookPurchaseMapper.insert(purchase2);
        
        createPurchaseItem(purchase2.getId(), "文学", "红楼梦", 30, new BigDecimal("35.00"));
        createPurchaseItem(purchase2.getId(), "文学", "三国演义", 25, new BigDecimal("38.00"));
        createPurchaseItem(purchase2.getId(), "历史", "史记", 25, new BigDecimal("32.00"));
        
        log.info("采购数据初始化完成");
    }
    
    private void createPurchaseItem(Long purchaseId, String bookType, String bookName, Integer quantity, BigDecimal unitPrice) {
        BookPurchaseItem item = new BookPurchaseItem();
        item.setPurchaseId(purchaseId);
        item.setBookType(bookType);
        item.setBookName(bookName);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);
        item.setSubtotal(unitPrice.multiply(BigDecimal.valueOf(quantity)));
        bookPurchaseItemMapper.insert(item);
    }
    
    private void initAttendanceRecords() {
        Long count = attendanceRecordMapper.selectCount(null);
        if (count > 0) {
            log.info("考勤数据已存在，跳过初始化");
            return;
        }
        
        log.info("初始化考勤数据...");
        
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 5; i++) {
            LocalDate date = today.minusDays(i);
            if (date.getDayOfWeek().getValue() >= 6) continue;
            
            createAttendanceRecord(1L, "张三", "EMP001", 1L, "技术部", date, 8, 55, 18, 5);
            createAttendanceRecord(2L, "李四", "EMP002", 2L, "市场部", date, 9, 10, 18, 30);
            createAttendanceRecord(3L, "王五", "EMP003", 1L, "技术部", date, 8, 45, 18, 0);
            createAttendanceRecord(4L, "赵六", "EMP004", 3L, "人事部", date, 9, 0, 17, 30);
            createAttendanceRecord(5L, "钱七", "EMP005", 4L, "财务部", date, 8, 50, 18, 10);
        }
        
        log.info("考勤数据初始化完成");
    }
    
    private void createAttendanceRecord(Long empId, String empName, String empNo, Long deptId, String deptName, 
                                        LocalDate date, int inHour, int inMin, int outHour, int outMin) {
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeId(empId);
        record.setEmployeeName(empName);
        record.setEmployeeNo(empNo);
        record.setDepartmentId(deptId);
        record.setDepartmentName(deptName);
        record.setAttendanceDate(date);
        
        LocalDateTime checkIn = LocalDateTime.of(date, LocalTime.of(inHour, inMin));
        LocalDateTime checkOut = LocalDateTime.of(date, LocalTime.of(outHour, outMin));
        
        record.setCheckInTime(checkIn);
        record.setCheckOutTime(checkOut);
        record.setCheckInDevice("DEV001");
        record.setCheckOutDevice("DEV001");
        
        if (inHour >= 9 && inMin > 0) {
            record.setCheckInType(2);
            record.setStatus(2);
        } else {
            record.setCheckInType(1);
            record.setStatus(1);
        }
        
        if (outHour < 18) {
            record.setCheckOutType(2);
            record.setStatus(2);
        } else {
            record.setCheckOutType(1);
        }
        
        long minutes = java.time.Duration.between(checkIn, checkOut).toMinutes();
        record.setWorkHours(BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 1, java.math.RoundingMode.HALF_UP));
        
        attendanceRecordMapper.insert(record);
    }
}
