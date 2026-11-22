package com.huit.sportsbooking.service.account;

import com.huit.sportsbooking.common.AppException;
import com.huit.sportsbooking.common.ErrorCode;
import com.huit.sportsbooking.common.IdGenerator;
import com.huit.sportsbooking.dto.account.AuthResponse;
import com.huit.sportsbooking.dto.account.GoogleLoginRequest;
import com.huit.sportsbooking.model.cassandra.AuditByDayType;
import com.huit.sportsbooking.model.mongo.UserDocument;
import com.huit.sportsbooking.model.neo4j.PlayerNode;
import com.huit.sportsbooking.repository.mongo.UserMongoRepository;
import com.huit.sportsbooking.repository.neo4j.PlayerNeo4jRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final UserMongoRepository userMongoRepo;
    private final PlayerNeo4jRepository playerNeo4jRepo;
    private final CassandraOperations cassandraOperations;

    public AccountServiceImpl(UserMongoRepository userMongoRepo,
                              PlayerNeo4jRepository playerNeo4jRepo,
                              CassandraOperations cassandraOperations) {
        this.userMongoRepo = userMongoRepo;
        this.playerNeo4jRepo = playerNeo4jRepo;
        this.cassandraOperations = cassandraOperations;
    }

    @Override
    public AuthResponse loginOrRegisterGoogle(GoogleLoginRequest request, String ipAddress) {
        if (request == null || request.email() == null || request.email().isBlank()) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Email Google không được để trống");
        }

        String email = request.email().trim().toLowerCase();
        String name = (request.name() != null && !request.name().isBlank())
                ? request.name().trim()
                : email.split("@")[0];

        Optional<UserDocument> existingOpt = userMongoRepo.findByEmail(email);
        UserDocument user;

        if (existingOpt.isPresent()) {
            // ---- ĐĂNG NHẬP (UC02) ----
            user = existingOpt.get();
            if ("LOCKED".equalsIgnoreCase(user.getStatus())) {
                throw new AppException(ErrorCode.FORBIDDEN, "Tài khoản của bạn đã bị tạm khóa bởi quản trị viên");
            }
            log.info("Người dùng đăng nhập thành công qua Google: {} ({})", user.getEmail(), user.getId());
        } else {
            // ---- ĐĂNG KÝ MỚI TỰ ĐỘNG (UC01) ----
            String newUserId = IdGenerator.nextId("01JU");
            String defaultRole = (request.role() != null && !request.role().isBlank())
                    ? request.role().trim().toUpperCase()
                    : "CUSTOMER";

            user = new UserDocument();
            user.setId(newUserId);
            user.setEmail(email);
            user.setFullName(name);
            user.setRole(defaultRole);
            user.setStatus("ACTIVE");
            user.setCreatedAt(Instant.now());

            // 1. Lưu vào MongoDB (users collection)
            userMongoRepo.save(user);

            // 2. Đồng bộ sang đồ thị Neo4j (Player node)
            try {
                PlayerNode playerNode = new PlayerNode(newUserId, name, Instant.now());
                playerNeo4jRepo.save(playerNode);
                log.info("Đã đồng bộ tạo Node Player trên Neo4j cho user: {}", newUserId);
            } catch (Exception e) {
                log.warn("Không thể đồng bộ Node Player trên Neo4j: {}", e.getMessage());
            }

            log.info("Đã đăng ký tài khoản mới qua Google thành công: {} ({})", email, newUserId);
        }

        // 3. Ghi nhật ký kiểm toán vào Apache Cassandra (audit_by_day_type)
        recordAuditLog(user.getId(), user.getRole(), "LOGIN", "Đăng nhập Google thành công (" + email + ")", ipAddress);

        // 4. Tạo token phiên làm việc (giả lập token thống nhất)
        String sessionToken = "sb-google-token-" + UUID.randomUUID();

        return new AuthResponse(
                sessionToken,
                user.getId(),
                user.getFullName(),
                user.getPhone() != null ? user.getPhone() : "",
                user.getRole()
        );
    }

    @Override
    public UserDocument getCurrentUserProfile(String userId) {
        return userMongoRepo.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy hồ sơ người dùng: " + userId));
    }

    @Override
    public void logout(String userId, String token, String ipAddress) {
        log.info("Người dùng đăng xuất: {}", userId);
        recordAuditLog(userId, "USER", "LOGOUT", "Người dùng đăng xuất", ipAddress);
    }

    private void recordAuditLog(String userId, String role, String eventType, String detail, String ip) {
        try {
            AuditByDayType audit = new AuditByDayType();
            audit.setDay(LocalDate.now(IdGenerator.VN_ZONE));
            audit.setEventType(eventType);
            audit.setEventTime(UUID.randomUUID());
            audit.setUserId(userId);
            audit.setRole(role);
            audit.setEntityType("AUTH");
            audit.setEntityId(userId);
            audit.setDetail(detail);
            audit.setIp(ip != null ? ip : "127.0.0.1");

            cassandraOperations.insert(audit);
        } catch (Exception e) {
            log.warn("Ghi nhật ký Cassandra kiểm toán thất bại (không ảnh hưởng luồng chính): {}", e.getMessage());
        }
    }
}
