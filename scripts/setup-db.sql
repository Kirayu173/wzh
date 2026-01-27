-- suyuan database schema + seed data

CREATE DATABASE IF NOT EXISTS `suyuan`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'suyuan_app'@'%' IDENTIFIED BY 'Suyuan@12345';
GRANT ALL PRIVILEGES ON `suyuan`.* TO 'suyuan_app'@'%';
FLUSH PRIVILEGES;

USE `suyuan`;

CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(64) NOT NULL UNIQUE,
  `password_hash` VARCHAR(255) NOT NULL,
  `phone` VARCHAR(20),
  `role` VARCHAR(32),
  `create_time` DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `product` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(128) NOT NULL,
  `price` DECIMAL(10, 2) NOT NULL,
  `stock` INT NOT NULL,
  `cover_url` VARCHAR(255),
  `desc` TEXT,
  `origin` VARCHAR(64),
  `status` VARCHAR(32),
  `create_time` DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `product_image` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `product_id` BIGINT NOT NULL,
  `url` VARCHAR(255) NOT NULL,
  `sort` INT,
  INDEX `idx_product_image_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `address` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `receiver` VARCHAR(64) NOT NULL,
  `phone` VARCHAR(20) NOT NULL,
  `province` VARCHAR(32),
  `city` VARCHAR(32),
  `detail` VARCHAR(255),
  `is_default` TINYINT(1),
  `create_time` DATETIME,
  INDEX `idx_address_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `cart_item` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  `quantity` INT NOT NULL,
  `selected` TINYINT(1) NOT NULL,
  `price_snapshot` DECIMAL(10, 2),
  `product_name` VARCHAR(128),
  `product_image` VARCHAR(255),
  `create_time` DATETIME,
  `update_time` DATETIME,
  UNIQUE KEY `uk_cart_user_product` (`user_id`, `product_id`),
  INDEX `idx_cart_user` (`user_id`),
  INDEX `idx_cart_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `order` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `total_amount` DECIMAL(10, 2),
  `status` VARCHAR(32) NOT NULL,
  `pay_time` DATETIME,
  `confirm_time` DATETIME,
  `ship_time` DATETIME,
  `express_no` VARCHAR(64),
  `express_company` VARCHAR(64),
  `receiver` VARCHAR(64),
  `phone` VARCHAR(20),
  `address` VARCHAR(255),
  `memo` VARCHAR(255),
  `request_id` VARCHAR(64),
  `create_time` DATETIME,
  UNIQUE KEY `uk_order_request` (`user_id`, `request_id`),
  INDEX `idx_order_user` (`user_id`),
  INDEX `idx_order_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `order_item` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  `price` DECIMAL(10, 2),
  `quantity` INT NOT NULL,
  `product_name` VARCHAR(128),
  `product_image` VARCHAR(255),
  INDEX `idx_order_item_order` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `trace_batch` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `product_id` BIGINT NOT NULL,
  `trace_code` VARCHAR(64) NOT NULL UNIQUE,
  `batch_no` VARCHAR(64),
  `origin` VARCHAR(128),
  `producer` VARCHAR(128),
  `harvest_date` DATE,
  `process_info` VARCHAR(2000),
  `test_org` VARCHAR(128),
  `test_date` DATE,
  `test_result` VARCHAR(255),
  `report_url` VARCHAR(255),
  `create_time` DATETIME,
  INDEX `idx_trace_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `logistics_node` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `trace_code` VARCHAR(64) NOT NULL,
  `node_time` DATETIME NOT NULL,
  `location` VARCHAR(128),
  `status_desc` VARCHAR(255),
  INDEX `idx_logistics_trace_code` (`trace_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- seed users (password: 123456)
INSERT INTO `user` (`username`, `password_hash`, `phone`, `role`, `create_time`)
VALUES
  ('admin1', '$2b$10$MqG.Tn0a.4lYRyALcBlIQuLEuuOiG9seW/1mRTgq3TEMnwp0WbLbi', '13900000000', 'admin', NOW()),
  ('user1', '$2b$10$MqG.Tn0a.4lYRyALcBlIQuLEuuOiG9seW/1mRTgq3TEMnwp0WbLbi', '13800000000', 'user', NOW())
ON DUPLICATE KEY UPDATE
  `password_hash` = VALUES(`password_hash`),
  `phone` = VALUES(`phone`),
  `role` = VALUES(`role`);

-- seed products
INSERT INTO `product` (`id`, `name`, `price`, `stock`, `cover_url`, `desc`, `origin`, `status`, `create_time`)
VALUES
  (1, 'Apple', 12.50, 100, 'https://picsum.photos/seed/apple/600/600', 'Fresh, crisp apples.', 'Shandong', 'online', NOW()),
  (2, 'Orange', 9.90, 120, 'https://picsum.photos/seed/orange/600/600', 'Juicy oranges with balanced sweetness.', 'Jiangxi', 'online', NOW()),
  (3, 'Rice', 39.90, 60, 'https://picsum.photos/seed/rice/600/600', 'Premium grains with rich aroma.', 'Heilongjiang', 'online', NOW())
ON DUPLICATE KEY UPDATE
  `name` = VALUES(`name`),
  `price` = VALUES(`price`),
  `stock` = VALUES(`stock`),
  `cover_url` = VALUES(`cover_url`),
  `desc` = VALUES(`desc`),
  `origin` = VALUES(`origin`),
  `status` = VALUES(`status`);

INSERT INTO `product_image` (`id`, `product_id`, `url`, `sort`)
VALUES
  (1, 1, 'https://picsum.photos/seed/apple-1/800/800', 1),
  (2, 1, 'https://picsum.photos/seed/apple-2/800/800', 2),
  (3, 2, 'https://picsum.photos/seed/orange-1/800/800', 1),
  (4, 3, 'https://picsum.photos/seed/rice-1/800/800', 1)
ON DUPLICATE KEY UPDATE
  `product_id` = VALUES(`product_id`),
  `url` = VALUES(`url`),
  `sort` = VALUES(`sort`);

-- seed trace batches
INSERT INTO `trace_batch` (
  `id`, `product_id`, `trace_code`, `batch_no`, `origin`, `producer`,
  `harvest_date`, `process_info`, `test_org`, `test_date`, `test_result`, `report_url`, `create_time`
)
VALUES
  (1, 1, 'TRACE-APPLE-001', 'A20260101', 'Shandong', 'LuGuo Cooperative', '2026-01-02',
   'Clean-Grade-Pack', 'Provincial QA Center', '2026-01-05', 'Pass', 'https://example.com/report/apple-001', NOW()),
  (2, 2, 'TRACE-ORANGE-001', 'O20260102', 'Jiangxi', 'GanOrange Farm', '2026-01-03',
   'Clean-Grade-Wax', 'Provincial QA Center', '2026-01-06', 'Pass', 'https://example.com/report/orange-001', NOW()),
  (3, 3, 'TRACE-RICE-001', 'R20260103', 'Heilongjiang', 'BeiGrain Base', '2025-12-28',
   'Dry-Mill-Pack', 'Provincial QA Center', '2026-01-04', 'Pass', 'https://example.com/report/rice-001', NOW())
ON DUPLICATE KEY UPDATE
  `product_id` = VALUES(`product_id`),
  `batch_no` = VALUES(`batch_no`),
  `origin` = VALUES(`origin`),
  `producer` = VALUES(`producer`),
  `harvest_date` = VALUES(`harvest_date`),
  `process_info` = VALUES(`process_info`),
  `test_org` = VALUES(`test_org`),
  `test_date` = VALUES(`test_date`),
  `test_result` = VALUES(`test_result`),
  `report_url` = VALUES(`report_url`);

-- seed logistics nodes
INSERT INTO `logistics_node` (`id`, `trace_code`, `node_time`, `location`, `status_desc`)
VALUES
  (1, 'TRACE-APPLE-001', '2026-01-06 09:30:00', 'JN Sorting Center', 'Collected'),
  (2, 'TRACE-APPLE-001', '2026-01-07 12:10:00', 'Beijing Transfer Hub', 'In Transit'),
  (3, 'TRACE-ORANGE-001', '2026-01-06 10:00:00', 'NC Sorting Center', 'Collected'),
  (4, 'TRACE-ORANGE-001', '2026-01-07 14:20:00', 'Shanghai Transfer Hub', 'In Transit'),
  (5, 'TRACE-RICE-001', '2026-01-05 08:40:00', 'Harbin Sorting Center', 'Collected'),
  (6, 'TRACE-RICE-001', '2026-01-06 16:00:00', 'Shenyang Transfer Hub', 'In Transit')
ON DUPLICATE KEY UPDATE
  `trace_code` = VALUES(`trace_code`),
  `node_time` = VALUES(`node_time`),
  `location` = VALUES(`location`),
  `status_desc` = VALUES(`status_desc`);
