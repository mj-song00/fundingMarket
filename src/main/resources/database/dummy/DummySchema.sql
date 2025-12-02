-- ===== User 50명 =====
INSERT INTO user (id, email, nick_name, password, deleted_at, created_at, updated_at) VALUES
                                                                                          (UUID(), 'user1@test.com', '유저1', '$2a$10$7eW1rDnZbOhDGOIh6hEONuFdyt41FrVSPc.xmT2fyHwuUmPuYi5p2', NULL, NOW(), NOW()),
                                                                                          (UUID(), 'user2@test.com', '유저2', '$2a$10$7eW1rDnZbOhDGOIh6hEONuFdyt41FrVSPc.xmT2fyHwuUmPuYi5p2', NULL, NOW(), NOW()),
-- ... user3~user50까지 반복
                                                                                          (UUID(), 'user50@test.com', '유저50', '$2a$10$7eW1rDnZbOhDGOIh6hEONuFdyt41FrVSPc.xmT2fyHwuUmPuYi5p2', NULL, NOW(), NOW());

-- ===== Creator 20명 =====
INSERT INTO creator (id, email, nick_name, password, user_role, introduce, bank, bank_account, deleted_at, is_active, created_at, updated_at) VALUES
                                                                                                                                                  (UUID(), 'creator1@test.com', '크리에이터1', '$2a$10$7eW1rDnZbOhDGOIh6hEONuFdyt41FrVSPc.xmT2fyHwuUmPuYi5p2', 'CREATOR', '안녕하세요 크리에이터1입니다', 'KB국민', '110-210-310', NULL, TRUE, NOW(), NOW()),
-- ... creator2~creator20 반복
                                                                                                                                                  (UUID(), 'creator20@test.com', '크리에이터20', '$2a$10$7eW1rDnZbOhDGOIh6hEONuFdyt41FrVSPc.xmT2fyHwuUmPuYi5p2', 'CREATOR', '안녕하세요 크리에이터20입니다', '국민', '220-330-440', NULL, TRUE, NOW(), NOW());

-- ===== Project 30개 =====
INSERT INTO project (id, title, category, contents, fundingAmount, collectedAmount, fundingSchedule, endDate, status, expectedDeliveryDate, deletedAt, creator_id) VALUES
                                                                                                                                                                       (NULL, '프로젝트1', 'GAME', '프로젝트1 내용입니다.', 1000000, 0, '2025.01.01 - 2025.03.31', NULL, 'IN_PROGRESS', '2025.04.15', NULL, (SELECT id FROM creator WHERE email='creator1@test.com')),
                                                                                                                                                                       (NULL, '프로젝트2', 'ART', '프로젝트2 내용입니다.', 1500000, 0, '2025.02.01 - 2025.04.30', NULL, 'IN_PROGRESS', '2025.05.15', NULL, (SELECT id FROM creator WHERE email='creator2@test.com')),
-- ... 프로젝트3~30까지 반복, creator 랜덤 선택

-- ===== Reward (각 Project 1~5개) =====
INSERT INTO reward (id, price, description, quantity, title, deletedAt, project_id, created_at, updated_at) VALUES
    (NULL, 50000, '프로젝트1 리워드1 설명입니다.', NULL, '프로젝트1 리워드1', NULL, (SELECT id FROM project WHERE title='프로젝트1'), NOW(), NOW()),
    (NULL, 99000, '프로젝트1 리워드2 설명입니다.', 10, '프로젝트1 리워드2', NULL, (SELECT id FROM project WHERE title='프로젝트1'), NOW(), NOW()),
    (NULL, 150000, '프로젝트2 리워드1 설명입니다.', NULL, '프로젝트2 리워드1', NULL, (SELECT id FROM project WHERE title='프로젝트2'), NOW(), NOW());
-- ... 각 프로젝트마다 1~5개 Reward 반복

-- ===== File (Project당 1~3개) =====
INSERT INTO file (id, originalFileName, imageUrl, isThumbnail, project_id, created_at, updated_at) VALUES
                                                                                                       (NULL, '프로젝트1-파일1.png', 'https://dummyimage.com/600x400/000/fff&text=프로젝트1-File1', TRUE, (SELECT id FROM project WHERE title='프로젝트1'), NOW(), NOW()),
                                                                                                       (NULL, '프로젝트1-파일2.png', 'https://dummyimage.com/600x400/000/fff&text=프로젝트1-File2', FALSE, (SELECT id FROM project WHERE title='프로젝트1'), NOW(), NOW());
-- ... 프로젝트2~30까지 반복

-- ===== Sponsorship / Payment / Order =====
-- User1이 프로젝트1 후원
INSERT INTO sponsorship (id, amount, sponsoredAt, quantity, isCanceled, orderId, paymentKey, orderName, method, created_at, updated_at) VALUES
    (NULL, 120000, '2025-03-15', 1, FALSE, '550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440000', '프로젝트1 리워드2', '카드', NOW(), NOW());

INSERT INTO payment (paymentKey, userId, orderName, method, price, status, requestedAt, approvedAt, orderId, cancelReason, created_at, updated_at) VALUES
    ('550e8400-e29b-41d4-a716-446655440000', (SELECT id FROM user WHERE email='user1@test.com'), '프로젝트1 리워드2', '카드', 120000, 'DONE', '2025-03-15', NOW(), '550e8400-e29b-41d4-a716-446655440000', NULL, NOW(), NOW());

INSERT INTO `order` (id, user_id, address, phoneNumber, status, canceledAt, sponsorship_id, created_at, updated_at) VALUES
    (NULL, (SELECT id FROM user WHERE email='user1@test.com'), '서울시 강남구 테스트로 12', '010-1234-5678', 'PAYMENT_COMPLETED', NULL, (SELECT id FROM sponsorship WHERE paymentKey='550e8400-e29b-41d4-a716-446655440000'), NOW(), NOW());

-- ... user2~user50 랜덤 프로젝트 후원 및 연관된 Reward, Payment, Order 동일 패턴