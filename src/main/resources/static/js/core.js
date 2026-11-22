/**
 * core.js – Điều hướng Cổng Khách hàng (User Portal - /)
 * Tông màu Trắng & Xanh Lá (Emerald), giao diện thân thiện, chỉ hiển thị tính năng người chơi.
 */
const $ = s => document.querySelector(s);

const userPages = [
  {
    id: 'home',
    label: 'Tìm kiếm sân',
    icon: 'layout-grid',
    title: 'Tìm kiếm sân thể thao',
    desc: 'Khám phá các cụm sân chất lượng, xem bảng giá và tìm sân gần bạn nhất'
  },
  {
    id: 'schedule',
    label: 'Lịch sân & Giữ chỗ',
    icon: 'calendar',
    title: 'Xem lịch sân & Giữ chỗ',
    desc: 'Lựa chọn khung giờ thi đấu phù hợp và giữ chỗ tiện lợi trong 5 phút'
  },
  {
    id: 'bookings',
    label: 'Đơn đặt của tôi',
    icon: 'receipt',
    title: 'Lịch sử & Đơn đặt sân',
    desc: 'Quản lý danh sách sân đã đặt, kiểm tra trạng thái thanh toán và đổi lịch'
  },
  {
    id: 'match',
    label: 'Ghép kèo giao lưu',
    icon: 'trophy',
    title: 'Cộng đồng & Ghép kèo thể thao',
    desc: 'Tạo kèo giao lưu, tìm kiếm đối thủ và giao lưu cùng bạn chơi cùng trình độ'
  },
  {
    id: 'auth',
    label: 'Hồ sơ cá nhân',
    icon: 'user-cog',
    title: 'Hồ sơ cá nhân & Tài khoản',
    desc: 'Quản lý thông tin tài khoản, cập nhật trình độ và môn thể thao yêu thích'
  }
];

function renderNav() {
  const navEl = $('#nav');
  if (!navEl) return;
  const currentTab = location.hash.slice(1) || window.INITIAL_TAB || 'home';

  navEl.innerHTML = userPages.map(p => {
    const isActive = (p.id === currentTab) || (currentTab === 'checkout' && p.id === 'schedule');
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
  route();
}

function go(p) {
  location.hash = p;
}

function route() {
  const initialTab = window.INITIAL_TAB || 'home';
  const h = location.hash.slice(1) || initialTab;

  document.querySelectorAll('.scr').forEach(s => s.classList.toggle('show', s.id === h));

  document.querySelectorAll('#nav a').forEach(a => {
    const isActive = a.dataset.p === h || (h === 'checkout' && a.dataset.p === 'schedule');
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

  const page = userPages.find(p => p.id === h);
  if (page) {
    if ($('#pageHeaderTitle')) $('#pageHeaderTitle').textContent = page.title;
    if ($('#pageHeaderDesc')) $('#pageHeaderDesc').textContent = page.desc;
  }

  if (h === 'checkout' && typeof startCd === 'function') startCd();
  scrollTo(0, 0);
  if (window.lucide) lucide.createIcons();
}

window.addEventListener('hashchange', route);

function openModal(id) {
  const el = $('#' + id);
  if (el) el.classList.remove('hidden');
}

function closeModal(id) {
  const el = $('#' + id);
  if (el) el.classList.add('hidden');
}

function toggleNotif() {
  const box = $('#notifBox');
  if (box) box.classList.toggle('hidden');
}

function selectChip(el) {
  el.parentNode.querySelectorAll('.chip').forEach(c => c.classList.remove('on'));
  el.classList.add('on');
}

/* =====================================================================
 * XỬ LÝ ĐĂNG NHẬP / ĐĂNG KÝ BẰNG GOOGLE (UC01, UC02)
 * ===================================================================== */

async function executeGoogleAuth(email, name, role) {
  try {
    const res = await fetch('/api/v1/auth/google', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-User-Id': localStorage.getItem('sb_user_id') || '01JU0001'
      },
      body: JSON.stringify({
        email: email,
        name: name,
        role: role || 'CUSTOMER'
      })
    });

    const data = await res.json();
    if (data.success && data.data) {
      const auth = data.data;
      localStorage.setItem('sb_user_id', auth.userId);
      localStorage.setItem('sb_token', auth.token);
      localStorage.setItem('sb_name', auth.fullName);
      localStorage.setItem('sb_role', auth.role);
      localStorage.setItem('sb_email', email);

      updateAuthUi(auth);
      alert(`Đăng nhập Google thành công!\nChào mừng: ${auth.fullName} (${auth.role})`);
    } else {
      alert('Đăng nhập thất bại: ' + (data.message || 'Lỗi không xác định'));
    }
  } catch (err) {
    console.error('Lỗi gọi API Google Auth:', err);
    alert('Lỗi kết nối máy chủ API: ' + err.message);
  }
}

function loginWithGooglePrompt() {
  const email = prompt('Nhập địa chỉ Gmail của bạn để đăng nhập:', 'an.nguyen@gmail.com');
  if (email && email.trim()) {
    const defaultName = email.split('@')[0];
    executeGoogleAuth(email.trim(), defaultName, 'CUSTOMER');
  }
}

function loginWithCustomGoogleEmail() {
  const input = $('#customGoogleEmail');
  if (!input || !input.value.trim()) {
    alert('Vui lòng nhập địa chỉ email Google hợp lệ!');
    return;
  }
  const email = input.value.trim();
  const name = email.split('@')[0];
  executeGoogleAuth(email, name, 'CUSTOMER');
}

function handleGoogleLogout() {
  localStorage.removeItem('sb_user_id');
  localStorage.removeItem('sb_token');
  localStorage.removeItem('sb_name');
  localStorage.removeItem('sb_role');
  localStorage.removeItem('sb_email');

  const statusBox = $('#authStatusBox');
  if (statusBox) statusBox.classList.add('hidden');

  const userLabel = $('#currentUserLabel');
  if (userLabel) userLabel.textContent = 'Khách vãng lai';
  if ($('#currentUserRoleTag')) $('#currentUserRoleTag').textContent = 'Chưa đăng nhập';
  if ($('#userAvatarChar')) $('#userAvatarChar').textContent = '?';

  alert('Đã đăng xuất tài khoản!');
}

function updateAuthUi(auth) {
  const statusBox = $('#authStatusBox');
  if (statusBox) statusBox.classList.remove('hidden');

  if ($('#authUserId')) $('#authUserId').textContent = auth.userId || '--';
  if ($('#authUserName')) $('#authUserName').textContent = auth.fullName || '--';
  if ($('#authUserEmail')) $('#authUserEmail').textContent = localStorage.getItem('sb_email') || auth.phone || '--';
  if ($('#authUserRole')) $('#authUserRole').textContent = auth.role || 'CUSTOMER';

  const userLabel = $('#currentUserLabel');
  if (userLabel && auth.fullName) userLabel.textContent = auth.fullName;

  if ($('#currentUserRoleTag')) {
    const roleLabels = {
      'CUSTOMER': 'Khách hàng',
      'STAFF': 'Nhân viên',
      'OWNER': 'Chủ cơ sở',
      'ADMIN': 'Quản trị viên'
    };
    $('#currentUserRoleTag').textContent = roleLabels[auth.role] || auth.role;
  }

  if ($('#userAvatarChar') && auth.fullName) {
    $('#userAvatarChar').textContent = auth.fullName.charAt(0).toUpperCase();
  }
}

// Khôi phục trạng thái khi người dùng tải lại trang
window.addEventListener('DOMContentLoaded', () => {
  const savedUserId = localStorage.getItem('sb_user_id');
  if (savedUserId) {
    updateAuthUi({
      userId: savedUserId,
      fullName: localStorage.getItem('sb_name') || 'Nguyễn Văn An',
      role: 'CUSTOMER'
    });
  } else {
    // Khách mẫu mặc định
    updateAuthUi({
      userId: '01JU0001',
      fullName: 'Nguyễn Văn An',
      role: 'CUSTOMER'
    });
  }
  renderNav();
});
