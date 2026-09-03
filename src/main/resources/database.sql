IF DB_ID(N'HelloCoAiKhongDB') IS NULL
BEGIN
    CREATE DATABASE HelloCoAiKhongDB;
END
GO

USE HelloCoAiKhongDB;
GO

IF OBJECT_ID(N'dbo.categories', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.categories (
        CategoryId INT IDENTITY(1,1) PRIMARY KEY,
        CategoryName NVARCHAR(50) NOT NULL,
        Images NVARCHAR(500) NULL,
        Status INT NOT NULL DEFAULT 1
    );
END
GO

IF OBJECT_ID(N'dbo.Videos', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Videos (
        VideoId NVARCHAR(50) PRIMARY KEY,
        Active INT NOT NULL DEFAULT 1,
        Description NVARCHAR(500) NULL,
        Poster NVARCHAR(500) NULL,
        Title NVARCHAR(500) NULL,
        Views INT NOT NULL DEFAULT 0,
        CategoryId INT NULL,
        CONSTRAINT FK_Videos_Categories FOREIGN KEY (CategoryId) REFERENCES dbo.categories(CategoryId)
    );
END
GO

IF OBJECT_ID(N'dbo.users', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.users (
        UserId BIGINT IDENTITY(1,1) PRIMARY KEY,
        FullName NVARCHAR(120) NOT NULL,
        Username VARCHAR(50) NOT NULL UNIQUE,
        Email VARCHAR(120) NOT NULL UNIQUE,
        PasswordHash VARCHAR(255) NOT NULL,
        RoleName VARCHAR(20) NOT NULL DEFAULT 'USER',
        Enabled BIT NOT NULL DEFAULT 0,
        Status INT NOT NULL DEFAULT 1,
        CreatedAt DATETIME2 NOT NULL DEFAULT SYSDATETIME()
    );
END
GO

IF COL_LENGTH(N'dbo.users', N'FullName') IS NULL
BEGIN
    ALTER TABLE dbo.users ADD FullName NVARCHAR(120) NULL;
END
GO

IF COL_LENGTH(N'dbo.users', N'Email') IS NULL
BEGIN
    ALTER TABLE dbo.users ADD Email VARCHAR(120) NULL;
END
GO

IF COL_LENGTH(N'dbo.users', N'PasswordHash') IS NULL
BEGIN
    ALTER TABLE dbo.users ADD PasswordHash VARCHAR(255) NULL;
END
GO

IF COL_LENGTH(N'dbo.users', N'Phone') IS NULL
BEGIN
    ALTER TABLE dbo.users ADD Phone VARCHAR(20) NULL;
END
GO

IF COL_LENGTH(N'dbo.users', N'Images') IS NULL
BEGIN
    ALTER TABLE dbo.users ADD Images NVARCHAR(500) NULL;
END
GO

IF COL_LENGTH(N'dbo.users', N'RoleName') IS NULL
BEGIN
    ALTER TABLE dbo.users ADD RoleName VARCHAR(20) NOT NULL CONSTRAINT DF_users_RoleName DEFAULT 'USER';
END
GO

IF COL_LENGTH(N'dbo.users', N'Enabled') IS NULL
BEGIN
    ALTER TABLE dbo.users ADD Enabled BIT NOT NULL CONSTRAINT DF_users_Enabled DEFAULT 0;
END
GO

IF COL_LENGTH(N'dbo.users', N'Status') IS NULL
BEGIN
    ALTER TABLE dbo.users ADD Status INT NOT NULL CONSTRAINT DF_users_Status DEFAULT 1;
END
GO

IF COL_LENGTH(N'dbo.users', N'CreatedAt') IS NULL
BEGIN
    ALTER TABLE dbo.users ADD CreatedAt DATETIME2 NOT NULL CONSTRAINT DF_users_CreatedAt DEFAULT SYSDATETIME();
END
GO

IF EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID(N'dbo.users') AND name = N'username')
BEGIN
    UPDATE dbo.users
    SET FullName = COALESCE(FullName, CAST(username AS NVARCHAR(120))),
        Email = COALESCE(Email, CONCAT('legacy_user_', UserId, '@local.invalid')),
        PasswordHash = COALESCE(PasswordHash, CAST([password] AS VARCHAR(255)))
    WHERE FullName IS NULL OR Email IS NULL OR PasswordHash IS NULL;
END
GO

IF EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID(N'dbo.users') AND name = N'password')
BEGIN
    EXEC('ALTER TABLE dbo.users ALTER COLUMN [password] NVARCHAR(255) NULL');
END
GO

IF COL_LENGTH(N'dbo.otp_verifications', N'CreatedAt') IS NULL
BEGIN
    ALTER TABLE dbo.otp_verifications ADD CreatedAt DATETIME2 NOT NULL CONSTRAINT DF_otp_verifications_CreatedAt DEFAULT SYSDATETIME();
END
GO

IF OBJECT_ID(N'dbo.otp_verifications', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.otp_verifications (
        OtpId BIGINT IDENTITY(1,1) PRIMARY KEY,
        UserId BIGINT NULL,
        Email VARCHAR(120) NOT NULL,
        OtpCode VARCHAR(10) NOT NULL,
        Purpose VARCHAR(30) NOT NULL,
        ExpiryAt DATETIME2 NOT NULL,
        Used BIT NOT NULL DEFAULT 0,
        CreatedAt DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        CONSTRAINT FK_Otp_User FOREIGN KEY (UserId) REFERENCES dbo.users(UserId)
    );
END
GO

IF OBJECT_ID(N'dbo.products', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.products (
        ProductId BIGINT IDENTITY(1,1) PRIMARY KEY,
        ProductName NVARCHAR(150) NOT NULL,
        Description NVARCHAR(2000) NULL,
        Price DECIMAL(18,2) NOT NULL,
        Quantity INT NOT NULL DEFAULT 0,
        Image NVARCHAR(500) NULL,
        Status INT NOT NULL DEFAULT 1,
        CreatedAt DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        CategoryId INT NOT NULL,
        CONSTRAINT FK_Product_Category FOREIGN KEY (CategoryId) REFERENCES dbo.categories(CategoryId)
    );
END
GO
