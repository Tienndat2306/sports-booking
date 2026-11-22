# Môi trường CSDL – Hệ thống quản lý sân thể thao và đặt lịch

Dựng đủ 4 hệ (MongoDB, Cassandra, Neo4j, Redis) và tạo sẵn cấu trúc theo Chương 3.

## Yêu cầu
- Docker Desktop (hoặc Docker Engine + Compose v2), RAM trống khoảng 3 GB.

## Chạy
```bash
docker compose up -d
docker compose ps -a          # 4 hệ "healthy", 4 container *-init "Exited (0)" là đúng
```
Lần đầu mất 1-3 phút (Cassandra khởi động chậm). Xem kết quả khởi tạo từng hệ:
```bash
docker compose logs mongo-init
docker compose logs cassandra-init
docker compose logs neo4j-init
docker compose logs redis-init      # có các dòng PASS và "== Redis OK =="
```
Làm lại từ đầu (xóa sạch dữ liệu): `docker compose down -v && docker compose up -d`

## Kết nối
| Hệ | Địa chỉ | Ghi chú |
|---|---|---|
| MongoDB | `mongodb://localhost:27018/sports_booking?replicaSet=rs0&directConnection=true` | Mở cả cổng `27017` và `27018`. Khuyên dùng `27018` để tránh đụng dịch vụ MongoDB cài sẵn trên Windows |
| Cassandra | `localhost:9042`, keyspace `sports_booking` | Datacenter mặc định `datacenter1`; TablePlus chọn Cassandra, không cần mật khẩu |
| Neo4j | Bolt `bolt://localhost:7687`, giao diện http://localhost:7474 | Tài khoản `neo4j`, mật khẩu `sbneo4j2026` (đổi qua file `.env`, xem `.env.example`) |
| Redis | `localhost:6379` | Không mật khẩu; bật AOF để không mất dữ liệu khi khởi động lại |

## Kiểm tra nhanh
```bash
# MongoDB: liệt kê collection và chỉ mục
docker compose exec mongo mongosh --quiet --eval "db.getSiblingDB('sports_booking').getCollectionNames()"

# Cassandra: liệt kê bảng
docker compose exec cassandra cqlsh -e "USE sports_booking; DESCRIBE TABLES;"

# Neo4j: liệt kê ràng buộc
docker compose exec neo4j cypher-shell -u neo4j -p sbneo4j2026 "SHOW CONSTRAINTS"

# Redis: nhóm tiêu thụ của stream
docker compose exec redis redis-cli XINFO GROUPS sb:stream:events
```

## Các file
| File | Nội dung | Mục trong báo cáo |
|---|---|---|
| `docker-compose.yml` | 4 CSDL và 4 container khởi tạo chạy một lần | 3.2.3 |
| `mongo/01-init.js` | 10 collection, chỉ mục (2dsphere, unique, TTL, partial), `$jsonSchema` cho `policies` | 3.4 |
| `cassandra/01-schema.cql` | Keyspace và 9 bảng | 3.5.4 |
| `neo4j/01-schema.cypher` | 5 ràng buộc duy nhất và chỉ mục `match_open` | 3.6.4 |
| `redis/lua/*.lua`, `redis/init.sh` | 4 script Lua, tạo nhóm tiêu thụ `cg-neo4j`, `cg-stats`, `cg-cache`, kiểm thử script | 3.7 |

## Điểm khác so với văn bản chương 3 (cần cập nhật báo cáo cho khớp)
1. `users.phone` và `users.email` dùng **partial unique index** (chỉ áp dụng khi trường là chuỗi) thay cho unique/sparse, để khách vãng lai không có số điện thoại vẫn tạo được và `email: null` không gây trùng.
2. Các câu lệnh CQL thêm `IF NOT EXISTS` để chạy lại không lỗi; thêm `USE sports_booking;`.
3. Script Lua chỉ được nạp thử và kiểm tra; ứng dụng Spring Boot sẽ tự nạp lại khi khởi động (bộ nhớ script của Redis mất khi Redis khởi động lại).
4. Đã tích hợp tự động nạp dữ liệu mẫu (`02-seed.js`, `02-seed.cql`, `02-seed.cypher`) ngay khi chạy `docker compose up -d`. Xem hướng dẫn đầy đủ của toàn dự án tại file `../README.md`.
