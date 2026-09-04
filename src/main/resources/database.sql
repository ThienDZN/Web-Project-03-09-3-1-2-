CREATE DATABASE IF NOT EXISTS thien
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE thien;

CREATE TABLE IF NOT EXISTS categories (
    CategoryId INT NOT NULL AUTO_INCREMENT,
    CategoryName VARCHAR(50) NOT NULL,
    Images VARCHAR(500) NULL,
    Status INT NOT NULL DEFAULT 1,
    PRIMARY KEY (CategoryId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Videos (
    VideoId VARCHAR(50) NOT NULL,
    Active INT NOT NULL DEFAULT 1,
    Description VARCHAR(500) NULL,
    Poster VARCHAR(500) NULL,
    Title VARCHAR(500) NULL,
    Views INT NOT NULL DEFAULT 0,
    CategoryId INT NULL,
    PRIMARY KEY (VideoId),
    KEY idx_videos_category (CategoryId),
    CONSTRAINT fk_videos_categories
        FOREIGN KEY (CategoryId) REFERENCES categories(CategoryId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS users (
    UserId BIGINT NOT NULL AUTO_INCREMENT,
    FullName VARCHAR(120) NOT NULL,
    Username VARCHAR(50) NOT NULL,
    Email VARCHAR(120) NOT NULL,
    PasswordHash VARCHAR(255) NOT NULL,
    Phone VARCHAR(20) NULL,
    Images VARCHAR(500) NULL,
    RoleName VARCHAR(20) NOT NULL DEFAULT 'USER',
    Enabled TINYINT(1) NOT NULL DEFAULT 0,
    Status INT NOT NULL DEFAULT 1,
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (UserId),
    UNIQUE KEY uq_users_username (Username),
    UNIQUE KEY uq_users_email (Email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS otp_verifications (
    OtpId BIGINT NOT NULL AUTO_INCREMENT,
    UserId BIGINT NULL,
    Email VARCHAR(120) NOT NULL,
    OtpCode VARCHAR(10) NOT NULL,
    Purpose VARCHAR(30) NOT NULL,
    ExpiryAt DATETIME NOT NULL,
    Used TINYINT(1) NOT NULL DEFAULT 0,
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (OtpId),
    KEY idx_otp_email_purpose (Email, Purpose),
    KEY idx_otp_user (UserId),
    CONSTRAINT fk_otp_user
        FOREIGN KEY (UserId) REFERENCES users(UserId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS products (
    ProductId BIGINT NOT NULL AUTO_INCREMENT,
    ProductName VARCHAR(150) NOT NULL,
    Description VARCHAR(2000) NULL,
    Price DECIMAL(18,2) NOT NULL,
    Quantity INT NOT NULL DEFAULT 0,
    Image VARCHAR(500) NULL,
    Status INT NOT NULL DEFAULT 1,
    CreatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CategoryId INT NOT NULL,
    PRIMARY KEY (ProductId),
    KEY idx_products_category (CategoryId),
    CONSTRAINT fk_product_category
        FOREIGN KEY (CategoryId) REFERENCES categories(CategoryId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
