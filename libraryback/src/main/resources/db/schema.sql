-- 图书采购批次表
CREATE TABLE IF NOT EXISTS `book_purchase` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `batch_no` varchar(50) NOT NULL COMMENT '批次号',
  `purchaser_id` bigint NOT NULL COMMENT '采购人ID（员工ID）',
  `purchaser_name` varchar(50) DEFAULT NULL COMMENT '采购人姓名',
  `total_price` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '批次总价',
  `total_quantity` int NOT NULL DEFAULT '0' COMMENT '总数量',
  `purchase_date` date DEFAULT NULL COMMENT '采购日期',
  `status` tinyint DEFAULT '0' COMMENT '状态：0-待审核，1-已审核，2-已完成',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_batch_no` (`batch_no`),
  KEY `idx_purchaser_id` (`purchaser_id`),
  KEY `idx_purchase_date` (`purchase_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书采购批次表';

-- 图书采购批次明细表
CREATE TABLE IF NOT EXISTS `book_purchase_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `purchase_id` bigint NOT NULL COMMENT '采购批次ID',
  `book_type` varchar(100) NOT NULL COMMENT '图书种类',
  `book_name` varchar(200) NOT NULL COMMENT '图书名称',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '单册数量',
  `unit_price` decimal(10,2) NOT NULL COMMENT '单册单价',
  `subtotal` decimal(12,2) NOT NULL COMMENT '小计金额',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_purchase_id` (`purchase_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书采购批次明细表';

-- 考勤记录表
CREATE TABLE IF NOT EXISTS `attendance_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` bigint NOT NULL COMMENT '员工ID',
  `employee_name` varchar(50) DEFAULT NULL COMMENT '员工姓名',
  `employee_no` varchar(50) DEFAULT NULL COMMENT '员工工号',
  `department_id` bigint DEFAULT NULL COMMENT '部门ID',
  `department_name` varchar(100) DEFAULT NULL COMMENT '部门名称',
  `attendance_date` date NOT NULL COMMENT '考勤日期',
  `check_in_time` datetime DEFAULT NULL COMMENT '上班打卡时间',
  `check_out_time` datetime DEFAULT NULL COMMENT '下班打卡时间',
  `check_in_device` varchar(50) DEFAULT NULL COMMENT '上班打卡设备ID',
  `check_out_device` varchar(50) DEFAULT NULL COMMENT '下班打卡设备ID',
  `check_in_type` tinyint DEFAULT NULL COMMENT '上班打卡类型：1-正常，2-迟到，3-缺卡',
  `check_out_type` tinyint DEFAULT NULL COMMENT '下班打卡类型：1-正常，2-早退，3-缺卡',
  `work_hours` decimal(4,1) DEFAULT NULL COMMENT '工作时长（小时）',
  `status` tinyint DEFAULT '1' COMMENT '状态：1-正常，2-异常，3-请假',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_employee_date` (`employee_id`, `attendance_date`),
  KEY `idx_employee_id` (`employee_id`),
  KEY `idx_department_id` (`department_id`),
  KEY `idx_attendance_date` (`attendance_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤记录表';

-- 人脸设备表
CREATE TABLE IF NOT EXISTS `face_device` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `device_no` varchar(50) NOT NULL COMMENT '设备编号',
  `device_name` varchar(100) NOT NULL COMMENT '设备名称',
  `location` varchar(200) DEFAULT NULL COMMENT '设备位置',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-离线，1-在线',
  `last_online_time` datetime DEFAULT NULL COMMENT '最后在线时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_no` (`device_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人脸设备表';

-- 给员工表添加登录相关字段
ALTER TABLE `sys_employee` ADD COLUMN `username` varchar(50) DEFAULT NULL COMMENT '登录账号' AFTER `employee_no`;
ALTER TABLE `sys_employee` ADD COLUMN `password` varchar(100) DEFAULT NULL COMMENT '登录密码' AFTER `username`;
ALTER TABLE `sys_employee` ADD COLUMN `face_id` varchar(100) DEFAULT NULL COMMENT '人脸ID' AFTER `password`;
ALTER TABLE `sys_employee` ADD UNIQUE KEY `uk_username` (`username`);

-- 插入测试数据

-- 插入人脸设备测试数据
INSERT INTO `face_device` (`device_no`, `device_name`, `location`, `status`, `last_online_time`) VALUES
('DEV001', '一楼大门考勤机', '一楼大厅入口', 1, NOW()),
('DEV002', '二楼考勤机', '二楼办公区入口', 1, NOW()),
('DEV003', '三楼考勤机', '三楼办公区入口', 1, NOW());

-- 更新员工表添加登录账号密码
UPDATE `sys_employee` SET `username` = CONCAT('emp', id), `password` = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH' WHERE `username` IS NULL;

-- 插入采购批次测试数据
INSERT INTO `book_purchase` (`batch_no`, `purchaser_id`, `purchaser_name`, `total_price`, `total_quantity`, `purchase_date`, `status`, `remark`) VALUES
('PO2024010001', 1, '张三', 1500.00, 50, '2024-01-15', 1, '第一批采购'),
('PO2024010002', 2, '李四', 2800.00, 80, '2024-01-20', 2, '第二批采购'),
('PO2024020001', 1, '张三', 950.00, 30, '2024-02-05', 0, '待审核采购');

-- 插入采购明细测试数据
INSERT INTO `book_purchase_item` (`purchase_id`, `book_type`, `book_name`, `quantity`, `unit_price`, `subtotal`) VALUES
(1, '计算机', 'Java编程思想', 20, 45.00, 900.00),
(1, '计算机', 'Python入门到精通', 15, 40.00, 600.00),
(2, '文学', '红楼梦', 30, 35.00, 1050.00),
(2, '文学', '三国演义', 25, 38.00, 950.00),
(2, '历史', '史记', 25, 32.00, 800.00),
(3, '科技', '人工智能导论', 10, 55.00, 550.00),
(3, '科技', '机器学习实战', 20, 20.00, 400.00);

-- 插入考勤记录测试数据
INSERT INTO `attendance_record` (`employee_id`, `employee_name`, `employee_no`, `department_id`, `department_name`, `attendance_date`, `check_in_time`, `check_out_time`, `check_in_device`, `check_out_device`, `check_in_type`, `check_out_type`, `work_hours`, `status`) VALUES
(1, '张三', 'EMP001', 1, '技术部', '2024-01-15', '2024-01-15 08:55:00', '2024-01-15 18:05:00', 'DEV001', 'DEV001', 1, 1, 9.2, 1),
(1, '张三', 'EMP001', 1, '技术部', '2024-01-16', '2024-01-16 09:10:00', '2024-01-16 18:30:00', 'DEV001', 'DEV001', 2, 1, 9.3, 2),
(1, '张三', 'EMP001', 1, '技术部', '2024-01-17', '2024-01-17 08:50:00', '2024-01-17 17:30:00', 'DEV001', 'DEV001', 1, 2, 8.7, 2),
(2, '李四', 'EMP002', 2, '市场部', '2024-01-15', '2024-01-15 08:45:00', '2024-01-15 18:10:00', 'DEV002', 'DEV002', 1, 1, 9.4, 1),
(2, '李四', 'EMP002', 2, '市场部', '2024-01-16', '2024-01-16 08:58:00', '2024-01-16 18:00:00', 'DEV002', 'DEV002', 1, 1, 9.0, 1),
(3, '王五', 'EMP003', 1, '技术部', '2024-01-15', '2024-01-15 09:20:00', '2024-01-15 18:30:00', 'DEV001', 'DEV001', 2, 1, 9.2, 2);
