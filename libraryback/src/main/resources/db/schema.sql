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
