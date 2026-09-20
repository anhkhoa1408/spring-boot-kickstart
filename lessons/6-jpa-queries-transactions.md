# Bài 6: Query và transaction trong JPA

Tiếp nối [bài 5](5-jpa-basics.md), các method repository dưới đây đặt trong interface kế thừa `JpaRepository<ProductEntity, Long>`.

Ví dụ giả sử entity `ProductEntity` ánh xạ bảng `products`, thuộc tính `productName` → cột `product_name`, `productPrice` → cột `product_price`. Các đoạn mã lược bỏ `package` và `import`.

## 1. Tạo query từ tên hàm

Spring Data JPA đọc tên hàm và tạo câu truy vấn, mình không cần viết SQL:

```java
List<ProductEntity> findByProductName(String name);
List<ProductEntity> findByProductPriceGreaterThan(BigDecimal price);
List<ProductEntity> findByProductNameContaining(String keyword);
```

Lần lượt là: tìm theo tên chính xác, tìm giá lớn hơn một mức, tìm tên chứa từ khóa.

**Tên hàm dùng thuộc tính Java:** `ProductName` ứng với `productName`, không viết `findByProduct_name`. Điều kiện đơn giản nên dùng cách này. Xem [tạo query từ tên hàm](https://docs.spring.io/spring-data/jpa/reference/repositories/query-methods-details.html).

## 2. JPQL và `@Query`

**JPQL dùng tên entity và thuộc tính Java.** Hibernate chuyển nó thành SQL phù hợp với database.

```java
@Query("select p from ProductEntity p where p.productPrice >= :minPrice")
List<ProductEntity> findWithJpql(@Param("minPrice") BigDecimal minPrice);
```

- `ProductEntity`: tên entity, không phải tên bảng `products`.
- `p`: tên viết tắt dùng trong query; `p.productPrice` là thuộc tính Java.
- `:minPrice`: chỗ nhận giá trị từ tham số có `@Param("minPrice")`.

`@Query` mặc định dùng JPQL. Dùng khi tên hàm bắt đầu quá dài hoặc cần tự viết điều kiện rõ ràng.

## 3. SQL trực tiếp với `@NativeQuery`

**Native query dùng tên bảng và cột thật trong database.** Cùng yêu cầu tìm theo giá ở trên:

```java
@NativeQuery("select p.* from products p where p.product_price >= :minPrice")
List<ProductEntity> findWithSql(@Param("minPrice") BigDecimal minPrice);
```

Trong ví dụ này, có thể thay `@NativeQuery(...)` bằng `@Query(value = "...", nativeQuery = true)` với cùng câu SQL. Chọn một cách viết, không gắn cả hai.

| Cách viết | Dùng tên gì? | Khi nào dùng? |
| --- | --- | --- |
| Tên hàm | Thuộc tính Java | Điều kiện đơn giản |
| `@Query` mặc định | Entity và thuộc tính Java | Tự viết query theo mô hình Java |
| `@NativeQuery` | Bảng và cột database | Cần SQL trực tiếp hoặc tính năng riêng của database |

Truyền giá trị qua `@Param`; không nối chuỗi dữ liệu người dùng vào query. Tham khảo [Query và Native Query](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html).

## 4. Update và Delete

Với câu `UPDATE`/`DELETE` tự viết, thêm **`@Modifying`** và chạy trong **transaction**:

```java
@Transactional
@Modifying
@Query("update ProductEntity p set p.productPrice = :price where p.id = :id")
int updatePrice(@Param("id") Long id, @Param("price") BigDecimal price);

@Transactional
@Modifying
@Query("delete from ProductEntity p where p.id = :id")
int deleteProduct(@Param("id") Long id);
```

- `@Modifying`: báo đây là query thay đổi dữ liệu; annotation này không tự mở transaction.
- `@Transactional`: chạy thao tác trong một giao dịch database.
- Kết quả `int`: số dòng bị ảnh hưởng; `0` có thể là không có bản ghi khớp.

Quy tắc này cũng áp dụng cho `UPDATE`/`DELETE` viết bằng `@NativeQuery`. Các hàm có sẵn như `save()` và `deleteById()` không cần mình thêm `@Modifying`. Xem [Modifying](https://docs.spring.io/spring-data/jpa/reference/api/java/org/springframework/data/jpa/repository/Modifying.html).

## 5. Transaction: nhiều thao tác cùng thành công hoặc hoàn tác

**Commit** là xác nhận lưu thay đổi. **Rollback** là hoàn tác thay đổi trong giao dịch khi có lỗi phù hợp.

`@Transactional` ở repository phía trên bảo vệ từng lần gọi riêng. Muốn hai lần cập nhật cùng một giao dịch, đặt annotation ở method service:

```java
@Service
@RequiredArgsConstructor
public class ProductPriceService {
    private final ProductJpaRepository repository;

    @Transactional
    public void changeTwoPrices(Long firstId, Long secondId, BigDecimal price) {
        int first = repository.updatePrice(firstId, price);
        int second = repository.updatePrice(secondId, price);

        if (first == 0 || second == 0) {
            throw new IllegalArgumentException("Không tìm thấy đủ sản phẩm");
        }
    }
}
```

Giả sử hai ID khác nhau: nếu lần đầu cập nhật được nhưng lần sau không tìm thấy, exception khiến **cả giao dịch rollback**, gồm thay đổi lần đầu. Nếu không có lỗi, cả hai được commit. Các method repository mặc định tham gia transaction đang có của service. Xem [transaction trong Spring Data JPA](https://docs.spring.io/spring-data/jpa/reference/jpa/transactions.html).

## 6. Lưu ý cốt lõi

- Mặc định rollback khi `RuntimeException` hoặc `Error` thoát khỏi method. Với checked exception, có thể dùng `@Transactional(rollbackFor = Exception.class)`. Xem [quy tắc rollback](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/rolling-back.html).
- Nếu bắt lỗi bằng `try/catch` rồi bỏ qua, Spring có thể không biết cần rollback.
- Với cách dùng mặc định, gọi method `@Transactional` qua bean được Spring quản lý. Gọi bằng `this.method()` trong cùng class không kích hoạt transaction mới từ annotation đó. Xem [cách gọi method có transaction](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html).
- Query update/delete trực tiếp có thể làm entity đã tải trong bộ nhớ bị cũ. Khi mới học, tránh trộn chúng với việc sửa các entity đó trong cùng transaction.
- Kiểm tra điều kiện `where`: bỏ điều kiện có thể cập nhật/xóa toàn bộ bảng.

Import: `Query`, `NativeQuery`, `Modifying` từ `org.springframework.data.jpa.repository`; `Param` từ `org.springframework.data.repository.query`; `Transactional` từ `org.springframework.transaction.annotation`.

Đây là các ví dụ học tập sau khi đã thiết lập entity và repository JPA ở bài 5; bài học chưa thay đổi mã ứng dụng.
