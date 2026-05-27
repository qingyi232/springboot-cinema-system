SET @db_name = DATABASE();

SET @sql = (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = @db_name
              AND TABLE_NAME = 'admin'
              AND COLUMN_NAME = 'password'
              AND CHARACTER_MAXIMUM_LENGTH < 255
        ),
        'ALTER TABLE admin MODIFY COLUMN password varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT ''密码''',
        'SELECT 1'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = @db_name
              AND TABLE_NAME = 'movie_order'
              AND COLUMN_NAME = 'reserve_expire_time'
        ),
        'SELECT 1',
        'ALTER TABLE movie_order ADD COLUMN reserve_expire_time datetime NULL COMMENT ''锁座过期时间'' AFTER payment_method'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = @db_name
              AND TABLE_NAME = 'movie_order'
              AND COLUMN_NAME = 'ticket_code'
        ),
        'SELECT 1',
        'ALTER TABLE movie_order ADD COLUMN ticket_code varchar(64) NULL COMMENT ''票码'' AFTER reserve_expire_time'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = @db_name
              AND TABLE_NAME = 'movie_order'
              AND COLUMN_NAME = 'verify_time'
        ),
        'SELECT 1',
        'ALTER TABLE movie_order ADD COLUMN verify_time datetime NULL COMMENT ''核销时间'' AFTER ticket_code'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS goods (
    id int NOT NULL AUTO_INCREMENT COMMENT 'id',
    name varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称',
    main_img text COLLATE utf8mb4_unicode_ci COMMENT '封面图',
    category varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类',
    price decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '价格',
    stock int NOT NULL DEFAULT 0 COMMENT '库存',
    status varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '上架' COMMENT '状态',
    intro text COLLATE utf8mb4_unicode_ci COMMENT '简介',
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='卖品商品';

CREATE TABLE IF NOT EXISTS goods_order (
    id int NOT NULL AUTO_INCREMENT COMMENT 'id',
    goods_id int NOT NULL COMMENT '商品id',
    goods_name varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称快照',
    goods_main_img text COLLATE utf8mb4_unicode_ci COMMENT '商品图片快照',
    unit_price decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '商品单价',
    quantity int NOT NULL DEFAULT 1 COMMENT '数量',
    total_price decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '订单金额',
    payment_method varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '支付方式',
    status varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '已完成' COMMENT '订单状态',
    user_id int NOT NULL COMMENT '用户id',
    evaluate_content varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '评价内容',
    evaluate_rate int DEFAULT NULL COMMENT '评价评分',
    evaluate_time datetime DEFAULT NULL COMMENT '评价时间',
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='卖品订单';

SET @sql = (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = @db_name
              AND TABLE_NAME = 'goods_order'
              AND COLUMN_NAME = 'evaluate_content'
        ),
        'SELECT 1',
        'ALTER TABLE goods_order ADD COLUMN evaluate_content varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT ''评价内容'' AFTER user_id'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = @db_name
              AND TABLE_NAME = 'goods_order'
              AND COLUMN_NAME = 'evaluate_rate'
        ),
        'SELECT 1',
        'ALTER TABLE goods_order ADD COLUMN evaluate_rate int DEFAULT NULL COMMENT ''评价评分'' AFTER evaluate_content'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = @db_name
              AND TABLE_NAME = 'goods_order'
              AND COLUMN_NAME = 'evaluate_time'
        ),
        'SELECT 1',
        'ALTER TABLE goods_order ADD COLUMN evaluate_time datetime DEFAULT NULL COMMENT ''评价时间'' AFTER evaluate_rate'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

INSERT INTO goods (name, main_img, category, price, stock, status, intro)
SELECT '双人爆米花套餐', 'http://localhost:1000/file/32031ad39475bb5ca41dd19b690409a3.jpeg', '卖品套餐', 39.90, 120, '上架', '含焦糖爆米花与双杯饮品，适合双人观影'
WHERE NOT EXISTS (SELECT 1 FROM goods WHERE name = '双人爆米花套餐');

INSERT INTO goods (name, main_img, category, price, stock, status, intro)
SELECT '可乐大杯', 'http://localhost:1000/file/c262a1b185217816cdec09b812549ffa.jpg', '饮品', 12.00, 200, '上架', '影院常规大杯可乐，冰爽解渴'
WHERE NOT EXISTS (SELECT 1 FROM goods WHERE name = '可乐大杯');

INSERT INTO goods (name, main_img, category, price, stock, status, intro)
SELECT '观影零食组合', 'http://localhost:1000/file/46f2af5677f981c41c5ff987bae77e20.jpg', '零食', 28.00, 80, '上架', '薯片、坚果与糖果组合装'
WHERE NOT EXISTS (SELECT 1 FROM goods WHERE name = '观影零食组合');

UPDATE goods
SET category = '卖品套餐',
    price = 39.90,
    stock = 120,
    status = '上架',
    intro = '含焦糖爆米花与双杯饮品，适合双人观影'
WHERE name = '双人爆米花套餐';

UPDATE goods
SET category = '饮品',
    price = 12.00,
    stock = 200,
    status = '上架',
    intro = '影院常规大杯可乐，冰爽解渴'
WHERE name = '可乐大杯';

UPDATE goods
SET category = '零食',
    price = 28.00,
    stock = 80,
    status = '上架',
    intro = '薯片、坚果与糖果组合装'
WHERE name = '观影零食组合';

UPDATE cinema
SET nickname = '惠友影院',
    tel = '13022506541',
    address = '河北省曲阳县惠友影院',
    label = '杜比音效,停车方便,亲子观影'
WHERE username = 'c1';

UPDATE cinema
SET nickname = '凯嘉影院',
    tel = '18775612310',
    address = '河北省曲阳县凯嘉影院',
    label = 'IMAX厅,休息区,情侣座'
WHERE username = 'c2';

UPDATE cinema
SET nickname = '永宁影院',
    tel = '19765405897',
    address = '河北省曲阳县永宁影院',
    label = '激光厅,杜比全景声,交通便利'
WHERE username = 'c3';

UPDATE movie SET movie_region_id = 13 WHERE id = 3;
UPDATE movie SET movie_region_id = 7 WHERE id = 4;
UPDATE movie SET movie_region_id = 11 WHERE id = 5;
UPDATE movie SET movie_region_id = 2 WHERE id = 8;
UPDATE movie SET movie_region_id = 5 WHERE id = 9;

UPDATE movie SET release_date = DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 20 DAY), '%Y-%m-%d 09:00') WHERE id = 1;
UPDATE movie SET release_date = DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 18 DAY), '%Y-%m-%d 09:00') WHERE id = 2;
UPDATE movie SET release_date = DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 40 DAY), '%Y-%m-%d 19:00') WHERE id = 3;
UPDATE movie SET release_date = DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 9 DAY), '%Y-%m-%d 18:00') WHERE id = 4;
UPDATE movie SET release_date = DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 12 DAY), '%Y-%m-%d 10:00') WHERE id = 5;
UPDATE movie SET release_date = DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 25 DAY), '%Y-%m-%d 18:00') WHERE id = 6;
UPDATE movie SET release_date = DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL 16 DAY), '%Y-%m-%d 19:00') WHERE id = 7;
UPDATE movie SET release_date = DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 15 DAY), '%Y-%m-%d 18:00') WHERE id = 8;
UPDATE movie SET release_date = DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 6 DAY), '%Y-%m-%d 20:00') WHERE id = 9;
UPDATE movie SET release_date = DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL 30 DAY), '%Y-%m-%d 18:30') WHERE id = 10;

INSERT INTO screening_plan (cinema_id, movie_room_id, movie_id, start_time, end_time, price, create_time)
SELECT 1, 1, 1,
       DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 1 DAY), INTERVAL 13 HOUR),
       DATE_ADD(DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 1 DAY), INTERVAL 13 HOUR), INTERVAL 144 MINUTE),
       55, NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM screening_plan
    WHERE cinema_id = 1 AND movie_room_id = 1 AND movie_id = 1
      AND start_time = DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 1 DAY), INTERVAL 13 HOUR)
);

INSERT INTO screening_plan (cinema_id, movie_room_id, movie_id, start_time, end_time, price, create_time)
SELECT 1, 2, 2,
       DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 1 DAY), INTERVAL 18 HOUR),
       DATE_ADD(DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 1 DAY), INTERVAL 18 HOUR), INTERVAL 136 MINUTE),
       49, NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM screening_plan
    WHERE cinema_id = 1 AND movie_room_id = 2 AND movie_id = 2
      AND start_time = DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 1 DAY), INTERVAL 18 HOUR)
);

INSERT INTO screening_plan (cinema_id, movie_room_id, movie_id, start_time, end_time, price, create_time)
SELECT 2, 3, 3,
       DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 2 DAY), INTERVAL 15 HOUR),
       DATE_ADD(DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 2 DAY), INTERVAL 15 HOUR), INTERVAL 125 MINUTE),
       38, NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM screening_plan
    WHERE cinema_id = 2 AND movie_room_id = 3 AND movie_id = 3
      AND start_time = DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 2 DAY), INTERVAL 15 HOUR)
);

INSERT INTO screening_plan (cinema_id, movie_room_id, movie_id, start_time, end_time, price, create_time)
SELECT 2, 3, 5,
       DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 3 DAY), INTERVAL 19 HOUR),
       DATE_ADD(DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 3 DAY), INTERVAL 19 HOUR), INTERVAL 107 MINUTE),
       52, NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM screening_plan
    WHERE cinema_id = 2 AND movie_room_id = 3 AND movie_id = 5
      AND start_time = DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 3 DAY), INTERVAL 19 HOUR)
);

INSERT INTO screening_plan (cinema_id, movie_room_id, movie_id, start_time, end_time, price, create_time)
SELECT 3, 4, 1,
       DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 2 DAY), INTERVAL 16 HOUR),
       DATE_ADD(DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 2 DAY), INTERVAL 16 HOUR), INTERVAL 144 MINUTE),
       46, NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM screening_plan
    WHERE cinema_id = 3 AND movie_room_id = 4 AND movie_id = 1
      AND start_time = DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 2 DAY), INTERVAL 16 HOUR)
);

INSERT INTO screening_plan (cinema_id, movie_room_id, movie_id, start_time, end_time, price, create_time)
SELECT 3, 5, 8,
       DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 2 DAY), INTERVAL 20 HOUR),
       DATE_ADD(DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 2 DAY), INTERVAL 20 HOUR), INTERVAL 116 MINUTE),
       44, NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM screening_plan
    WHERE cinema_id = 3 AND movie_room_id = 5 AND movie_id = 8
      AND start_time = DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 2 DAY), INTERVAL 20 HOUR)
);

INSERT INTO screening_plan (cinema_id, movie_room_id, movie_id, start_time, end_time, price, create_time)
SELECT 1, 1, 6,
       DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 4 DAY), INTERVAL 19 HOUR),
       DATE_ADD(DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 4 DAY), INTERVAL 19 HOUR), INTERVAL 122 MINUTE),
       41, NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM screening_plan
    WHERE cinema_id = 1 AND movie_room_id = 1 AND movie_id = 6
      AND start_time = DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 4 DAY), INTERVAL 19 HOUR)
);

INSERT INTO screening_plan (cinema_id, movie_room_id, movie_id, start_time, end_time, price, create_time)
SELECT 2, 3, 4,
       DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 4 DAY), INTERVAL 14 HOUR),
       DATE_ADD(DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 4 DAY), INTERVAL 14 HOUR), INTERVAL 113 MINUTE),
       37, NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM screening_plan
    WHERE cinema_id = 2 AND movie_room_id = 3 AND movie_id = 4
      AND start_time = DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 4 DAY), INTERVAL 14 HOUR)
);

INSERT INTO screening_plan (cinema_id, movie_room_id, movie_id, start_time, end_time, price, create_time)
SELECT 1, 2, 3,
       DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 5 DAY), INTERVAL 19 HOUR),
       DATE_ADD(DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 5 DAY), INTERVAL 19 HOUR), INTERVAL 125 MINUTE),
       42, NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM screening_plan
    WHERE cinema_id = 1 AND movie_room_id = 2 AND movie_id = 3
      AND start_time = DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 5 DAY), INTERVAL 19 HOUR)
);

INSERT INTO screening_plan (cinema_id, movie_room_id, movie_id, start_time, end_time, price, create_time)
SELECT 3, 5, 9,
       DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 5 DAY), INTERVAL 16 HOUR),
       DATE_ADD(DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 5 DAY), INTERVAL 16 HOUR), INTERVAL 103 MINUTE),
       43, NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM screening_plan
    WHERE cinema_id = 3 AND movie_room_id = 5 AND movie_id = 9
      AND start_time = DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 5 DAY), INTERVAL 16 HOUR)
);

UPDATE slideshow
SET link = 'movieDetails/5'
WHERE id = 4 AND title = '帕丁顿熊3';

UPDATE movie_order_evaluate
SET rate = LEAST(GREATEST(IFNULL(rate, 5), 1), 5);
