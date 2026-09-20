# Bài 5: JPA cơ bản

JPA giúp ánh xạ đối tượng Java với bảng trong database. **Hibernate** triển khai JPA; **Spring Data JPA** cung cấp repository để mình gọi các hàm như `save`, `findAll` mà ít phải viết SQL cho thao tác cơ bản.

Kết nối MySQL đã được giải thích ở [bài 4](4-jpa-yaml-configuration.md). Bài này tập trung vào cách dùng trong Java.

## 1. Entity: class đại diện cho dữ liệu trong bảng

Ví dụ `ProductEntity` ánh xạ với bảng `products`: mỗi đối tượng tương ứng một dòng dữ liệu, các thuộc tính tương ứng với các cột.

Các ví dụ lược bỏ `package` và `import`. Annotation JPA dùng `jakarta.persistence.*`; getter, setter và constructor dùng Lombok; `BigDecimal` thuộc `java.math`.

```java
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", nullable = false)
    private String productName;

    private BigDecimal productPrice;
}
```

| Annotation | Hiểu đơn giản |
| --- | --- |
| `@Entity` | Cho JPA biết đây là class cần ánh xạ với database |
| `@Table` | Chỉ định tên bảng |
| `@Id` | Đánh dấu khóa chính, dùng để phân biệt từng dòng |
| `@GeneratedValue` với `IDENTITY` | Dùng khóa tự tăng của MySQL; khi tạo mới không tự gán `id` |
| `@Column` | Cấu hình cột; ví dụ trên đặt tên `product_name` và không cho phép `NULL` |
| `@NoArgsConstructor` | Lombok tạo constructor không tham số để JPA khởi tạo đối tượng |

Entity cần constructor không tham số `public` hoặc `protected`. `@Getter`/`@Setter` tạo hàm đọc/gán giá trị. Xem [hướng dẫn entity](https://spring.io/guides/gs/accessing-data-jpa/).

## 2. Repository: gọi các thao tác với database

Ví dụ file `repository/ProductJpaRepository.java`:

```java
public interface ProductJpaRepository
        extends JpaRepository<ProductEntity, Long> {
}
```

`JpaRepository` thuộc `org.springframework.data.jpa.repository`. `ProductEntity` là kiểu dữ liệu quản lý, `Long` là kiểu của khóa chính `id`.

Spring Data JPA tạo phần triển khai khi chạy; không cần tự viết class `ProductJpaRepositoryImpl`. Đặt repository dưới package gốc ứng dụng để Spring Boot tìm thấy. Xem [bắt đầu với Spring Data JPA](https://docs.spring.io/spring-data/jpa/reference/jpa/getting-started.html).

| Hàm có sẵn | Dùng để làm gì? |
| --- | --- |
| `save(product)` | Lưu đối tượng mới hoặc thay đổi của đối tượng đã có |
| `findAll()` | Lấy toàn bộ sản phẩm; khi dữ liệu nhiều nên dùng phân trang |
| `findById(id)` | Tìm theo khóa chính; trả `Optional`, có thể không tìm thấy |
| `existsById(id)` | Kiểm tra có sản phẩm mang ID đó không |
| `deleteById(id)` | Xóa theo khóa chính |

## 3. Ví dụ thêm và sửa sản phẩm

Giả sử service đã nhận `ProductJpaRepository` qua constructor như cách DI ở bài 3, với biến tên `repository`. Các đoạn sau đặt trong method của service.

**Thêm mới:** để `id` là `null`, MySQL sẽ sinh ID.

```java
ProductEntity product = new ProductEntity();
product.setProductName("Keyboard");
product.setProductPrice(new BigDecimal("250000"));
ProductEntity saved = repository.save(product);
```

**Cập nhật:** tìm bản ghi trước, đổi giá trị rồi lưu.

```java
ProductEntity product = repository.findById(1L)
    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
product.setProductPrice(new BigDecimal("300000"));
ProductEntity saved = repository.save(product);
```

`save()` không có nghĩa là luôn thêm dòng mới. Với entity ví dụ, ID `null` được xem là mới; khi cập nhật nên đọc bản ghi trước thay vì tự gán ID để đoán. Dùng đối tượng mà `save()` trả về cho các bước tiếp theo. Xem [cách lưu entity](https://docs.spring.io/spring-data/jpa/reference/jpa/entity-persistence.html).

## 4. Những điều cần nhớ

- Chỉ gọi `new ProductEntity()` hoặc setter thì chưa đồng nghĩa dữ liệu đã được lưu xuống MySQL.
- `@Entity` khai báo ánh xạ; việc tự tạo/cập nhật bảng còn phụ thuộc `ddl-auto` và cấu hình ở bài 4.
- `findById()` có thể không tìm thấy: xử lý bằng `orElseThrow` hoặc kiểm tra kết quả, tránh gọi `.get()` ngay.
- Với nhiều thao tác cần thành công cùng nhau, đặt `@Transactional` trên method service để chạy trong một giao dịch. Mặc định, `RuntimeException` thoát ra sẽ khiến giao dịch được hoàn tác. Xem [transaction trong Spring Data JPA](https://docs.spring.io/spring-data/jpa/reference/jpa/transactions.html).
- `@Repository` hoặc tên class có chữ `Repository` không tự biến mã viết tay thành Spring Data JPA. Interface trong bài kế thừa `JpaRepository` mới có các hàm trên.

Đây là ví dụ để học. `ProductEntity` hiện mới có `@Data`, còn repository tự viết đang trả dữ liệu giả. Cần bổ sung mapping và dùng repository JPA để lưu/đọc MySQL như trong bài.
