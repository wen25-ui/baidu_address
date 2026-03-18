-- =============================================
-- 绉佸杞︿綅鍏变韩骞冲彴 - 鏁版嵁搴撳垵濮嬪寲鑴氭湰
-- 鏁版嵁搴? graduation_project
-- 鍒涘缓鏃ユ湡: 2026-03-14
-- =============================================

-- 浣跨敤鏁版嵁搴?
USE graduation_project;

-- =============================================
-- 1. 鐢ㄦ埛琛?
-- =============================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '鐢ㄦ埛ID',
    `openid` VARCHAR(64) DEFAULT NULL COMMENT '寰俊openid',
    username VARCHAR(50) DEFAULT NULL COMMENT '用户名',
    password VARCHAR(255) DEFAULT NULL COMMENT '登录密码(BCrypt)',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '鎵嬫満鍙?,
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '鏄电О',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '澶村儚URL',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '鐪熷疄濮撳悕',
    `id_card` VARCHAR(18) DEFAULT NULL COMMENT '韬唤璇佸彿',
    `is_verified` TINYINT DEFAULT 0 COMMENT '鏄惁瀹炲悕璁よ瘉 0-鍚?1-鏄?,
    `credit_score` INT DEFAULT 100 COMMENT '淇＄敤鍒?,
    `is_owner` TINYINT DEFAULT 0 COMMENT '鏄惁涓鸿溅浣嶄富浜?0-鍚?1-鏄?,
    `status` TINYINT DEFAULT 1 COMMENT '鐘舵€?0-绂佺敤 1-姝ｅ父',
    `deleted` TINYINT DEFAULT 0 COMMENT '閫昏緫鍒犻櫎 0-鏈垹闄?1-宸插垹闄?,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_openid` (`openid`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY uk_username (username),
    KEY `idx_status` (`status`),
    KEY `idx_credit_score` (`credit_score`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鐢ㄦ埛琛?;

-- =============================================
-- 2. 杞︿綅琛?
-- =============================================
DROP TABLE IF EXISTS `parking_space`;
CREATE TABLE `parking_space` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '杞︿綅ID',
    `owner_id` BIGINT NOT NULL COMMENT '杞︿綅涓讳汉ID',
    `title` VARCHAR(100) NOT NULL COMMENT '杞︿綅鏍囬',
    `description` TEXT DEFAULT NULL COMMENT '杞︿綅鎻忚堪',
    `community_name` VARCHAR(100) NOT NULL COMMENT '灏忓尯鍚嶇О',
    `address` VARCHAR(255) NOT NULL COMMENT '璇︾粏鍦板潃',
    `longitude` DECIMAL(10,7) NOT NULL COMMENT '缁忓害',
    `latitude` DECIMAL(10,7) NOT NULL COMMENT '绾害',
    `space_number` VARCHAR(50) DEFAULT NULL COMMENT '杞︿綅缂栧彿',
    `price_per_hour` DECIMAL(10,2) NOT NULL COMMENT '姣忓皬鏃朵环鏍?,
    `images` VARCHAR(1000) DEFAULT NULL COMMENT '杞︿綅鍥剧墖URLs锛孞SON鏁扮粍',
    `status` TINYINT DEFAULT 0 COMMENT '鐘舵€?0-寰呭鏍?1-宸蹭笂鏋?2-宸蹭笅鏋?3-瀹℃牳鎷掔粷',
    `reject_reason` VARCHAR(255) DEFAULT NULL COMMENT '瀹℃牳鎷掔粷鍘熷洜',
    `deleted` TINYINT DEFAULT 0 COMMENT '閫昏緫鍒犻櫎 0-鏈垹闄?1-宸插垹闄?,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    PRIMARY KEY (`id`),
    KEY `idx_owner_id` (`owner_id`),
    KEY `idx_status` (`status`),
    KEY `idx_location` (`longitude`, `latitude`),
    KEY `idx_community` (`community_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='杞︿綅琛?;

-- =============================================
-- 3. 杞︿綅鍙敤鏃舵瑙勫垯琛?
-- =============================================
DROP TABLE IF EXISTS `parking_space_rule`;
CREATE TABLE `parking_space_rule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '瑙勫垯ID',
    `space_id` BIGINT NOT NULL COMMENT '杞︿綅ID',
    `rule_type` TINYINT NOT NULL COMMENT '瑙勫垯绫诲瀷 1-姣忓懆閲嶅 2-鍗曟璁剧疆',
    `day_of_week` VARCHAR(20) DEFAULT NULL COMMENT '鍛ㄥ嚑 1-7锛屽涓敤閫楀彿鍒嗛殧',
    `specific_date` DATE DEFAULT NULL COMMENT '鐗瑰畾鏃ユ湡锛堝崟娆¤缃椂浣跨敤锛?,
    `start_time` TIME NOT NULL COMMENT '寮€濮嬫椂闂?,
    `end_time` TIME NOT NULL COMMENT '缁撴潫鏃堕棿',
    `is_available` TINYINT DEFAULT 1 COMMENT '鏄惁鍙敤 0-涓嶅彲鐢?1-鍙敤',
    `deleted` TINYINT DEFAULT 0 COMMENT '閫昏緫鍒犻櫎 0-鏈垹闄?1-宸插垹闄?,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    PRIMARY KEY (`id`),
    KEY `idx_space_id` (`space_id`),
    KEY `idx_specific_date` (`specific_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='杞︿綅鍙敤鏃舵瑙勫垯琛?;

-- =============================================
-- 4. 棰勭害璁㈠崟琛?
-- =============================================
DROP TABLE IF EXISTS `reservation`;
CREATE TABLE `reservation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '棰勭害ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '璁㈠崟缂栧彿',
    `user_id` BIGINT NOT NULL COMMENT '棰勭害鐢ㄦ埛ID',
    `space_id` BIGINT NOT NULL COMMENT '杞︿綅ID',
    `owner_id` BIGINT NOT NULL COMMENT '杞︿綅涓讳汉ID',
    `start_time` DATETIME NOT NULL COMMENT '棰勭害寮€濮嬫椂闂?,
    `end_time` DATETIME NOT NULL COMMENT '棰勭害缁撴潫鏃堕棿',
    `duration` DECIMAL(4,2) NOT NULL COMMENT '鏃堕暱锛堝皬鏃讹級',
    `price_per_hour` DECIMAL(10,2) NOT NULL COMMENT '鍗曚环',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '鎬婚噾棰?,
    `status` TINYINT DEFAULT 0 COMMENT '鐘舵€?0-寰呮敮浠?1-寰呬娇鐢?2-浣跨敤涓?3-宸插畬鎴?4-宸插彇娑?5-宸茶秴鏃?,
    `verify_code` VARCHAR(6) DEFAULT NULL COMMENT '鏍搁攢楠岃瘉鐮?,
    `verified_at` DATETIME DEFAULT NULL COMMENT '鏍搁攢鏃堕棿',
    `completed_at` DATETIME DEFAULT NULL COMMENT '瀹屾垚鏃堕棿',
    `cancelled_at` DATETIME DEFAULT NULL COMMENT '鍙栨秷鏃堕棿',
    `cancel_reason` VARCHAR(255) DEFAULT NULL COMMENT '鍙栨秷鍘熷洜',
    `deleted` TINYINT DEFAULT 0 COMMENT '閫昏緫鍒犻櫎 0-鏈垹闄?1-宸插垹闄?,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_space_id` (`space_id`),
    KEY `idx_owner_id` (`owner_id`),
    KEY `idx_status` (`status`),
    KEY `idx_start_time` (`start_time`),
    KEY `idx_verify_code` (`verify_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='棰勭害璁㈠崟琛?;

-- =============================================
-- 5. 鐢ㄦ埛閽卞寘琛?
-- =============================================
DROP TABLE IF EXISTS `wallet`;
CREATE TABLE `wallet` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '閽卞寘ID',
    `user_id` BIGINT NOT NULL COMMENT '鐢ㄦ埛ID',
    `balance` DECIMAL(10,2) DEFAULT 0.00 COMMENT '鍙敤浣欓',
    `frozen_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '鍐荤粨閲戦',
    `total_income` DECIMAL(10,2) DEFAULT 0.00 COMMENT '绱鏀跺叆',
    `total_withdraw` DECIMAL(10,2) DEFAULT 0.00 COMMENT '绱鎻愮幇',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鐢ㄦ埛閽卞寘琛?;

-- =============================================
-- 6. 浜ゆ槗娴佹按琛?
-- =============================================
DROP TABLE IF EXISTS `transaction`;
CREATE TABLE `transaction` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '娴佹按ID',
    `user_id` BIGINT NOT NULL COMMENT '鐢ㄦ埛ID',
    `reservation_id` BIGINT DEFAULT NULL COMMENT '鍏宠仈棰勭害ID',
    `type` TINYINT NOT NULL COMMENT '绫诲瀷 1-鏀嚭 2-鏀跺叆 3-鎻愮幇 4-閫€娆?,
    `amount` DECIMAL(10,2) NOT NULL COMMENT '閲戦',
    `balance` DECIMAL(10,2) NOT NULL COMMENT '鍙樺姩鍚庝綑棰?,
    `description` VARCHAR(255) DEFAULT NULL COMMENT '鎻忚堪',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_reservation_id` (`reservation_id`),
    KEY `idx_type` (`type`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='浜ゆ槗娴佹按琛?;

-- =============================================
-- 7. 绠＄悊鍛樿〃
-- =============================================
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '绠＄悊鍛業D',
    `username` VARCHAR(50) NOT NULL COMMENT '鐢ㄦ埛鍚?,
    `password` VARCHAR(255) NOT NULL COMMENT '瀵嗙爜锛堝姞瀵嗭級',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '鐪熷疄濮撳悕',
    `role` TINYINT NOT NULL COMMENT '瑙掕壊 1-瓒呯骇绠＄悊鍛?2-杩愯惀浜哄憳',
    `status` TINYINT DEFAULT 1 COMMENT '鐘舵€?0-绂佺敤 1-姝ｅ父',
    `last_login_at` DATETIME DEFAULT NULL COMMENT '鏈€鍚庣櫥褰曟椂闂?,
    `deleted` TINYINT DEFAULT 0 COMMENT '閫昏緫鍒犻櫎 0-鏈垹闄?1-宸插垹闄?,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='绠＄悊鍛樿〃';

-- =============================================
-- 8. 鐢ㄦ埛鏀惰棌琛?
-- =============================================
DROP TABLE IF EXISTS `user_favorite`;
CREATE TABLE `user_favorite` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '鏀惰棌ID',
    `user_id` BIGINT NOT NULL COMMENT '鐢ㄦ埛ID',
    `space_id` BIGINT NOT NULL COMMENT '杞︿綅ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_space` (`user_id`, `space_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_space_id` (`space_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鐢ㄦ埛鏀惰棌琛?;

-- =============================================
-- 9. 淇＄敤鍒嗗彉鍔ㄨ褰曡〃
-- =============================================
DROP TABLE IF EXISTS `credit_record`;
CREATE TABLE `credit_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '璁板綍ID',
    `user_id` BIGINT NOT NULL COMMENT '鐢ㄦ埛ID',
    `reservation_id` BIGINT DEFAULT NULL COMMENT '鍏宠仈棰勭害ID',
    `change_type` TINYINT NOT NULL COMMENT '鍙樺姩绫诲瀷 1-瀹屾垚璁㈠崟 2-瓒呮椂鏈牳閿€ 3-涓存椂鍙栨秷 4-琚姇璇?5-绯荤粺璋冩暣',
    `change_value` INT NOT NULL COMMENT '鍙樺姩鍒嗗€硷紙鍙鍙礋锛?,
    `before_score` INT NOT NULL COMMENT '鍙樺姩鍓嶅垎鏁?,
    `after_score` INT NOT NULL COMMENT '鍙樺姩鍚庡垎鏁?,
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '澶囨敞',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_reservation_id` (`reservation_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='淇＄敤鍒嗗彉鍔ㄨ褰曡〃';

-- =============================================
-- 鍒濆鍖栨暟鎹細瓒呯骇绠＄悊鍛樿处鍙?
-- 瀵嗙爜: 123456 (BCrypt鍔犲瘑)
-- =============================================
INSERT INTO `admin` (`username`, `password`, `real_name`, `role`, `status`) VALUES
('admin', '$2a$10$scV5UWiS27yow5PAlsnUQ.j/Ry9JbtCSIghEaikZHTD/1wsy08tIK', '瓒呯骇绠＄悊鍛?, 1, 1);

-- =============================================
-- 瀹屾垚
-- =============================================
SELECT '鏁版嵁搴撳垵濮嬪寲瀹屾垚锛? AS message;
