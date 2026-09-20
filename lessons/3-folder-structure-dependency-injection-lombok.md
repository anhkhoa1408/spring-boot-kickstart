# Bài 3: Cấu trúc thư mục, annotation, DI và Lombok

## 1. Mỗi thư mục dùng để làm gì?

Cấu trúc gợi ý cho dự án:

```text
src/main/java/com/shopdevjava/springboot_hello/
├── SpringbootHelloApplication.java  # Khởi động ứng dụng
├── controller/                     # Nhận request, trả kết quả API
├── service/                        # Xử lý nghiệp vụ
├── repository/                     # Đọc/ghi dữ liệu
├── entities/                       # Class ánh xạ với bảng database
├── dto/                            # Dữ liệu nhận vào/trả ra qua API
└── config/                         # Cấu hình bằng Java

src/main/resources/                 # application.yaml và tài nguyên
src/test/java/                      # Mã kiểm thử
pom.xml                             # Thư viện và cấu hình build
```

Ví dụ: lấy danh sách sản phẩm thường đi qua **Controller → Service → Repository → Database**, rồi trả kết quả về client.

Đặt class khởi động trong package gốc, các class còn lại trong package con. Spring mặc định tìm các class cần quản lý trong phạm vi này. Tên thư mục là quy ước; không bắt buộc tạo hết ngay. Xem [cấu trúc Spring Boot](https://docs.spring.io/spring-boot/reference/using/structuring-your-code.html).

## 2. Annotation thường gặp

Annotation là phần đánh dấu có dạng `@...`, cho Spring hoặc thư viện biết cần xử lý class/method như thế nào. **Bean** là đối tượng được Spring quản lý.

| Annotation | Hiểu đơn giản |
| --- | --- |
| `@SpringBootApplication` | Đặt ở class khởi động; bật cấu hình tự động và tìm các component |
| `@Component` | Đánh dấu class để Spring phát hiện và quản lý |
| `@Service` | Như `@Component`, dành cho class xử lý nghiệp vụ |
| `@Repository` | Đánh dấu class truy cập dữ liệu |
| `@RestController` | Class nhận request API, trả dữ liệu như JSON hoặc chuỗi |
| `@RequestMapping("/products")` | Đặt đường dẫn chung cho controller |
| `@GetMapping` / `@PostMapping` | Nhận request GET / POST; thường dùng để lấy / tạo dữ liệu |
| `@PathVariable("id")` | Lấy `id` từ đường dẫn, ví dụ `/products/10` |
| `@RequestParam("page")` | Lấy `page` từ query, ví dụ `/products?page=1` |
| `@RequestBody` | Đọc JSON trong request body thành đối tượng Java |
| `@Configuration` | Class chứa cấu hình Java |
| `@Bean` | Đặt trên method để đăng ký đối tượng method trả về cho Spring quản lý |

`@RequestMapping("/products")` ở class + `@GetMapping("/{id}")` ở method tạo đường dẫn GET `/products/{id}`. Annotation chỉ nối request tới method; mình vẫn cần viết nội dung xử lý.

Tham khảo: [component](https://docs.spring.io/spring-framework/reference/core/beans/classpath-scanning.html), [API mapping](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-requestmapping.html).

## 3. Dependency Injection (DI) là gì?

`ProductController` cần `ProductService` để lấy sản phẩm. **DI là truyền đối tượng cần dùng từ bên ngoài vào class.** Trong Spring, Spring tìm bean phù hợp rồi truyền vào constructor — hàm khởi tạo của class.

Ví dụ minh họa dưới đây lược bỏ `package` và `import`; mỗi class đặt trong file riêng:

```java
@Service
public class ProductService {
    public String getProduct() {
        return "Keyboard";
    }
}
```

```java
@RestController
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public String getProducts() {
        return productService.getProduct();
    }
}
```

Spring tạo bean `ProductService`, rồi truyền nó khi tạo `ProductController`. Khi gọi GET `/products`, controller gọi service và trả chuỗi `Keyboard`.

**Một constructor thì không cần `@Autowired`.** Annotation này yêu cầu Spring truyền dependency vào; ví dụ trên đã đủ để Spring biết cần truyền gì. Dùng constructor giúp nhìn rõ class cần những đối tượng nào và dễ truyền đối tượng thay thế khi kiểm thử. Xem [Spring DI](https://docs.spring.io/spring-boot/reference/using/spring-beans-and-dependency-injection.html).

## 4. Lombok giúp gì?

Lombok sinh các đoạn mã lặp lại khi biên dịch, giúp mình viết ít hơn. Dự án đã có dependency Lombok trong `pom.xml`.

| Annotation Lombok | Tác dụng |
| --- | --- |
| `@Getter` / `@Setter` | Sinh hàm đọc / gán giá trị field |
| `@NoArgsConstructor` | Sinh constructor không tham số nếu các field cho phép |
| `@AllArgsConstructor` | Sinh constructor nhận các field, trừ `static` và `final` đã khởi tạo |
| `@RequiredArgsConstructor` | Sinh constructor cho field `final` hoặc `@NonNull` chưa khởi tạo |
| `@Data` | Gộp getter, setter cho field không final, `toString`, `equals`, `hashCode` và constructor như `@RequiredArgsConstructor` |
| `@Builder` | Tạo đối tượng theo dạng `.builder().name("Keyboard").build()` khi class có field `name` |

Thay controller viết tay ở trên bằng bản dùng Lombok:

```java
@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/products")
    public String getProducts() {
        return productService.getProduct();
    }
}
```

**Lombok sinh constructor; Spring dùng constructor đó để truyền service vào.** `@RequiredArgsConstructor` không tự tạo bean. Xem [constructor Lombok](https://projectlombok.org/features/constructor) và [Data](https://projectlombok.org/features/Data).

## 5. Những điều cần nhớ

- Tên class có chữ `Service` chưa đủ: class cần được đăng ký, thường bằng `@Service`, và nằm trong phạm vi Spring quét.
- Với `@RequiredArgsConstructor`, giữ dependency là `private final`. Field không có `final` hoặc `@NonNull` sẽ không được đưa vào constructor này.
- Nếu không tìm thấy bean cần truyền vào, ứng dụng thường báo lỗi khi khởi động. Nếu có nhiều bean cùng kiểu, dùng `@Qualifier` để chỉ rõ hoặc `@Primary` để chọn bean ưu tiên.
- Entity và DTO thường là đối tượng dữ liệu, không cần gắn `@Component`. `@Data` cũng không biến class thành Spring bean hay entity JPA.
