CREATE DATABASE IF NOT EXISTS hairsalon
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE hairsalon;

CREATE TABLE IF NOT EXISTS users (
    users_id INT NOT NULL AUTO_INCREMENT,
    login VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    PRIMARY KEY (users_id),
    UNIQUE KEY uq_users_login (login)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS clients (
    client_id INT NOT NULL AUTO_INCREMENT,
    users_id INT NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    phone_number VARCHAR(30),
    PRIMARY KEY (client_id),
    UNIQUE KEY uq_clients_user (users_id),
    CONSTRAINT fk_clients_user FOREIGN KEY (users_id)
        REFERENCES users (users_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS employees (
    employee_id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    experience_years INT NOT NULL DEFAULT 0,
    specialization VARCHAR(255),
    work_start_time TIME NOT NULL,
    work_end_time TIME NOT NULL,
    PRIMARY KEY (employee_id),
    UNIQUE KEY uq_employees_user (user_id),
    CONSTRAINT fk_employees_user FOREIGN KEY (user_id)
        REFERENCES users (users_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS services (
    servise_id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    duration VARCHAR(100) NOT NULL,
    PRIMARY KEY (servise_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS employee_services (
    employee_id INT NOT NULL,
    servise_id INT NOT NULL,
    PRIMARY KEY (employee_id, servise_id),
    CONSTRAINT fk_employee_services_employee FOREIGN KEY (employee_id)
        REFERENCES employees (employee_id) ON DELETE CASCADE,
    CONSTRAINT fk_employee_services_service FOREIGN KEY (servise_id)
        REFERENCES services (servise_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS schedule_slots (
    slot_id INT NOT NULL AUTO_INCREMENT,
    employee_id INT NOT NULL,
    slot_date DATE NOT NULL,
    slot_time TIME NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (slot_id),
    UNIQUE KEY uq_schedule_slot (employee_id, slot_date, slot_time),
    CONSTRAINT fk_schedule_employee FOREIGN KEY (employee_id)
        REFERENCES employees (employee_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS appointments (
    appointment_id INT NOT NULL AUTO_INCREMENT,
    client_id INT NOT NULL,
    employee_id INT NOT NULL,
    servise_id INT NOT NULL,
    slot_date DATE NOT NULL,
    slot_time TIME NOT NULL,
    PRIMARY KEY (appointment_id),
    UNIQUE KEY uq_appointment_start (employee_id, slot_date, slot_time),
    CONSTRAINT fk_appointment_client FOREIGN KEY (client_id)
        REFERENCES clients (client_id) ON DELETE CASCADE,
    CONSTRAINT fk_appointment_employee FOREIGN KEY (employee_id)
        REFERENCES employees (employee_id) ON DELETE CASCADE,
    CONSTRAINT fk_appointment_service FOREIGN KEY (servise_id)
        REFERENCES services (servise_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS reviews (
    review_id INT NOT NULL AUTO_INCREMENT,
    users_id INT NOT NULL,
    review_text TEXT NOT NULL,
    rating TINYINT NOT NULL,
    PRIMARY KEY (review_id),
    CONSTRAINT fk_reviews_user FOREIGN KEY (users_id)
        REFERENCES users (users_id) ON DELETE CASCADE,
    CONSTRAINT chk_reviews_rating CHECK (rating BETWEEN 1 AND 5)
) ENGINE=InnoDB;
