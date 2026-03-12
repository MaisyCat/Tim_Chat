CREATE TABLE t_users (
    userid BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(512) NOT NULL,
    age INT,
    sex VARCHAR(10),
    soi VARCHAR(50),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20)
);

-- 聊天消息表
CREATE TABLE t_chat_msg (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    message TEXT,
    timestamp BIGINT,
    type VARCHAR(20)
);

-- 好友关系表
CREATE TABLE t_friend_relation (
    user_id1 BIGINT NOT NULL,
    user_id2 BIGINT NOT NULL,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id1, user_id2)
);

-- 好友请求表
CREATE TABLE t_requests (
    sender_uid BIGINT NOT NULL,
    receiver_uid BIGINT NOT NULL,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (sender_uid, receiver_uid)

);
