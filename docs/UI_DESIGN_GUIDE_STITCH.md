# Quy chuẩn Thiết kế Giao diện (UI/UX Design System & Guidelines)
**Hệ thống Quản lý Sân Thể thao & Đặt lịch (HUIT Sports)**  
**Áp dụng cho:** Tất cả thành viên trong nhóm phát triển Frontend & Giao diện đồ án.

---

## 1. Nguyên tắc Bố cục Cốt lõi của Hệ thống (Layout Architecture)

Toàn bộ hệ thống giao diện hiện tại được xây dựng nhất quán theo **mô hình Single Page Layout kết hợp Sidebar trái cố định (260px)** trên nền tảng **Tailwind CSS (CDN)** và **Lucide Icons**:

```text
+----------------------------------------------------------------------------------+
|  SIDEBAR TRÁI (260px)  |  KHU VỰC NỘI DUNG CHÍNH (app-main)                      |
|  (app-sidebar sticky)  |                                                         |
|                        |  1. Top Header (Tiêu đề trang + Bộ lọc cơ sở / môn)     |
|  - Logo Cúp Xanh lá    |  2. Hàng 4 Thẻ KPI Thông số (Cards)                     |
|  - Danh mục Menu Tab   |  -----------------------------------------------------  |
|  - Chọn cơ sở hoạt động|  3. Khung màn hình hiển thị chính (.scr.show):          |
|  - Thẻ người dùng      |     - Trang chủ tìm kiếm & Khám phá sân                 |
|  - Nút chuyển Cổng     |     - Lưới đặt 17 khung giờ & Giữ chỗ 5 phút (Redis)   |
|                        |     - Thanh toán cọc & Đồng hồ đếm ngược                |
|                        |     - Sàn ghép kèo giao lưu & Đồ thị bạn bè (Neo4j)     |
|                        |     - Quầy POS / Bảng giá / Bảo trì / Thống kê          |
+----------------------------------------------------------------------------------+
```

### Hai Cổng Giao diện Riêng biệt:
1. **Cổng Khách hàng (User Portal - `http://localhost:8080/`):**
   * File template chính: `src/main/resources/templates/index.html`.
   * Đối tượng: Người chơi thể thao (`CUSTOMER`).
   * Mục tiêu: Tìm kiếm sân nhanh, chọn khung giờ trống trực quan, thanh toán cọc và ghép kèo thể thao.
2. **Cổng Quản trị (Admin Portal - `http://localhost:8080/admin`):**
   * File template chính: `src/main/resources/templates/admin/index.html`.
   * Đối tượng: Quản trị viên (`ADMIN`), Nhân viên quầy trực sân (`STAFF`), Chủ sân (`OWNER`).
   * Mục tiêu: Quầy POS check-in/thu tiền, thiết lập bảng giá, lịch bảo trì sân, báo cáo doanh thu & tỷ lệ lấp đầy.
3. **Trang Đăng nhập Tập trung (`http://localhost:8080/login`):**
   * File template: `src/main/resources/templates/auth/login.html`.
   * Bố cục 2 cột hiện đại: Cột trái xác thực Google OAuth 2.0 (Google Identity Services) / Email; cột phải hiển thị hình ảnh sân bóng đá mini và các ưu điểm nổi bật.

---

## 2. Bảng Màu & Typography (Design Tokens)

### 2.1. Font chữ Chuẩn
* **Font chính (Sans-serif):** `'Plus Jakarta Sans', system-ui, sans-serif` (import từ Google Fonts với các độ đậm 400, 500, 600, 700, 800).
* **Font tiêu đề Trang Đăng nhập (Serif):** `'Newsreader', Georgia, serif` (tạo cảm giác trang nhã, tin cậy).

### 2.2. Bảng Màu Thương Hiệu (Brand Colors)
* **Màu chủ đạo (Primary Emerald):**
  * Nền chính / Nút bấm / Điểm nhấn: `bg-emerald-600` (`#059669`)
  * Trạng thái Hover: `hover:bg-emerald-700` (`#047857`)
  * Nền nhạt / Highlight: `bg-emerald-50` (`#ecfdf5`), `bg-emerald-100` (`#d1fae5`)
  * Chữ thương hiệu: `text-emerald-700` (`#047857`)
* **Nền & Viền (Neutral Slate):**
  * Nền toàn trang: `bg-[#f8fafc]` (Slate-50)
  * Nền thẻ Card / Bảng: `#ffffff` (White)
  * Đường viền phân cách: `border-slate-200` (`#e2e8f0`)
  * Chữ tiêu đề: `text-slate-900` (`#0f172a`), Chữ nội dung: `text-slate-700` (`#334155`)
  * Chữ phụ / Gợi ý: `text-slate-400` (`#94a3b8`), `text-slate-500` (`#64748b`)

### 2.3. Quy chuẩn 5 Màu Trạng Thái Khung Giờ Sân (Time-Slot States)
Đây là quy chuẩn nhận diện quan trọng nhất của đồ án (liên quan trực tiếp đến cơ chế Redis Hold và Cassandra LWT):

| Trạng thái | Tên mã | Màu nền | Viền (Border) | Màu chữ | Tương tác & Nghiệp vụ |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Khung giờ Trống** | `AVAILABLE` | Nền trắng (`#ffffff`) | `border-slate-200` | `text-slate-600` | Click được để chọn đặt sân. Hover chuyển viền xanh Emerald. |
| **Đang được chọn** | `SELECTED` | Xanh ngọc đậm (`#059669`) | Viền `#059669` | Trắng (`#ffffff`) | Chữ in đậm, hiển thị tổng tiền ở thanh tóm tắt. |
| **Đang giữ chỗ 5p** | `HELD` | Vàng cam nhạt (`#fef3c7`) | `border-amber-500` | `text-amber-800` | Khóa bởi Redis Hold Key (TTL đếm ngược 300s). Khách khác không thể bấm chọn. |
| **Đã đặt hoàn tất** | `BOOKED` | Đỏ hồng nhạt (`#fee2e2`) | `border-rose-400` | `text-rose-800` | Khóa cứng bởi Cassandra LWT. Cho phép rê chuột bấm "Đăng ký chờ (Waitlist)". |
| **Đang bảo trì** | `MAINTENANCE`| Xám nhạt (`#e2e8f0`) | `border-slate-200` | `text-slate-500` | Chữ gạch ngang (`line-through`), khóa click (`cursor: not-allowed`). |

---

## 3. Bộ CSS Class Chuẩn Dùng Chung (Component Utility Classes)

Khi thiết kế thêm màn hình hoặc chỉnh sửa component, **bắt buộc sử dụng các CSS class đã được định nghĩa sẵn** trong hệ thống để đảm bảo sự đồng nhất:

```css
/* 1. Khung Thẻ Chứa Nội Dung (Card) */
.card {
  background-color: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 1rem;            /* bo góc rounded-2xl */
  padding: 1.25rem;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

/* 2. Nút Bấm Chính (Primary Button) */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  border-radius: 0.75rem;          /* rounded-xl */
  background-color: #059669;       /* Emerald-600 */
  padding: 0.625rem 1rem;
  font-size: 0.875rem;             /* 14px */
  font-weight: 600;
  color: #ffffff;
  transition: all 0.2s;
  cursor: pointer;
  border: none;
  box-shadow: 0 2px 4px 0 rgba(5, 150, 105, 0.2);
}
.btn:hover { background-color: #047857; }

/* 3. Nút Bấm Phụ (Secondary / Outline Button) */
.btn2 {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  border-radius: 0.75rem;
  border: 1px solid #e2e8f0;
  background-color: #ffffff;
  padding: 0.625rem 1rem;
  font-size: 0.875rem;
  font-weight: 600;
  color: #334155;
  transition: all 0.2s;
  cursor: pointer;
}
.btn2:hover { border-color: #059669; color: #059669; }

/* 4. Thẻ Lọc Tròn / Pill Filter (Chip) */
.chip {
  display: inline-flex;
  align-items: center;
  border-radius: 9999px;
  border: 1px solid #e2e8f0;
  background-color: #ffffff;
  padding: 0.375rem 0.875rem;
  font-size: 0.75rem;              /* 12px */
  font-weight: 500;
  color: #475569;
  transition: all 0.2s;
  cursor: pointer;
}
.chip:hover { border-color: #059669; }
.chip.on {
  background-color: #ecfdf5;
  border-color: #059669;
  color: #059669;
  font-weight: 600;
}

/* 5. Ô Nhập Liệu Chuẩn (Input / Select) */
.in {
  width: 100%;
  border-radius: 0.75rem;
  border: 1px solid #e2e8f0;
  background-color: #ffffff;
  padding: 0.5rem 0.75rem;
  font-size: 0.875rem;
  outline: none;
  transition: border-color 0.2s;
}
.in:focus { border-color: #059669; }

/* 6. Ô Khung Giờ Sân (Time-Slot Unit) */
.slot {
  height: 2.75rem;
  min-width: 60px;
  border-radius: 0.5rem;
  border: 1px solid #e2e8f0;
  background-color: #ffffff;
  font-size: 0.75rem;
  color: #475569;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}
.slot:hover { border-color: #059669; }
.slot.sel { background-color: #059669; border-color: #059669; color: #ffffff; font-weight: 600; }
.slot.held { background-color: #fef3c7; border-color: #f59e0b; color: #92400e; }
.slot.booked { background-color: #fee2e2; border-color: #f87171; color: #991b1b; }
.slot.mt { background-color: #e2e8f0; color: #64748b; text-decoration: line-through; cursor: not-allowed; }

/* 7. Cơ chế Chuyển Màn Hình (SPA Screen) */
.scr { display: none; }
.scr.show { display: block; animation: fadeIn 0.15s ease-in-out; }
```

---

## 4. Chi tiết Bố cục Từng Phân Hệ Giao diện Hiện Tại

### 4.1. Cổng Khách Hàng (User Portal - `index.html`)

Trang chủ người dùng hoạt động mượt mà với Sidebar cố định 260px và 5 phân đoạn màn hình con (gắn qua Thymeleaf Fragments):

```html
<!-- Cấu trúc trong templates/index.html -->
<main class="flex-1 px-6 lg:px-8 pb-10 pt-1">
  <!-- 1. Tìm kiếm sân & Bảng xếp hạng Top sân Hot (customer/home-search.html) -->
  <section th:replace="~{customer/home-search :: homeSearchSection}"></section>

  <!-- 2. Ma trận 17 khung giờ & Giữ chỗ 5 phút (customer/facility-detail-schedule.html) -->
  <section th:replace="~{customer/facility-detail-schedule :: facilityScheduleSection}"></section>

  <!-- 3. Thanh toán cọc & Đếm ngược Redis TTL (customer/checkout-payment.html) -->
  <section th:replace="~{customer/checkout-payment :: checkoutPaymentSection}"></section>

  <!-- 4. Lịch sử đặt sân, Hủy/Đổi lịch (customer/my-bookings.html) -->
  <section th:replace="~{customer/my-bookings :: myBookingsSection}"></section>

  <!-- 5. Sàn ghép kèo giao lưu thể thao Neo4j (community/matchmaking-hub.html) -->
  <section th:replace="~{community/matchmaking-hub :: matchmakingSection}"></section>
</main>
```

#### Menu Điều hướng Sidebar Khách hàng:
1. `overview`: **Tổng quan & Tìm kiếm** (Icon `search`)
2. `schedule`: **Lịch sân & Giữ chỗ** (Icon `calendar`)
3. `matchmaking`: **Sàn ghép kèo** (Icon `users-round`)
4. `bookings`: **Đơn đặt của tôi** (Icon `ticket`)
5. `checkout`: **Thanh toán giữ chỗ** (Icon `credit-card`)

#### Hàng 4 Thẻ KPI Khách hàng:
* **Thẻ 1 (Sân đang phục vụ):** `10 sân` (Tại 2 cơ sở Tân Phú & Tân Bình).
* **Thẻ 2 (Điểm đánh giá sân):** `4.78 / 5.0` (Icon sao vàng, hơn 1.200 lượt đánh giá).
* **Thẻ 3 (Kèo giao lưu mở):** `18 kèo` (Đang tìm thêm bạn cùng chơi).
* **Thẻ 4 (Giờ vàng thi đấu):** `17:00 – 21:00` (Khung giờ cao điểm khuyến nghị đặt trước).

---

### 4.2. Cổng Quản Trị Hệ Thống (Admin Portal - `admin/index.html`)

Chuyên biệt cho công tác vận hành, có bộ chuyển đổi quyền hạn nội bộ (`ADMIN`, `STAFF`, `OWNER`) ngay trên Sidebar:

```html
<!-- Cấu trúc trong templates/admin/index.html -->
<main class="flex-1 px-6 lg:px-8 pb-10 pt-1">
  <!-- 1. Dashboard Thống kê Doanh thu & Giờ cao điểm (admin/dashboard-analytics.html) -->
  <section th:replace="~{admin/dashboard-analytics :: dashboardSection}"></section>

  <!-- 2. Quầy trực sân POS, Check-in & No-Show (admin/staff-pos-bookings.html) -->
  <section th:replace="~{admin/staff-pos-bookings :: staffPosSection}"></section>

  <!-- 3. Quản lý Cơ sở, Sân & Thiết lập Bảng giá (admin/facility-court-pricing.html) -->
  <section th:replace="~{admin/facility-court-pricing :: facilityCourtSection}"></section>

  <!-- 4. Lập lịch Bảo trì & Nhật ký Kiểm toán Audit (admin/maintenance-audit-users.html) -->
  <section th:replace="~{admin/maintenance-audit-users :: maintenanceAuditSection}"></section>
</main>
```

#### Menu Điều hướng Sidebar Quản trị:
1. `dashboard`: **Dashboard & Thống kê** (Icon `layout-dashboard`)
2. `staffPos`: **Quầy trực sân POS** (Icon `shopping-cart` / `check-circle`)
3. `facilities`: **Cơ sở, Sân & Bảng giá** (Icon `building-2`)
4. `maintenance`: **Bảo trì & Kiểm toán Audit** (Icon `wrench` / `file-text`)

#### Hàng 4 Thẻ KPI Quản trị:
* **Thẻ 1 (Doanh thu hôm nay):** Thu nhập thực tế thu về từ tiền cọc & thanh toán đủ.
* **Thẻ 2 (Tỷ lệ lấp đầy sân):** Tỷ lệ % khung giờ đã có khách đặt trong ngày.
* **Thẻ 3 (Khách đang giữ chỗ tạm):** Số khung giờ đang có trạng thái `HELD` bởi Redis.
* **Thẻ 4 (Chờ xử lý tại quầy):** Số đơn sắp đến giờ cần check-in hoặc cảnh báo No-Show quá 15 phút.

---

### 4.3. Trang Đăng Nhập & Xác Thực (Login Page - `auth/login.html`)

Bố cục 2 cột hiện đại theo tiêu chuẩn ứng dụng SaaS:
* **Cột trái (Form Xác thực):**
  * Nút đăng nhập Google chính thức tích hợp Google Identity Services (GIS).
  * Form nhập email trực tiếp (dự phòng khi mất kết nối mạng hoặc không muốn dùng tài khoản Google thật).
  * 4 Thẻ chọn nhanh tài khoản mẫu phục vụ demo đồ án:
    1. **Nguyễn Văn An (`CUSTOMER`)**: Vào thẳng Cổng Khách hàng.
    2. **Võ Thanh Nhân (`STAFF`)**: Vào thẳng Quầy POS trực sân.
    3. **Đặng Quốc Chủ (`OWNER`)**: Vào thẳng Quản lý cơ sở & bảng giá.
    4. **Quản Trị Hệ Thống (`ADMIN`)**: Toàn quyền quản trị hệ thống & nhật ký kiểm toán.
* **Cột phải (Showcase Trực quan):**
  * Hình ảnh chất lượng cao sân bóng đá mini (`/images/san_bong_da_mini.jpg`).
  * 3 Thẻ nổi bật về kỹ thuật:
    - *Đặt sân thời gian thực:* Khóa giữ chỗ Redis 5 phút không lo xung đột.
    - *Mạng xã hội thể thao Neo4j:* Ghép kèo và kết nối người chơi cùng đam mê.
    - *Kiểm toán minh bạch:* Lưu vết Cassandra và báo cáo doanh thu đa chiều.

---

## 5. Quy Chuẩn Đặt Tên & Viết Mã Khi Bổ Sung Giao Diện Mới

Để code của các thành viên không bị xung đột, tuân thủ các quy tắc sau:

1. **Thư viện Icon:** Luôn sử dụng thẻ `<i data-lucide="icon-name" class="h-4 w-4"></i>` và gọi `lucide.createIcons();` sau khi render lại DOM.
2. **Cơ chế gọi API:**
   * Sử dụng `fetch()` với đường dẫn `/api/v1/...`
   * Kiểm tra `data.success` theo cấu trúc chuẩn `ApiResponse<T>` từ backend.
   * Gửi kèm Header `X-User-Id` nếu gọi các API cần ngữ cảnh người dùng.
3. **Hiển thị Tiền tệ & Ngày giờ:**
   * Tiền tệ: Viết hoa `đ`, định dạng có dấu chấm phân cách hàng nghìn (Ví dụ: `350.000đ`, `70.000đ`).
   * Ngày giờ: `dd/MM/yyyy` (Ví dụ: `20/11/2026`).
   * Khung giờ: `HH:00` (Ví dụ: `18:00 - 19:00`).

---

## 6. Phân Công Trách Nhiệm Thiết Kế & Hoàn Thiện Theo 3 Thành Viên (A, B, C)

| Thành viên | Phân hệ đảm nhiệm (Use Cases) | File Giao diện & JS phụ trách | Hệ CSDL NoSQL & Ghi chú kỹ thuật |
| :--- | :--- | :--- | :--- |
| **Thành viên A**<br>*(Lõi Đặt Sân, Thanh Toán, POS & Worker nền)* | **UC09 – UC15**, Worker nền, Hạ tầng `common` (`RedisStreamPublisher`, `SseHub`) | `customer/facility-detail-schedule.html`<br>`customer/checkout-payment.html`<br>`customer/my-bookings.html`<br>`admin/staff-pos-bookings.html`<br>JS: `module2-booking-schedule.js` & phần POS trong `module4-staff-analytics-audit.js` | **Cassandra** (`slot_state_by_court_day` LWT `IF NOT EXISTS`, `booking_history_by_user`, `booking_events_by_booking`, `payments_*`), **Redis** (`hold_slots.lua`, `release_hold.lua`, `pop_due.lua`, Waitlist ZSET, Stream Publisher), **MongoDB** (`bookings`). |
| **Thành viên B**<br>*(Tài Khoản, Danh Mục, Quản Trị & Nhật Ký Audit)* | **UC01 – UC07**, **UC21 – UC23**, **UC25**, **UC26** | `auth/login.html`, `auth/profile.html`<br>`admin/facility-court-pricing.html`<br>`admin/maintenance-audit-users.html` (phần User & Audit)<br>JS: `core.js`, `admin-core.js`, phần Admin của `module1-search-admin.js` & phần Audit của `module4-staff-analytics-audit.js` | **MongoDB** (`users`, `facilities`, `courts`, `price_rules`, `policies`, `categories`), **Redis** (`rate_limit.lua`, `sb:auth:revoked:<jti>`, `sb:cache:price`, `sb:cache:policy`), **Cassandra** (`audit_by_day_type`, `audit_by_user`). |
| **Thành viên C**<br>*(Cộng Đồng Neo4j, Tìm Kiếm 2dsphere, Bảo Trì & Thống Kê)* | **UC08**, **UC16 – UC20**, **UC24**, Thông báo SSE & Stream Consumers | `customer/home-search.html`<br>`community/matchmaking-hub.html`<br>`admin/dashboard-analytics.html`<br>`admin/maintenance-audit-users.html` (phần Bảo trì)<br>JS: `module3-community-match.js`, phần Tìm kiếm của `module1-search-admin.js` & phần Thống kê của `module4-staff-analytics-audit.js` | **Neo4j** (`Player`, `Sport`, `Facility`, `Court`, `Match`; Cypher gợi ý bạn bè & ghép kèo), **MongoDB** (`2dsphere` tìm sân gần đây, `reviews`, `maintenance`, `notifications`), **Cassandra** (`revenue_daily`, `usage_by_hour`), **Redis** (`sb:rank:court`, Stream Consumer `cg-neo4j`, `cg-stats`). |
