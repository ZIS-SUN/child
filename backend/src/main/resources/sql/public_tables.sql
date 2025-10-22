-- ===================================================
-- 前台公开页面相关数据库表
-- ===================================================

-- 1. 每周食谱表
CREATE TABLE IF NOT EXISTS `weekly_menu` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `weekday` INT(1) NOT NULL COMMENT '星期几(1-5)',
  `menu_date` DATE NOT NULL COMMENT '食谱日期',
  `breakfast` TEXT COMMENT '早餐(JSON格式)',
  `snack1` TEXT COMMENT '上午加餐(JSON格式)',
  `lunch` TEXT COMMENT '午餐(JSON格式)',
  `snack2` TEXT COMMENT '下午点心(JSON格式)',
  `week_offset` INT(3) DEFAULT 0 COMMENT '周偏移量',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_menu_date` (`menu_date`),
  KEY `idx_week_offset` (`week_offset`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每周食谱表';

-- 2. 相册照片表
CREATE TABLE IF NOT EXISTS `album_photo` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` VARCHAR(200) NOT NULL COMMENT '照片标题',
  `category` VARCHAR(50) NOT NULL COMMENT '分类:activity/classroom/playground/meal',
  `image_url` VARCHAR(500) NOT NULL COMMENT '图片URL',
  `description` TEXT COMMENT '描述',
  `photo_date` DATETIME COMMENT '拍摄日期',
  `sort_order` INT(5) DEFAULT 0 COMMENT '排序',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态:0-隐藏 1-显示',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`),
  KEY `idx_photo_date` (`photo_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='相册照片表';

-- 3. 在线报名表
CREATE TABLE IF NOT EXISTS `enrollment` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `child_name` VARCHAR(50) NOT NULL COMMENT '孩子姓名',
  `child_gender` VARCHAR(10) NOT NULL COMMENT '性别',
  `child_birthday` DATE NOT NULL COMMENT '出生日期',
  `child_id_card` VARCHAR(18) NOT NULL COMMENT '身份证号',
  `parent_name` VARCHAR(50) NOT NULL COMMENT '家长姓名',
  `relationship` VARCHAR(20) NOT NULL COMMENT '与孩子关系',
  `phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
  `address` VARCHAR(500) NOT NULL COMMENT '家庭地址',
  `intent_class` VARCHAR(50) NOT NULL COMMENT '意向班级',
  `remark` TEXT COMMENT '备注',
  `attachments` TEXT COMMENT '附件URL(JSON格式)',
  `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态:PENDING/CONTACTED/ACCEPTED/REJECTED',
  `processed_by` BIGINT(20) COMMENT '处理人ID',
  `processed_time` DATETIME COMMENT '处理时间',
  `process_note` TEXT COMMENT '处理备注',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_phone` (`phone`),
  KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='在线报名表';

-- 4. 教师信息表(用于前台展示)
CREATE TABLE IF NOT EXISTS `teacher_info` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT(20) COMMENT '关联sys_user表',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `position` VARCHAR(100) NOT NULL COMMENT '职位',
  `avatar` VARCHAR(500) COMMENT '头像URL',
  `class_type` VARCHAR(20) NOT NULL COMMENT '班级类型:admin/senior/middle/junior/special',
  `class_name` VARCHAR(50) COMMENT '班级名称',
  `tags` TEXT COMMENT '资质标签(JSON数组)',
  `description` TEXT COMMENT '个人简介',
  `sort_order` INT(5) DEFAULT 0 COMMENT '排序',
  `display_on_home` TINYINT(1) DEFAULT 0 COMMENT '是否在首页显示:0-否 1-是',
  `status` TINYINT(1) DEFAULT 1 COMMENT '状态:0-隐藏 1-显示',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_class_type` (`class_type`),
  KEY `idx_status` (`status`),
  KEY `idx_display_on_home` (`display_on_home`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师信息表';

-- ===================================================
-- 初始化示例数据
-- ===================================================

-- 插入本周食谱数据
INSERT INTO `weekly_menu` (`weekday`, `menu_date`, `breakfast`, `snack1`, `lunch`, `snack2`, `week_offset`) VALUES
(1, CURDATE(), '[{"name":"小米粥","allergens":""},{"name":"鸡蛋","allergens":"鸡蛋"},{"name":"牛奶","allergens":"牛奶"}]',
 '[{"name":"香蕉","allergens":""},{"name":"核桃","allergens":"坚果"}]',
 '[{"name":"番茄炒蛋","allergens":"鸡蛋"},{"name":"清炒油菜","allergens":""},{"name":"米饭","allergens":""}]',
 '[{"name":"酸奶","allergens":"牛奶"},{"name":"饼干","allergens":"麸质"}]', 0),
(2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '[{"name":"豆浆","allergens":""},{"name":"包子","allergens":"麸质"}]',
 '[{"name":"苹果","allergens":""}]',
 '[{"name":"红烧肉","allergens":""},{"name":"炒青菜","allergens":""},{"name":"米饭","allergens":""}]',
 '[{"name":"蛋糕","allergens":"鸡蛋,牛奶,麸质"}]', 0);

-- 插入教师信息
INSERT INTO `teacher_info` (`name`, `position`, `class_type`, `class_name`, `tags`, `description`, `sort_order`, `display_on_home`, `status`) VALUES
('张慧敏', '园长 / 高级教师', 'admin', '', '["20年教龄","特级教师","学前教育专家"]', '从事幼教工作20余年，具有丰富的教学和管理经验。', 1, 1, 1),
('李婷婷', '副园长 / 教研组长', 'admin', '', '["15年教龄","教研专家","课程开发"]', '专注于幼儿园课程研发与教师培训。', 2, 1, 1),
('王芳', '大一班班主任', 'senior', '大一班', '["10年教龄","蒙氏认证","优秀班主任"]', '热爱幼教事业，善于发现每个孩子的闪光点。', 3, 1, 1),
('刘梅', '中一班班主任', 'middle', '中一班', '["12年教龄","心理咨询师","艺术教育"]', '注重儿童心理健康教育，擅长通过艺术活动培养孩子的表达能力。', 4, 1, 1);

-- 插入相册照片
INSERT INTO `album_photo` (`title`, `category`, `image_url`, `description`, `photo_date`, `sort_order`, `status`) VALUES
('春游活动', 'activity', 'https://via.placeholder.com/400x300', '孩子们在公园里快乐游玩', NOW(), 1, 1),
('温馨教室', 'classroom', 'https://via.placeholder.com/400x400', '明亮整洁的教室环境', NOW(), 1, 1),
('户外游戏', 'playground', 'https://via.placeholder.com/400x350', '孩子们在操场上玩耍', NOW(), 1, 1),
('美味午餐', 'meal', 'https://via.placeholder.com/400x300', '营养丰富的午餐时光', NOW(), 1, 1);
