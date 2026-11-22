# Hệ thống Quản lý Sân Thể thao & Đặt lịch (HUIT Sports)

Đồ án môn học **Các Hệ Cơ Sở Dữ Liệu NoSQL** – Trường Đại học Công Thương TP.HCM (HUIT).  
Hệ thống áp dụng kiến trúc **Polyglot Persistence** kết hợp đồng thời 4 hệ quản trị CSDL NoSQL (**MongoDB**, **Apache Cassandra**, **Neo4j**, **Redis**) trên nền tảng **Spring Boot (Java 21)**.

---

## 1. Các Cổng Giao diện Chính của Hệ thống

Sau khi khởi động ứng dụng, bạn có thể truy cập 3 phân hệ độc lập:

| Phân hệ | Đường dẫn (URL) | Đối tượng sử dụng | Mô tả chức năng |
| :--- | :--- | :--- | :--- |
| **Cổng Khách hàng** | [http://localhost:8080/](http://localhost:8080/) | Khách hàng đặt sân (`CUSTOMER`) | Tìm kiếm sân, ma trận lịch trống 17 khung giờ, giữ chỗ 5 phút, thanh toán cọc và sàn ghép kèo giao lưu. |
| **Cổng Quản trị** | [http://localhost:8080/admin](http://localhost:8080/admin) | Quản trị viên (`ADMIN`), Nhân viên (`STAFF`), Chủ sân (`OWNER`) | Báo cáo doanh thu & tỷ lệ lấp đầy, quầy trực sân POS (check-in/thu tiền), quản lý bảng giá, lịch bảo trì và khách hàng. |
| **Trang Đăng nhập** | [http://localhost:8080/login](http://localhost:8080/login) | Mọi người dùng | Đăng nhập bằng Google chuẩn OAuth 2.0 (Google Identity Services) với cơ chế tự động phân quyền và chuyển hướng thông minh. |

---

## 2. Yêu cầu môi trường & Công nghệ
- **Ngôn ngữ & Framework:** Java 21, Spring Boot 4.x
- **Cơ sở dữ liệu NoSQL (Docker):**
  - **MongoDB 7.0:** Quản lý Document (Người dùng, Cơ sở, Sân, Bảng giá, Chính sách, Đơn đặt, Đánh giá).
  - **Apache Cassandra 5.0:** Quản lý Trạng thái khung giờ (LWT IF NOT EXISTS), Lịch sử thanh toán, Doanh thu theo ngày, Audit Log.
  - **Neo4j 5.x:** Đồ thị mạng xã hội thể thao (Player, Sport, Facility, Court, Match; quan hệ FRIEND, BOOKED, JOINED).
  - **Redis 7.2:** Quản lý Khóa giữ chỗ tạm 5 phút (Lua Script), Hàng đợi ưu tiên Waitlist (ZSET), Cache và Stream Events.
- **Công cụ:** Docker Desktop, Maven Wrapper (`mvnw` / `mvnw.cmd`).

---

## 3. Hướng dẫn chạy dự án từ A – Z

### Bước 1: Khởi động 4 hệ CSDL NoSQL (Docker Compose)
Mở Docker Desktop, sau đó mở terminal tại thư mục gốc của dự án và chạy:
```powershell
docker compose up -d
```
> **Lưu ý:** Lần đầu khởi động mất khoảng **60 – 90 giây** để Cassandra và Neo4j sẵn sàng. Bộ script khởi tạo (`init-containers`) sẽ tự động tạo bảng, chỉ mục và nạp dữ liệu mẫu ban đầu.

Kiểm tra trạng thái container:
```powershell
docker compose ps
```
Đảm bảo 4 container chính (`sb-mongo`, `sb-cassandra`, `sb-neo4j`, `sb-redis`) đều báo **`healthy`**.

### Bước 2: Chạy ứng dụng Spring Boot
```powershell
.\mvnw.cmd spring-boot:run
```
*(Trên Linux/macOS: `./mvnw spring-boot:run`)*

### Bước 3: Kiểm tra kết nối 4 CSDL
Truy cập: [http://localhost:8080/api/v1/system/health](http://localhost:8080/api/v1/system/health)  
Khi cả 4 hệ CSDL đều báo `"status": "UP"`, hệ thống đã sẵn sàng 100%!

---

## 4. Tài liệu hướng dẫn chi tiết trong thư mục `docs/`

Dự án được tổ chức gọn gàng với các tài liệu hướng dẫn chuyên sâu đặt tại thư mục [`docs/`](file:///D:/NoSQL/DoAn/sportsbooking/docs/):

* 📋 **[Bảng Phân công Nhiệm vụ Chi tiết 3 Thành viên A, B, C (docs/PHAN_CONG_NHIEM_VU_CHI_TIET.md)](file:///D:/NoSQL/DoAn/sportsbooking/docs/PHAN_CONG_NHIEM_VU_CHI_TIET.md)**: Bảng phân công đầy đủ 26 Use Case, hệ CSDL NoSQL sử dụng, file phụ trách và kết quả mong đợi (Definition of Done) cho từng thành viên.
* 📘 **[Hướng dẫn Đăng nhập & Đăng xuất Google (docs/HUONG_DAN_DANG_NHAP_DANG_XUAT.md)](file:///D:/NoSQL/DoAn/sportsbooking/docs/HUONG_DAN_DANG_NHAP_DANG_XUAT.md)**: Hướng dẫn cơ chế Google OAuth 2.0, cấu hình Client ID, đồng bộ dữ liệu vào MongoDB (`users`), Neo4j (`Player`) và phân quyền.
* 🚀 **[Hướng dẫn Triển khai Toàn diện (docs/HUONG_DAN_TRIEN_KHAI.md)](file:///D:/NoSQL/DoAn/sportsbooking/docs/HUONG_DAN_TRIEN_KHAI.md)**: Chi tiết cấu hình kết nối, cổng dịch vụ, xử lý sự cố và kiểm thử 26 Use Case đồ án.
* 🎨 **[Quy chuẩn Thiết kế Giao diện (docs/UI_DESIGN_GUIDE_STITCH.md)](file:///D:/NoSQL/DoAn/sportsbooking/docs/UI_DESIGN_GUIDE_STITCH.md)**: Quy chuẩn thiết kế màu sắc (Emerald `#059669`), typography và bố cục Dashboard.

---

## 5. Cấu trúc Thư mục Dự án

```text
sportsbooking/
├── docs/                               # Thư mục tài liệu & hướng dẫn cho nhóm
│   ├── HUONG_DAN_DANG_NHAP_DANG_XUAT.md # Hướng dẫn chi tiết chức năng Google Auth
│   ├── HUONG_DAN_TRIEN_KHAI.md         # Hướng dẫn triển khai & cấu hình hệ thống
│   ├── UI_DESIGN_GUIDE_STITCH.md       # Quy chuẩn thiết kế giao diện
│   └── images/                         # Hình ảnh minh họa tài liệu
├── sports-booking-db/                  # Toàn bộ script DDL, Cypher, Lua & Docker CSDL
│   ├── cassandra/                      # Script tạo Keyspace, Bảng và Seed Data
│   ├── mongo/                          # Script tạo Collections, Schema Validation, 2dsphere
│   ├── neo4j/                          # Script Cypher tạo Nodes, Constraints, Quan hệ
│   ├── redis/                          # 4 Lua Scripts (hold_slots, release_hold, rate_limit, pop_due)
│   └── docker-compose.yml              # Cấu hình container 4 hệ CSDL
├── src/main/java/com/huit/sportsbooking/
│   ├── common/                         # ApiResponse, AppException, IdGenerator, UserContext
│   ├── config/                         # MongoConfig, CassandraConfig, Neo4jConfig, RedisConfig
│   ├── controller/
│   │   ├── api/                        # AuthApiController, FacilityApiController, SystemHealthController
│   │   └── WebPageController.java      # Điều hướng Web: / (User), /admin (Admin), /login
│   ├── dto/                            # DTO Request / Response các phân hệ
│   ├── model/                          # Enums, MongoDB Documents, Cassandra Tables, Neo4j Nodes
│   ├── repository/                     # Interface Repository cho từng hệ CSDL NoSQL
│   └── service/                        # Triển khai logic nghiệp vụ (AccountService, FacilityService...)
├── src/main/resources/
│   ├── application.yml                 # Cấu hình kết nối 4 CSDL & Google Client ID
│   ├── static/                         # Static assets (JS modules, CSS, ảnh sân thể thao)
│   └── templates/                      # Giao diện Thymeleaf tách biệt:
│       ├── index.html                  # Giao diện Cổng Khách hàng (User Portal - /)
│       ├── admin/                      # Giao diện Cổng Quản trị (Admin Dashboard - /admin)
│       ├── auth/                       # Trang Đăng nhập riêng biệt (/login)
│       ├── customer/                   # Các màn hình thành phần khách hàng
│       └── community/                  # Các màn hình ghép kèo & mạng xã hội
├── .gitignore                          # Cấu hình chuẩn loại trừ các file build/cache khi đẩy Git
├── docker-compose.yml                  # File docker compose root liên kết bộ CSDL
└── pom.xml                             # Quản lý dependency Maven
```

---

## 6. Thông tin Kết nối 4 CSDL NoSQL

| Hệ CSDL | Địa chỉ kết nối | Tài khoản / Keyspace | Ghi chú |
| :--- | :--- | :--- | :--- |
| **MongoDB** | `mongodb://localhost:27018/sports_booking?directConnection=true` | DB: `sports_booking` | Cổng 27018 hoặc 27017 (chế độ Replica Set `rs0`). |
| **Cassandra** | `localhost:9042` | Keyspace: `sports_booking` | Dùng TablePlus, DBeaver hoặc lệnh `cqlsh`. |
| **Neo4j** | Web GUI: [http://localhost:7474](http://localhost:7474)<br>Bolt: `bolt://localhost:7687` | User: `neo4j`<br>Pass: `sbneo4j2026` | Trực quan hóa đồ thị người chơi, quan hệ bạn bè, ghép trận. |
| **Redis** | `localhost:6379` | Không mật khẩu | Quản lý khóa giữ chỗ (hold), hàng đợi waitlist, stream events. |

---

## 7. Hướng dẫn Kiểm tra & Giám sát Dữ liệu trên 4 Hệ CSDL NoSQL

Hệ thống sử dụng đầy đủ 4 công nghệ NoSQL chuyên biệt. Bạn có thể kiểm tra dữ liệu bằng các ứng dụng giao diện (GUI) phổ biến hoặc chạy lệnh dòng lệnh (CLI) trực tiếp qua Docker:

### 7.1. MongoDB (Dữ liệu Document & Giao dịch Đặt sân)

* **Ứng dụng giao diện (MongoDB Compass):**
  1. Mở MongoDB Compass, dán chuỗi kết nối vào ô **New Connection**:
     ```text
     mongodb://localhost:27017/?directConnection=true
     ```
     *(Hoặc `mongodb://localhost:27018/?directConnection=true`)*
  2. Bấm **Connect** $\rightarrow$ chọn database **`sports_booking`**.
  3. Các Collection trọng tâm để kiểm tra:
     * `users`: Danh sách tài khoản người dùng, vai trò (`CUSTOMER`, `STAFF`, `OWNER`, `ADMIN`), trạng thái (`ACTIVE`/`LOCKED`). Tìm tài khoản Google vừa đăng nhập bằng filter: `{ email: "your_email@gmail.com" }`.
     * `facilities`: Danh sách các cụm sân, tọa độ địa lý GeoJSON `Point [kinh độ, vĩ độ]` phục vụ tìm kiếm bán kính `$nearSphere`.
     * `courts`: Các sân con trực thuộc cơ sở thể thao.
     * `pricing_rules`: Quy tắc giá theo loại ngày, khung giờ vàng/thường, thể thao.
     * `bookings`: Các đơn đặt sân hoàn tất với các chặng trạng thái (`CONFIRMED`, `CANCELLED`, `COMPLETED`).
* **Lệnh nhanh qua Docker Terminal:**
  ```powershell
  # Xem danh sách người dùng trong MongoDB
  docker exec -i sb-mongo mongosh sports_booking --quiet --eval "db.users.find({}, {fullName: 1, email: 1, role: 1, status: 1})"

  # Tra cứu chi tiết tài khoản theo email
  docker exec -i sb-mongo mongosh sports_booking --quiet --eval "db.users.findOne({email: 'an@example.com'})"
  ```

---

### 7.2. Neo4j (Mạng Xã hội Thể thao, Ghép trận & Gợi ý Bạn bè)

* **Xem trực tiếp trên Web Browser (Không cần cài thêm phần mềm):**
  1. Mở trình duyệt web truy cập: [http://localhost:7474](http://localhost:7474)
  2. Nhập thông tin:
     * **Connect URL:** `bolt://localhost:7687`
     * **Authentication type:** `Username / Password`
     * **Username:** `neo4j`
     * **Password:** `sbneo4j2026`
  3. Bấm **Connect**.
* **Ứng dụng giao diện (Neo4j Desktop):**
  1. Mở Neo4j Desktop $\rightarrow$ bấm **Add** (hoặc dấu `+`) $\rightarrow$ chọn **Remote connection**.
  2. Điền URL `bolt://localhost:7687`, User `neo4j`, Password `sbneo4j2026` $\rightarrow$ bấm **Connect**.
* **Các câu truy vấn Cypher trọng tâm để kiểm tra:**
  ```cypher
  // 1. Xem tất cả người chơi thể thao (Player) bao gồm tài khoản Google vừa tạo
  MATCH (p:Player) RETURN p LIMIT 25;

  // 2. Trực quan hóa toàn bộ mạng lưới (Cầu thủ, Môn thể thao, Kèo đấu, Đặt sân)
  MATCH (n) RETURN n LIMIT 100;

  // 3. Xem danh sách bạn bè thể thao giữa các người chơi
  MATCH (p1:Player)-[r:FRIEND]-(p2:Player) RETURN p1, r, p2;

  // 4. Gợi ý bạn bè chung (Mutual Friends) cho một người chơi
  MATCH (me:Player {id: '01JU0001'})-[:FRIEND]-(f:Player)-[:FRIEND]-(suggested:Player)
  WHERE NOT (me)-[:FRIEND]-(suggested) AND me <> suggested
  RETURN suggested.name, count(f) AS mutualFriends
  ORDER BY mutualFriends DESC;
  ```
* **Lệnh nhanh qua Docker Terminal:**
  ```powershell
  docker exec -i sb-neo4j cypher-shell -u neo4j -p sbneo4j2026 "MATCH (p:Player) RETURN p.id, p.name;"
  ```

---

### 7.3. Apache Cassandra (Trạng thái Khung giờ LWT, Doanh thu & Audit Log)

* **Ứng dụng giao diện (DBeaver / TablePlus / DataGrip):**
  1. Tạo kết nối mới loại **Apache Cassandra**.
  2. Cấu hình: **Host:** `localhost`, **Port:** `9042`, **Keyspace:** `sports_booking`.
  3. Các bảng (Table) trọng tâm để kiểm tra:
     * `slot_status_by_day`: Bảng lưu 17 khung giờ sân theo ngày, hỗ trợ cơ chế khóa chống đặt trùng (LWT `IF NOT EXISTS`).
     * `audit_by_day_type`: Bảng nhật ký kiểm toán hệ thống. Mỗi khi đăng nhập, đăng xuất, phân quyền hay hủy sân, 1 sự kiện sẽ được ghi nhận tại đây.
     * `booking_history_by_user`: Lịch sử đặt sân theo từng mã khách hàng.
     * `revenue_by_facility_day`: Doanh thu tổng hợp theo ngày của từng cơ sở.
* **Lệnh nhanh qua Docker Terminal:**
  ```powershell
  # Kiểm tra bảng nhật ký kiểm toán đăng nhập / hệ thống
  docker exec -i sb-cassandra cqlsh -e "SELECT day, event_type, user_id, detail, ip FROM sports_booking.audit_by_day_type LIMIT 10;"

  # Xem trạng thái các khung giờ sân trong ngày
  docker exec -i sb-cassandra cqlsh -e "SELECT date, court_id, slot_code, status, booked_by_user_id FROM sports_booking.slot_status_by_day LIMIT 10;"
  ```

---

### 7.4. Redis (Khóa Giữ chỗ Tạm 5 phút, Hàng đợi Waitlist & Cache)

* **Ứng dụng giao diện (Redis Insight):**
  1. Tải và mở công cụ **Redis Insight** (chính thức từ Redis).
  2. Bấm **Add Connection** $\rightarrow$ nhập **Host:** `localhost`, **Port:** `6379` (không cần mật khẩu) $\rightarrow$ bấm **Add Redis Database**.
  3. Khám phá các khóa (Keys) nghiệp vụ thời gian thực:
     * `sb:hold:{courtId}:{date}:{slot}`: Khóa giữ chỗ 300 giây khi khách chọn khung giờ (TTL giảm dần từ 300s về 0).
     * `sb:waitlist:{courtId}:{date}:{slot}`: Danh sách hàng đợi ưu tiên (Sorted Set - ZSET) xếp hàng nhận sân khi có người nhả chỗ.
     * `sb:ratelimit:{ip}`: Đếm số lượng request để ngăn chặn spam/DDOS.
     * `sb:stream:events`: Hàng đợi Redis Stream phân phối sự kiện bất đồng bộ sang Neo4j và Cassandra.
* **Lệnh nhanh qua Docker Terminal:**
  ```powershell
  # Xem danh sách tất cả các key đang có trên Redis
  docker exec -i sb-redis redis-cli KEYS "sb:*"

  # Theo dõi các lệnh Lua Script & thao tác chạy theo thời gian thực (Realtime Monitor)
  docker exec -i sb-redis redis-cli MONITOR
  ```

---

## 8. Tài khoản Mẫu để Kiểm thử

Khi vừa khởi động xong CSDL, bạn có thể dùng ngay các tài khoản mẫu sau:

* **Khách hàng (`CUSTOMER`):** `an@example.com` (hoặc email Google của bạn sau khi đăng nhập).
* **Nhân viên trực sân (`STAFF`):** `staff@example.com` (SĐT `0901000010`).
* **Chủ cơ sở thể thao (`OWNER`):** `owner@example.com` (SĐT `0901000020`).
* **Quản trị viên hệ thống (`ADMIN`):** `admin@example.com` (SĐT `0901000099`).
