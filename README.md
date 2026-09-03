# Assignment 03 User Profile

Project Jakarta Servlet/JSP duoc tao bang cach copy tu Assignment 02 de lam bai 03, tap trung vao chuc nang cap nhat profile user bang JPA va multipart upload.

## Muc tieu bai tap hien tai

1. Giu lai nen tang auth, OTP, category, product cua project goc de dung lam xuat phat diem.
2. Chi trien khai yeu cau 3: user cap nhat `fullName`, `phone`, `images`.
3. Tach rieng module profile de sau nay co the tu boc SiteMesh va them validation form ma khong can doi lai logic profile.

## Cong nghe

- Java 17
- Maven WAR
- Jakarta Servlet / JSP / JSTL
- JPA (Hibernate)
- SQL Server
- Jakarta Mail
- BCrypt
- Multipart file upload

## Chuc nang dang co

- Dang ky tai khoan voi OTP email.
- Dang nhap, dang xuat, quen mat khau, dat lai mat khau bang OTP.
- CRUD category va product tu project goc.
- Public catalog, chi tiet san pham, upload anh cho category va product.
- User profile update tai `/profile` voi `fullName`, `phone`, `images`.

## Pham vi da lam cho bai 03

- Them route `/profile` rieng biet voi auth va admin.
- Them service profile rieng de cap nhat thong tin nguoi dung.
- Them cac cot `Phone`, `Images` cho bang `users`.
- Ho tro upload avatar bang multipart va luu bang JPA.
- Lam moi `currentUser` trong session sau khi update profile.

## Pham vi chua lam

- Chua cau hinh SiteMesh Decorator 3.
- Chua bo sung validation cho cac form theo yeu cau bai 03.

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
- `/admin/products`

## Co so du lieu

Project su dung cac bang chinh:

- `users`
- `otp_verifications`
- `categories`
- `products`

Script tao va nang cap bang nam trong file `src/main/resources/database.sql`.
Database hien dang tro ve `HelloCoAiKhongDB` de dung chung moi truong SQL Server san co cua project goc.

## Cau hinh upload

Upload dir mac dinh:

```properties
app.upload.dir=/home/thien/uploads/assignment03-user-profile
```

## Build project

```bash
mvn clean package
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
