// Khởi tạo MongoDB cho đồ án (mục 3.4): 10 collection, chỉ mục, kiểm tra lược đồ cho policies.
// Chạy lại nhiều lần vẫn an toàn.
const sb = db.getSiblingDB('sports_booking');
const existing = sb.getCollectionNames();

// ---- 1. Tạo 10 collection (mục 3.4.2) ----
const names = ['users', 'facilities', 'courts', 'price_rules', 'policies',
               'bookings', 'maintenance', 'reviews', 'categories', 'notifications'];

// $jsonSchema chặn tham số chính sách không hợp lệ (UC25 luồng 4a, NFR12)
const policiesValidator = {
  $jsonSchema: {
    bsonType: 'object',
    required: ['facilityId', 'version', 'current', 'depositPercent', 'holdMinutes',
               'graceMinutes', 'noShowThreshold', 'cancelTiers', 'effectiveFrom'],
    properties: {
      facilityId:      { bsonType: 'string' },
      version:         { bsonType: 'number', minimum: 1 },
      current:         { bsonType: 'bool' },
      depositPercent:  { bsonType: 'number', minimum: 0, maximum: 100 },
      holdMinutes:     { bsonType: 'number', minimum: 1, maximum: 30 },
      graceMinutes:    { bsonType: 'number', minimum: 0, maximum: 60 },
      noShowThreshold: { bsonType: 'number', minimum: 1 },
      cancelTiers: {
        bsonType: 'array', minItems: 1,
        items: {
          bsonType: 'object',
          required: ['beforeHours', 'refundPercent'],
          properties: {
            beforeHours:   { bsonType: 'number', minimum: 0 },
            refundPercent: { bsonType: 'number', minimum: 0, maximum: 100 }
          }
        }
      }
    }
  }
};

names.forEach(n => {
  if (n === 'policies') {
    if (existing.includes(n)) {
      sb.runCommand({ collMod: n, validator: policiesValidator, validationLevel: 'strict', validationAction: 'error' });
    } else {
      sb.createCollection(n, { validator: policiesValidator, validationLevel: 'strict', validationAction: 'error' });
    }
  } else if (!existing.includes(n)) {
    sb.createCollection(n);
  }
});

// ---- 2. Chỉ mục (mục 3.4.5) ----
// users: phone và email duy nhất; dùng partial index để khách vãng lai không có phone/email vẫn tạo được
sb.users.createIndex({ phone: 1 }, { unique: true, partialFilterExpression: { phone: { $type: 'string' } } });
sb.users.createIndex({ email: 1 }, { unique: true, partialFilterExpression: { email: { $type: 'string' } } });
sb.users.createIndex({ role: 1, facilityIds: 1 });

sb.facilities.createIndex({ location: '2dsphere' });
sb.facilities.createIndex({ status: 1 });

sb.courts.createIndex({ facilityId: 1, sportCode: 1, status: 1 });

sb.price_rules.createIndex({ facilityId: 1, sportCode: 1, dayType: 1, fromTime: 1 });

// mỗi cơ sở chỉ có một chính sách hiện hành
sb.policies.createIndex({ facilityId: 1 }, { unique: true, partialFilterExpression: { current: true } });

sb.bookings.createIndex({ code: 1 }, { unique: true });
sb.bookings.createIndex({ facilityId: 1, bookingDate: 1, status: 1 });
sb.bookings.createIndex({ userId: 1, createdAt: -1 });
sb.bookings.createIndex({ courtId: 1, bookingDate: 1 });
sb.bookings.createIndex({ holdExpiresAt: 1 }, { partialFilterExpression: { status: 'HELD' } });

sb.maintenance.createIndex({ courtId: 1, 'windows.day': 1 });
sb.maintenance.createIndex({ facilityId: 1, status: 1 });

sb.reviews.createIndex({ bookingId: 1 }, { unique: true });
sb.reviews.createIndex({ courtId: 1, createdAt: -1 });

sb.categories.createIndex({ type: 1, code: 1 }, { unique: true });

sb.notifications.createIndex({ userId: 1, createdAt: -1 });
sb.notifications.createIndex({ createdAt: 1 }, { expireAfterSeconds: 7776000 }); // 90 ngày

// ---- 3. Báo cáo kết quả ----
print('=== MongoDB sports_booking ===');
sb.getCollectionNames().sort().forEach(n => {
  print(n.padEnd(14) + ' chỉ mục: ' + sb.getCollection(n).getIndexes().map(i => i.name).join(', '));
});
