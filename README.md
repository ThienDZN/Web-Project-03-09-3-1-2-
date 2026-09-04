# Assignment 03 User Profile

Project Jakarta Servlet/JSP duoc copy tu Assignment 02 va duoc mo rong de dap ung 2 yeu cau hien tai:

1. Cau hinh SiteMesh Decorator 3 voi 1 template Bootstrap dung chung.
2. Them validation cho cac chuc nang co form.

## Cong nghe

- Java 17
- Maven WAR
- Jakarta Servlet / JSP / JSTL
- JPA (Hibernate)
- MySQL
- SiteMesh Decorator 3
- Bootstrap 5
- Jakarta Mail
- BCrypt
- Multipart file upload

## Chuc nang dang co

- Dang ky tai khoan voi OTP email.
- Dang nhap, dang xuat, quen mat khau, dat lai mat khau bang OTP.
- CRUD category va product.
- Public catalog, chi tiet san pham, upload anh cho category va product.
- User profile update tai `/profile` voi `fullName`, `phone`, `images`.
- Decorator Bootstrap dung chung cho auth, profile, public catalog va admin pages qua SiteMesh.
- Validation server-side cho cac form login, register, OTP, forgot/reset password, profile, category va product.

## Pham vi da lam

- Them SiteMesh dependency va file cau hinh `src/main/webapp/WEB-INF/sitemesh3.xml`.
- Tao decorator Bootstrap chung tai `src/main/webapp/WEB-INF/decorators/bootstrap-template.jsp`.
- Bo sung validation state cho controller va hien thi field errors tren JSP.
- Cho phep ghi de cau hinh DB qua `-Dapp.db.url`, `-Dapp.db.user`, `-Dapp.db.password` hoac cac bien moi truong `APP_DB_URL`, `APP_DB_USER`, `APP_DB_PASSWORD`.
- Doi database mac dinh cua project sang `thien` tren MySQL localhost:3306.

## Route chinh

- `/register`
- `/verify-otp`
- `/resend-otp`
- `/login`
- `/logout`
- `/forgot-password`
- `/reset-password`
- `/profile`
- `/home`
- `/product`
- `/product/detail?id=...`
- `/admin/categories`
- `/admin/products`

## Co so du lieu

Project su dung cac bang chinh:

- `users`
- `otp_verifications`
- `categories`
- `products`

Script tao va nang cap bang nam trong file `src/main/resources/database.sql`.
Persistence mac dinh dang tro ve MySQL database `thien` tren `localhost:3306`.

## Cau hinh upload

Upload dir mac dinh:

```properties
app.upload.dir=/home/thien/uploads/assignment03-user-profile
```

## Build va kiem tra

```bash
mvn package
```

WAR sau khi build:

```text
target/assignment03-user-profile.war
```

## URL deploy mau

- `http://localhost:8080/assignment03-user-profile/home`
- `http://localhost:8080/assignment03-user-profile/product`
- `http://localhost:8080/assignment03-user-profile/login`
- `http://localhost:8080/assignment03-user-profile/profile`
- `http://localhost:8080/assignment03-user-profile/admin/products`

## Tai khoan seed mac dinh

- Username: `admin`
- Email: `admin@example.com`
- Password: `Admin@123`
- Demo user profile: `thien` / `User123@Aa1`
