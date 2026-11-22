/**
 * module4-staff-analytics-audit.js
 * Phụ trách Module 4: Quầy nhân viên POS (Đặt tại quầy, Check-in, Xử lý khách No-Show),
 * Thống kê doanh thu & hiệu suất (Cassandra Counters revenue_daily, usage_by_hour)
 * và Nhật ký kiểm toán (audit_by_day_type, audit_by_user).
 */
const staffBookings = [
  { code: 'BK-20261120-0001', user: 'Nguyễn Văn An (0901000001)', noShow: 1, court: 'FAC01-FB-01', slot: '18:00 – 20:00', source: 'ONLINE', due: '490.000đ', status: 'CONFIRMED' },
  { code: 'BK-20261120-0002', user: 'Trần Thị Bình (0901000002)', noShow: 0, court: 'FAC01-BM-01', slot: '19:00 – 20:00', source: 'ONLINE', due: '0đ', status: 'CHECKED_IN' },
  { code: 'BK-20261120-0003', user: 'Phạm Minh Dũng (0901000004)', noShow: 2, court: 'FAC01-FB-02', slot: '17:00 – 18:00', source: 'COUNTER', due: '245.000đ', status: 'CONFIRMED' }
];

function renderStaffTable() {
  const rowsEl = $('#rows');
  if (!rowsEl) return;
  const q = ($('#staffSearch') ? $('#staffSearch').value : '').toLowerCase();
  const list = staffBookings.filter(r => r.code.toLowerCase().includes(q) || r.user.toLowerCase().includes(q));
  rowsEl.innerHTML = list.map((r, idx) => `
    <tr>
      <td class="font-bold">${r.code}</td>
      <td>
        <div>${r.user}</div>
        ${r.noShow > 0 ? `<span class="tag bg-amber-50 text-amber-700">Vắng: ${r.noShow} lần</span>` : ''}
      </td>
      <td>${r.court}</td>
      <td>${r.slot}</td>
      <td><span class="tag bg-slate-100 text-slate-700">${r.source}</span></td>
      <td class="font-semibold ${r.due === '0đ' ? 'text-emerald-700' : 'text-amber-700'}">Còn thiếu: ${r.due}</td>
      <td><span class="tag ${r.status === 'CHECKED_IN' ? 'bg-emerald-50 text-emerald-700' : 'bg-blue-50 text-blue-700'}">${r.status}</span></td>
      <td class="text-right space-x-1">
        ${r.status === 'CONFIRMED' ? `<button class="btn !py-1 !px-2.5 text-xs" onclick="staffBookings[${idx}].status='CHECKED_IN';renderStaffTable()">Check-in</button>` : ''}
        ${r.due !== '0đ' ? `<button class="btn2 !py-1 !px-2.5 text-xs" onclick="staffBookings[${idx}].due='0đ';renderStaffTable()">Thu đủ tiền</button>` : ''}
      </td>
    </tr>
  `).join('');
}

function handleNoShow(excused) {
  if (excused) {
    alert('Đã đánh dấu miễn trừ và hoàn cọc cho khách!');
  } else {
    alert('Đã cập nhật trạng thái vắng mặt (No-Show) và giải phóng khung giờ!');
  }
  if ($('#noshowBanner')) $('#noshowBanner').classList.add('hidden');
}

function submitWalkinBooking() {
  const name = $('#wkName').value || 'Khách vãng lai tại quầy';
  const phone = $('#wkPhone').value || 'Không SĐT (isWalkIn=true)';
  staffBookings.unshift({
    code: 'BK-20261120-0099',
    user: `${name} (${phone})`,
    noShow: 0,
    court: $('#wkCourt').value,
    slot: $('#wkSlot').value,
    source: 'COUNTER',
    due: '0đ',
    status: 'CHECKED_IN'
  });
  closeModal('walkinModal');
  renderStaffTable();
}

const auditData = [
  { time: '2026-11-17 09:03:00', type: 'BOOKING_CREATE', user: '01JU0001 (Nguyễn Văn An)', role: 'CUSTOMER', entity: 'BOOKING · 01JB0001', detail: 'Đặt sân FAC01-FB-01 ngày 2026-11-20 (18:00–20:00)', ip: '127.0.0.1' },
  { time: '2026-11-17 09:03:02', type: 'PAYMENT', user: '01JU0001 (Nguyễn Văn An)', role: 'CUSTOMER', entity: 'PAYMENT · 01JP0001', detail: 'Thanh toán cọc DEPOSIT 210.000đ qua EWALLET', ip: '127.0.0.1' },
  { time: '2026-11-17 08:30:11', type: 'DATA_CHANGE', user: '01JU0020 (Đặng Quốc Chủ)', role: 'OWNER', entity: 'POLICY · POL-FAC01-v1', detail: 'Cập nhật chính sách hoàn hủy cơ sở FAC01', ip: '192.168.1.15' },
  { time: '2026-11-17 08:00:00', type: 'DATA_CHANGE', user: '01JU0010 (Võ Thanh Nhân)', role: 'STAFF', entity: 'MAINTENANCE · 01JM0001', detail: 'Khóa bảo trì sân FAC01-BM-02 ngày 2026-11-22 (08:00–10:00)', ip: '192.168.1.10' },
  { time: '2026-11-17 07:45:20', type: 'LOGIN', user: '01JU0099 (Quản Trị Hệ Thống)', role: 'ADMIN', entity: 'USER · 01JU0099', detail: 'Đăng nhập hệ thống quản trị thành công', ip: '127.0.0.1' }
];

function renderAnalyticsCharts() {
  if ($('#kpi')) {
    $('#kpi').innerHTML = [
      ['Tổng doanh thu', '48.200.000đ', '+14% so với tháng trước'],
      ['Tổng hoàn tiền', '3.150.000đ', '9 đơn hủy đúng hạn'],
      ['Tổng số đơn đặt', '312 đơn', 'Online 82% · Quầy 18%'],
      ['Tỷ lệ lấp đầy', '68,4%', '3.488 / 5.100 khung giờ']
    ].map(k => `
      <div class="card">
        <div class="text-xs font-medium text-slate-500">${k[0]}</div>
        <div class="mt-1 text-2xl font-bold text-slate-900">${k[1]}</div>
        <div class="mt-1 text-xs text-emerald-700">${k[2]}</div>
      </div>
    `).join('');
  }
  if ($('#rev')) {
    $('#rev').innerHTML = [40, 55, 35, 60, 48, 80, 95, 50, 45, 58, 62, 70, 88, 100, 76, 84]
      .map(v => `<div class="flex-1 rounded-t-md bg-emerald-500 hover:bg-emerald-600 transition" style="height:${v}%" title="Doanh thu ngày"></div>`).join('');
  }
  if ($('#hrs')) {
    $('#hrs').innerHTML = Array.from({ length: 17 }, (_, i) =>
      `<div class="flex-1 rounded-t-md ${i >= 12 ? 'bg-emerald-600' : 'bg-emerald-200'}" style="height:${[12, 18, 28, 22, 15, 10, 12, 15, 18, 22, 30, 45, 75, 92, 100, 88, 55][i]}%" title="${5 + i}:00"></div>`
    ).join('');
  }
}

function renderAuditLogs() {
  const el = $('#auditRows');
  if (!el) return;
  const f = $('#auditTypeFilter') ? $('#auditTypeFilter').value : 'ALL';
  const list = auditData.filter(a => f === 'ALL' || a.type === f);
  el.innerHTML = list.map(a => `
    <tr>
      <td class="text-xs text-slate-500 font-mono">${a.time}</td>
      <td><span class="tag bg-slate-100 text-slate-700">${a.type}</span></td>
      <td class="font-medium">${a.user}</td>
      <td><span class="text-xs font-semibold text-emerald-700">${a.role}</span></td>
      <td class="text-xs font-mono">${a.entity}</td>
      <td class="text-xs text-slate-600">${a.detail}</td>
      <td class="text-xs text-slate-400">${a.ip}</td>
    </tr>
  `).join('');
}
