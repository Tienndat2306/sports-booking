/**
 * module3-community-match.js
 * Phụ trách Module 3: Cộng đồng & Sàn ghép kèo thể thao trên Neo4j
 * (Player, Match, Sport, Court; FRIEND, FOLLOWS, PLAYS, PLAYED_WITH, BOOKED).
 */
const matchesData = [
  { id: '01JMATCH01', title: 'Giao lưu bóng đá 5v5 tối thứ Sáu (Cần thêm 3 bạn)', sport: 'FOOTBALL5', court: 'FAC01-FB-01 · Sân bóng đá 1', time: '20/11/2026 · 18:00 – 20:00', level: '2 – 4', cur: 7, max: 10, status: 'OPEN', host: 'Nguyễn Văn An' },
  { id: '01JMATCH02', title: 'Giao lưu Cầu lông đôi nam nữ cuối tuần', sport: 'BADMINTON', court: 'FAC01-BM-01 · Sân cầu lông 1', time: '21/11/2026 · 09:00 – 11:00', level: '2 – 3', cur: 4, max: 4, status: 'FULL', host: 'Hoàng Thảo Em' }
];

function renderMatches(sportFilter = 'ALL') {
  const el = $('#matchFeed');
  if (!el) return;
  const list = matchesData.filter(m => sportFilter === 'ALL' || m.sport === sportFilter);
  el.innerHTML = list.map(m => `
    <div class="card">
      <div class="flex flex-wrap items-center justify-between gap-2">
        <div>
          <span class="text-xs font-semibold text-emerald-700">Kèo giao lưu</span>
          <h3 class="font-bold text-base">${m.title}</h3>
        </div>
        <span class="tag ${m.status === 'OPEN' ? 'bg-emerald-50 text-emerald-700' : 'bg-slate-100 text-slate-600'}">${m.status}</span>
      </div>
      <div class="mt-1 text-sm text-slate-500">Sân: ${m.court} · ${m.time} · Trình độ yêu cầu: Cấp ${m.level}</div>
      <div class="mt-3 h-2 rounded-full bg-slate-100"><div class="h-2 rounded-full bg-emerald-500" style="width:${Math.round(m.cur / m.max * 100)}%"></div></div>
      <div class="mt-3 flex flex-wrap items-center justify-between gap-2 text-sm">
        <span class="text-slate-600">Chủ kèo: <b>${m.host}</b> · Đã tham gia: <b>${m.cur}/${m.max} người</b></span>
        <div class="flex gap-2">
          ${m.status === 'OPEN' ? `<button class="btn !py-1.5 text-xs" onclick="alert('Đã gửi yêu cầu tham gia kèo thành công!')">Xin tham gia</button>` : ''}
        </div>
      </div>
    </div>
  `).join('');
}

function filterMatches(sp, el) {
  selectChip(el);
  renderMatches(sp);
}

function submitNewMatch() {
  matchesData.unshift({
    id: '01JMATCH0' + (matchesData.length + 1),
    title: $('#mTitle').value,
    sport: $('#mSport').value,
    court: $('#mCourt').value,
    time: '21/11/2026 · 18:00 – 20:00',
    level: $('#mLevel').value,
    cur: 1,
    max: 10,
    status: 'OPEN',
    host: 'Nguyễn Văn An'
  });
  closeModal('createMatchModal');
  renderMatches();
}

function renderTeammates() {
  const el = $('#mates');
  if (!el) return;
  el.innerHTML = [
    ['01JU0002', 'Trần Thị Bình', 'Bạn bè · Cùng chơi Bóng đá 5 người'],
    ['01JU0003', 'Lê Hoàng Cường', 'Bạn của bạn · Đã chơi cùng 4 lần'],
    ['01JU0004', 'Phạm Minh Dũng', 'Cùng cơ sở quen Tân Phú · Cầu lông cấp 3']
  ].map(p => `
    <div class="flex items-center gap-3">
      <div class="grid h-9 w-9 place-items-center rounded-full bg-emerald-50 font-bold text-emerald-700">${p[1].split(' ').pop()[0]}</div>
      <div class="flex-1">
        <div class="font-semibold">${p[1]} <span class="text-[11px] text-slate-400">(${p[0]})</span></div>
        <div class="text-xs text-slate-500">${p[2]}</div>
      </div>
      <button class="btn2 !px-2.5 !py-1 text-xs" onclick="this.textContent='Đã kết bạn'">Kết bạn</button>
    </div>
  `).join('');
}
