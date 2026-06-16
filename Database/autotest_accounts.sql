USE vietnamjobs;

SET NAMES utf8mb4;

/*
Deterministic test identities for stable E2E and integration runs.

Credentials:
  - seeker:   autotest_seeker / Autotest@123
  - employer: autotest_employer / Autotest@123
  - admin:    autotest_admin / Autotest@123

Also fixes:
  - Minh / Nguyen221@ so that it becomes an active seeker login
*/

START TRANSACTION;

DELETE FROM follow WHERE id IN (900021);
DELETE FROM application_history WHERE id IN (900020);
DELETE FROM postingspayment WHERE postings_id IN (900010);
DELETE FROM payment WHERE postings_id IN (900010);
DELETE FROM postings WHERE id IN (900010);
DELETE FROM admin WHERE id IN (900003) OR account_id IN (900003);
DELETE FROM employer WHERE id IN (900002) OR account_id IN (900002);
DELETE FROM seeker WHERE id IN (900001) OR account_id IN (900001);
DELETE FROM account WHERE id IN (900001, 900002, 900003)
   OR username IN ('autotest_seeker', 'autotest_employer', 'autotest_admin');

UPDATE account
SET username = 'Minh_legacy_27'
WHERE id = 27 AND username = 'Minh';

UPDATE account
SET username = 'Minh',
    password = '$2a$10$ULhCxXkUKGkbFLa5nJmNI.7OTEF1ibVGflioae9t4xW8Ih9bXNznW',
    type_account = 1,
    status = 1
WHERE id = 2778;

UPDATE seeker
SET fullname = 'Minh Nguyen',
    phone = '0900002210',
    description = 'Recovered active seeker account',
    cv_information = 'cvThanhTu.pdf',
    status = 1,
    avatar = '1.jpeg'
WHERE account_id = 2778;

INSERT INTO seeker (id, account_id, fullname, phone, description, cv_information, status, avatar)
SELECT 900004, 2778, 'Minh Nguyen', '0900002210', 'Recovered active seeker account', 'cvThanhTu.pdf', 1, '1.jpeg'
WHERE NOT EXISTS (SELECT 1 FROM seeker WHERE account_id = 2778);

INSERT INTO account (id, username, password, type_account, created, email, status, security_code, wallet) VALUES
(900001, 'autotest_seeker', '$2a$10$cRJta7iVxXiVquTeZgP2YOioh42r0omAQuAznkEF4QQfgq69PSwzu', 1, CURDATE(), 'autotest_seeker@vietnamjobs.local', 1, 'AUTOSEEK01', 0),
(900002, 'autotest_employer', '$2a$10$cRJta7iVxXiVquTeZgP2YOioh42r0omAQuAznkEF4QQfgq69PSwzu', 2, CURDATE(), 'autotest_employer@vietnamjobs.local', 1, 'AUTOEMP001', 500000),
(900003, 'autotest_admin', '$2a$10$cRJta7iVxXiVquTeZgP2YOioh42r0omAQuAznkEF4QQfgq69PSwzu', 3, CURDATE(), 'autotest_admin@vietnamjobs.local', 1, 'AUTOADM001', 0);

INSERT INTO seeker (id, account_id, fullname, phone, description, cv_information, status, avatar) VALUES
(900001, 900001, 'Autotest Seeker', '0900000001', 'Stable E2E seeker profile', 'cvThanhTu.pdf', 1, '1.jpeg');

INSERT INTO employer (id, account_id, name, scale, logo, link, description, address, map_link, status, cover) VALUES
(900002, 900002, 'Autotest Employer Company', '25-99 nhan vien', 'company1.jpg', 'https://example.com/autotest-employer', 'Stable E2E employer profile', '123 Autotest Street, Ho Chi Minh City', 'https://maps.google.com', 1, 'company_cover_1.jpg');

INSERT INTO admin (id, account_id, fullname, phone, photo, status) VALUES
(900003, 900003, 'Autotest Admin', '0900000003', 'avatar1.jpg', 1);

INSERT INTO postings (
    id, employer_id, title, description, created, deadline, gender, quantity,
    wage_id, category_id, local_id, rank_id, type_id, experience_id, status, open
) VALUES (
    900010, 900002, 'Autotest Java Posting', 'Stable posting for E2E integration and regression.', CURDATE(),
    DATE_ADD(CURDATE(), INTERVAL 90 DAY), 'Any', 2, 1, 18, 1, 1, 4, 1, 1, 1
);

INSERT INTO application_history (id, created, postings_id, seeker_id, status, result) VALUES
(900020, CURDATE(), 900010, 900001, 0, 0);

INSERT INTO follow (id, employer_id, seeker_id, status) VALUES
(900021, 900002, 900001, 1);

COMMIT;

SELECT id, username, email, status
FROM account
WHERE id IN (2778, 900001, 900002, 900003)
ORDER BY id;

SELECT id, title, employer_id, status, open
FROM postings
WHERE id = 900010;
