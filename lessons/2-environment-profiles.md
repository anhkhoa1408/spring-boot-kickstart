# Bài 2: Thiết lập profile cho từng môi trường

Profile giúp cùng một ứng dụng dùng cấu hình khác nhau theo môi trường, như cổng chạy, mức log hoặc kết nối database.

| Profile | Dùng khi nào? |
| --- | --- |
| `dev` | Phát triển trên máy cá nhân |
| `test` | Kiểm thử |
| `prod` | Chạy thực tế |

Đây là tên do mình quy ước. Spring Boot không tự chọn môi trường và không tự bật `test` khi chạy kiểm thử.

## 1. File chung và file profile

Đặt các file trong `src/main/resources/`:

- `application.yaml`: cấu hình chung.
- `application-dev.yaml`: phần cấu hình riêng cho `dev`. Tương tự với `application-test.yaml` và `application-prod.yaml`.
- Có thể dùng `.yml` hoặc `.properties` thay `.yaml`; nên chọn một định dạng thống nhất.

Ví dụ `application.yaml`:

```yaml
spring:
  application:
    name: springboot-hello
server:
  port: 8080
```

Và `application-dev.yaml`:

```yaml
server:
  port: 8081
logging:
  level:
    com.shopdevjava: DEBUG
```

Với hai file ở cùng vị trí này, khi bật `dev`: tên ứng dụng vẫn là **springboot-hello**, cổng được ghi đè thành **8081**, mức log của `com.shopdevjava` được thêm là **DEBUG**. File chung vẫn được nạp.

## 2. Kích hoạt profile

Chọn một trong các cách dưới đây. Tạo file `application-dev.yaml` thôi chưa đủ; cần bật profile `dev`.

**Trong file chung:** thêm `profiles` vào khối `spring` hiện có của `application.yaml`:

```yaml
spring:
  profiles:
    active: dev
```

Nếu dùng `application.properties`, cấu hình tương đương là:

```properties
spring.profiles.active=dev
```

Không đặt `spring.profiles.active` trong file profile như `application-dev.yaml`.

**Khi chạy Maven:** chạy từ thư mục gốc dự án:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

**Khi chạy JAR:** giả sử đã build được file `app.jar`:

```bash
java -jar app.jar --spring.profiles.active=prod
```

Thay `dev` hoặc `prod` bằng tên profile cần dùng. Tham số `--spring.profiles.active` ghi đè lựa chọn trong file cấu hình.

## 3. Những điều cần nhớ

- `active` chọn profile để chạy. `default` chỉ áp dụng khi chưa chỉ định profile hoạt động.
- Nếu không đặt gì, profile mặc định có tên `default`, không phải `dev`. Muốn mặc định dùng `dev`, đặt `spring.profiles.default=dev` trong `application.properties` chung; với YAML, dùng `default: dev` thay `active: dev` ở ví dụ trên.
- Nếu chọn bằng file, đặt `active` và `default` trong file cấu hình chung, không đặt trong file profile.
- Sau khi chạy ví dụ với `dev`, đọc log khởi động để kiểm tra profile **dev** và cổng **8081**.

Tham khảo: [Profiles](https://docs.spring.io/spring-boot/reference/features/profiles.html), [quy tắc nạp cấu hình](https://docs.spring.io/spring-boot/reference/features/external-config.html).
