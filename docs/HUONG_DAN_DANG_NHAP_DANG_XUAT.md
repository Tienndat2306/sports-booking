# Hướng Dẫn Sử Dụng Chức Năng Đăng Nhập & Đăng Xuất (Google OAuth 2.0)

Tài liệu hướng dẫn chi tiết quy trình xác thực, phân quyền tự động và lưu trữ dữ liệu đa hệ CSDL NoSQL của dự án **HUIT Sports**.

---

## 1. Tổng quan kiến trúc xác thực (Module 1 - UC01 & UC02)

Hệ thống áp dụng phương thức đăng nhập tập trung chuẩn **Google Identity Services (GIS)** kết hợp tự động phân quyền dựa trên vai trò:

```
[Người dùng]
     │
     ├── (1) Bấm "Continue with Google" / Google One Tap
     ▼
[Cửa sổ Google accounts.google.com]
     │  (Xác thực tài khoản Gmail cá nhân)
     ├── (2) Trả về JWT ID Token (đã ký số cryptographically)
     ▼
[Frontend /login]
     │  (Giải mã payload: email, name, avatarUrl)
     ├── (3) POST /api/v1/auth/google
     ▼
[Spring Boot Backend (AccountServiceImpl)]
     │
     ├── (4) MongoDB: Kiểm tra/Tạo UserDocument trong collection `users`
     ├── (5) Neo4j: Tạo/Đồng bộ Node `(:Player {id, name})`
     ├── (6) Cassandra: Ghi nhật ký kiểm toán vào bảng `audit_by_day_type`
     ▼
[Điều hướng Phân quyền Tự động]
     ├── Nếu role = 'CUSTOMER'          ──► Điều hướng về Trang Khách hàng (/)
     └── Nếu role = 'ADMIN'|'STAFF'|'OWNER' ──► Điều hướng vào Trang Quản trị (/admin)
```

---

## 2. Các cách đăng nhập trên giao diện `/login`

Truy cập đường dẫn: **[http://localhost:8080/login](http://localhost:8080/login)**

### Cách 1: Đăng nhập bằng Google thật (Khuyên dùng)
1. Trên giao diện, thư viện của Google sẽ tự động render nút **"Continue with Google"** chính thức (hoặc thanh One Tap trượt ra ở góc trên bên phải).
2. Khi bấm vào nút, cửa sổ Popup của Google sẽ mở ra.
3. Bạn chọn tài khoản Gmail cá nhân trên máy tính của mình.
4. Hệ thống tự động giải mã thông tin, tạo mã Customer ID (dạng `01JU-xxxxxx`), lưu vào CSDL và tự động chuyển bạn về trang chủ đặt sân.

> **Client ID đã cấu hình sẵn trong dự án:**  
> `389791032309-ha38ggklmlvjejqnvdeucoora43nn191.apps.googleusercontent.com`

### Cách 2: Tiếp tục bằng Email cá nhân (Dự phòng khi offline)
1. Nhập địa chỉ email bất kỳ của bạn (ví dụ: `yourname@gmail.com`) vào ô nhập liệu.
2. Bấm nút **"Tiếp tục bằng email"**.
3. Hệ thống sẽ lấy phần tên trước dấu `@` làm họ tên và tạo tài khoản ngay lập tức.

### Cách 3: Chọn nhanh 4 tài khoản mẫu (Dành cho việc thuyết trình/demo đồ án)
Ngay dưới ô nhập email có 4 thẻ tài khoản mẫu để bạn kiểm thử nhanh các quyền hạn khác nhau:
* **Nguyễn Văn An (`CUSTOMER`)**: Vào thẳng Cổng Khách hàng (`/`).
* **Trần Thu Hà (`STAFF`)**: Vào thẳng Cổng Quản trị (`/admin`) với vai trò Nhân viên quầy POS.
* **Lê Hoàng (`OWNER`)**: Vào thẳng Cổng Quản trị (`/admin`) với vai trò Chủ cơ sở thể thao.
* **Ban Quản Trị HUIT (`ADMIN`)**: Vào thẳng Cổng Quản trị (`/admin`) với quyền quản trị cao nhất.

---

## 3. Cơ chế Phân quyền & Điều hướng Tự động

Sau khi xác thực thành công, hàm `executeAuth()` trong file `login.html` sẽ phân tích vai trò tài khoản (`auth.role`):

```javascript
if (auth.role === 'ADMIN' || auth.role === 'STAFF' || auth.role === 'OWNER') {
  // Chuyển hướng sang giao diện Quản trị viên
  window.location.href = '/admin';
} else {
  // Chuyển hướng về giao diện Khách hàng đặt sân
  window.location.href = '/';
}
```

* **Cổng Khách hàng (`/`)**:
  * Chuyên biệt cho người chơi thể thao: Tìm kiếm sân, ma trận lịch trống 17 khung giờ, giữ chỗ 5 phút, thanh toán cọc và sàn ghép kèo giao lưu.
* **Cổng Quản trị (`/admin`)**:
  * Chuyên biệt cho quản lý: Báo cáo thống kê doanh thu, quầy trực sân POS, quản lý bảng giá, lịch bảo trì và khách hàng.

---

## 4. Dữ liệu lưu trữ trong 4 hệ CSDL NoSQL khi Đăng nhập

Khi một người dùng đăng nhập lần đầu, backend thực thi luồng đồng bộ đa CSDL (Polyglot Persistence):

### 4.1. Lưu thông tin người dùng trong MongoDB (`users` collection)
```json
{
  "_id": "01JU-MWLU3V0GF75938",
  "fullName": "Nguyễn Tiến Đạt",
  "email": "dattrithuc123@gmail.com",
  "role": "CUSTOMER",
  "status": "ACTIVE",
  "noShowCount": 0,
  "isWalkIn": false,
  "createdAt": ISODate("2026-11-18T08:03:23.203Z")
}
```

### 4.2. Đồng bộ sang đồ thị Neo4j (Node `:Player`)
```cypher
(:Player {
  id: "01JU-MWLU3V0GF75938",
  name: "Nguyễn Tiến Đạt",
  createdAt: "2026-11-18T08:03:23.372Z"
})
```
*Node này là điểm nút quan trọng để thực hiện các mối quan hệ `(:Player)-[:FRIEND]->(:Player)` và `(:Player)-[:BOOKED]->(:Court)` ở Module 3.*

### 4.3. Ghi vết kiểm toán vào Apache Cassandra (`audit_by_day_type`)
Mỗi lần đăng nhập, hệ thống tự động ghi 1 dòng sự kiện vào bảng nhật ký kiểm toán:
```sql
SELECT day, event_type, user_id, detail, ip 
FROM sports_booking.audit_by_day_type 
WHERE day = '2026-11-18';
```

---

## 5. Hướng dẫn Đăng xuất (Logout)

### Thao tác trên giao diện:
1. **Tại Cổng Khách hàng (`/`):** Ở góc dưới thanh Sidebar bên trái, tại thẻ tài khoản người dùng, bấm nút **"Đổi"** (hoặc vào tab *Hồ sơ cá nhân* $\rightarrow$ bấm *Đăng xuất*).
2. **Tại Cổng Quản trị (`/admin`):** Ở góc dưới Sidebar, bấm nút **"Đổi"** cạnh tên quản trị viên.

### Cơ chế kỹ thuật:
* Trình duyệt sẽ xóa toàn bộ khóa định danh lưu tạm:
  - `localStorage.removeItem('sb_user_id')`
  - `localStorage.removeItem('sb_token')`
  - `localStorage.removeItem('sb_name')`
  - `localStorage.removeItem('sb_role')`
* Chuyển hướng người dùng về trang **`/login`** để chọn tài khoản khác.

---

## 6. Hướng dẫn cấu hình Google Client ID cho thành viên nhóm

Nếu nhóm muốn thay đổi hoặc sử dụng Google Client ID riêng:
1. Mở file [src/main/resources/application.yml](file:///D:/NoSQL/DoAn/sportsbooking/src/main/resources/application.yml).
2. Cập nhật tại mục:
   ```yaml
   app:
     auth:
       google-client-id: ${GOOGLE_CLIENT_ID:YOUR_NEW_CLIENT_ID.apps.googleusercontent.com}
   ```
3. Trên [Google Cloud Console](https://console.cloud.google.com/apis/credentials), luôn đảm bảo mục **Authorized JavaScript origins** đã thêm:
   * `http://localhost:8080`
   * `http://127.0.0.1:8080`
