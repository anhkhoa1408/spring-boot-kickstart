# Bài 4: Cấu hình JPA trong YAML

Dựa trên [application.yaml](../src/main/resources/application.yaml) của dự án. Cần nhớ hai nhóm:

- **`datasource`**: kết nối đến database nào, bằng tài khoản nào.
- **`jpa`**: cách làm việc với bảng và hiển thị câu SQL. Hibernate là thư viện thực hiện phần này.

## 1. Cấu hình mẫu

```yaml
spring:
  application:
    name: springboot-kickstart
  datasource:
    url: jdbc:mysql://localhost:33336/shopdev
    username: root
    password: "your-password"
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    database: mysql
    show-sql: true
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true
```

Thay `your-password` bằng mật khẩu MySQL của bạn khi thực hành.

## 2. Ý nghĩa từng cấu hình

Các khóa trong bảng được viết ngắn theo vị trí ở YAML trên.

| Cấu hình | Hiểu đơn giản |
| --- | --- |
| `application.name` | Tên ứng dụng, không phải tên database |
| `datasource.url` | Kết nối MySQL trên máy `localhost`, cổng `33336`, database `shopdev` |
| `datasource.username` / `password` | Tài khoản và mật khẩu đăng nhập MySQL |
| `driver-class-name` | Driver giúp Java kết nối MySQL; thường có thể bỏ vì Spring Boot suy ra từ URL |
| `jpa.database: mysql` | Cho biết loại database là MySQL; không tạo database |
| `show-sql: true` | In câu SQL Hibernate thực thi ra console |
| `ddl-auto: update` | Khi khởi động, Hibernate cố gắng cập nhật bảng theo entity — class Java đại diện cho dữ liệu trong bảng |
| `format_sql: true` | Xuống dòng, căn lề SQL cho dễ đọc; không tự bật in SQL |
| `use_sql_comments: true` | Thêm chú thích vào SQL để dễ biết câu lệnh phục vụ thao tác nào |

**`33336` là cổng MySQL.** Cổng gọi API được cấu hình riêng bằng `server.port`.

Tham khảo: [cấu hình Spring Boot](https://docs.spring.io/spring-boot/appendix/application-properties/), [các tùy chọn Hibernate](https://docs.jboss.org/hibernate/orm/7.0/userguide/html_single/Hibernate_User_Guide.html).

## 3. Chú ý nhất: `ddl-auto`

Thuộc tính này quyết định Hibernate có tự thay đổi cấu trúc bảng hay không.

| Giá trị | Điều xảy ra |
| --- | --- |
| `none` | Không tự tạo, sửa hoặc kiểm tra bảng |
| `validate` | Kiểm tra bảng có khớp entity; sai thì báo lỗi, không tự sửa |
| `update` | Cố gắng bổ sung/cập nhật bảng; tiện khi học, không đảm bảo xử lý mọi thay đổi |
| `create` | Xóa rồi tạo lại các bảng Hibernate quản lý khi khởi động; mất dữ liệu cũ |
| `create-drop` | Như `create`, thêm việc xóa bảng khi ứng dụng đóng bình thường |

**Khi học:** có thể dùng `update`. **Khi có dữ liệu cần giữ:** tránh `create` và `create-drop`. Với môi trường chạy thật, thường dùng `validate` cùng công cụ quản lý thay đổi database như Flyway/Liquibase. `validate` yêu cầu bảng đã được chuẩn bị đúng trước đó. Xem [hướng dẫn khởi tạo database](https://docs.spring.io/spring-boot/how-to/data-initialization.html).

## 4. Những điều cần nhớ

- MySQL phải đang chạy; database `shopdev` phải tồn tại. `update` không tự tạo database này với URL trên.
- Cần dependency JPA và driver MySQL. Dự án hiện đã có cả hai trong `pom.xml`.
- Muốn Hibernate nhận một class là entity, cần `@Entity`, khóa chính `@Id` và class nằm trong phạm vi quét. Đặt tên `ProductEntity` thôi chưa đủ.
- `datasource` và `jpa` cùng nằm dưới `spring`. Thụt lề bằng dấu cách; giữ đúng tên `format_sql`, `use_sql_comments` có dấu gạch dưới.
- Khi triển khai, dùng `password: ${DB_PASSWORD}` rồi cung cấp biến môi trường `DB_PASSWORD`, thay vì ghi mật khẩu thật vào mã nguồn.

