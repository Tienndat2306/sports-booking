# HƯỚNG DẪN TRIỂN KHAI HỆ THỐNG
## Quản lý sân thể thao và đặt lịch (Nhóm 9, đa CSDL MongoDB – Cassandra – Neo4j – Redis)

> Tài liệu này dùng để cả nhóm 3 người cùng làm từ bước **nạp dữ liệu** đến khi có **website hoàn chỉnh**.
> Nguồn chuẩn về thiết kế là **Chương 2 và Chương 3 của báo cáo**. Khi tài liệu này và báo cáo khác nhau thì sửa cho khớp nhau, đừng để hai bản lệch.
> Những chỗ ghi **[Đề xuất]** là gợi ý của người soạn, không có trong báo cáo, nhóm có thể đổi sau khi thống nhất.

---

## 0. Việc cần làm ngay (trong 1-2 ngày đầu)

1. Cả 3 người chạy được môi trường CSDL (`docker compose up -d`, xem `README.md` trong `sports-booking-db`). Ai chưa chạy được thì báo ngay.
2. Tạo kho Git chung (GitHub/GitLab), đưa thư mục `sports-booking-db` vào, mời 2 thành viên còn lại.
3. Họp 30-45 phút để **chốt các quyết định ở mục 1** và **phân công ở mục 3**.
4. Người phụ trách lõi đặt sân (Thành viên A) dựng khung dự án backend (mục 4) rồi đẩy lên để hai người còn lại bắt đầu.
5. Mỗi người đọc phần chương 3 liên quan tới module của mình trước khi viết mã.

---

## 1. Các quyết định công nghệ cần nhóm chốt

| # | Vấn đề | Đã có trong báo cáo | Đề xuất |
|---|---|---|---|
| 1 | Ngôn ngữ, framework backend | Java 21, Spring Boot 3, REST + SSE, Spring Data cho 4 hệ, Redis qua Lettuce | Giữ nguyên. Build bằng Maven |
| 2 | Giao diện web | "Giao diện web" dùng REST và SSE, hiển thị tốt trên máy tính và điện thoại (NFR08) | **[Đề xuất]** React + Vite + Tailwind gọi REST, vì có sẵn SSE. Nếu không ai rành JavaScript thì dùng Thymeleaf + htmx chạy chung trong Spring Boot cho nhanh |
| 3 | Xác thực | Mật khẩu băm, token, phân quyền theo vai trò, có `auth:revoked:<jti>` (NFR07) | Spring Security + JWT (thư viện `jjwt` hoặc `spring-security-oauth2-resource-server`), BCrypt cho mật khẩu |
| 4 | Thanh toán | "Đơn vị thanh toán (mô phỏng)" | Viết một dịch vụ giả lập: trang hoặc endpoint trả về thành công hoặc thất bại, có thể chọn cố ý thất bại để demo |
| 5 | Kiểu mã nguồn | Mã tách theo từng dịch vụ dữ liệu, có tài liệu và script triển khai (NFR09) | Một ứng dụng Spring Boot duy nhất (monolith theo module), mỗi module có thư mục riêng, không tách microservice |
| 6 | Kiểm thử tự động | Chưa nêu | JUnit 5 + Testcontainers (chạy 4 CSDL thật). Nếu ngại Testcontainers thì kiểm thử tích hợp chạy trên stack Docker Compose đang bật |
| 7 | Ngày lễ (`dayType = HOLIDAY`) | Báo cáo có giá trị này nhưng chưa có nơi chứa danh sách ngày lễ | Thêm loại `HOLIDAY` vào collection `categories`, hoặc bỏ HOLIDAY. **Chốt trước khi viết nạp bảng giá** |

---

## 2. Kiến trúc và cấu trúc dự án

### 2.1 Kiến trúc (đúng Hình 3.1 của báo cáo)

```
Trình duyệt ──REST/SSE──▶ Ứng dụng Spring Boot ──▶ MongoDB   (hồ sơ, danh mục, sân, giá, chính sách, đơn đặt, bảo trì)
                                  │            ──▶ Cassandra (trạng thái khung giờ – nguồn chuẩn, lịch sử, thanh toán, thống kê, nhật ký)
                                  │            ──▶ Neo4j     (đồ thị người chơi, kèo, sân, môn)
                                  │            ──▶ Redis     (giữ chỗ, cache, danh sách chờ, xếp hạng, hàng đợi, Stream, Pub/Sub)
                                  ▼
                          Worker nền (cùng ứng dụng, chạy @Scheduled hoặc luồng nền):
                          hết giữ chỗ, không đến, đồng bộ sự kiện sang Neo4j / bộ đếm / cache, đối soát
```

### 2.2 Cấu trúc thư mục [Đề xuất]

```
sports-booking/
├── db/                         # chính là thư mục sports-booking-db đã có (docker-compose, script)
├── backend/                    # Spring Boot (Maven)
│   └── src/main/java/vn/huit/sportsbooking/
│       ├── common/             # ULID, lỗi chuẩn, cấu hình bảo mật, thời gian, sự kiện, SSE hub
│       ├── account/            # UC01-UC04
│       ├── catalog/            # UC05-UC08, UC21 (cơ sở, sân, giá, danh mục, tìm kiếm)
│       ├── booking/            # UC09-UC14 (lịch, giữ chỗ, đặt, hủy, chờ, check-in)
│       ├── payment/            # UC15
│       ├── customer/           # UC22, UC23 (quản lý khách, quản lý đơn)
│       ├── maintenance/        # UC24
│       ├── policy/             # UC25
│       ├── community/          # UC16-UC19 (Neo4j)
│       ├── stats/              # UC20 (Cassandra counter, Redis xếp hạng)
│       ├── audit/              # UC26
│       └── worker/             # các tác vụ nền
├── frontend/                   # giao diện web
├── seed/                       # dữ liệu mẫu cố định (JSON hoặc YAML) và script nạp
└── docs/
    ├── api.md                  # hợp đồng API (mục 5)
    ├── test-plan.md            # kế hoạch và kết quả kiểm thử
    └── demo-script.md          # kịch bản demo
```

Bên trong mỗi module, tách theo dịch vụ dữ liệu để đúng NFR09:

```
booking/
├── api/            # Controller, DTO
├── service/        # nghiệp vụ
├── repo/mongo/     # BookingMongoRepository
├── repo/cassandra/ # SlotStateRepository (LWT), HistoryRepository
├── repo/redis/     # HoldRepository (Lua), WaitlistRepository
└── event/          # phát sự kiện vào Stream
```

### 2.3 Quy ước chung (áp dụng cho cả 3 người)

- **Mã định danh**: ULID cho đơn và người dùng; `FAC01`, `FAC01-FB-01` cho cơ sở và sân; mã môn `FOOTBALL5`, `BADMINTON`, `VOLLEYBALL` (đúng bảng quy ước ở mục 3.1).
- **Thời gian**: múi giờ `Asia/Ho_Chi_Minh`. Ngày lưu `yyyy-MM-dd`, giờ khung lưu `HH:mm`, mỗi khung 60 phút, sân hoạt động 05:00-22:00 (17 khung mỗi ngày). Đặt `spring.jackson.time-zone` và JVM `-Duser.timezone=Asia/Ho_Chi_Minh`.
- **Định dạng lỗi** [Đề xuất]: `{ "code": "SLOT_TAKEN", "message": "...", "details": {...} }`, kèm mã HTTP đúng (400, 401, 403, 404, 409, 429).
- **Phân trang**: `?page=0&size=20`.
- **Cấu hình**: `application.yml` có profile `dev` (kết nối `localhost`) và `test`. Không ghi mật khẩu vào mã, dùng biến môi trường.
- **Git**: nhánh `main` luôn chạy được; mỗi chức năng một nhánh `feature/<module>-<việc>`; mở Pull Request, **người khác review** rồi mới gộp; commit theo dạng `feat(booking): thêm giữ chỗ bằng Lua`.
- **Không đổi cấu trúc bảng/collection tùy ý**: muốn đổi phải báo cả nhóm, sửa script trong `db/` và sửa báo cáo chương 3.

---

## 3. Phân công 3 thành viên

Phân theo **lát cắt dọc**: mỗi người chịu trách nhiệm một nhóm use case từ CSDL, backend đến giao diện. Cách này giảm chờ nhau và dễ chấm điểm đóng góp.

| Thành viên | Vai trò | Use case | CSDL chính |
|---|---|---|---|
| **A** (lõi đặt sân) | Người nắm thiết kế CSDL, làm phần khó nhất | UC09, UC10, UC11, UC12, UC13, UC14, UC15 + Worker nền + hạ tầng sự kiện | Cassandra, Redis, MongoDB (bookings) |
| **B** (tài khoản, danh mục, quản trị) | Nền tảng bảo mật và CRUD | UC01-UC07, UC21, UC22, UC23, UC25, UC26 + bảo mật toàn hệ thống | MongoDB, Redis (token, cache), Cassandra (nhật ký) |
| **C** (cộng đồng, thống kê, bảo trì) | Neo4j và dữ liệu phân tích | UC08, UC16, UC17, UC18, UC19, UC20, UC24 + thông báo thời gian thực | Neo4j, Cassandra (thống kê), Redis (xếp hạng, Pub/Sub) |

Gợi ý: người đã thiết kế và hiểu rõ nhất toàn bộ CSDL (người đang soạn báo cáo) nên nhận vai **A**, vì phần đặt sân đồng thời là chỗ rủi ro nhất.

### 3.1 Việc chi tiết của từng người

**Thành viên A – lõi đặt sân**
- Dựng khung `backend`, module `common` (ULID, lỗi chuẩn, cấu hình 4 kết nối, `StreamPublisher`, `SseHub`).
- UC09 Xem lịch trống: đọc `slot_state_by_court_day`, gộp với giữ chỗ trong Redis, cache `sb:cache:sched`.
- UC10 Giữ chỗ: gọi `hold_slots.lua`, ghi `holdses`, `q:holdexp`, tạo đơn `HELD`.
- UC11 Đặt sân (khách tự đặt và nhân viên đặt hộ): chốt khung bằng LWT trong Cassandra, xác nhận đơn.
- UC12 Hủy và đổi lịch theo `policySnapshot`.
- UC13 Danh sách chờ, UC14 Check-in, UC15 Thanh toán (giả lập, có chống lặp `sb:idem`).
- Worker: hết giữ chỗ, không đến (`q:noshow`), đọc Stream cho `cg-cache`, đối soát.

**Thành viên B – tài khoản, danh mục, quản trị**
- UC01-UC02 Đăng ký, đăng nhập, đăng xuất: JWT, BCrypt, `sb:rl:login:<ip>`, `sb:auth:revoked:<jti>`.
- UC03 Hồ sơ cá nhân (đồng bộ tên và trình độ sang Neo4j qua sự kiện, phối hợp với C).
- UC04 Phân quyền, khóa/mở khóa tài khoản (thu hồi token).
- UC05-UC07 Quản lý cơ sở, sân, bảng giá (xóa cache liên quan).
- UC21 Danh mục dùng chung (môn, mặt sân, khung giờ chuẩn, tiện ích, ngày lễ).
- UC22 Quản lý khách hàng (số lần không đến, ghi chú, hạn chế đặt sân).
- UC23 Quản lý đơn đặt (tra cứu, lọc, hủy hộ, đánh dấu không đến, gọi worker của A).
- UC25 Chính sách đặt cọc và hủy (có phiên bản, `cache:policy`).
- UC26 Nhật ký hoạt động: viết một **bộ chặn chung (Interceptor/AOP)** ghi `audit_by_day_type` và `audit_by_user` cho mọi thao tác quan trọng.
- Cấu hình Spring Security, phân quyền theo vai trò, giới hạn tần suất (NFR07).

**Thành viên C – cộng đồng, thống kê, bảo trì**
- UC08 Tìm kiếm và lọc sân (MongoDB `2dsphere`, lọc theo môn, khu vực, giá).
- UC16 Kết bạn, theo dõi; UC17 Tạo và tham gia kèo; UC18 Gợi ý đồng đội và sân (Cypher, cache `cache:rec`).
- UC19 Đánh giá sân (`reviews`, cập nhật `courts.ratingAvg`).
- UC20 Thống kê doanh thu, hiệu suất, xếp hạng sân (`revenue_daily`, `usage_by_hour`, `rank:court`).
- Consumer của Stream cho `cg-neo4j` và `cg-stats` (cập nhật `BOOKED`, `PLAYED_WITH`, bộ đếm, xếp hạng).
- UC24 Bảo trì: khóa khung giờ `MAINTENANCE`, liệt kê đơn bị ảnh hưởng, xử lý và thông báo.
- Thông báo thời gian thực: kênh Pub/Sub `sb:ch:user`, `sb:ch:sched` và điểm cuối SSE (phối hợp với A dùng chung `SseHub`).

### 3.2 Phần dùng chung, ai làm

| Phần | Người chịu trách nhiệm | Người dùng |
|---|---|---|
| Khung dự án, cấu hình 4 kết nối, ULID, lỗi chuẩn | A | Cả nhóm |
| Spring Security, JWT, phân quyền | B | Cả nhóm |
| Bộ chặn ghi nhật ký | B | Cả nhóm |
| Phát sự kiện vào Stream (`StreamPublisher`) | A | A, B, C |
| Tiêu thụ Stream (`cg-neo4j`, `cg-stats`, `cg-cache`) | C (neo4j, stats), A (cache) | |
| `SseHub` (đẩy SSE tới trình duyệt) | A viết bản đầu, C mở rộng thông báo | A, C |
| Khung giao diện (layout, đăng nhập, điều hướng theo vai trò) | B | Cả nhóm |
| Dữ liệu mẫu | mỗi người nạp phần của mình, A điều phối (mục 6) | Cả nhóm |
| Báo cáo chương 4, 5 và slide | chia theo module, A ghép (mục 12) | |

---

## 4. Lộ trình theo giai đoạn

Thời gian bên dưới là **tuần tương đối**, một người làm khoảng 10-15 giờ mỗi tuần. Hãy nén hoặc giãn theo hạn nộp thực tế. Nếu thiếu thời gian, xem **mục 11** để biết thứ tự cắt giảm.

| Giai đoạn | Nội dung | Thời gian | Kết quả phải có |
|---|---|---|---|
| **G0** | Môi trường, Git, họp phân công, chốt quyết định (mục 1) | Tuần 1 (2-3 ngày) | 3 máy chạy được `docker compose`, kho Git, `docs/api.md` bản đầu |
| **G1** | Khung backend và frontend, đăng nhập, dữ liệu mẫu tầng 1 | Tuần 1-2 | Đăng nhập được, gọi được API, thấy danh sách cơ sở và sân từ dữ liệu mẫu |
| **G2** | Các chức năng cơ bản song song (mỗi người một lát cắt) | Tuần 2-4 | Tìm sân, xem lịch, giữ chỗ, đặt sân; CRUD cơ sở, sân, giá; kết bạn |
| **G3** | Chức năng nâng cao và đồng bộ | Tuần 4-5 | Hủy, danh sách chờ, bảo trì, thống kê, gợi ý, worker nền, SSE |
| **G4** | Dữ liệu mẫu tầng 2, ghép giao diện, sửa lỗi | Tuần 5 | Toàn bộ kịch bản demo chạy trơn |
| **G5** | Kiểm thử và đo kết quả (mục 9) | Tuần 5-6 | `docs/test-plan.md` có kết quả thật, ảnh chụp |
| **G6** | Viết báo cáo, làm slide, tập demo | Tuần 6 | Báo cáo đủ 50-60 trang, slide, bản demo dự phòng |

Nguyên tắc: **xong một lát cắt chạy được từ giao diện đến CSDL rồi mới làm tiếp**, không viết hết backend rồi mới làm giao diện.

---

## 5. Hợp đồng API (viết trước khi code)

Mỗi người ghi API của mình vào `docs/api.md` **trước khi cài đặt** để frontend và backend làm song song. Tiền tố chung: `/api/v1`. Dưới đây là danh sách gợi ý [Đề xuất]:

**Tài khoản (B)**
| Phương thức | Đường dẫn | Mô tả | Vai trò |
|---|---|---|---|
| POST | `/auth/register`, `/auth/login`, `/auth/logout` | Đăng ký, đăng nhập, đăng xuất | Tất cả |
| GET, PUT | `/me` | Xem, sửa hồ sơ | Đã đăng nhập |
| GET | `/admin/users` | Danh sách tài khoản | Quản trị |
| PATCH | `/admin/users/{id}/status`, `/admin/users/{id}/role` | Khóa/mở khóa, phân quyền | Quản trị |
| GET, POST, PUT | `/admin/categories` | Danh mục dùng chung | Quản trị |
| GET | `/admin/audit?day&type&userId` | Nhật ký hoạt động | Quản trị |

**Danh mục, cơ sở, sân, giá, chính sách (B, C)**
| Phương thức | Đường dẫn | Mô tả | Vai trò |
|---|---|---|---|
| GET | `/facilities?sport&district&lat&lng&radius&maxPrice` | Tìm và lọc | Tất cả |
| GET | `/facilities/{id}`, `/courts/{id}` | Chi tiết | Tất cả |
| POST, PUT, DELETE | `/owner/facilities`, `/owner/courts`, `/owner/price-rules` | Quản lý cơ sở, sân, giá | Chủ cơ sở |
| GET, PUT | `/owner/facilities/{id}/policy` | Chính sách (có phiên bản) | Chủ cơ sở |

**Đặt sân (A)**
| Phương thức | Đường dẫn | Mô tả | Vai trò |
|---|---|---|---|
| GET | `/courts/{id}/schedule?day=` | Lịch trống theo ngày | Khách, Nhân viên |
| POST | `/holds` | Giữ chỗ (body: sân, ngày, các khung) | Khách |
| DELETE | `/holds/{holdId}` | Hủy giữ chỗ | Khách |
| POST | `/bookings` | Xác nhận đặt từ `holdId` | Khách |
| POST | `/staff/bookings` | Đặt hộ tại quầy | Nhân viên |
| POST | `/bookings/{id}/cancel`, `/bookings/{id}/reschedule` | Hủy, đổi lịch | Khách, Nhân viên |
| POST | `/courts/{id}/waitlist` | Đăng ký danh sách chờ | Khách |
| POST | `/staff/bookings/{id}/check-in` | Check-in | Nhân viên |
| POST | `/staff/bookings/{id}/no-show` | Đánh dấu không đến | Nhân viên, Chủ cơ sở |
| GET | `/me/bookings`, `/staff/bookings?facility&day&status` | Đơn của tôi, tra cứu đơn | Khách, Nhân viên |
| POST | `/payments`, `/payments/callback` | Thanh toán và kết quả giả lập | Khách, Nhân viên |
| GET | `/bookings/{id}/payments` | Lịch sử thanh toán | Khách, Nhân viên |

**Khách hàng, bảo trì (B, C)**
| Phương thức | Đường dẫn | Mô tả | Vai trò |
|---|---|---|---|
| GET | `/staff/customers?q=`, `/staff/customers/{id}` | Tra cứu khách | Nhân viên, Chủ cơ sở |
| POST, PATCH | `/staff/customers/{id}/notes`, `/staff/customers/{id}/restriction` | Ghi chú, hạn chế đặt sân | Nhân viên, Chủ cơ sở |
| POST, PATCH | `/owner/maintenance`, `/owner/maintenance/{id}/complete` | Lập lịch, hoàn tất bảo trì | Chủ cơ sở, Nhân viên |
| GET, POST | `/owner/maintenance/{id}/affected`, `/owner/maintenance/{id}/resolve` | Đơn bị ảnh hưởng và cách xử lý | Chủ cơ sở, Nhân viên |

**Cộng đồng, thống kê, thông báo (C)**
| Phương thức | Đường dẫn | Mô tả | Vai trò |
|---|---|---|---|
| POST, DELETE | `/friends/{playerId}`, `/follows/{playerId}` | Kết bạn, theo dõi | Khách |
| POST, GET | `/matches`, `/matches/{id}/join` | Tạo, tìm, tham gia kèo | Khách |
| GET | `/recommendations/players`, `/recommendations/courts` | Gợi ý | Khách |
| POST, GET | `/reviews`, `/courts/{id}/reviews` | Đánh giá | Khách |
| GET | `/owner/stats/revenue?from&to&groupBy`, `/owner/stats/usage` | Doanh thu, hiệu suất | Chủ cơ sở |
| GET | `/rankings/courts?month=` | Xếp hạng sân | Chủ cơ sở, Khách |
| GET | `/me/notifications` | Thông báo | Đã đăng nhập |
| GET (SSE) | `/sse/schedule?facilityId=`, `/sse/me` | Lịch và thông báo thời gian thực | Đã đăng nhập |

---

## 6. Dữ liệu mẫu

Mục tiêu: có đủ dữ liệu để demo mọi use case, **mã giữa bốn hệ phải khớp nhau** (cùng `facilityId`, `courtId`, `userId`, `bookingId`).

### 6.1 Hai tầng dữ liệu mẫu [Đề xuất]

- **Tầng 1 – danh mục và tham chiếu** (làm trong G1, viết script thủ công):
  - MongoDB: `categories`, `facilities`, `courts`, `price_rules`, `policies`, `users`.
  - Neo4j: nút `Sport`, `Facility`, `Court`, `Player` lấy từ cùng mã với MongoDB.
  - Mục đích: để frontend và backend có dữ liệu ngay từ tuần đầu.
- **Tầng 2 – dữ liệu giao dịch** (làm trong G4): đơn đặt, thanh toán, thống kê, bạn bè, kèo, đánh giá. **Tạo bằng chính service của backend** (một lớp `SeedRunner` chạy khi bật profile `seed`) thay vì chèn tay vào từng CSDL. Cách này giữ bốn hệ nhất quán và tự cập nhật bộ đếm.

> Bộ đếm Cassandra (`revenue_daily`, `usage_by_hour`) **không chạy lại được**: nạp lần hai sẽ cộng đôi. Muốn nạp lại thì phải `docker compose down -v` rồi dựng mới.

### 6.2 Phạm vi dữ liệu đã chốt

| Nội dung | Số lượng |
|---|---|
| Cơ sở | 2 (`FAC01`, `FAC02`) |
| Môn | 3 (`FOOTBALL5`, `BADMINTON`, `VOLLEYBALL`) |
| Sân cơ sở 1 | 3 sân bóng đá 5 người (`FAC01-FB-01..03`), 3 sân cầu lông (`FAC01-BM-01..03`), 1 sân bóng chuyền (`FAC01-VB-01`) |
| Sân cơ sở 2 | 3 sân bóng đá (`FAC02-FB-01..03`) |
| Tài khoản | 1 quản trị, 2 chủ cơ sở (mỗi cơ sở một), 2-3 nhân viên, khoảng 10-15 khách hàng (trong đó 1-2 khách đã có `noShowCount`, 1 khách vãng lai không có số điện thoại) |
| Bảng giá | Mỗi môn có giá giờ cao điểm và giờ thường theo ngày thường và cuối tuần (HOLIDAY nếu nhóm giữ) |
| Chính sách | Mỗi cơ sở 1 bản `current = true`, ví dụ cọc 30%, giữ chỗ 5 phút, ân hạn 15 phút, ngưỡng không đến 3 |
| Đơn đặt | 30-60 đơn trải 30 ngày gần nhất và vài ngày sắp tới, đủ trạng thái `CONFIRMED`, `COMPLETED`, `CANCELED`, `NO_SHOW`, `EXPIRED`, vài `CHECKED_IN` |
| Bảo trì | 1 lịch đang diễn ra, 1 đã hoàn tất, có 1-2 đơn bị ảnh hưởng |
| Cộng đồng | Mỗi khách có vài bạn, vài người theo dõi, 3-5 kèo mở và 1-2 kèo đã đủ người |

### 6.3 Quy tắc để các hệ khớp nhau

1. Dùng một **file danh sách mã cố định** `seed/ids.yml` (ví dụ `userId` của từng khách, `courtId`). Mọi script và `SeedRunner` đọc cùng file này.
2. Mỗi đơn `CONFIRMED` hoặc `CHECKED_IN` trong MongoDB phải có **đủ dòng khung giờ** trong `slot_state_by_court_day`.
3. Mỗi đơn có `booking_history_by_user`, `booking_events_by_booking`, `payments_by_booking` và `payments_by_facility_day` tương ứng.
4. Mỗi `Court` ở MongoDB có nút `Court` cùng `id` ở Neo4j; mỗi người dùng vai trò khách có nút `Player`.
5. Sau khi nạp, chạy **đối soát**: số đơn xác nhận ở MongoDB phải bằng số đơn có khung giờ ở Cassandra, và `revenue_daily` phải bằng tổng thanh toán đã ghi.

### 6.4 Ai làm gì

- B: tầng 1 MongoDB (`categories`, `facilities`, `courts`, `price_rules`, `policies`, `users`).
- C: tầng 1 Neo4j và phần cộng đồng, đánh giá, thống kê của tầng 2.
- A: tầng 2 đơn đặt, thanh toán, bảo trì; viết `SeedRunner`, `ids.yml` và script đối soát.

---

## 7. Cách cài đặt các luồng khó

> Dưới đây là cách cài đặt gợi ý bám theo thiết kế chương 3. Đối chiếu với bảng thuộc tính và khóa Redis trong báo cáo khi viết mã.

### 7.1 Xem lịch trống (UC09, NFR02)

1. Đọc `sb:cache:sched:<fac>:<day>`. Nếu có, trả luôn.
2. Nếu không, đọc `slot_state_by_court_day` của mỗi sân (một phân vùng, tối đa 17 dòng).
3. Gộp với các khóa `sb:hold:{court:day}:HH:mm` đang tồn tại (khung đang được giữ).
4. Ghi cache (TTL 60 s) rồi trả về. Mỗi khung có trạng thái `FREE`, `HELD`, `BOOKED`, `MAINTENANCE`.

### 7.2 Giữ chỗ (UC10, NFR03)

1. Kiểm tra giới hạn tần suất `sb:rl:hold:<userId>`.
2. Kiểm tra các khung chưa bị chiếm trong Cassandra.
3. Gọi script `hold_slots.lua`: được tất cả hoặc không được khung nào, TTL = thời gian giữ chỗ của chính sách.
4. Ghi `sb:holdses:<holdId>`, thêm vào `sb:q:holdexp`, tạo đơn `HELD` trong MongoDB (có `holdId`, `holdExpiresAt`).
5. Đẩy thông điệp lên `sb:ch:sched:<fac>` để người đang xem lịch thấy khung chuyển sang `HELD`.

> Lưu ý: TTL của `sb:holdses` phải **bằng hoặc lớn hơn** TTL của khóa hold (`holdMinutes × 60` cộng thêm khoảng đệm). Đừng để cố định 360 s.

### 7.3 Đặt sân và thanh toán (UC11, UC15, NFR01)

1. Khách (hoặc nhân viên đặt hộ) gửi `holdId` và thông tin thanh toán. Kiểm tra phiên giữ chỗ còn hiệu lực.
2. Gọi dịch vụ thanh toán giả lập. Dùng khóa chống lặp `sb:idem:<scope>:<key>` để thanh toán không bị ghi hai lần khi bấm nhiều lần.
3. **Chốt khung giờ trong Cassandra bằng LWT** (bước quyết định NFR01). Nếu đơn có nhiều khung, đưa tất cả vào một `BATCH` có điều kiện `IF NOT EXISTS` (các dòng phải cùng một phân vùng), kiểm tra `wasApplied()`.
4. Nếu LWT **thành công**: cập nhật đơn MongoDB sang `CONFIRMED`, ghi `booking_history_by_user`, `booking_events_by_booking`, `payments_*`.
5. Xóa khóa hold và `holdses` (script `release_hold.lua`), xóa cache lịch.
6. Phát sự kiện `BookingConfirmed` vào Stream `sb:stream:events` để các worker cập nhật Neo4j (`BOOKED`, `PLAYED_WITH`), bộ đếm (`revenue_daily`, `usage_by_hour`), xếp hạng và cache. Đẩy thông báo tới khách.
7. Nếu LWT **thất bại** (có người chốt trước): giải phóng hold, đánh dấu đơn thất bại, hoàn tiền giả lập và thông báo cho khách.

**Kịch bản lỗi cần xử lý** (đưa vào kiểm thử ở mục 9):
- Thanh toán thành công nhưng LWT thất bại: hoàn tiền.
- LWT thành công nhưng ghi MongoDB lỗi: thử lại; nếu vẫn lỗi thì xóa có điều kiện khung giờ và hoàn tiền; ghi nhật ký để đối soát.
- Redis ngừng: vẫn xem được lịch từ Cassandra (NFR05), đặt sân vẫn đúng nhờ LWT, chỉ mất giữ chỗ tạm.

### 7.4 Hủy và đổi lịch (UC12)

1. Đọc `bookings.policySnapshot` (bản chụp chính sách lúc đặt, không dùng chính sách hiện hành).
2. Tính phần hoàn theo `cancelTiers` và số giờ còn lại trước giờ chơi.
3. Xóa có điều kiện: `DELETE ... IF booking_id = ?` trên `slot_state_by_court_day`.
4. Cập nhật đơn `CANCELED` (kèm `cancel`), ghi `payments_by_booking` loại hoàn tiền, ghi sự kiện, giảm bộ đếm.
5. Phát `BookingCanceled` và `SlotReleased`; worker lấy người đầu `sb:wait:{court:day}:HH:mm` và thông báo.
6. Đổi lịch: tạo đơn mới (`rescheduledFrom`), hủy đơn cũ với lý do `RESCHEDULE`, **không hoàn tiền theo bậc thang**.

### 7.5 Hết giữ chỗ và không đến (worker)

- **Hết giữ chỗ**: định kỳ (mỗi vài giây) gọi `pop_due.lua` trên `sb:q:holdexp`; với mỗi `holdId` đến hạn, chuyển đơn MongoDB `HELD` sang `EXPIRED`, thông báo.
- **Không đến**: gọi `pop_due.lua` trên `sb:q:noshow` (điểm là giờ bắt đầu cộng thời gian ân hạn). Nếu đơn chưa check-in, dùng **transaction MongoDB** để chuyển đơn sang `NO_SHOW` và tăng `users.noShowCount`; nếu vượt ngưỡng thì bật `bookingRestriction`.
- Bật quét dự phòng bằng chỉ mục `holdExpiresAt` (phần `partial` đã tạo) phòng khi Redis mất khóa.

### 7.6 Bảo trì (UC24, NFR11)

1. Chủ cơ sở lập lịch bảo trì cho một hoặc nhiều khung của sân.
2. Tìm các đơn bị ảnh hưởng (`bookings` theo `courtId`, `bookingDate`, `status`) và ghi vào `maintenance.affectedBookings`.
3. Khóa khung giờ trong Cassandra với `status = MAINTENANCE` (chỉ khóa các khung còn trống bằng LWT).
4. Xử lý từng đơn bị ảnh hưởng: đổi sân, đổi giờ hoặc hủy hoàn cọc; thông báo khách.
5. Xóa cache lịch; phát `MaintenanceLocked`.
6. Hoàn tất bảo trì: mở khóa khung giờ, cập nhật `completedAt`.

### 7.7 Đồng bộ sự kiện (NFR06)

- Mỗi sự kiện có `eventId` (ULID). Trước khi xử lý, ghi khóa `sb:idem:evt:<eventId>` (SET NX). Có rồi thì bỏ qua, để thử lại không bị cộng đôi.
- Ba nhóm tiêu thụ độc lập: `cg-neo4j` (cập nhật đồ thị), `cg-stats` (bộ đếm Cassandra, xếp hạng), `cg-cache` (xóa cache). Đọc bằng `XREADGROUP`, xử lý xong thì `XACK`; phần tử treo thì `XAUTOCLAIM`.
- Chấp nhận độ trễ ngắn giữa các hệ (đúng NFR06). Cassandra luôn là nguồn chuẩn cho trạng thái khung giờ.

### 7.8 Gợi ý đồng đội và sân (UC18)

- Đồng đội: bạn của bạn cùng môn, ưu tiên `PLAYED_WITH` nhiều lần và trình độ gần nhau. Cache `sb:cache:rec:<userId>:<sport>` 5 phút.
- Sân: sân mà bạn bè hay đặt (`BOOKED.count`) mà người dùng chưa đặt. Khi Neo4j lỗi, dùng `sb:rank:court` làm phương án dự phòng.

---

## 8. Giao diện web

### 8.1 Màn hình theo vai trò

| Vai trò | Màn hình chính |
|---|---|
| **Khách hàng** | Trang chủ và tìm sân (lọc môn, khu vực, giá, gần tôi); chi tiết sân (ảnh, tiện ích, đánh giá, lịch theo ngày); chọn khung giờ, giữ chỗ (có đếm ngược), thanh toán; đơn của tôi (hủy, đổi lịch, đánh giá); danh sách chờ; cộng đồng (bạn bè, kèo, gợi ý); hồ sơ; thông báo |
| **Nhân viên** | Lịch quầy theo ngày và sân; đặt hộ; check-in; tra cứu đơn; tra cứu khách; bảo trì |
| **Chủ cơ sở** | Quản lý cơ sở, sân, bảng giá, chính sách; bảo trì; quản lý khách và đơn; thống kê (doanh thu, hiệu suất, giờ cao điểm, xếp hạng sân) |
| **Quản trị** | Tài khoản và phân quyền; danh mục dùng chung; nhật ký hoạt động |

### 8.2 Yêu cầu giao diện

- Dùng được trên máy tính và điện thoại (NFR08); đặt sân trong vài bước: chọn sân, chọn khung, giữ chỗ, thanh toán.
- Lịch cập nhật **thời gian thực** qua SSE `/sse/schedule`: khi người khác giữ hoặc đặt khung, màu khung đổi ngay.
- Đếm ngược thời gian giữ chỗ; hết giờ thì tự quay lại bước chọn khung.
- Thông báo (đặt thành công, bảo trì ảnh hưởng đơn, có chỗ trống từ danh sách chờ) qua SSE `/sse/me`.
- Mọi lỗi từ API (`SLOT_TAKEN`, `HOLD_EXPIRED`, `RATE_LIMITED`) phải hiển thị thông báo dễ hiểu.
- Ẩn hoặc chặn các nút không thuộc quyền của vai trò, nhưng **kiểm tra quyền ở backend** là bắt buộc.

---

## 9. Kiểm thử và đo kết quả

Ghi kết quả vào `docs/test-plan.md` kèm ảnh chụp, đây là nguồn cho báo cáo chương 4 và 5.

| # | Kiểm thử | Cách làm | Kết quả mong đợi | Yêu cầu |
|---|---|---|---|---|
| 1 | Đặt trùng khung đồng thời | Chạy 50 luồng cùng đặt một sân và khung | Đúng **1** đơn thành công, 49 nhận `SLOT_TAKEN` | NFR01 |
| 2 | Giữ chỗ nhiều khung | 2 người cùng giữ 2 khung chồng nhau | Chỉ một người giữ được; khung còn lại không bị giữ dở | NFR01, NFR03 |
| 3 | Hết giữ chỗ | Giữ chỗ rồi chờ hết hạn | Khung trống lại, đơn `EXPIRED`, SSE cập nhật | NFR03 |
| 4 | Hủy theo chính sách | Hủy ở các mốc trước giờ chơi khác nhau | Tỷ lệ hoàn đúng với `policySnapshot` | FR13, NFR12 |
| 5 | Đổi chính sách | Đổi chính sách cơ sở rồi hủy đơn cũ | Đơn cũ vẫn theo chính sách lúc đặt | NFR12 |
| 6 | Không đến | Đơn quá giờ ân hạn, chưa check-in | `NO_SHOW`, `noShowCount` tăng, vượt ngưỡng thì bị hạn chế | FR25, FR26 |
| 7 | Danh sách chờ | Hủy một đơn khi có người đang chờ | Người đầu danh sách nhận thông báo | FR14 |
| 8 | Bảo trì | Khóa khung có đơn | Khung không đặt được, đơn bị ảnh hưởng được liệt kê và thông báo | NFR11 |
| 9 | Một CSDL ngừng | `docker compose stop neo4j`, rồi thử xem lịch và tìm sân | Xem lịch và tìm sân vẫn chạy; chức năng gợi ý báo lỗi nhẹ | NFR05 |
| 10 | Redis ngừng | `docker compose stop redis` | Xem lịch từ Cassandra vẫn được; đặt sân vẫn không trùng | NFR05, NFR01 |
| 11 | Chống lặp sự kiện | Phát lại cùng một sự kiện | Bộ đếm và quan hệ không bị cộng đôi | NFR06 |
| 12 | Đồng bộ | Đặt một đơn rồi chờ vài giây | Neo4j có `BOOKED`, bộ đếm tăng, xếp hạng đổi | NFR06 |
| 13 | Hiệu năng xem lịch | Gọi 100 lần `/schedule` (có và không có cache) | Đa số dưới 2 giây (ghi trung bình và p95) | NFR02 |
| 14 | Phân quyền | Khách gọi API của chủ cơ sở | `403` | NFR07 |
| 15 | Giới hạn tần suất | Giữ chỗ liên tục nhiều lần | `429` | NFR07 |
| 16 | Đối soát dữ liệu | Chạy script đối soát sau khi nạp và sau test | Các hệ khớp nhau | NFR10 |

---

## 10. Kịch bản demo (mỗi bước nên có ảnh chụp dự phòng)

1. Khách đăng nhập, tìm sân bóng đá 5 người gần đây.
2. Xem lịch sân `FAC01-FB-01`; mở **hai cửa sổ**, một người giữ chỗ khung 18:00, cửa sổ kia thấy khung đổi màu ngay.
3. Hai người **cùng đặt một khung**: một người thành công, người kia nhận thông báo đã có người đặt (chứng minh LWT).
4. Thanh toán giả lập, đơn chuyển `CONFIRMED`; xem lịch sử đơn và thanh toán.
5. Hủy đơn, xem số tiền hoàn theo chính sách; người trong danh sách chờ nhận thông báo.
6. Nhân viên đặt hộ tại quầy rồi check-in; một đơn quá giờ ân hạn tự chuyển `NO_SHOW`.
7. Khách kết bạn, tạo kèo, nhận gợi ý đồng đội; xem sân bạn bè hay đặt.
8. Chủ cơ sở lập lịch bảo trì, xử lý đơn bị ảnh hưởng.
9. Chủ cơ sở xem thống kê doanh thu, giờ cao điểm, xếp hạng sân.
10. Quản trị xem nhật ký hoạt động, khóa một tài khoản.
11. (Tùy chọn) Dừng Neo4j, chứng minh xem lịch và đặt sân vẫn chạy.

---

## 11. Thứ tự ưu tiên nếu thiếu thời gian

Làm theo thứ tự, mức nào cũng phải chạy được từ giao diện đến CSDL.

| Mức | Use case | Ghi chú |
|---|---|---|
| **Bắt buộc** | UC01, UC02, UC05, UC06, UC07, UC08, UC09, UC10, UC11, UC12, UC15 | Đủ để chứng minh luồng đặt sân và đặt trùng |
| **Nên có** | UC14, UC13, UC17, UC16, UC18, UC20, UC23, UC25 | Thể hiện được cả 4 CSDL |
| **Có thể rút gọn** | UC03, UC04, UC19, UC21, UC22, UC24, UC26 | Làm giao diện đơn giản, hoặc chỉ qua API |

Nếu cắt use case nào thì **phải sửa lại chương 2 và bảng ánh xạ ở mục 3.3 của báo cáo** cho khớp, đừng để báo cáo nói có mà ứng dụng không có.

---

## 12. Báo cáo, slide và bàn giao

### 12.1 Việc sửa chương 3 (đã bàn, làm khi có dịp)
- Rút gọn hoặc bổ sung phần truy vấn (3.1, 3.3), bỏ hoặc viết mục 3.8.
- Sửa các điểm: TTL `sb:holdses`, chỉ mục `phone` và `email` thành partial unique, giá trị HOLIDAY, trạng thái đổi lịch, giải thích LWT, tính chống lặp của bộ đếm.

### 12.2 Chương 4 – Cài đặt và kết quả [Đề xuất]
1. Môi trường và công nghệ (Docker Compose, phiên bản từng CSDL).
2. Cấu trúc dự án và kiến trúc cài đặt.
3. Dữ liệu mẫu và cách nạp.
4. Cài đặt các luồng chính (mục 7), có sơ đồ tuần tự.
5. Giao diện (ảnh chụp từng vai trò).
6. Truy vấn tiêu biểu trên từng hệ và kết quả chạy.

### 12.3 Chương 5 – Kiểm thử, đánh giá, kết luận
- Bảng kết quả ở mục 9 (có số liệu thật), ưu và nhược điểm của mô hình đa CSDL, khó khăn gặp phải, hướng phát triển (mở rộng nhiều nút, thanh toán thật, ứng dụng di động, gợi ý bằng học máy).

### 12.4 Phụ lục
- Script khởi tạo (`db/`), danh sách API, mẫu dữ liệu, hướng dẫn chạy dự án.

### 12.5 Bàn giao cuối
- Kho Git sạch, `README.md` chạy được trong 3 lệnh (`docker compose up -d`, chạy backend, chạy frontend).
- Slide, bản chụp màn hình dự phòng, video quay màn hình demo nếu có thể.
- Mỗi người ghi **phần mình làm** vào báo cáo để đối chiếu với commit.

---

## 13. Rủi ro thường gặp

| Rủi ro | Cách giảm |
|---|---|
| Ba người làm lệch cấu trúc dữ liệu | Mọi thay đổi cấu trúc phải qua nhóm, sửa script trong `db/` và báo cáo |
| Khó ghép vì mỗi người làm một kiểu lỗi và định dạng | Chốt định dạng lỗi, phân trang và `docs/api.md` ngay từ G0 |
| Dữ liệu các hệ không khớp | Dùng `ids.yml` chung, nạp tầng 2 bằng service, chạy script đối soát |
| Docker nặng, máy yếu | Cassandra giới hạn heap 512 MB (đã cấu hình); đóng ứng dụng khác; một người chạy stack, người khác kết nối qua mạng nội bộ nếu cần |
| Làm giao diện quá muộn | Mỗi người làm giao diện **cùng lúc** với backend của mình; chốt khung giao diện ở G1 |
| Bảo vệ lỗi vì Docker hỏng | Có ảnh chụp và video demo dự phòng; thử chạy lại trên máy bảo vệ trước một ngày |
| Quá phạm vi | Bám mục 11, bỏ use case sớm thay vì làm dở nhiều thứ |

---

## 14. Danh sách kiểm tra bàn giao theo thành viên

**Thành viên A**
- [ ] Khung dự án và 4 kết nối chạy được
- [ ] Giữ chỗ bằng Lua, đặt sân bằng LWT, test 50 luồng cùng đặt
- [ ] Hủy, đổi lịch, danh sách chờ, check-in, thanh toán giả lập
- [ ] Worker hết giữ chỗ, không đến, đồng bộ Stream
- [ ] `SeedRunner`, `ids.yml`, script đối soát

**Thành viên B**
- [ ] Đăng ký, đăng nhập, JWT, thu hồi token, giới hạn đăng nhập
- [ ] Phân quyền cho mọi API, khung giao diện theo vai trò
- [ ] CRUD cơ sở, sân, giá, chính sách (có phiên bản), danh mục
- [ ] Quản lý khách và đơn, hạn chế đặt sân khi vi phạm
- [ ] Bộ chặn ghi nhật ký, màn hình nhật ký
- [ ] Dữ liệu mẫu tầng 1 MongoDB

**Thành viên C**
- [ ] Tìm kiếm, lọc sân (2dsphere), cache
- [ ] Kết bạn, kèo, gợi ý bằng Cypher, đánh giá sân
- [ ] Consumer `cg-neo4j`, `cg-stats`; bộ đếm, xếp hạng, màn hình thống kê
- [ ] Bảo trì (khóa khung, đơn bị ảnh hưởng, thông báo)
- [ ] SSE thông báo và lịch thời gian thực
- [ ] Dữ liệu mẫu Neo4j, cộng đồng, đánh giá, thống kê
