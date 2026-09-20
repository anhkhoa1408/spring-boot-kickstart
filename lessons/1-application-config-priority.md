# Bài 1: Thứ tự ưu tiên cấu hình ứng dụng

Spring Boot đọc các file `application` để biết cách chạy ứng dụng, ví dụ dùng cổng nào. Khi nhiều file có cùng khóa, giá trị có ưu tiên cao hơn được dùng.

## 1. Bốn vị trí cơ bản

Bài này xét cấu hình mặc định, không xét profile. Thứ tự từ **cao xuống thấp** trong 4 vị trí:

| Ưu tiên | Vị trí | Ý nghĩa |
| --- | --- | --- |
| 1 | `./config/application.properties` | Thư mục `config` bên ngoài ứng dụng |
| 2 | `./application.properties` | Thư mục làm việc khi chạy ứng dụng |
| 3 | `classpath:/config/application.properties` | Tương ứng `src/main/resources/config/` trong mã nguồn |
| 4 | `classpath:/application.properties` | Tương ứng `src/main/resources/` trong mã nguồn |

`./` là thư mục làm việc khi chạy ứng dụng. `classpath:` chỉ tài nguyên đi kèm ứng dụng. Thứ tự này cũng áp dụng cho YAML.

Ví dụ: nếu 4 vị trí lần lượt đặt `server.port` là `8083`, `8082`, `8081`, `8080`, ứng dụng dùng cổng **8083**, khi không có nguồn cấu hình ưu tiên cao hơn.

## 2. Properties và YAML

Hai cách viết dưới đây có cùng ý nghĩa: cổng `8080`, tên ứng dụng `springboot-hello`.

Trong `application.properties`, viết khóa đầy đủ với dấu `=`:

```properties
server.port=8080
spring.application.name=springboot-hello
```

Trong `application.yaml` hoặc `application.yml`, dùng dấu `:` và thụt lề:

```yaml
server:
  port: 8080
spring:
  application:
    name: springboot-hello
```

YAML thụt lề bằng **dấu cách**, không dùng tab. `.yaml` và `.yml` đều được hỗ trợ.

## 3. Khi nhiều file cùng tồn tại

- **Cùng vị trí:** `.properties` ưu tiên hơn YAML khi trùng khóa. Ví dụ, trong `src/main/resources/`, properties đặt cổng `8080`, YAML đặt `9090` thì dùng **8080**.
- **Khác vị trí:** xét thứ tự ở bảng trên. `./config/application.yaml` đặt cổng `9090` vẫn thắng `classpath:/application.properties` đặt `8080`.
- **Ghi đè từng khóa:** nếu file ưu tiên cao chỉ đổi `server.port`, giá trị `spring.application.name` từ file ưu tiên thấp vẫn được giữ.

Nên chọn một định dạng thống nhất để dễ theo dõi.

## 4. Cần nhớ

- Cấu hình bên ngoài ưu tiên hơn bên trong; `config` ưu tiên hơn thư mục gốc tương ứng.
- `.properties` không luôn thắng YAML ở mọi vị trí.
- Ngoài phạm vi bài này còn có `./config/*/`, biến môi trường, tham số dòng lệnh và các cách tùy chỉnh khác.

Tham khảo: [Tài liệu cấu hình Spring Boot](https://docs.spring.io/spring-boot/reference/features/external-config.html).
