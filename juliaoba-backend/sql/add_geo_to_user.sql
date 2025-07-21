-- 为用户表添加地理位置字段
ALTER TABLE user
ADD COLUMN longitude DOUBLE DEFAULT NULL COMMENT '经度',
ADD COLUMN latitude DOUBLE DEFAULT NULL COMMENT '纬度',
ADD COLUMN lastLocationUpdateTime DATETIME DEFAULT NULL COMMENT '最后位置更新时间';

-- 为经度和纬度分别创建普通索引，加速查询
ALTER TABLE user
ADD INDEX idx_longitude (longitude),
ADD INDEX idx_latitude (latitude); 