/**
 * module1-search-admin.js
 * Phụ trách Module 1: Tìm kiếm sân (MongoDB 2dsphere + Redis ZSET Ranking),
 * Quản trị Cơ sở, Sân, Bảng giá, Chính sách ($jsonSchema), Khách hàng và Danh mục.
 */
const facilitiesData = [
  {
    id: 'FAC01',
    name: 'Tổ hợp Thể thao HUIT Cơ sở 1 (Tân Phú)',
    street: '140 Lê Trọng Tấn, Quận Tân Phú, TP.HCM',
    district: 'Tân Phú',
    coords: '[106.6286, 10.8069]',
    dist: '1,2 km',
    priceRange: '80.000đ – 380.000đ/giờ',
    rating: '4,8',
    courtCount: 7,
    sports: [
      { code: 'FOOTBALL5', label: 'Bóng đá 5 người (3 sân)' },
      { code: 'BADMINTON', label: 'Cầu lông (3 sân)' },
      { code: 'VOLLEYBALL', label: 'Bóng chuyền (1 sân)' }
    ]
  },
  {
    id: 'FAC02',
    name: 'Cụm Sân Bóng Đá HUIT Cơ sở 2 (Tân Bình)',
    street: '300 Cộng Hòa, Quận Tân Bình, TP.HCM',
    district: 'Tân Bình',
    coords: '[106.6438, 10.8016]',
    dist: '3,5 km',
    priceRange: '200.000đ – 350.000đ/giờ',
    rating: '4,6',
    courtCount: 3,
    sports: [
      { code: 'FOOTBALL5', label: 'Bóng đá 5 người (3 sân)' }
    ]
  }
];

const allCourts = [
  { id: 'FAC01-FB-01', fac: 'FAC01', sport: 'FOOTBALL5', name: 'Sân bóng đá 1', attr: 'Cỏ nhân tạo 5x5 · Có đèn', off: 220, peak: 350 },
  { id: 'FAC01-FB-02', fac: 'FAC01', sport: 'FOOTBALL5', name: 'Sân bóng đá 2', attr: 'Cỏ nhân tạo 5x5 · Có đèn', off: 220, peak: 350 },
  { id: 'FAC01-FB-03', fac: 'FAC01', sport: 'FOOTBALL5', name: 'Sân bóng đá 3 (Mái che)', attr: 'Cỏ nhân tạo 5x5 · Mái che', off: 220, peak: 350 },
  { id: 'FAC01-BM-01', fac: 'FAC01', sport: 'BADMINTON', name: 'Sân cầu lông 1', attr: 'Trong nhà · Thảm PVC · Điều hòa', off: 80, peak: 120 },
  { id: 'FAC01-BM-02', fac: 'FAC01', sport: 'BADMINTON', name: 'Sân cầu lông 2', attr: 'Trong nhà · Thảm PVC · Điều hòa', off: 80, peak: 120 },
  { id: 'FAC01-BM-03', fac: 'FAC01', sport: 'BADMINTON', name: 'Sân cầu lông 3', attr: 'Trong nhà · Thảm PVC', off: 80, peak: 120 },
  { id: 'FAC01-VB-01', fac: 'FAC01', sport: 'VOLLEYBALL', name: 'Sân bóng chuyền 1', attr: 'Ngoài trời · Sân cát · Có đèn', off: 180, peak: 260 },
  { id: 'FAC02-FB-01', fac: 'FAC02', sport: 'FOOTBALL5', name: 'Sân bóng đá 2A', attr: 'Cỏ nhân tạo 5x5 · Có đèn', off: 200, peak: 320 },
  { id: 'FAC02-FB-02', fac: 'FAC02', sport: 'FOOTBALL5', name: 'Sân bóng đá 2B', attr: 'Cỏ nhân tạo 5x5 · Có đèn', off: 200, peak: 320 },
  { id: 'FAC02-FB-03', fac: 'FAC02', sport: 'FOOTBALL5', name: 'Sân bóng đá 2C (Mái che)', attr: 'Cỏ nhân tạo 5x5 · Mái che', off: 200, peak: 320 }
];

const topCourtsData = [
  { rank: '🥇 Top 1', id: 'FAC01-FB-01', name: 'Sân bóng đá 1 (FAC01)', sport: 'FOOTBALL5', count: 42 },
  { rank: '🥈 Top 2', id: 'FAC01-BM-01', name: 'Sân cầu lông 1 (FAC01)', sport: 'BADMINTON', count: 38 },
  { rank: '🥉 Top 3', id: 'FAC02-FB-01', name: 'Sân bóng đá 2A (FAC02)', sport: 'FOOTBALL5', count: 31 }
];

function renderTopCourts() {
  const el = $('#topCourts');
  if (!el) return;
  el.innerHTML = topCourtsData.map(c => `
    <div class="card !p-4 flex items-center justify-between border-emerald-100 bg-gradient-to-br from-white to-emerald-50/40">
      <div>
        <span class="tag bg-emerald-100 text-emerald-800">${c.rank}</span>
        <div class="mt-1.5 font-bold">${c.name}</div>
        <div class="text-xs text-slate-500">Mã sân: ${c.id} · ${c.count} khung đã đặt</div>
      </div>
      <button class="btn2 !px-3 !py-1.5 text-xs" onclick="go('schedule')">Đặt ngay</button>
    </div>
  `).join('');
}

function renderFacilities() {
  const facEl = $('#fac');
  if (!facEl) return;
  const sp = $('#filterSport') ? $('#filterSport').value : 'ALL';
  const dist = $('#filterDistrict') ? $('#filterDistrict').value : 'ALL';
  const list = facilitiesData.filter(f => {
    const okSport = sp === 'ALL' || f.sports.some(s => s.code === sp);
    const okDist = dist === 'ALL' || f.district === dist;
    return okSport && okDist;
  });
  facEl.innerHTML = list.map(f => `
    <div class="card flex flex-col justify-between">
      <div>
        <div class="flex items-start justify-between gap-2">
          <div>
            <span class="text-xs font-semibold text-emerald-700">${f.id}</span>
            <h3 class="text-lg font-bold">${f.name}</h3>
            <div class="mt-0.5 text-sm text-slate-500">${f.street} · Cách bạn ${f.dist}</div>
          </div>
          <span class="tag bg-amber-50 text-amber-700">★ ${f.rating}</span>
        </div>
        <div class="mt-3 flex flex-wrap gap-1.5">
          ${f.sports.map(s => `<span class="tag bg-emerald-50 text-emerald-700">${s.label}</span>`).join('')}
        </div>
      </div>
      <div class="mt-4 flex items-center justify-between border-t border-slate-100 pt-3">
        <div>
          <span class="text-xs text-slate-400">Tổng ${f.courtCount} sân · Giá khung giờ:</span>
          <div class="font-bold text-slate-800">${f.priceRange}</div>
        </div>
        <button class="btn !py-2" onclick="selectFacilityForSchedule('${f.id}')">Xem lịch trống (${f.courtCount} sân)</button>
      </div>
    </div>
  `).join('');
}

function switchAdminSubTab(tabId, chipEl) {
  if (chipEl) selectChip(chipEl);
  document.querySelectorAll('.admin-subtab').forEach(el => {
    el.classList.toggle('hidden', el.id !== tabId);
  });
}
