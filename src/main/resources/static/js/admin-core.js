/**
 * admin-core.js – Điều hướng Cổng Quản trị (Admin Portal - /admin)
 * Dành riêng cho Quản trị viên, Nhân viên trực quầy và Chủ cơ sở.
 */
const $ = s => document.querySelector(s);

const adminPages = [
  {
    id: 'dash',
    label: 'Báo cáo & Thống kê',
    icon: 'bar-chart-3',
    roles: ['ALL', 'ADMIN', 'OWNER'],
    title: 'Thống kê Doanh thu & Hiệu suất sân',
    desc: 'Theo dõi tổng số lượt đặt, doanh thu và tỷ lệ sử dụng sân theo khung giờ'
  },
  {
    id: 'staff',
    label: 'Quầy trực sân (POS)',
    icon: 'monitor',
    roles: ['ALL', 'STAFF', 'OWNER', 'ADMIN'],
    title: 'Quầy nhân viên trực sân (POS)',
    desc: 'Tiếp nhận khách check-in, thanh toán tại quầy và điều phối sân trực tiếp'
  },
  {
    id: 'admin',
    label: 'Quản lý Sân & Giá',
    icon: 'building-2',
    roles: ['ALL', 'ADMIN', 'OWNER'],
    title: 'Quản lý Cơ sở, Sân & Bảng giá',
    desc: 'Cập nhật danh sách sân, thiết lập bảng giá theo khung giờ và lịch bảo trì'
  }
];

let currentAdminRole = localStorage.getItem('sb_admin_role') || 'ADMIN';

function renderAdminNav() {
  const navEl = $('#nav');
  if (!navEl) return;
  const currentTab = location.hash.slice(1) || window.INITIAL_TAB || 'dash';

  const visible = adminPages.filter(p => p.roles.includes('ALL') || p.roles.includes(currentAdminRole));
  navEl.innerHTML = visible.map(p => {
    const isActive = p.id === currentTab;
    const activeClass = isActive
      ? 'bg-emerald-600 text-white shadow-sm font-semibold'
      : 'text-slate-600 hover:text-emerald-700 hover:bg-emerald-50/70 font-medium';
    const iconColor = isActive ? 'text-white' : 'text-slate-400 group-hover:text-emerald-600';

    return `
      <a href="#${p.id}" data-p="${p.id}" class="group rounded-xl py-2.5 px-3.5 flex items-center gap-3 text-sm transition cursor-pointer ${activeClass}">
        <i data-lucide="${p.icon}" class="h-4 w-4 shrink-0 ${iconColor}"></i>
        <span class="truncate">${p.label}</span>
      </a>
    `;
  }).join('');

  if (window.lucide) lucide.createIcons();
  routeAdmin();
}

function changeAdminRole(r) {
  currentAdminRole = r;
  localStorage.setItem('sb_admin_role', r);
  const roleSelect = $('#adminRoleSelect');
  if (roleSelect) roleSelect.value = r;

  const roleLabels = {
    'ADMIN': 'Quản trị viên',
    'STAFF': 'Nhân viên quầy',
    'OWNER': 'Chủ cơ sở'
  };
  if ($('#adminRoleTag')) $('#adminRoleTag').textContent = roleLabels[r] || r;

  const visible = adminPages.filter(p => p.roles.includes('ALL') || p.roles.includes(r));
  if (visible.length && !visible.some(p => p.id === location.hash.slice(1))) {
    location.hash = visible[0].id;
  }
  renderAdminNav();
}

function go(p) {
  location.hash = p;
}

function routeAdmin() {
  const initialTab = window.INITIAL_TAB || 'dash';
  const h = location.hash.slice(1) || initialTab;

  document.querySelectorAll('.scr').forEach(s => s.classList.toggle('show', s.id === h));

  document.querySelectorAll('#nav a').forEach(a => {
    const isActive = a.dataset.p === h;
    a.classList.toggle('bg-emerald-600', isActive);
    a.classList.toggle('text-white', isActive);
    a.classList.toggle('shadow-sm', isActive);
    a.classList.toggle('font-semibold', isActive);

    a.classList.toggle('text-slate-600', !isActive);
    a.classList.toggle('hover:bg-emerald-50/70', !isActive);
    a.classList.toggle('font-medium', !isActive);

    const icon = a.querySelector('i');
    if (icon) {
      icon.classList.toggle('text-white', isActive);
      icon.classList.toggle('text-slate-400', !isActive);
    }
  });

  const page = adminPages.find(p => p.id === h);
  if (page) {
    if ($('#pageHeaderTitle')) $('#pageHeaderTitle').textContent = page.title;
    if ($('#pageHeaderDesc')) $('#pageHeaderDesc').textContent = page.desc;
  }

  scrollTo(0, 0);
  if (window.lucide) lucide.createIcons();
}

window.addEventListener('hashchange', routeAdmin);

function openModal(id) {
  const el = $('#' + id);
  if (el) el.classList.remove('hidden');
}

function closeModal(id) {
  const el = $('#' + id);
  if (el) el.classList.add('hidden');
}

function selectChip(el) {
  el.parentNode.querySelectorAll('.chip').forEach(c => c.classList.remove('on'));
  el.classList.add('on');
}

// Khôi phục trạng thái nhân sự
window.addEventListener('DOMContentLoaded', () => {
  const savedRole = localStorage.getItem('sb_role');
  if (savedRole && (savedRole === 'ADMIN' || savedRole === 'STAFF' || savedRole === 'OWNER')) {
    currentAdminRole = savedRole;
  } else {
    currentAdminRole = 'ADMIN';
  }

  const roleSelect = $('#adminRoleSelect');
  if (roleSelect) roleSelect.value = currentAdminRole;

  const savedName = localStorage.getItem('sb_name') || 'Ban Quản Trị HUIT';
  if ($('#adminUserName')) $('#adminUserName').textContent = savedName;
  if ($('#adminAvatarChar')) $('#adminAvatarChar').textContent = savedName.charAt(0).toUpperCase();

  const roleLabels = {
    'ADMIN': 'Quản trị viên',
    'STAFF': 'Nhân viên quầy',
    'OWNER': 'Chủ cơ sở'
  };
  if ($('#adminRoleTag')) $('#adminRoleTag').textContent = roleLabels[currentAdminRole] || currentAdminRole;

  renderAdminNav();
});
