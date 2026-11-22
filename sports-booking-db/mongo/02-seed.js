// Nạp dữ liệu mẫu chuẩn cho MongoDB theo Chương 3 (mục 3.1.1 & 3.4.3).
// Dùng replaceOne(..., { upsert: true }) để chạy lại nhiều lần không bị lỗi trùng khóa.
const sb = db.getSiblingDB('sports_booking');

function upsertMany(collName, docs) {
  const coll = sb.getCollection(collName);
  docs.forEach(doc => {
    coll.replaceOne({ _id: doc._id }, doc, { upsert: true });
  });
  print(`[SEED] ${collName.padEnd(14)}: đã nạp ${docs.length} bản ghi (tổng hiện có: ${coll.countDocuments()})`);
}

// 1. Danh mục (categories) - 3 môn thể thao, mặt sân, tiện ích, khung giờ chuẩn
upsertMany('categories', [
  { _id: 'SPORT:FOOTBALL5', type: 'SPORT', code: 'FOOTBALL5', name: 'Bóng đá 5 người', active: true, meta: { defaultMaxPlayers: 10 } },
  { _id: 'SPORT:BADMINTON', type: 'SPORT', code: 'BADMINTON', name: 'Cầu lông', active: true, meta: { defaultMaxPlayers: 4 } },
  { _id: 'SPORT:VOLLEYBALL', type: 'SPORT', code: 'VOLLEYBALL', name: 'Bóng chuyền', active: true, meta: { defaultMaxPlayers: 12 } },
  { _id: 'SURFACE:CO_NHAN_TAO', type: 'SURFACE', code: 'CO_NHAN_TAO', name: 'Cỏ nhân tạo', active: true, meta: {} },
  { _id: 'SURFACE:THAM_PVC', type: 'SURFACE', code: 'THAM_PVC', name: 'Thảm PVC', active: true, meta: {} },
  { _id: 'SURFACE:CAT', type: 'SURFACE', code: 'CAT', name: 'Sân cát / xi măng ngoài trời', active: true, meta: {} },
  { _id: 'AMENITY:WC', type: 'AMENITY', code: 'WC', name: 'Nhà vệ sinh & phòng thay đồ', active: true, meta: {} },
  { _id: 'AMENITY:PARKING', type: 'AMENITY', code: 'PARKING', name: 'Bãi giữ xe', active: true, meta: {} },
  { _id: 'AMENITY:RACKET_RENTAL', type: 'AMENITY', code: 'RACKET_RENTAL', name: 'Cho thuê vợt / bóng', active: true, meta: {} },
  { _id: 'SLOT_TEMPLATE:DEFAULT_60M', type: 'SLOT_TEMPLATE', code: 'DEFAULT_60M', name: 'Khung 60 phút (05:00 - 22:00)', active: true,
    meta: { open: '05:00', close: '22:00', slotMinutes: 60 } }
]);

// 2. Người dùng mẫu (users) - mật khẩu mẫu: "123456" (BCrypt hash)
const DEFAULT_HASH = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy';
upsertMany('users', [
  {
    _id: '01JU0001', role: 'CUSTOMER', fullName: 'Nguyễn Văn An', phone: '0901000001', email: 'an@example.com',
    passwordHash: DEFAULT_HASH, status: 'ACTIVE',
    profile: { favoriteSports: ['FOOTBALL5', 'BADMINTON'], skillLevels: { FOOTBALL5: 3, BADMINTON: 2 } },
    noShowCount: 1, bookingRestriction: { active: false, requirePrepay: false, since: null },
    notes: [{ at: ISODate('2026-11-02T10:00:00Z'), by: '01JU0010', text: 'Khách quen, hay đặt tối thứ sáu' }],
    facilityIds: [], isWalkIn: false, createdAt: ISODate('2026-10-01T03:00:00Z'), deletedAt: null
  },
  {
    _id: '01JU0002', role: 'CUSTOMER', fullName: 'Trần Thị Bình', phone: '0901000002', email: 'binh@example.com',
    passwordHash: DEFAULT_HASH, status: 'ACTIVE',
    profile: { favoriteSports: ['FOOTBALL5'], skillLevels: { FOOTBALL5: 3 } },
    noShowCount: 0, bookingRestriction: { active: false, requirePrepay: false, since: null },
    notes: [], facilityIds: [], isWalkIn: false, createdAt: ISODate('2026-10-02T03:00:00Z'), deletedAt: null
  },
  {
    _id: '01JU0003', role: 'CUSTOMER', fullName: 'Lê Hoàng Cường', phone: '0901000003', email: 'cuong@example.com',
    passwordHash: DEFAULT_HASH, status: 'ACTIVE',
    profile: { favoriteSports: ['FOOTBALL5', 'VOLLEYBALL'], skillLevels: { FOOTBALL5: 3, VOLLEYBALL: 4 } },
    noShowCount: 0, bookingRestriction: { active: false, requirePrepay: false, since: null },
    notes: [], facilityIds: [], isWalkIn: false, createdAt: ISODate('2026-10-03T03:00:00Z'), deletedAt: null
  },
  {
    _id: '01JU0004', role: 'CUSTOMER', fullName: 'Phạm Minh Dũng', phone: '0901000004', email: 'dung@example.com',
    passwordHash: DEFAULT_HASH, status: 'ACTIVE',
    profile: { favoriteSports: ['FOOTBALL5', 'BADMINTON'], skillLevels: { FOOTBALL5: 2, BADMINTON: 3 } },
    noShowCount: 0, bookingRestriction: { active: false, requirePrepay: false, since: null },
    notes: [], facilityIds: [], isWalkIn: false, createdAt: ISODate('2026-10-04T03:00:00Z'), deletedAt: null
  },
  {
    _id: '01JU0005', role: 'CUSTOMER', fullName: 'Hoàng Thảo Em', phone: '0901000005', email: 'em@example.com',
    passwordHash: DEFAULT_HASH, status: 'ACTIVE',
    profile: { favoriteSports: ['BADMINTON'], skillLevels: { BADMINTON: 4 } },
    noShowCount: 0, bookingRestriction: { active: false, requirePrepay: false, since: null },
    notes: [], facilityIds: [], isWalkIn: false, createdAt: ISODate('2026-10-05T03:00:00Z'), deletedAt: null
  },
  {
    _id: '01JU0010', role: 'STAFF', fullName: 'Võ Thanh Nhân (Nhân viên trực sân)', phone: '0901000010', email: 'staff@example.com',
    passwordHash: DEFAULT_HASH, status: 'ACTIVE',
    profile: { favoriteSports: [], skillLevels: {} },
    noShowCount: 0, bookingRestriction: { active: false, requirePrepay: false, since: null },
    notes: [], facilityIds: ['FAC01', 'FAC02'], isWalkIn: false, createdAt: ISODate('2026-09-15T03:00:00Z'), deletedAt: null
  },
  {
    _id: '01JU0020', role: 'OWNER', fullName: 'Đặng Quốc Chủ (Chủ cụm sân)', phone: '0901000020', email: 'owner@example.com',
    passwordHash: DEFAULT_HASH, status: 'ACTIVE',
    profile: { favoriteSports: [], skillLevels: {} },
    noShowCount: 0, bookingRestriction: { active: false, requirePrepay: false, since: null },
    notes: [], facilityIds: ['FAC01', 'FAC02'], isWalkIn: false, createdAt: ISODate('2026-09-01T03:00:00Z'), deletedAt: null
  },
  {
    _id: '01JU0099', role: 'ADMIN', fullName: 'Quản Trị Hệ Thống', phone: '0901000099', email: 'admin@example.com',
    passwordHash: DEFAULT_HASH, status: 'ACTIVE',
    profile: { favoriteSports: [], skillLevels: {} },
    noShowCount: 0, bookingRestriction: { active: false, requirePrepay: false, since: null },
    notes: [], facilityIds: ['FAC01', 'FAC02'], isWalkIn: false, createdAt: ISODate('2026-09-01T00:00:00Z'), deletedAt: null
  }
]);

// 3. Cơ sở thể thao (facilities) - 2 cơ sở tại TP.HCM (mục 3.1.1)
upsertMany('facilities', [
  {
    _id: 'FAC01',
    name: 'Tổ hợp Thể thao HUIT Cơ sở 1 (Tân Phú)',
    address: { street: '140 Lê Trọng Tấn', district: 'Tân Phú', city: 'TP.HCM' },
    location: { type: 'Point', coordinates: [106.6286, 10.8069] },
    hours: { open: '05:00', close: '22:00', slotMinutes: 60 },
    sports: ['FOOTBALL5', 'BADMINTON', 'VOLLEYBALL'],
    ownerId: '01JU0020',
    status: 'ACTIVE'
  },
  {
    _id: 'FAC02',
    name: 'Cụm Sân Bóng Đá HUIT Cơ sở 2 (Tân Bình)',
    address: { street: '300 Cộng Hòa', district: 'Tân Bình', city: 'TP.HCM' },
    location: { type: 'Point', coordinates: [106.6438, 10.8016] },
    hours: { open: '05:00', close: '22:00', slotMinutes: 60 },
    sports: ['FOOTBALL5'],
    ownerId: '01JU0020',
    status: 'ACTIVE'
  }
]);

// 4. Danh sách 10 sân (courts) - 7 sân ở FAC01, 3 sân ở FAC02 (mục 3.1.1)
upsertMany('courts', [
  { _id: 'FAC01-FB-01', facilityId: 'FAC01', sportCode: 'FOOTBALL5', name: 'Sân bóng đá 1', status: 'ACTIVE', maxPlayers: 10,
    attributes: { surface: 'CO_NHAN_TAO', size: '5x5', lighting: true, covered: false }, amenities: ['WC', 'PARKING'] },
  { _id: 'FAC01-FB-02', facilityId: 'FAC01', sportCode: 'FOOTBALL5', name: 'Sân bóng đá 2', status: 'ACTIVE', maxPlayers: 10,
    attributes: { surface: 'CO_NHAN_TAO', size: '5x5', lighting: true, covered: false }, amenities: ['WC', 'PARKING'] },
  { _id: 'FAC01-FB-03', facilityId: 'FAC01', sportCode: 'FOOTBALL5', name: 'Sân bóng đá 3 (Có mái che)', status: 'ACTIVE', maxPlayers: 10,
    attributes: { surface: 'CO_NHAN_TAO', size: '5x5', lighting: true, covered: true }, amenities: ['WC', 'PARKING'] },
  { _id: 'FAC01-BM-01', facilityId: 'FAC01', sportCode: 'BADMINTON', name: 'Sân cầu lông 1', status: 'ACTIVE', maxPlayers: 4,
    attributes: { indoor: true, floor: 'THAM_PVC', lighting: true, airConditioned: true }, amenities: ['WC', 'RACKET_RENTAL', 'PARKING'] },
  { _id: 'FAC01-BM-02', facilityId: 'FAC01', sportCode: 'BADMINTON', name: 'Sân cầu lông 2', status: 'ACTIVE', maxPlayers: 4,
    attributes: { indoor: true, floor: 'THAM_PVC', lighting: true, airConditioned: true }, amenities: ['WC', 'RACKET_RENTAL', 'PARKING'] },
  { _id: 'FAC01-BM-03', facilityId: 'FAC01', sportCode: 'BADMINTON', name: 'Sân cầu lông 3', status: 'ACTIVE', maxPlayers: 4,
    attributes: { indoor: true, floor: 'THAM_PVC', lighting: true, airConditioned: false }, amenities: ['WC', 'PARKING'] },
  { _id: 'FAC01-VB-01', facilityId: 'FAC01', sportCode: 'VOLLEYBALL', name: 'Sân bóng chuyền 1', status: 'ACTIVE', maxPlayers: 12,
    attributes: { indoor: false, surface: 'CAT', lighting: true }, amenities: ['WC', 'PARKING'] },
  { _id: 'FAC02-FB-01', facilityId: 'FAC02', sportCode: 'FOOTBALL5', name: 'Sân bóng đá 2A', status: 'ACTIVE', maxPlayers: 10,
    attributes: { surface: 'CO_NHAN_TAO', size: '5x5', lighting: true, covered: false }, amenities: ['WC', 'PARKING'] },
  { _id: 'FAC02-FB-02', facilityId: 'FAC02', sportCode: 'FOOTBALL5', name: 'Sân bóng đá 2B', status: 'ACTIVE', maxPlayers: 10,
    attributes: { surface: 'CO_NHAN_TAO', size: '5x5', lighting: true, covered: false }, amenities: ['WC', 'PARKING'] },
  { _id: 'FAC02-FB-03', facilityId: 'FAC02', sportCode: 'FOOTBALL5', name: 'Sân bóng đá 2C', status: 'ACTIVE', maxPlayers: 10,
    attributes: { surface: 'CO_NHAN_TAO', size: '5x5', lighting: true, covered: true }, amenities: ['WC', 'PARKING'] }
]);

// 5. Bảng giá (price_rules)
upsertMany('price_rules', [
  // FAC01 - FOOTBALL5
  { _id: 'PR-FAC01-FB-WD-OFF', facilityId: 'FAC01', sportCode: 'FOOTBALL5', dayType: 'WEEKDAY', band: 'OFFPEAK', fromTime: '05:00', toTime: '17:00', priceVnd: 220000, validFrom: ISODate('2026-01-01T00:00:00Z'), validTo: null },
  { _id: 'PR-FAC01-FB-WD-PEAK', facilityId: 'FAC01', sportCode: 'FOOTBALL5', dayType: 'WEEKDAY', band: 'PEAK', fromTime: '17:00', toTime: '22:00', priceVnd: 350000, validFrom: ISODate('2026-01-01T00:00:00Z'), validTo: null },
  { _id: 'PR-FAC01-FB-WE-ALL', facilityId: 'FAC01', sportCode: 'FOOTBALL5', dayType: 'WEEKEND', band: 'PEAK', fromTime: '05:00', toTime: '22:00', priceVnd: 380000, validFrom: ISODate('2026-01-01T00:00:00Z'), validTo: null },
  // FAC01 - BADMINTON
  { _id: 'PR-FAC01-BM-WD-OFF', facilityId: 'FAC01', sportCode: 'BADMINTON', dayType: 'WEEKDAY', band: 'OFFPEAK', fromTime: '05:00', toTime: '17:00', priceVnd: 80000, validFrom: ISODate('2026-01-01T00:00:00Z'), validTo: null },
  { _id: 'PR-FAC01-BM-WD-PEAK', facilityId: 'FAC01', sportCode: 'BADMINTON', dayType: 'WEEKDAY', band: 'PEAK', fromTime: '17:00', toTime: '22:00', priceVnd: 120000, validFrom: ISODate('2026-01-01T00:00:00Z'), validTo: null },
  { _id: 'PR-FAC01-BM-WE-ALL', facilityId: 'FAC01', sportCode: 'BADMINTON', dayType: 'WEEKEND', band: 'PEAK', fromTime: '05:00', toTime: '22:00', priceVnd: 130000, validFrom: ISODate('2026-01-01T00:00:00Z'), validTo: null },
  // FAC01 - VOLLEYBALL
  { _id: 'PR-FAC01-VB-WD-OFF', facilityId: 'FAC01', sportCode: 'VOLLEYBALL', dayType: 'WEEKDAY', band: 'OFFPEAK', fromTime: '05:00', toTime: '17:00', priceVnd: 180000, validFrom: ISODate('2026-01-01T00:00:00Z'), validTo: null },
  { _id: 'PR-FAC01-VB-WD-PEAK', facilityId: 'FAC01', sportCode: 'VOLLEYBALL', dayType: 'WEEKDAY', band: 'PEAK', fromTime: '17:00', toTime: '22:00', priceVnd: 260000, validFrom: ISODate('2026-01-01T00:00:00Z'), validTo: null },
  { _id: 'PR-FAC01-VB-WE-ALL', facilityId: 'FAC01', sportCode: 'VOLLEYBALL', dayType: 'WEEKEND', band: 'PEAK', fromTime: '05:00', toTime: '22:00', priceVnd: 280000, validFrom: ISODate('2026-01-01T00:00:00Z'), validTo: null },
  // FAC02 - FOOTBALL5
  { _id: 'PR-FAC02-FB-WD-OFF', facilityId: 'FAC02', sportCode: 'FOOTBALL5', dayType: 'WEEKDAY', band: 'OFFPEAK', fromTime: '05:00', toTime: '17:00', priceVnd: 200000, validFrom: ISODate('2026-01-01T00:00:00Z'), validTo: null },
  { _id: 'PR-FAC02-FB-WD-PEAK', facilityId: 'FAC02', sportCode: 'FOOTBALL5', dayType: 'WEEKDAY', band: 'PEAK', fromTime: '17:00', toTime: '22:00', priceVnd: 320000, validFrom: ISODate('2026-01-01T00:00:00Z'), validTo: null },
  { _id: 'PR-FAC02-FB-WE-ALL', facilityId: 'FAC02', sportCode: 'FOOTBALL5', dayType: 'WEEKEND', band: 'PEAK', fromTime: '05:00', toTime: '22:00', priceVnd: 350000, validFrom: ISODate('2026-01-01T00:00:00Z'), validTo: null }
]);

// 6. Chính sách đặt cọc và hoàn hủy (policies - tuân thủ $jsonSchema trong 01-init.js)
upsertMany('policies', [
  {
    _id: 'POL-FAC01-v1',
    facilityId: 'FAC01',
    version: 1,
    current: true,
    depositPercent: 30,
    holdMinutes: 5,
    graceMinutes: 15,
    noShowThreshold: 3,
    cancelTiers: [
      { beforeHours: 24, refundPercent: 100 },
      { beforeHours: 6, refundPercent: 50 },
      { beforeHours: 0, refundPercent: 0 }
    ],
    effectiveFrom: ISODate('2026-11-01T00:00:00Z'),
    createdBy: '01JU0020'
  },
  {
    _id: 'POL-FAC02-v1',
    facilityId: 'FAC02',
    version: 1,
    current: true,
    depositPercent: 30,
    holdMinutes: 5,
    graceMinutes: 15,
    noShowThreshold: 3,
    cancelTiers: [
      { beforeHours: 24, refundPercent: 100 },
      { beforeHours: 6, refundPercent: 50 },
      { beforeHours: 0, refundPercent: 0 }
    ],
    effectiveFrom: ISODate('2026-11-01T00:00:00Z'),
    createdBy: '01JU0020'
  }
]);

// 7. Đơn đặt sân mẫu (bookings)
upsertMany('bookings', [
  {
    _id: '01JB0001',
    code: 'BK-20261120-0001',
    facilityId: 'FAC01',
    courtId: 'FAC01-FB-01',
    sportCode: 'FOOTBALL5',
    userId: '01JU0001',
    createdBy: '01JU0001',
    source: 'ONLINE',
    bookingDate: '2026-11-20',
    slotStarts: ['18:00', '19:00'],
    startAt: ISODate('2026-11-20T11:00:00Z'),
    endAt: ISODate('2026-11-20T13:00:00Z'),
    players: 10,
    status: 'CONFIRMED',
    pricing: {
      lines: [
        { slot: '18:00', priceVnd: 350000 },
        { slot: '19:00', priceVnd: 350000 }
      ],
      totalVnd: 700000,
      depositVnd: 210000,
      paidVnd: 210000
    },
    policySnapshot: {
      policyId: 'POL-FAC01-v1',
      version: 1,
      depositPercent: 30,
      graceMinutes: 15,
      cancelTiers: [
        { beforeHours: 24, refundPercent: 100 },
        { beforeHours: 6, refundPercent: 50 },
        { beforeHours: 0, refundPercent: 0 }
      ]
    },
    holdExpiresAt: null,
    checkedInAt: null,
    noShow: null,
    cancel: null,
    rescheduledFrom: null,
    matchId: null,
    note: 'Đặt sân giao hữu tối thứ Sáu',
    version: 1,
    createdAt: ISODate('2026-11-16T09:00:00Z'),
    updatedAt: ISODate('2026-11-16T09:03:00Z')
  }
]);

// 8. Bảo trì mẫu (maintenance)
upsertMany('maintenance', [
  {
    _id: '01JM0001',
    facilityId: 'FAC01',
    courtId: 'FAC01-BM-02',
    reason: 'LIGHT',
    description: 'Bảo dưỡng hệ thống đèn chiếu sáng',
    emergency: false,
    windows: [{ day: '2026-11-22', fromSlot: '08:00', toSlot: '10:00' }],
    status: 'ACTIVE',
    affectedBookings: [],
    reportedBy: '01JU0010',
    completedAt: null,
    createdAt: ISODate('2026-11-16T08:00:00Z')
  }
]);

// 9. Đánh giá mẫu (reviews)
upsertMany('reviews', [
  {
    _id: '01JR0001',
    bookingId: '01JB0001',
    courtId: 'FAC01-FB-01',
    facilityId: 'FAC01',
    userId: '01JU0001',
    rating: 5,
    comment: 'Mặt cỏ nhân tạo mới, đèn sáng và có chỗ đậu xe rộng rãi.',
    createdAt: ISODate('2026-11-17T02:00:00Z')
  }
]);

// 10. Thông báo mẫu (notifications)
upsertMany('notifications', [
  {
    _id: '01JN0001',
    userId: '01JU0001',
    type: 'BOOKING_CONFIRMED',
    title: 'Đơn đặt sân BK-20261120-0001 đã được xác nhận',
    refs: { bookingId: '01JB0001', courtId: 'FAC01-FB-01', day: '2026-11-20' },
    readAt: null,
    createdAt: ISODate('2026-11-16T09:03:00Z')
  }
]);

print('=== Hoàn tất nạp dữ liệu mẫu MongoDB ===');
