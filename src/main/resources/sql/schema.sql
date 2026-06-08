CREATE DATABASE IF NOT EXISTS fic_portal;
USE fic_portal;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fullname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question VARCHAR(1000) NOT NULL,
    category VARCHAR(80),
    option_a VARCHAR(255) NOT NULL,
    option_b VARCHAR(255) NOT NULL,
    option_c VARCHAR(255) NOT NULL,
    option_d VARCHAR(255) NOT NULL,
    correct_answer VARCHAR(1) NOT NULL
);

CREATE TABLE IF NOT EXISTS results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    score INT NOT NULL,
    total_questions INT NOT NULL,
    attempted_questions INT NOT NULL,
    correct_answers INT NOT NULL,
    wrong_answers INT NOT NULL,
    percentage DOUBLE NOT NULL,
    status VARCHAR(20) NOT NULL,
    exam_date DATETIME NOT NULL,
    CONSTRAINT fk_results_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    result_id BIGINT,
    selected_answer VARCHAR(1),
    CONSTRAINT fk_answers_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_answers_question FOREIGN KEY (question_id) REFERENCES questions(id),
    CONSTRAINT fk_answers_result FOREIGN KEY (result_id) REFERENCES results(id)
);

CREATE TABLE IF NOT EXISTS certificates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    certificate_id VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    result_id BIGINT NOT NULL UNIQUE,
    student_name VARCHAR(255) NOT NULL,
    percentage DOUBLE NOT NULL,
    issue_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_certificates_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_certificates_result FOREIGN KEY (result_id) REFERENCES results(id)
);
