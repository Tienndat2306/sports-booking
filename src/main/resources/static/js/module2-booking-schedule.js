/**
 * module2-booking-schedule.js
 * Phụ trách Module 2: Lưới 17 khung giờ (Cassandra slot_state_by_court_day),
 * Giữ chỗ tạm 5 phút (Redis hold_slots.lua), Danh sách chờ Waitlist (UC13),
 * Thanh toán cọc & hoàn hủy (UC11, UC12, UC15), Đánh giá sân (UC19).
 */
let schedFac = 'FAC01';
let schedSport = 'ALL';
const slotStates = {
  'FAC01-FB-01': { 13: 'booked', 14: 'booked', 9: 'held', 10: 'held' },
  'FAC01-BM-02': { 3: 'mt', 4: 'mt', 13: 'booked' },
  'FAC01-VB-01': { 12: 'booked' },
  'FAC02-FB-01': { 13: 'booked', 14: 'booked' }
};
let sel = new Set(['FAC01-FB-02|13', 'FAC01-FB-02|14']);
let pendingWaitlistSlot = '';
let tm;

function selectFacilityForSchedule(facId) {
  const selEl = $('#schedFacSelect');
  if (selEl) selEl.value = facId;
  changeScheduleFacility(facId);
  go('schedule');
}

function changeScheduleFacility(facId) {
  schedFac = facId;
  schedSport = 'ALL';
  const f = facilitiesData.find(x => x.id === facId);
  if (f && $('#schedFacAddr')) {
    $('#schedFacAddr').textContent = `${f.street} · Giờ mở cửa: 05:00 – 22:00`;
  }
  renderScheduleSportTabs();
  grid();
}

function renderScheduleSportTabs() {
  const el = $('#schedSportTabs');
  if (!el) return;
  const sports = schedFac === 'FAC01'
    ? [['ALL', 'Tất cả (7 sân)'], ['FOOTBALL5', 'Bóng đá 5v5 (3)'], ['BADMINTON', 'Cầu lông (3)'], ['VOLLEYBALL', 'Bóng chuyền (1)']]
    : [['ALL', 'Tất cả (3 sân bóng đá)']];
  el.innerHTML = sports.map(s =>
    `<span class="chip ${schedSport === s[0] ? 'on' : ''}" onclick="schedSport='${s[0]}';renderScheduleSportTabs();grid()">${s[1]}</span>`
  ).join('');
}

function renderDaysBar() {
  const daysEl = $('#days');
  if (!daysEl) return;
  daysEl.innerHTML = ['T6 20/11', 'T7 21/11', 'CN 22/11', 'T2 23/11', 'T3 24/11', 'T4 25/11', 'T5 26/11']
    .map((d, i) => `<span class="chip ${i ? '' : 'on'}" onclick="selectChip(this)">${d}</span>`).join('');
}

function grid() {
  const gridEl = $('#grid');
  if (!gridEl) return;
  const courts = allCourts.filter(c => c.fac === schedFac && (schedSport === 'ALL' || c.sport === schedSport));
  let h = '<table class="w-full border-separate" style="border-spacing:4px"><thead><tr><th>Sân / Khung giờ</th>' +
    Array.from({ length: 17 }, (_, i) => `<th class="text-center text-xs font-semibold ${i >= 12 ? 'text-emerald-700' : ''}">${String(5 + i).padStart(2, '0')}:00</th>`).join('') +
    '</tr></thead><tbody>';

  courts.forEach(c => {
    h += `<tr><td class="!border-0 whitespace-nowrap pr-3"><div class="font-bold text-slate-800">${c.id} · ${c.name}</div><div class="text-[11px] text-slate-400">${c.attr}</div></td>`;
    for (let i = 0; i < 17; i++) {
      const hourStr = String(5 + i).padStart(2, '0') + ':00';
      const k = `${c.id}|${i}`;
      const st = (slotStates[c.id] || {})[i];
      const cl = st || (sel.has(k) ? 'sel' : '');
      const price = i >= 12 ? c.peak : c.off;
      if (st === 'booked' || st === 'held') {
        h += `<td class="!border-0 !p-0"><button title="Click để đăng ký Danh sách chờ (Waitlist UC13)" class="slot w-full ${cl}" onclick="openWaitlist('${c.id}','${hourStr}','${st}')">${st === 'held' ? 'Giữ' : 'Đã đặt'}</button></td>`;
      } else if (st === 'mt') {
        h += `<td class="!border-0 !p-0"><button class="slot w-full mt" disabled>Bảo trì</button></td>`;
      } else {
        h += `<td class="!border-0 !p-0"><button class="slot w-full ${cl}" onclick="tg('${k}','${c.id}')">${price}k</button></td>`;
      }
    }
    h += '</tr>';
  });
  gridEl.innerHTML = h + '</tbody></table>';

  let total = 0, chosenCourt = '', chosenHours = [];
  sel.forEach(k => {
    const [cid, idx] = k.split('|');
    const cObj = allCourts.find(x => x.id === cid);
    if (cObj) {
      chosenCourt = cid;
      chosenHours.push(String(5 + Number(idx)).padStart(2, '0') + ':00');
      total += (Number(idx) >= 12 ? cObj.peak : cObj.off) * 1000;
    }
  });
  const dep = Math.round(total * 0.3);
  if ($('#sumCourt')) $('#sumCourt').textContent = chosenCourt ? `Sân: ${chosenCourt} · Ngày 20/11/2026 (${chosenHours.join(', ')})` : 'Chưa chọn khung giờ';
  if ($('#sum')) $('#sum').textContent = sel.size ? `Đã chọn ${sel.size} khung giờ` : 'Hãy click chọn các khung giờ trống trên lưới';
  if ($('#tot')) $('#tot').textContent = total.toLocaleString('vi-VN') + 'đ';
  if ($('#dep')) $('#dep').textContent = `Cần cọc (30%): ${dep.toLocaleString('vi-VN')}đ`;
}

function tg(k, cid) {
  [...sel].forEach(existing => { if (!existing.startsWith(cid + '|')) sel.delete(existing); });
  sel.has(k) ? sel.delete(k) : sel.add(k);
  grid();
}

function openWaitlist(courtId, hourStr, st) {
  pendingWaitlistSlot = `${courtId}:${hourStr}`;
  $('#waitlistDesc').innerHTML = `Khung <b>${hourStr}</b> ngày <b>20/11/2026</b> tại sân <b>${courtId}</b> đang ở trạng thái <b>${st === 'held' ? 'Đang giữ chỗ' : 'Đã đặt'}</b>. Bạn có muốn đăng ký vào danh sách chờ ưu tiên không?`;
  openModal('waitlistModal');
}

function confirmWaitlist() {
  closeModal('waitlistModal');
  alert('Đã đăng ký vào danh sách chờ thành công!\nHệ thống sẽ gửi thông báo ngay khi khung giờ trống.');
}

function holdAndCheckout() {
  if (!sel.size) { alert('Vui lòng chọn ít nhất 1 khung giờ trống!'); return; }
  go('checkout');
}

function startCd() {
  clearInterval(tm);
  let s = 300;
  const f = () => {
    if ($('#cd')) {
      $('#cd').textContent = String(Math.floor(s / 60)).padStart(2, '0') + ':' + String(s % 60).padStart(2, '0');
    }
    if (s-- <= 0) { clearInterval(tm); go('schedule'); }
  };
  f();
  tm = setInterval(f, 1000);
}

function confirmBookingSuccess() {
  clearInterval(tm);
  alert('Đặt sân thành công!\nĐơn đặt sân đã được xác nhận.');
  go('bookings');
}

function cancelHoldAndBack() {
  clearInterval(tm);
  go('schedule');
}

const myBookingsData = [
  { id: '01JB0001', code: 'BK-20261120-0001', court: 'FAC01-FB-01 · Sân bóng đá 1', date: '20/11/2026', slots: '18:00, 19:00', total: '700.000đ', paid: '210.000đ (Đã cọc 30%)', status: 'CONFIRMED' },
  { id: '01JB0002', code: 'BK-20261112-0007', court: 'FAC01-BM-01 · Sân cầu lông 1', date: '12/11/2026', slots: '19:00', total: '120.000đ', paid: '120.000đ (Đã thanh toán đủ)', status: 'COMPLETED' },
  { id: '01JB0003', code: 'BK-20261110-0004', court: 'FAC02-FB-01 · Sân bóng đá 2A', date: '10/11/2026', slots: '17:00', total: '320.000đ', paid: 'Hoàn cọc 100% (96.000đ)', status: 'CANCELED' }
];

function renderMyBookings(filter = 'ALL') {
  const el = $('#myBookingList');
  if (!el) return;
  const list = myBookingsData.filter(b => filter === 'ALL' || b.status === filter);
  el.innerHTML = list.map(b => `
    <div class="card flex flex-wrap items-center justify-between gap-4">
      <div>
        <div class="flex items-center gap-2">
          <b class="text-base">${b.court}</b>
          <span class="tag ${b.status === 'CONFIRMED' ? 'bg-emerald-50 text-emerald-700' : b.status === 'COMPLETED' ? 'bg-blue-50 text-blue-700' : 'bg-rose-50 text-rose-700'}">${b.status}</span>
        </div>
        <div class="mt-1 text-sm text-slate-500">Mã đơn: <b>${b.code}</b> · Ngày: ${b.date} · Khung: ${b.slots}</div>
        <div class="mt-1 text-sm">Tổng: <b>${b.total}</b> · ${b.paid}</div>
      </div>
      <div class="flex flex-wrap gap-2">
        <button class="btn2 !py-1.5 text-xs" onclick="showBookingTimeline('${b.code}')">Xem lịch sử đơn</button>
        ${b.status === 'CONFIRMED' ? `
          <button class="btn2 !py-1.5 text-xs" onclick="go('schedule')">Đổi lịch</button>
          <button class="btn2 !py-1.5 text-xs" onclick="openModal('createMatchModal')">Tạo kèo giao lưu</button>
          <button class="btn2 !py-1.5 text-xs !text-rose-600" onclick="alert('Đã hủy đơn thành công! Tiền cọc được hoàn lại theo chính sách.')">Hủy đơn</button>
        ` : ''}
        ${b.status === 'COMPLETED' ? `
          <button class="btn2 !py-1.5 text-xs" onclick="showReviewPrompt('${b.code}')">★ Đánh giá sân</button>
        ` : ''}
      </div>
    </div>
  `).join('');
}

function filterMyBookings(st, el) {
  selectChip(el);
  renderMyBookings(st);
}

function showBookingTimeline(code) {
  $('#eventModalTitle').textContent = `Chi tiết lịch sử – Đơn ${code}`;
  $('#eventModalBody').innerHTML = `
    <div class="rounded-xl bg-slate-50 p-3 space-y-2 text-xs">
      <div class="font-bold text-slate-700">Tiến trình xử lý đơn đặt:</div>
      <div>• <b>2026-11-16 09:00</b> – Tạo yêu cầu giữ chỗ 5 phút thành công</div>
      <div>• <b>2026-11-16 09:03</b> – Xác nhận thanh toán cọc & khóa lịch thi đấu</div>
    </div>
    <div class="rounded-xl bg-emerald-50 p-3 space-y-1 text-xs text-emerald-900">
      <div class="font-bold">Chi tiết giao dịch:</div>
      <div>• Mã GD: <b>01JP0001</b> · Loại: <b>Đặt cọc (30%)</b> · Phương thức: <b>Ví điện tử</b> · Số tiền: <b>210.000đ</b> · Trạng thái: <b>Thành công</b></div>
    </div>
  `;
  openModal('eventModal');
}

function showReviewPrompt(code) {
  const cmt = prompt(`Nhập nhận xét đánh giá (1-5 sao) cho đơn ${code}:`, `Mặt sân rất tốt, đèn chiếu sáng rõ!`);
  if (cmt) alert('Cảm ơn bạn đã gửi đánh giá sân thành công!');
}
