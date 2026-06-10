DROP DATABASE IF EXISTS badminton_db;

CREATE DATABASE badminton_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE badminton_db;

CREATE TABLE users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(255) NOT NULL UNIQUE,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    enabled     BOOLEAN      NOT NULL DEFAULT TRUE,
    role        VARCHAR(50)  NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE courts (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    location    VARCHAR(255),
    description TEXT,
    image_url   VARCHAR(500),
    active      BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE bookings (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_date DATE         NOT NULL,
    time_slot    VARCHAR(100) NOT NULL,
    status       VARCHAR(50)  NOT NULL,
    image_url    VARCHAR(500),
    user_id      BIGINT       NOT NULL,
    court_id     BIGINT       NOT NULL,
    FOREIGN KEY (user_id)  REFERENCES users(id)  ON DELETE CASCADE,
    FOREIGN KEY (court_id) REFERENCES courts(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE refresh_tokens (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    token      VARCHAR(512) NOT NULL UNIQUE,
    expired_at DATETIME     NOT NULL,
    user_id    BIGINT       NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE token_blacklist (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    token      VARCHAR(512) NOT NULL,
    expired_at DATETIME     NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO users (username, email, password, role, enabled) VALUES
('admin1',    'admin1@gmail.com',    '$2a$10$KKFs7dWkSV2fPc/VjU4DmeTRdvVoESFe5HALOi.WVPO3nrY4ifX9O', 'ADMIN',    true),
('manager1',  'manager1@gmail.com',  '$2a$10$KKFs7dWkSV2fPc/VjU4DmeTRdvVoESFe5HALOi.WVPO3nrY4ifX9O', 'MANAGER',  true),
('customer1', 'customer1@gmail.com', '$2a$10$KKFs7dWkSV2fPc/VjU4DmeTRdvVoESFe5HALOi.WVPO3nrY4ifX9O', 'CUSTOMER', true);

INSERT INTO courts (name, location, description, image_url, active) VALUES
('Sân số 1', 'Tầng 2, Tòa nhà ABC', 'Sân tiêu chuẩn quốc tế', NULL, true),
('Sân số 2', 'Tầng 2, Tòa nhà ABC', 'Sân có máy lạnh',        NULL, true),
('Sân số 3', 'Tầng 3, Tòa nhà ABC', 'Sân VIP',                NULL, true);
