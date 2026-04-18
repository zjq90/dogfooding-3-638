-- 图书后台管理系统数据库脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS library_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE library_db;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像URL',
    role TINYINT DEFAULT 0 COMMENT '角色：0-普通用户，1-管理员',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_username (username),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 图书分类表
CREATE TABLE IF NOT EXISTS book_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    code VARCHAR(30) NOT NULL UNIQUE COMMENT '分类编码',
    description VARCHAR(255) COMMENT '分类描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID，0为顶级分类',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_parent_id (parent_id),
    INDEX idx_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书分类表';

-- 图书信息表
CREATE TABLE IF NOT EXISTS book_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '图书ID',
    isbn VARCHAR(20) NOT NULL UNIQUE COMMENT 'ISBN编号',
    title VARCHAR(100) NOT NULL COMMENT '书名',
    author VARCHAR(50) COMMENT '作者',
    publisher VARCHAR(100) COMMENT '出版社',
    publish_date DATE COMMENT '出版日期',
    category_id BIGINT COMMENT '分类ID',
    description TEXT COMMENT '图书简介',
    cover_image VARCHAR(255) COMMENT '封面图片',
    price DECIMAL(10,2) COMMENT '价格',
    total_quantity INT DEFAULT 0 COMMENT '总库存',
    available_quantity INT DEFAULT 0 COMMENT '可借数量',
    location VARCHAR(50) COMMENT '存放位置',
    status TINYINT DEFAULT 1 COMMENT '状态：0-下架，1-可借，2-借完',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_isbn (isbn),
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_title (title),
    FOREIGN KEY (category_id) REFERENCES book_category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书信息表';

-- 借阅记录表
CREATE TABLE IF NOT EXISTS borrow_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    book_id BIGINT NOT NULL COMMENT '图书ID',
    borrow_date DATE NOT NULL COMMENT '借阅日期',
    due_date DATE NOT NULL COMMENT '应还日期',
    return_date DATE COMMENT '实际归还日期',
    status TINYINT DEFAULT 0 COMMENT '状态：0-借阅中，1-已归还，2-逾期',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_user_id (user_id),
    INDEX idx_book_id (book_id),
    INDEX idx_status (status),
    INDEX idx_borrow_date (borrow_date),
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    FOREIGN KEY (book_id) REFERENCES book_info(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅记录表';

-- 默认管理员用户由应用启动时自动创建
-- 用户名: admin, 密码: admin123

-- 插入图书分类数据
INSERT INTO book_category (name, code, description, sort_order, parent_id) VALUES
('文学', 'LITERATURE', '文学类图书', 1, 0),
('小说', 'NOVEL', '小说类图书', 2, 0),
('科技', 'TECHNOLOGY', '科技类图书', 3, 0),
('历史', 'HISTORY', '历史类图书', 4, 0),
('艺术', 'ART', '艺术类图书', 5, 0),
('教育', 'EDUCATION', '教育类图书', 6, 0),
('经济', 'ECONOMICS', '经济类图书', 7, 0),
('医学', 'MEDICINE', '医学类图书', 8, 0);

-- 插入示例图书数据
INSERT INTO book_info (isbn, title, author, publisher, publish_date, category_id, description, price, total_quantity, available_quantity, location, status) VALUES
('978-7-111-1', '红楼梦', '曹雪芹', '人民文学出版社', '2020-01-15', 1, '中国古典文学四大名著之一', 45.00, 10, 8, 'A区-01-01', 1),
('978-7-111-2', '西游记', '吴承恩', '人民文学出版社', '2020-03-20', 1, '中国古典文学四大名著之一', 42.00, 8, 6, 'A区-01-02', 1),
('978-7-111-3', '三体', '刘慈欣', '重庆出版社', '2019-06-01', 2, '科幻小说巅峰之作', 58.00, 15, 12, 'B区-02-01', 1),
('978-7-111-4', 'Java编程思想', 'Bruce Eckel', '机械工业出版社', '2021-08-10', 3, 'Java程序员必读经典', 108.00, 5, 3, 'C区-03-01', 1),
('978-7-111-5', 'Spring实战', 'Craig Walls', '人民邮电出版社', '2022-02-28', 3, 'Spring框架实战指南', 89.00, 6, 4, 'C区-03-02', 1),
('978-7-111-6', '明朝那些事儿', '当年明月', '中国友谊出版公司', '2018-11-01', 4, '历史通俗读物', 168.00, 7, 5, 'D区-04-01', 1),
('978-7-111-7', '艺术的故事', '贡布里希', '广西美术出版社', '2017-05-15', 5, '艺术史经典著作', 280.00, 3, 2, 'E区-05-01', 1),
('978-7-111-8', '深度学习', 'Ian Goodfellow', '人民邮电出版社', '2021-09-20', 3, '人工智能领域经典教材', 128.00, 4, 2, 'C区-03-03', 1);

-- 部门表
CREATE TABLE IF NOT EXISTS sys_department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '部门ID',
    name VARCHAR(50) NOT NULL COMMENT '部门名称',
    code VARCHAR(30) NOT NULL UNIQUE COMMENT '部门编号(格式：B-数字)',
    parent_id BIGINT DEFAULT 0 COMMENT '上级部门ID，0为顶级部门',
    description VARCHAR(255) COMMENT '部门描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_code (code),
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 内部人员表
CREATE TABLE IF NOT EXISTS sys_employee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '人员ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    employee_no VARCHAR(30) NOT NULL UNIQUE COMMENT '工号',
    department_id BIGINT NOT NULL COMMENT '所属部门ID',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    position VARCHAR(50) COMMENT '职位',
    status TINYINT DEFAULT 1 COMMENT '状态：0-离职，1-在职',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_employee_no (employee_no),
    INDEX idx_department_id (department_id),
    INDEX idx_status (status),
    FOREIGN KEY (department_id) REFERENCES sys_department(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内部人员表';

-- 权限表（菜单、按钮、信息权限）
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '权限ID',
    name VARCHAR(50) NOT NULL COMMENT '权限名称',
    code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码',
    type TINYINT NOT NULL COMMENT '权限类型：1-菜单，2-按钮，3-信息',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID',
    path VARCHAR(255) COMMENT '菜单路径',
    component VARCHAR(255) COMMENT '组件路径',
    icon VARCHAR(50) COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_code (code),
    INDEX idx_parent_id (parent_id),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 部门权限关联表
CREATE TABLE IF NOT EXISTS sys_department_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    department_id BIGINT NOT NULL COMMENT '部门ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_dept_perm (department_id, permission_id),
    INDEX idx_department_id (department_id),
    INDEX idx_permission_id (permission_id),
    FOREIGN KEY (department_id) REFERENCES sys_department(id),
    FOREIGN KEY (permission_id) REFERENCES sys_permission(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门权限关联表';

-- 插入部门测试数据
INSERT INTO sys_department (name, code, parent_id, description, sort_order, status) VALUES
('总公司', 'B-001', 0, '总公司', 1, 1),
('技术部', 'B-002', 1, '技术研发部门', 1, 1),
('市场部', 'B-003', 1, '市场营销部门', 2, 1),
('人事部', 'B-004', 1, '人力资源部门', 3, 1),
('财务部', 'B-005', 1, '财务管理部门', 4, 1),
('前端开发组', 'B-006', 2, '前端开发小组', 1, 1),
('后端开发组', 'B-007', 2, '后端开发小组', 2, 1);

-- 插入权限测试数据
INSERT INTO sys_permission (name, code, type, parent_id, path, component, icon, sort_order, status) VALUES
('数据概览', 'dashboard', 1, 0, '/dashboard', 'dashboard/index', 'el-icon-s-data', 1, 1),
('图书管理', 'books', 1, 0, '/books', 'book/index', 'el-icon-reading', 2, 1),
('分类管理', 'categories', 1, 0, '/categories', 'category/index', 'el-icon-folder-opened', 3, 1),
('借阅管理', 'borrow', 1, 0, '/borrow', 'borrow/index', 'el-icon-document', 4, 1),
('用户管理', 'users', 1, 0, '/users', 'user/index', 'el-icon-user', 5, 1),
('部门管理', 'departments', 1, 0, '/departments', 'department/index', 'el-icon-office-building', 6, 1),
('人员管理', 'employees', 1, 0, '/employees', 'employee/index', 'el-icon-s-custom', 7, 1),
('新增图书', 'books:add', 2, 2, NULL, NULL, NULL, 1, 1),
('编辑图书', 'books:edit', 2, 2, NULL, NULL, NULL, 2, 1),
('删除图书', 'books:delete', 2, 2, NULL, NULL, NULL, 3, 1),
('新增用户', 'users:add', 2, 5, NULL, NULL, NULL, 1, 1),
('编辑用户', 'users:edit', 2, 5, NULL, NULL, NULL, 2, 1),
('删除用户', 'users:delete', 2, 5, NULL, NULL, NULL, 3, 1),
('查看手机号', 'info:phone', 3, 0, NULL, NULL, NULL, 1, 1),
('查看邮箱', 'info:email', 3, 0, NULL, NULL, NULL, 2, 1);

-- 插入内部人员测试数据
INSERT INTO sys_employee (name, employee_no, department_id, phone, email, position, status) VALUES
('张三', 'E001', 2, '13800138001', 'zhangsan@library.com', '前端工程师', 1),
('李四', 'E002', 2, '13800138002', 'lisi@library.com', '后端工程师', 1),
('王五', 'E003', 3, '13800138003', 'wangwu@library.com', '市场经理', 1),
('赵六', 'E004', 4, '13800138004', 'zhaoliu@library.com', '人事专员', 1),
('钱七', 'E005', 5, '13800138005', 'qianqi@library.com', '财务主管', 1),
('孙八', 'E006', 6, '13800138006', 'sunba@library.com', '前端组长', 1),
('周九', 'E007', 7, '13800138007', 'zhoujiu@library.com', '后端组长', 1);

-- 插入部门权限关联测试数据（技术部拥有图书管理、借阅管理权限）
INSERT INTO sys_department_permission (department_id, permission_id) VALUES
(2, 1), (2, 2), (2, 4), (2, 8), (2, 9),
(3, 1), (3, 3), (3, 4),
(4, 1), (4, 5), (4, 6), (4, 7), (4, 11), (4, 12),
(5, 1), (5, 4),
(6, 1), (6, 2), (6, 8), (6, 9),
(7, 1), (7, 2), (7, 4), (7, 8), (7, 9);

-- ============================================
-- 新增功能：图书采购管理模块
-- ============================================

-- 图书采购批次表
CREATE TABLE IF NOT EXISTS book_purchase_batch (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '批次ID',
    batch_no VARCHAR(50) NOT NULL UNIQUE COMMENT '批次编号',
    batch_name VARCHAR(100) NOT NULL COMMENT '批次名称',
    purchaser_id BIGINT NOT NULL COMMENT '采购人ID（内部人员）',
    purchaser_name VARCHAR(50) COMMENT '采购人姓名',
    department_id BIGINT NOT NULL COMMENT '采购部门ID',
    total_amount DECIMAL(12,2) DEFAULT 0 COMMENT '批次总价',
    total_books INT DEFAULT 0 COMMENT '图书总册数',
    total_types INT DEFAULT 0 COMMENT '图书种类数',
    purchase_date DATE COMMENT '采购日期',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待审核，1-已审核，2-已完成，3-已取消',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_batch_no (batch_no),
    INDEX idx_purchaser_id (purchaser_id),
    INDEX idx_department_id (department_id),
    INDEX idx_status (status),
    INDEX idx_purchase_date (purchase_date),
    FOREIGN KEY (purchaser_id) REFERENCES sys_employee(id),
    FOREIGN KEY (department_id) REFERENCES sys_department(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书采购批次表';

-- 图书采购明细表
CREATE TABLE IF NOT EXISTS book_purchase_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '明细ID',
    batch_id BIGINT NOT NULL COMMENT '批次ID',
    book_type VARCHAR(50) NOT NULL COMMENT '图书种类/分类',
    book_name VARCHAR(100) NOT NULL COMMENT '图书名称',
    isbn VARCHAR(20) COMMENT 'ISBN编号',
    author VARCHAR(50) COMMENT '作者',
    publisher VARCHAR(100) COMMENT '出版社',
    quantity INT NOT NULL DEFAULT 1 COMMENT '单册数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单册单价',
    total_price DECIMAL(12,2) NOT NULL COMMENT '小计金额',
    category_id BIGINT COMMENT '图书分类ID',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_batch_id (batch_id),
    INDEX idx_book_name (book_name),
    INDEX idx_isbn (isbn),
    INDEX idx_category_id (category_id),
    FOREIGN KEY (batch_id) REFERENCES book_purchase_batch(id),
    FOREIGN KEY (category_id) REFERENCES book_category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书采购明细表';

-- ============================================
-- 新增功能：内部人员考勤管理模块
-- ============================================

-- 考勤记录表
CREATE TABLE IF NOT EXISTS attendance_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    employee_no VARCHAR(30) COMMENT '员工工号',
    employee_name VARCHAR(50) COMMENT '员工姓名',
    department_id BIGINT COMMENT '部门ID',
    department_name VARCHAR(50) COMMENT '部门名称',
    attendance_date DATE NOT NULL COMMENT '考勤日期',
    check_in_time TIME COMMENT '上班时间',
    check_out_time TIME COMMENT '下班时间',
    check_in_device VARCHAR(50) COMMENT '上班打卡设备',
    check_out_device VARCHAR(50) COMMENT '下班打卡设备',
    check_in_location VARCHAR(100) COMMENT '上班打卡地点',
    check_out_location VARCHAR(100) COMMENT '下班打卡地点',
    work_hours DECIMAL(4,1) COMMENT '工作时长（小时）',
    status TINYINT DEFAULT 0 COMMENT '状态：0-正常，1-迟到，2-早退，3-缺勤，4-请假，5-加班',
    overtime_hours DECIMAL(4,1) DEFAULT 0 COMMENT '加班时长',
    leave_type TINYINT COMMENT '请假类型：1-事假，2-病假，3-年假，4-调休',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_employee_id (employee_id),
    INDEX idx_department_id (department_id),
    INDEX idx_attendance_date (attendance_date),
    INDEX idx_status (status),
    UNIQUE KEY uk_emp_date (employee_id, attendance_date),
    FOREIGN KEY (employee_id) REFERENCES sys_employee(id),
    FOREIGN KEY (department_id) REFERENCES sys_department(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤记录表';

-- 考勤统计表（按月统计）
CREATE TABLE IF NOT EXISTS attendance_statistics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '统计ID',
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    employee_name VARCHAR(50) COMMENT '员工姓名',
    department_id BIGINT COMMENT '部门ID',
    department_name VARCHAR(50) COMMENT '部门名称',
    statistics_year INT NOT NULL COMMENT '统计年份',
    statistics_month INT NOT NULL COMMENT '统计月份',
    total_days INT DEFAULT 0 COMMENT '应出勤天数',
    actual_days INT DEFAULT 0 COMMENT '实际出勤天数',
    normal_days INT DEFAULT 0 COMMENT '正常出勤天数',
    late_count INT DEFAULT 0 COMMENT '迟到次数',
    early_leave_count INT DEFAULT 0 COMMENT '早退次数',
    absent_count INT DEFAULT 0 COMMENT '缺勤次数',
    leave_days DECIMAL(4,1) DEFAULT 0 COMMENT '请假天数',
    overtime_hours DECIMAL(6,1) DEFAULT 0 COMMENT '加班总时长',
    total_work_hours DECIMAL(8,1) DEFAULT 0 COMMENT '总工作时长',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_employee_id (employee_id),
    INDEX idx_department_id (department_id),
    INDEX idx_year_month (statistics_year, statistics_month),
    UNIQUE KEY uk_emp_year_month (employee_id, statistics_year, statistics_month),
    FOREIGN KEY (employee_id) REFERENCES sys_employee(id),
    FOREIGN KEY (department_id) REFERENCES sys_department(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤统计表';

-- 人脸设备表
CREATE TABLE IF NOT EXISTS face_device (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '设备ID',
    device_no VARCHAR(50) NOT NULL UNIQUE COMMENT '设备编号',
    device_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    device_type VARCHAR(30) COMMENT '设备类型',
    location VARCHAR(100) COMMENT '安装位置',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    status TINYINT DEFAULT 1 COMMENT '状态：0-离线，1-在线，2-故障',
    last_heartbeat DATETIME COMMENT '最后心跳时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_device_no (device_no),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人脸设备表';

-- 新增权限数据（图书采购、考勤管理、行政管理）
INSERT INTO sys_permission (name, code, type, parent_id, path, component, icon, sort_order, status) VALUES
-- 图书采购菜单
('图书采购', 'purchase', 1, 0, '/purchase', NULL, 'el-icon-shopping-cart-full', 8, 1),
('采购批次', 'purchase:batch', 1, 15, '/purchase/batch', 'purchase/batch/index', 'el-icon-s-order', 1, 1),
('采购明细', 'purchase:detail', 1, 15, '/purchase/detail', 'purchase/detail/index', 'el-icon-s-goods', 2, 1),
-- 行政管理菜单
('行政管理', 'administration', 1, 0, '/administration', NULL, 'el-icon-s-management', 9, 1),
('考勤统计', 'administration:attendance', 1, 18, '/administration/attendance', 'administration/attendance/index', 'el-icon-time', 1, 1),
('人员管理', 'administration:employee', 1, 18, '/administration/employee', 'administration/employee/index', 'el-icon-user-solid', 2, 1),
-- 按钮权限
('新增采购', 'purchase:add', 2, 16, NULL, NULL, NULL, 1, 1),
('编辑采购', 'purchase:edit', 2, 16, NULL, NULL, NULL, 2, 1),
('删除采购', 'purchase:delete', 2, 16, NULL, NULL, NULL, 3, 1),
('审核采购', 'purchase:audit', 2, 16, NULL, NULL, NULL, 4, 1),
('导出考勤', 'attendance:export', 2, 19, NULL, NULL, NULL, 1, 1),
('生成考勤', 'attendance:generate', 2, 19, NULL, NULL, NULL, 2, 1);

-- ============================================
-- 测试数据
-- ============================================

-- 人脸设备测试数据
INSERT INTO face_device (device_no, device_name, device_type, location, ip_address, status) VALUES
('FACE001', '正门入口设备', '人脸识别门禁', '公司正门入口', '192.168.1.101', 1),
('FACE002', '后门入口设备', '人脸识别门禁', '公司后门入口', '192.168.1.102', 1),
('FACE003', '办公区设备A', '人脸识别考勤机', '办公区A区', '192.168.1.103', 1),
('FACE004', '办公区设备B', '人脸识别考勤机', '办公区B区', '192.168.1.104', 1);

-- 图书采购批次测试数据
INSERT INTO book_purchase_batch (batch_no, batch_name, purchaser_id, purchaser_name, department_id, total_amount, total_books, total_types, purchase_date, status, remark) VALUES
('CG202504140001', '2025年第一季度图书采购', 1, '张三', 2, 2580.00, 45, 8, '2025-03-15', 1, '技术部第一季度图书采购'),
('CG202504140002', '文学类图书采购', 2, '李四', 2, 1560.50, 32, 5, '2025-04-01', 0, '文学类图书补充采购'),
('CG202504140003', '科技类图书采购', 3, '王五', 3, 3280.00, 28, 6, '2025-04-05', 2, '市场部科技类图书采购');

-- 图书采购明细测试数据
INSERT INTO book_purchase_detail (batch_id, book_type, book_name, isbn, author, publisher, quantity, unit_price, total_price, category_id, remark) VALUES
(1, '计算机技术', '深入理解Java虚拟机', '978-7-111-34966-2', '周志明', '机械工业出版社', 5, 79.00, 395.00, 3, '技术部推荐'),
(1, '计算机技术', 'Spring Boot实战', '978-7-115-45678-9', 'Craig Walls', '人民邮电出版社', 8, 89.00, 712.00, 3, '开发必备'),
(1, '人工智能', '深度学习', '978-7-115-46105-0', 'Ian Goodfellow', '人民邮电出版社', 3, 128.00, 384.00, 3, 'AI学习'),
(1, '文学', '活着', '978-7-5063-4516-8', '余华', '作家出版社', 10, 35.00, 350.00, 1, '经典文学'),
(1, '文学', '围城', '978-7-0200-3273-2', '钱钟书', '人民文学出版社', 8, 39.00, 312.00, 1, '经典文学'),
(1, '历史', '明朝那些事儿', '978-7-5057-2461-1', '当年明月', '中国友谊出版公司', 5, 168.00, 840.00, 4, '历史读物'),
(1, '经济', '经济学原理', '978-7-3011-7891-2', '曼昆', '北京大学出版社', 3, 88.00, 264.00, 7, '经济学基础'),
(1, '教育', '如何阅读一本书', '978-7-1080-2935-2', '莫提默·艾德勒', '商务印书馆', 3, 38.00, 114.00, 6, '阅读方法'),
(2, '文学', '红楼梦', '978-7-0200-0220-7', '曹雪芹', '人民文学出版社', 10, 45.00, 450.00, 1, '四大名著'),
(2, '文学', '西游记', '978-7-0200-0221-4', '吴承恩', '人民文学出版社', 8, 42.00, 336.00, 1, '四大名著'),
(2, '文学', '三国演义', '978-7-0200-0222-1', '罗贯中', '人民文学出版社', 8, 48.00, 384.00, 1, '四大名著'),
(2, '文学', '水浒传', '978-7-0200-0223-8', '施耐庵', '人民文学出版社', 6, 56.00, 336.00, 1, '四大名著'),
(2, '小说', '三体全集', '978-7-2290-3093-3', '刘慈欣', '重庆出版社', 3, 98.00, 294.00, 2, '科幻经典'),
(3, '科技', 'Python编程从入门到实践', '978-7-1154-2822-8', 'Eric Matthes', '人民邮电出版社', 10, 89.00, 890.00, 3, '编程入门'),
(3, '科技', '算法导论', '978-7-1114-0777-5', 'Thomas H. Cormen', '机械工业出版社', 5, 128.00, 640.00, 3, '算法经典'),
(3, '科技', 'Clean Code', '978-7-1152-5876-6', 'Robert C. Martin', '电子工业出版社', 6, 68.00, 408.00, 3, '代码规范'),
(3, '科技', '设计模式', '978-7-1110-7575-2', 'Erich Gamma', '机械工业出版社', 4, 89.00, 356.00, 3, '设计模式'),
(3, '医学', '人体解剖学', '978-7-1172-3456-7', '柏树令', '人民卫生出版社', 3, 168.00, 504.00, 8, '医学教材'),
(3, '艺术', '艺术的故事', '978-7-5495-6789-0', '贡布里希', '广西美术出版社', 2, 280.00, 560.00, 5, '艺术史经典');

-- 考勤记录测试数据
INSERT INTO attendance_record (employee_id, employee_no, employee_name, department_id, department_name, attendance_date, check_in_time, check_out_time, check_in_device, check_out_device, work_hours, status, overtime_hours, remark) VALUES
(1, 'E001', '张三', 2, '技术部', '2025-04-14', '08:55:00', '18:30:00', 'FACE001', 'FACE001', 9.5, 0, 0.5, '正常出勤'),
(2, 'E002', '李四', 2, '技术部', '2025-04-14', '09:10:00', '18:00:00', 'FACE001', 'FACE001', 8.8, 1, 0, '迟到10分钟'),
(3, 'E003', '王五', 3, '市场部', '2025-04-14', '08:50:00', '19:00:00', 'FACE002', 'FACE002', 10.2, 5, 1.0, '加班'),
(4, 'E004', '赵六', 4, '人事部', '2025-04-14', '09:00:00', '18:00:00', 'FACE003', 'FACE003', 9.0, 0, 0, '正常出勤'),
(5, 'E005', '钱七', 5, '财务部', '2025-04-14', '08:45:00', '17:30:00', 'FACE004', 'FACE004', 8.8, 2, 0, '早退30分钟'),
(6, 'E006', '孙八', 6, '前端开发组', '2025-04-14', '09:05:00', '18:15:00', 'FACE003', 'FACE003', 9.2, 1, 0, '迟到5分钟'),
(7, 'E007', '周九', 7, '后端开发组', '2025-04-14', '08:58:00', '18:45:00', 'FACE003', 'FACE003', 9.8, 0, 0.8, '正常出勤，有加班'),
(1, 'E001', '张三', 2, '技术部', '2025-04-13', '08:52:00', '18:10:00', 'FACE001', 'FACE001', 9.3, 0, 0, '正常出勤'),
(2, 'E002', '李四', 2, '技术部', '2025-04-13', '09:00:00', '18:00:00', 'FACE001', 'FACE001', 9.0, 0, 0, '正常出勤'),
(3, 'E003', '王五', 3, '市场部', '2025-04-13', NULL, NULL, NULL, NULL, NULL, 3, 0, '缺勤');

-- 考勤统计测试数据
INSERT INTO attendance_statistics (employee_id, employee_name, department_id, department_name, statistics_year, statistics_month, total_days, actual_days, normal_days, late_count, early_leave_count, absent_count, leave_days, overtime_hours, total_work_hours) VALUES
(1, '张三', 2, '技术部', 2025, 4, 22, 20, 18, 2, 0, 0, 0, 3.5, 185.5),
(2, '李四', 2, '技术部', 2025, 4, 22, 20, 17, 3, 0, 0, 0, 0, 178.0),
(3, '王五', 3, '市场部', 2025, 4, 22, 19, 15, 1, 0, 1, 0, 5.0, 175.5),
(4, '赵六', 4, '人事部', 2025, 4, 22, 21, 20, 1, 0, 0, 0, 0, 189.0),
(5, '钱七', 5, '财务部', 2025, 4, 22, 20, 18, 0, 2, 0, 0, 0, 176.0),
(6, '孙八', 6, '前端开发组', 2025, 4, 22, 21, 19, 2, 0, 0, 0, 2.5, 188.5),
(7, '周九', 7, '后端开发组', 2025, 4, 22, 21, 20, 1, 0, 0, 0, 4.0, 192.0);

-- 更新部门权限关联（添加新菜单权限）
INSERT INTO sys_department_permission (department_id, permission_id) VALUES
(2, 15), (2, 16), (2, 17), (2, 20), (2, 21),
(3, 15), (3, 16), (3, 17),
(4, 18), (4, 19), (4, 20), (4, 21),
(5, 18), (5, 19), (5, 20);
