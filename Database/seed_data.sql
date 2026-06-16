USE vietnamjobs;

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS perf_seed_meta (
    meta_key VARCHAR(100) PRIMARY KEY,
    meta_value BIGINT NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

DELIMITER $$

DROP PROCEDURE IF EXISTS seed_seekers $$
CREATE PROCEDURE seed_seekers(IN p_count INT)
BEGIN
    DECLARE v_i INT DEFAULT 1;
    DECLARE v_run_tag BIGINT DEFAULT UNIX_TIMESTAMP();
    DECLARE v_account_id INT;
    DECLARE v_first_seeker_id INT DEFAULT NULL;
    DECLARE v_last_seeker_id INT DEFAULT NULL;

    IF p_count IS NULL OR p_count <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'seed_seekers: p_count must be > 0';
    END IF;

    START TRANSACTION;

    WHILE v_i <= p_count DO
        INSERT INTO account (
            username,
            password,
            type_account,
            created,
            email,
            status,
            security_code,
            wallet
        ) VALUES (
            CONCAT('perf_seeker_', v_run_tag, '_', v_i),
            '$2a$10$Gp21raY5TB/WmTtx6ZOCouy77LPkmuSBXqbuxpvE6ikFgPSVIgDqe',
            1,
            DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND() * 365) DAY),
            CONCAT('perf_seeker_', v_run_tag, '_', v_i, '@example.com'),
            1,
            SUBSTRING(REPLACE(UUID(), '-', ''), 1, 10),
            0
        );

        SET v_account_id = LAST_INSERT_ID();

        INSERT INTO seeker (
            account_id,
            fullname,
            phone,
            description,
            cv_information,
            status,
            avatar
        ) VALUES (
            v_account_id,
            CONCAT('Perf Seeker ', v_i),
            CONCAT('09', LPAD(FLOOR(RAND() * 100000000), 8, '0')),
            'Performance seed seeker',
            'cvThanhTu.pdf',
            1,
            '1.jpeg'
        );

        IF v_first_seeker_id IS NULL THEN
            SET v_first_seeker_id = LAST_INSERT_ID();
        END IF;

        SET v_last_seeker_id = LAST_INSERT_ID();
        SET v_i = v_i + 1;
    END WHILE;

    COMMIT;

    REPLACE INTO perf_seed_meta(meta_key, meta_value) VALUES ('seed_seekers_count', p_count);
    REPLACE INTO perf_seed_meta(meta_key, meta_value) VALUES ('seed_seekers_first_id', v_first_seeker_id);
    REPLACE INTO perf_seed_meta(meta_key, meta_value) VALUES ('seed_seekers_last_id', v_last_seeker_id);
END $$

DROP PROCEDURE IF EXISTS seed_employers $$
CREATE PROCEDURE seed_employers(IN p_count INT)
BEGIN
    DECLARE v_i INT DEFAULT 1;
    DECLARE v_run_tag BIGINT DEFAULT UNIX_TIMESTAMP();
    DECLARE v_account_id INT;
    DECLARE v_first_employer_id INT DEFAULT NULL;
    DECLARE v_last_employer_id INT DEFAULT NULL;

    IF p_count IS NULL OR p_count <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'seed_employers: p_count must be > 0';
    END IF;

    START TRANSACTION;

    WHILE v_i <= p_count DO
        INSERT INTO account (
            username,
            password,
            type_account,
            created,
            email,
            status,
            security_code,
            wallet
        ) VALUES (
            CONCAT('perf_employer_', v_run_tag, '_', v_i),
            '$2a$10$Gp21raY5TB/WmTtx6ZOCouy77LPkmuSBXqbuxpvE6ikFgPSVIgDqe',
            2,
            DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND() * 365) DAY),
            CONCAT('perf_employer_', v_run_tag, '_', v_i, '@example.com'),
            1,
            SUBSTRING(REPLACE(UUID(), '-', ''), 1, 10),
            FLOOR(RAND() * 1000000)
        );

        SET v_account_id = LAST_INSERT_ID();

        INSERT INTO employer (
            account_id,
            name,
            scale,
            logo,
            link,
            description,
            address,
            map_link,
            status,
            cover
        ) VALUES (
            v_account_id,
            CONCAT('Perf Employer ', v_i),
            CONCAT(10 + FLOOR(RAND() * 990), '+ nhân viên'),
            'company1.jpg',
            CONCAT('https://example.com/employer/', v_run_tag, '/', v_i),
            'Performance seed employer',
            CONCAT('Performance Address ', v_i, ', Ho Chi Minh City'),
            'https://maps.google.com',
            1,
            'company_cover_1.jpg'
        );

        IF v_first_employer_id IS NULL THEN
            SET v_first_employer_id = LAST_INSERT_ID();
        END IF;

        SET v_last_employer_id = LAST_INSERT_ID();
        SET v_i = v_i + 1;
    END WHILE;

    COMMIT;

    REPLACE INTO perf_seed_meta(meta_key, meta_value) VALUES ('seed_employers_count', p_count);
    REPLACE INTO perf_seed_meta(meta_key, meta_value) VALUES ('seed_employers_first_id', v_first_employer_id);
    REPLACE INTO perf_seed_meta(meta_key, meta_value) VALUES ('seed_employers_last_id', v_last_employer_id);
END $$

DROP PROCEDURE IF EXISTS seed_postings $$
CREATE PROCEDURE seed_postings(IN p_count INT)
BEGIN
    DECLARE v_i INT DEFAULT 1;
    DECLARE v_run_tag BIGINT DEFAULT UNIX_TIMESTAMP();
    DECLARE v_first_posting_id INT DEFAULT NULL;
    DECLARE v_last_posting_id INT DEFAULT NULL;

    DECLARE v_employer_first_id INT;
    DECLARE v_employer_last_id INT;

    DECLARE v_category_count INT;
    DECLARE v_local_count INT;
    DECLARE v_rank_count INT;
    DECLARE v_type_count INT;
    DECLARE v_experience_count INT;
    DECLARE v_wage_count INT;

    DECLARE v_category_id INT;
    DECLARE v_local_id INT;
    DECLARE v_rank_id INT;
    DECLARE v_type_id INT;
    DECLARE v_experience_id INT;
    DECLARE v_wage_id INT;
    DECLARE v_employer_id INT;
    DECLARE v_created DATE;
    DECLARE v_deadline DATE;
    DECLARE v_offset INT;

    IF p_count IS NULL OR p_count <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'seed_postings: p_count must be > 0';
    END IF;

    SELECT meta_value INTO v_employer_first_id
    FROM perf_seed_meta WHERE meta_key = 'seed_employers_first_id';

    SELECT meta_value INTO v_employer_last_id
    FROM perf_seed_meta WHERE meta_key = 'seed_employers_last_id';

    IF v_employer_first_id IS NULL OR v_employer_last_id IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'seed_postings: run seed_employers first';
    END IF;

    SELECT COUNT(*) INTO v_category_count FROM category WHERE status = 1;
    SELECT COUNT(*) INTO v_local_count FROM local WHERE status = 1;
    SELECT COUNT(*) INTO v_rank_count FROM `rank` WHERE status = 1;
    SELECT COUNT(*) INTO v_type_count FROM `type` WHERE status = 1;
    SELECT COUNT(*) INTO v_experience_count FROM experience WHERE status = 1;
    SELECT COUNT(*) INTO v_wage_count FROM wage WHERE status = 1;

    IF v_category_count = 0 OR v_local_count = 0 OR v_rank_count = 0
        OR v_type_count = 0 OR v_experience_count = 0 OR v_wage_count = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'seed_postings: required master data is missing';
    END IF;

    START TRANSACTION;

    WHILE v_i <= p_count DO
        SET v_employer_id = v_employer_first_id + FLOOR(RAND() * (v_employer_last_id - v_employer_first_id + 1));

        SET v_offset = FLOOR(RAND() * v_category_count);
        SELECT id INTO v_category_id
        FROM category
        WHERE status = 1
        ORDER BY id
        LIMIT v_offset, 1;

        SET v_offset = FLOOR(RAND() * v_local_count);
        SELECT id INTO v_local_id
        FROM local
        WHERE status = 1
        ORDER BY id
        LIMIT v_offset, 1;

        SET v_offset = FLOOR(RAND() * v_rank_count);
        SELECT id INTO v_rank_id
        FROM `rank`
        WHERE status = 1
        ORDER BY id
        LIMIT v_offset, 1;

        SET v_offset = FLOOR(RAND() * v_type_count);
        SELECT id INTO v_type_id
        FROM `type`
        WHERE status = 1
        ORDER BY id
        LIMIT v_offset, 1;

        SET v_offset = FLOOR(RAND() * v_experience_count);
        SELECT id INTO v_experience_id
        FROM experience
        WHERE status = 1
        ORDER BY id
        LIMIT v_offset, 1;

        SET v_offset = FLOOR(RAND() * v_wage_count);
        SELECT id INTO v_wage_id
        FROM wage
        WHERE status = 1
        ORDER BY id
        LIMIT v_offset, 1;

        SET v_created = DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND() * 180) DAY);
        SET v_deadline = DATE_ADD(v_created, INTERVAL 15 + FLOOR(RAND() * 120) DAY);

        INSERT INTO postings (
            employer_id,
            title,
            description,
            created,
            deadline,
            gender,
            quantity,
            wage_id,
            category_id,
            local_id,
            rank_id,
            type_id,
            experience_id,
            status,
            open
        ) VALUES (
            v_employer_id,
            CONCAT('Perf Posting ', v_run_tag, ' #', v_i),
            CONCAT('Performance seed posting description ', v_i),
            v_created,
            v_deadline,
            ELT(1 + FLOOR(RAND() * 3), 'Male', 'Female', 'Any'),
            1 + FLOOR(RAND() * 10),
            v_wage_id,
            v_category_id,
            v_local_id,
            v_rank_id,
            v_type_id,
            v_experience_id,
            IF(RAND() < 0.75, 1, 0),
            IF(RAND() < 0.80, 1, 0)
        );

        IF v_first_posting_id IS NULL THEN
            SET v_first_posting_id = LAST_INSERT_ID();
        END IF;

        SET v_last_posting_id = LAST_INSERT_ID();
        SET v_i = v_i + 1;
    END WHILE;

    COMMIT;

    REPLACE INTO perf_seed_meta(meta_key, meta_value) VALUES ('seed_postings_count', p_count);
    REPLACE INTO perf_seed_meta(meta_key, meta_value) VALUES ('seed_postings_first_id', v_first_posting_id);
    REPLACE INTO perf_seed_meta(meta_key, meta_value) VALUES ('seed_postings_last_id', v_last_posting_id);
END $$

DROP PROCEDURE IF EXISTS seed_applications $$
CREATE PROCEDURE seed_applications(IN p_count INT)
BEGIN
    DECLARE v_i INT DEFAULT 1;
    DECLARE v_posting_first_id INT;
    DECLARE v_posting_last_id INT;
    DECLARE v_seeker_first_id INT;
    DECLARE v_seeker_last_id INT;
    DECLARE v_posting_id INT;
    DECLARE v_seeker_id INT;
    DECLARE v_status INT;
    DECLARE v_result INT;

    IF p_count IS NULL OR p_count <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'seed_applications: p_count must be > 0';
    END IF;

    SELECT meta_value INTO v_posting_first_id
    FROM perf_seed_meta WHERE meta_key = 'seed_postings_first_id';

    SELECT meta_value INTO v_posting_last_id
    FROM perf_seed_meta WHERE meta_key = 'seed_postings_last_id';

    SELECT meta_value INTO v_seeker_first_id
    FROM perf_seed_meta WHERE meta_key = 'seed_seekers_first_id';

    SELECT meta_value INTO v_seeker_last_id
    FROM perf_seed_meta WHERE meta_key = 'seed_seekers_last_id';

    IF v_posting_first_id IS NULL OR v_posting_last_id IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'seed_applications: run seed_postings first';
    END IF;

    IF v_seeker_first_id IS NULL OR v_seeker_last_id IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'seed_applications: run seed_seekers first';
    END IF;

    START TRANSACTION;

    WHILE v_i <= p_count DO
        SET v_posting_id = v_posting_first_id + FLOOR(RAND() * (v_posting_last_id - v_posting_first_id + 1));
        SET v_seeker_id = v_seeker_first_id + FLOOR(RAND() * (v_seeker_last_id - v_seeker_first_id + 1));
        SET v_status = IF(RAND() < 0.55, 0, 1);
        SET v_result = CASE
            WHEN RAND() < 0.70 THEN 0
            WHEN RAND() < 0.88 THEN 1
            ELSE 2
        END;

        INSERT INTO application_history (
            created,
            postings_id,
            seeker_id,
            status,
            result
        ) VALUES (
            DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND() * 120) DAY),
            v_posting_id,
            v_seeker_id,
            v_status,
            v_result
        );

        SET v_i = v_i + 1;
    END WHILE;

    COMMIT;

    REPLACE INTO perf_seed_meta(meta_key, meta_value) VALUES ('seed_applications_count', p_count);
END $$

DROP PROCEDURE IF EXISTS seed_follows $$
CREATE PROCEDURE seed_follows(IN p_count INT)
BEGIN
    DECLARE v_i INT DEFAULT 1;
    DECLARE v_employer_first_id INT;
    DECLARE v_employer_last_id INT;
    DECLARE v_seeker_first_id INT;
    DECLARE v_seeker_last_id INT;
    DECLARE v_employer_id INT;
    DECLARE v_seeker_id INT;

    IF p_count IS NULL OR p_count <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'seed_follows: p_count must be > 0';
    END IF;

    SELECT meta_value INTO v_employer_first_id
    FROM perf_seed_meta WHERE meta_key = 'seed_employers_first_id';

    SELECT meta_value INTO v_employer_last_id
    FROM perf_seed_meta WHERE meta_key = 'seed_employers_last_id';

    SELECT meta_value INTO v_seeker_first_id
    FROM perf_seed_meta WHERE meta_key = 'seed_seekers_first_id';

    SELECT meta_value INTO v_seeker_last_id
    FROM perf_seed_meta WHERE meta_key = 'seed_seekers_last_id';

    IF v_employer_first_id IS NULL OR v_employer_last_id IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'seed_follows: run seed_employers first';
    END IF;

    IF v_seeker_first_id IS NULL OR v_seeker_last_id IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'seed_follows: run seed_seekers first';
    END IF;

    START TRANSACTION;

    WHILE v_i <= p_count DO
        SET v_employer_id = v_employer_first_id + FLOOR(RAND() * (v_employer_last_id - v_employer_first_id + 1));
        SET v_seeker_id = v_seeker_first_id + FLOOR(RAND() * (v_seeker_last_id - v_seeker_first_id + 1));

        INSERT INTO follow (
            employer_id,
            seeker_id,
            status
        ) VALUES (
            v_employer_id,
            v_seeker_id,
            1
        );

        SET v_i = v_i + 1;
    END WHILE;

    COMMIT;

    REPLACE INTO perf_seed_meta(meta_key, meta_value) VALUES ('seed_follows_count', p_count);
END $$

DROP PROCEDURE IF EXISTS ensure_performance_indexes $$
CREATE PROCEDURE ensure_performance_indexes()
BEGIN
    SET @v_sql = (
        SELECT IF(
            COUNT(*) = 0,
            'CREATE INDEX idx_postings_search ON postings(status, open, local_id, category_id, wage_id, experience_id)',
            'SELECT ''idx_postings_search exists'''
        )
        FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name = 'postings'
          AND index_name = 'idx_postings_search'
    );
    PREPARE stmt FROM @v_sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;

    SET @v_sql = (
        SELECT IF(
            COUNT(*) = 0,
            'CREATE INDEX idx_postings_title ON postings(title)',
            'SELECT ''idx_postings_title exists'''
        )
        FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name = 'postings'
          AND index_name = 'idx_postings_title'
    );
    PREPARE stmt FROM @v_sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;

    SET @v_sql = (
        SELECT IF(
            COUNT(*) = 0,
            'CREATE INDEX idx_application_posting ON application_history(postings_id)',
            'SELECT ''idx_application_posting exists'''
        )
        FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name = 'application_history'
          AND index_name = 'idx_application_posting'
    );
    PREPARE stmt FROM @v_sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;

    SET @v_sql = (
        SELECT IF(
            COUNT(*) = 0,
            'CREATE INDEX idx_application_seeker ON application_history(seeker_id)',
            'SELECT ''idx_application_seeker exists'''
        )
        FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name = 'application_history'
          AND index_name = 'idx_application_seeker'
    );
    PREPARE stmt FROM @v_sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;

    SET @v_sql = (
        SELECT IF(
            COUNT(*) = 0,
            'CREATE INDEX idx_follow_employer ON follow(employer_id)',
            'SELECT ''idx_follow_employer exists'''
        )
        FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name = 'follow'
          AND index_name = 'idx_follow_employer'
    );
    PREPARE stmt FROM @v_sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;

    SET @v_sql = (
        SELECT IF(
            COUNT(*) = 0,
            'CREATE INDEX idx_follow_seeker ON follow(seeker_id)',
            'SELECT ''idx_follow_seeker exists'''
        )
        FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name = 'follow'
          AND index_name = 'idx_follow_seeker'
    );
    PREPARE stmt FROM @v_sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;

    ANALYZE TABLE postings, application_history, follow;
END $$

DROP PROCEDURE IF EXISTS cleanup_seeded_performance_data $$
CREATE PROCEDURE cleanup_seeded_performance_data()
BEGIN
    DELETE ah
    FROM application_history ah
    LEFT JOIN seeker s ON ah.seeker_id = s.id
    LEFT JOIN account sa ON s.account_id = sa.id
    LEFT JOIN postings p ON ah.postings_id = p.id
    LEFT JOIN employer e ON p.employer_id = e.id
    LEFT JOIN account ea ON e.account_id = ea.id
    WHERE sa.username LIKE 'perf_seeker_%'
       OR ea.username LIKE 'perf_employer_%'
       OR p.title LIKE 'Perf Posting %';

    DELETE f
    FROM follow f
    LEFT JOIN seeker s ON f.seeker_id = s.id
    LEFT JOIN account sa ON s.account_id = sa.id
    LEFT JOIN employer e ON f.employer_id = e.id
    LEFT JOIN account ea ON e.account_id = ea.id
    WHERE sa.username LIKE 'perf_seeker_%'
       OR ea.username LIKE 'perf_employer_%';

    DELETE pp
    FROM postingspayment pp
    INNER JOIN postings p ON pp.postings_id = p.id
    WHERE p.title LIKE 'Perf Posting %';

    DELETE pay
    FROM payment pay
    INNER JOIN postings p ON pay.postings_id = p.id
    WHERE p.title LIKE 'Perf Posting %';

    DELETE p
    FROM postings p
    WHERE p.title LIKE 'Perf Posting %';

    DELETE e
    FROM employer e
    INNER JOIN account a ON e.account_id = a.id
    WHERE a.username LIKE 'perf_employer_%';

    DELETE s
    FROM seeker s
    INNER JOIN account a ON s.account_id = a.id
    WHERE a.username LIKE 'perf_seeker_%';

    DELETE a
    FROM account a
    WHERE a.username LIKE 'perf_employer_%'
       OR a.username LIKE 'perf_seeker_%';

    DELETE FROM perf_seed_meta;
END $$

DROP PROCEDURE IF EXISTS seed_performance_dataset $$
CREATE PROCEDURE seed_performance_dataset(
    IN p_seekers INT,
    IN p_employers INT,
    IN p_postings INT,
    IN p_applications INT,
    IN p_follows INT
)
BEGIN
    CALL seed_seekers(p_seekers);
    CALL seed_employers(p_employers);
    CALL seed_postings(p_postings);
    CALL seed_applications(p_applications);
    CALL seed_follows(p_follows);
    CALL ensure_performance_indexes();
END $$

DELIMITER ;

/*
Shared seed procedures only.

Recommended usage:
  1. Run Database/seed_smoke.sql for E2E / integration / regression.
  2. Run Database/seed_performance.sql for performance benchmarking.
  3. Run CALL cleanup_seeded_performance_data(); before reseeding if needed.
*/
