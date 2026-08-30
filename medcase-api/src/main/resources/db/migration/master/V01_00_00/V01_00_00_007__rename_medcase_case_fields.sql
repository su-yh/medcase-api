ALTER TABLE medcase_doctor_case
    CHANGE COLUMN title case_name VARCHAR(255) NOT NULL COMMENT '病例名称',
    CHANGE COLUMN remark content TEXT NULL COMMENT '病例内容';
