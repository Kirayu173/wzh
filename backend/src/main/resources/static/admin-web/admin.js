const state = {
  baseUrl: '',
  token: '',
  user: null,
  sameOrigin: true,
  view: 'dashboard',
  products: { page: 1, size: 10, status: '', keyword: '', total: 0 },
  orders: { page: 1, size: 10, status: '', keyword: '', total: 0 },
  traces: { page: 1, size: 10, keyword: '', total: 0 },
  editingProductId: null,
  editingTraceId: null,
  productMap: new Map(),
  orderMap: new Map(),
  traceMap: new Map(),
  qrUrl: null
};

const el = {};

function initElements() {
  el.notice = document.getElementById('notice');
  el.loginScreen = document.getElementById('loginScreen');
  el.adminShell = document.getElementById('adminShell');
  el.loginForm = document.getElementById('loginForm');
  el.baseUrlInput = document.getElementById('baseUrlInput');
  el.usernameInput = document.getElementById('usernameInput');
  el.passwordInput = document.getElementById('passwordInput');
  el.userAvatar = document.getElementById('userAvatar');
  el.userName = document.getElementById('userName');
  el.userRole = document.getElementById('userRole');
  el.logoutBtn = document.getElementById('logoutBtn');
  el.refreshBtn = document.getElementById('refreshBtn');
  el.viewTitle = document.getElementById('viewTitle');
  el.statProducts = document.getElementById('statProducts');
  el.statOrders = document.getElementById('statOrders');
  el.statTraces = document.getElementById('statTraces');
  el.productTable = document.getElementById('productTable');
  el.productKeyword = document.getElementById('productKeyword');
  el.productStatus = document.getElementById('productStatus');
  el.productSearchBtn = document.getElementById('productSearchBtn');
  el.productResetBtn = document.getElementById('productResetBtn');
  el.productPrev = document.getElementById('productPrev');
  el.productNext = document.getElementById('productNext');
  el.productPageInfo = document.getElementById('productPageInfo');
  el.productForm = document.getElementById('productForm');
  el.productFormTitle = document.getElementById('productFormTitle');
  el.productFormCancel = document.getElementById('productFormCancel');
  el.productFields = {
    name: el.productForm.querySelector('[name=\"name\"]'),
    price: el.productForm.querySelector('[name=\"price\"]'),
    stock: el.productForm.querySelector('[name=\"stock\"]'),
    status: el.productForm.querySelector('[name=\"status\"]'),
    origin: el.productForm.querySelector('[name=\"origin\"]'),
    coverUrl: el.productForm.querySelector('[name=\"coverUrl\"]'),
    description: el.productForm.querySelector('[name=\"description\"]')
  };
  el.orderTable = document.getElementById('orderTable');
  el.orderKeyword = document.getElementById('orderKeyword');
  el.orderStatus = document.getElementById('orderStatus');
  el.orderSearchBtn = document.getElementById('orderSearchBtn');
  el.orderResetBtn = document.getElementById('orderResetBtn');
  el.orderPrev = document.getElementById('orderPrev');
  el.orderNext = document.getElementById('orderNext');
  el.orderPageInfo = document.getElementById('orderPageInfo');
  el.traceTable = document.getElementById('traceTable');
  el.traceKeyword = document.getElementById('traceKeyword');
  el.traceSearchBtn = document.getElementById('traceSearchBtn');
  el.traceResetBtn = document.getElementById('traceResetBtn');
  el.tracePrev = document.getElementById('tracePrev');
  el.traceNext = document.getElementById('traceNext');
  el.tracePageInfo = document.getElementById('tracePageInfo');
  el.traceForm = document.getElementById('traceForm');
  el.traceFormTitle = document.getElementById('traceFormTitle');
  el.traceFormCancel = document.getElementById('traceFormCancel');
  el.traceFields = {
    productId: el.traceForm.querySelector('[name=\"productId\"]'),
    batchNo: el.traceForm.querySelector('[name=\"batchNo\"]'),
    origin: el.traceForm.querySelector('[name=\"origin\"]'),
    producer: el.traceForm.querySelector('[name=\"producer\"]'),
    harvestDate: el.traceForm.querySelector('[name=\"harvestDate\"]'),
    processInfo: el.traceForm.querySelector('[name=\"processInfo\"]'),
    testOrg: el.traceForm.querySelector('[name=\"testOrg\"]'),
    testDate: el.traceForm.querySelector('[name=\"testDate\"]'),
    testResult: el.traceForm.querySelector('[name=\"testResult\"]'),
    reportUrl: el.traceForm.querySelector('[name=\"reportUrl\"]')
  };
  el.logisticsForm = document.getElementById('logisticsForm');
  el.logisticsFields = {
    traceCode: el.logisticsForm.querySelector('[name=\"traceCode\"]'),
    nodeTime: el.logisticsForm.querySelector('[name=\"nodeTime\"]'),
    location: el.logisticsForm.querySelector('[name=\"location\"]'),
    statusDesc: el.logisticsForm.querySelector('[name=\"statusDesc\"]')
  };
  el.traceQrBtn = document.getElementById('traceQrBtn');
  el.qrResult = document.getElementById('qrResult');
  el.qrImage = document.getElementById('qrImage');
  el.qrDownload = document.getElementById('qrDownload');
  el.settingsForm = document.getElementById('settingsForm');
  el.settingsBaseUrl = document.getElementById('settingsBaseUrl');
  el.modal = document.getElementById('modal');
  el.modalTitle = document.getElementById('modalTitle');
  el.modalBody = document.getElementById('modalBody');
}

function initEvents() {
  document.querySelectorAll('[data-nav]').forEach((button) => {
    button.addEventListener('click', () => showView(button.dataset.nav));
  });

  document.querySelectorAll('[data-jump]').forEach((button) => {
    button.addEventListener('click', () => showView(button.dataset.jump));
  });

  document.querySelectorAll('[data-modal-close]').forEach((button) => {
    button.addEventListener('click', closeModal);
  });

  el.loginForm.addEventListener('submit', onLoginSubmit);
  el.logoutBtn.addEventListener('click', logout);
  el.refreshBtn.addEventListener('click', () => loadView(state.view));

  el.productSearchBtn.addEventListener('click', () => {
    state.products.page = 1;
    state.products.keyword = el.productKeyword.value.trim();
    state.products.status = el.productStatus.value;
    loadProducts();
  });

  el.productResetBtn.addEventListener('click', () => {
    el.productKeyword.value = '';
    el.productStatus.value = '';
    state.products.page = 1;
    state.products.keyword = '';
    state.products.status = '';
    loadProducts();
  });

  el.productPrev.addEventListener('click', () => {
    if (state.products.page > 1) {
      state.products.page -= 1;
      loadProducts();
    }
  });

  el.productNext.addEventListener('click', () => {
    if (state.products.page < totalPages(state.products)) {
      state.products.page += 1;
      loadProducts();
    }
  });

  el.productForm.addEventListener('submit', onProductSubmit);
  el.productFormCancel.addEventListener('click', resetProductForm);
  el.productTable.addEventListener('click', onProductTableClick);

  el.orderSearchBtn.addEventListener('click', () => {
    state.orders.page = 1;
    state.orders.keyword = el.orderKeyword.value.trim();
    state.orders.status = el.orderStatus.value;
    loadOrders();
  });

  el.orderResetBtn.addEventListener('click', () => {
    el.orderKeyword.value = '';
    el.orderStatus.value = '';
    state.orders.page = 1;
    state.orders.keyword = '';
    state.orders.status = '';
    loadOrders();
  });

  el.orderPrev.addEventListener('click', () => {
    if (state.orders.page > 1) {
      state.orders.page -= 1;
      loadOrders();
    }
  });

  el.orderNext.addEventListener('click', () => {
    if (state.orders.page < totalPages(state.orders)) {
      state.orders.page += 1;
      loadOrders();
    }
  });

  el.orderTable.addEventListener('click', onOrderTableClick);

  el.traceSearchBtn.addEventListener('click', () => {
    state.traces.page = 1;
    state.traces.keyword = el.traceKeyword.value.trim();
    loadTraces();
  });

  el.traceResetBtn.addEventListener('click', () => {
    el.traceKeyword.value = '';
    state.traces.page = 1;
    state.traces.keyword = '';
    loadTraces();
  });

  el.tracePrev.addEventListener('click', () => {
    if (state.traces.page > 1) {
      state.traces.page -= 1;
      loadTraces();
    }
  });

  el.traceNext.addEventListener('click', () => {
    if (state.traces.page < totalPages(state.traces)) {
      state.traces.page += 1;
      loadTraces();
    }
  });

  el.traceTable.addEventListener('click', onTraceTableClick);
  el.traceForm.addEventListener('submit', onTraceSubmit);
  el.traceFormCancel.addEventListener('click', resetTraceForm);
  el.logisticsForm.addEventListener('submit', onLogisticsSubmit);
  el.traceQrBtn.addEventListener('click', onTraceQrClick);

  el.settingsForm.addEventListener('submit', onSettingsSubmit);
}

function normalizeBaseUrl(value) {
  if (!value) {
    return '';
  }
  return value.replace(/\/+$/, '');
}

function isSameOrigin(baseUrl) {
  if (!baseUrl) {
    return false;
  }
  try {
    const target = new URL(baseUrl, window.location.origin);
    return target.origin === window.location.origin;
  } catch (error) {
    return false;
  }
}

function getCookieValue(name) {
  const cookies = document.cookie ? document.cookie.split(';') : [];
  const prefix = `${name}=`;
  for (const raw of cookies) {
    const item = raw.trim();
    if (item.startsWith(prefix)) {
      return decodeURIComponent(item.slice(prefix.length));
    }
  }
  return '';
}

function getCsrfToken() {
  return getCookieValue('XSRF-TOKEN');
}

function showNotice(type, message) {
  if (!message) {
    return;
  }
  el.notice.textContent = message;
  el.notice.className = `notice ${type || ''}`.trim();
  el.notice.classList.add('show');
  clearTimeout(showNotice.timer);
  showNotice.timer = setTimeout(() => {
    el.notice.classList.remove('show');
  }, 3000);
}

function showLogin() {
  el.loginScreen.hidden = false;
  el.adminShell.hidden = true;
}

function showShell() {
  el.loginScreen.hidden = true;
  el.adminShell.hidden = false;
}

function setUser(user) {
  state.user = user;
  const username = user?.username || 'Admin';
  el.userName.textContent = username;
  el.userRole.textContent = user?.role || '-';
  el.userAvatar.textContent = username.slice(0, 2).toUpperCase();
}

function setBaseUrl(value) {
  state.baseUrl = normalizeBaseUrl(value);
  state.sameOrigin = isSameOrigin(state.baseUrl);
  localStorage.setItem('admin.baseUrl', state.baseUrl);
  el.baseUrlInput.value = state.baseUrl;
  el.settingsBaseUrl.value = state.baseUrl;
  if (!state.sameOrigin) {
    showNotice('warning', 'CSRF protection requires using the same origin as /admin-web/.');
  }
}

async function apiRequest(path, options = {}) {
  const url = `${state.baseUrl}${path}`;
  const headers = options.headers ? { ...options.headers } : {};
  if (state.token) {
    headers.Authorization = `Bearer ${state.token}`;
  }
  const csrfToken = getCsrfToken();
  if (csrfToken) {
    headers['X-XSRF-TOKEN'] = csrfToken;
  }
  const config = { ...options, headers };
  config.credentials = state.sameOrigin ? 'same-origin' : 'omit';
  if (config.body && typeof config.body !== 'string') {
    headers['Content-Type'] = 'application/json';
    config.body = JSON.stringify(config.body);
  }
  const response = await fetch(url, config);
  const contentType = response.headers.get('content-type') || '';
  if (contentType.includes('application/json')) {
    const payload = await response.json();
    if (!response.ok || payload.code !== 0) {
      throw new Error(payload.message || response.statusText);
    }
    return payload.data;
  }
  if (!response.ok) {
    throw new Error(response.statusText);
  }
  return response;
}

async function apiBlob(path) {
  const url = `${state.baseUrl}${path}`;
  const headers = {};
  if (state.token) {
    headers.Authorization = `Bearer ${state.token}`;
  }
  const csrfToken = getCsrfToken();
  if (csrfToken) {
    headers['X-XSRF-TOKEN'] = csrfToken;
  }
  const response = await fetch(url, { headers, credentials: state.sameOrigin ? 'same-origin' : 'omit' });
  const contentType = response.headers.get('content-type') || '';
  if (contentType.includes('application/json')) {
    const payload = await response.json();
    throw new Error(payload.message || response.statusText);
  }
  if (!response.ok) {
    throw new Error(response.statusText);
  }
  return response.blob();
}

async function onLoginSubmit(event) {
  event.preventDefault();
  const baseUrl = normalizeBaseUrl(el.baseUrlInput.value.trim());
  const username = el.usernameInput.value.trim();
  const password = el.passwordInput.value;
  if (!baseUrl || !username || !password) {
    showNotice('error', 'Base URL, username, and password are required.');
    return;
  }
  setBaseUrl(baseUrl);
  try {
    const data = await apiRequest('/auth/login', {
      method: 'POST',
      body: { username, password }
    });
    state.token = data.token;
    localStorage.setItem('admin.token', state.token);
    await loadUser();
  } catch (error) {
    showNotice('error', error.message || 'Login failed.');
  }
}

async function loadUser() {
  try {
    const user = await apiRequest('/auth/me', { method: 'GET' });
    setUser(user);
    showShell();
    showView('dashboard');
    if (!user?.role || user.role.toLowerCase() !== 'admin') {
      showNotice('error', 'This account does not have admin role.');
    }
  } catch (error) {
    logout();
  }
}

function logout() {
  state.token = '';
  state.user = null;
  localStorage.removeItem('admin.token');
  showLogin();
}

function showView(view) {
  state.view = view;
  document.querySelectorAll('[data-view]').forEach((section) => {
    section.hidden = section.dataset.view !== view;
  });
  document.querySelectorAll('[data-nav]').forEach((button) => {
    button.classList.toggle('active', button.dataset.nav === view);
  });
  el.viewTitle.textContent = view.charAt(0).toUpperCase() + view.slice(1);
  loadView(view);
}

async function loadView(view) {
  if (!state.token) {
    return;
  }
  if (view === 'dashboard') {
    await loadDashboard();
  } else if (view === 'products') {
    await loadProducts();
  } else if (view === 'orders') {
    await loadOrders();
  } else if (view === 'trace') {
    await loadTraces();
  } else if (view === 'settings') {
    el.settingsBaseUrl.value = state.baseUrl;
  }
}

async function loadDashboard() {
  try {
    const [products, orders, traces] = await Promise.all([
      apiRequest('/admin/products?page=1&size=1'),
      apiRequest('/admin/orders?page=1&size=1'),
      apiRequest('/admin/trace?page=1&size=1')
    ]);
    el.statProducts.textContent = products.total ?? 0;
    el.statOrders.textContent = orders.total ?? 0;
    el.statTraces.textContent = traces.total ?? 0;
  } catch (error) {
    showNotice('error', error.message || 'Failed to load dashboard.');
  }
}

function totalPages(listState) {
  const pages = Math.ceil((listState.total || 0) / listState.size);
  return Math.max(pages, 1);
}

async function loadProducts() {
  el.productTable.innerHTML = '<tr><td colspan="7">Loading...</td></tr>';
  try {
    const query = buildQuery({
      page: state.products.page,
      size: state.products.size,
      status: state.products.status || undefined,
      keyword: state.products.keyword || undefined
    });
    const data = await apiRequest(`/admin/products${query}`);
    state.products.total = data.total || 0;
    renderProducts(data.items || []);
  } catch (error) {
    showNotice('error', error.message || 'Failed to load products.');
  }
}

function renderProducts(items) {
  state.productMap = new Map(items.map((item) => [item.id, item]));
  if (!items.length) {
    el.productTable.innerHTML = '<tr><td colspan="7">No products found.</td></tr>';
  } else {
    const rows = items
      .map((item) => {
        return `
          <tr>
            <td>${escapeHtml(item.id)}</td>
            <td>${escapeHtml(item.name)}</td>
            <td>${formatMoney(item.price)}</td>
            <td>${escapeHtml(item.stock)}</td>
            <td>${escapeHtml(item.status)}</td>
            <td>${escapeHtml(item.origin || '-')}</td>
            <td>
              <div class="table-actions">
                <button class="ghost" data-action="product-edit" data-id="${item.id}">Edit</button>
                <button class="ghost" data-action="product-delete" data-id="${item.id}">Delete</button>
              </div>
            </td>
          </tr>
        `;
      })
      .join('');
    el.productTable.innerHTML = rows;
  }
  updatePagination(state.products, el.productPageInfo, el.productPrev, el.productNext);
}

function updatePagination(listState, infoEl, prevBtn, nextBtn) {
  const pages = totalPages(listState);
  infoEl.textContent = `Page ${listState.page} / ${pages}`;
  prevBtn.disabled = listState.page <= 1;
  nextBtn.disabled = listState.page >= pages;
}

async function onProductTableClick(event) {
  const button = event.target.closest('button[data-action]');
  if (!button) {
    return;
  }
  const id = Number(button.dataset.id);
  if (button.dataset.action === 'product-edit') {
    const product = state.productMap.get(id);
    if (product) {
      await fillProductForm(product);
    }
  }
  if (button.dataset.action === 'product-delete') {
    confirmDelete('Delete this product?', async () => {
      await apiRequest(`/admin/products/${id}`, { method: 'DELETE' });
      showNotice('', 'Product deleted.');
      await loadProducts();
    });
  }
}

async function fillProductForm(product) {
  state.editingProductId = product.id;
  el.productFormTitle.textContent = `Edit product #${product.id}`;
  el.productFormCancel.hidden = false;
  let detail = product;
  try {
    detail = await apiRequest(`/products/${product.id}`);
  } catch (error) {
    showNotice('error', 'Product details not available, using list data.');
  }
  el.productFields.name.value = detail.name || '';
  el.productFields.price.value = detail.price ?? '';
  el.productFields.stock.value = detail.stock ?? '';
  el.productFields.status.value = detail.status || 'online';
  el.productFields.origin.value = detail.origin || '';
  el.productFields.coverUrl.value = detail.coverUrl || '';
  el.productFields.description.value = detail.description || '';
}

function resetProductForm() {
  state.editingProductId = null;
  el.productFormTitle.textContent = 'Create product';
  el.productFormCancel.hidden = true;
  el.productForm.reset();
  el.productFields.status.value = 'online';
}

async function onProductSubmit(event) {
  event.preventDefault();
  const payload = {
    name: el.productFields.name.value.trim(),
    price: toNumber(el.productFields.price.value),
    stock: toInteger(el.productFields.stock.value),
    status: el.productFields.status.value,
    origin: cleanValue(el.productFields.origin.value),
    coverUrl: cleanValue(el.productFields.coverUrl.value),
    description: cleanValue(el.productFields.description.value)
  };
  if (!payload.name || payload.price === null || payload.stock === null) {
    showNotice('error', 'Name, price, and stock are required.');
    return;
  }
  const id = state.editingProductId;
  try {
    if (id) {
      await apiRequest(`/admin/products/${id}`, { method: 'PUT', body: payload });
      showNotice('', 'Product updated.');
    } else {
      await apiRequest('/admin/products', { method: 'POST', body: payload });
      showNotice('', 'Product created.');
    }
    resetProductForm();
    await loadProducts();
  } catch (error) {
    showNotice('error', error.message || 'Failed to save product.');
  }
}

async function loadOrders() {
  el.orderTable.innerHTML = '<tr><td colspan="7">Loading...</td></tr>';
  try {
    const query = buildQuery({
      page: state.orders.page,
      size: state.orders.size,
      status: state.orders.status || undefined,
      keyword: state.orders.keyword || undefined
    });
    const data = await apiRequest(`/admin/orders${query}`);
    state.orders.total = data.total || 0;
    renderOrders(data.items || []);
  } catch (error) {
    showNotice('error', error.message || 'Failed to load orders.');
  }
}

function renderOrders(items) {
  state.orderMap = new Map(items.map((item) => [item.id, item]));
  if (!items.length) {
    el.orderTable.innerHTML = '<tr><td colspan="7">No orders found.</td></tr>';
  } else {
    const rows = items
      .map((item) => {
        const shipBtn = item.status === 'PAID'
          ? `<button class="ghost" data-action="order-ship" data-id="${item.id}">Ship</button>`
          : '';
        return `
          <tr>
            <td>${escapeHtml(item.id)}</td>
            <td>${escapeHtml(item.status)}</td>
            <td>${formatMoney(item.totalAmount)}</td>
            <td>${escapeHtml(item.receiver || '-')}</td>
            <td>${escapeHtml(item.address || '-')}</td>
            <td>${escapeHtml(summarizeItems(item.items))}</td>
            <td>
              <div class="table-actions">
                <button class="ghost" data-action="order-view" data-id="${item.id}">View</button>
                ${shipBtn}
              </div>
            </td>
          </tr>
        `;
      })
      .join('');
    el.orderTable.innerHTML = rows;
  }
  updatePagination(state.orders, el.orderPageInfo, el.orderPrev, el.orderNext);
}

function onOrderTableClick(event) {
  const button = event.target.closest('button[data-action]');
  if (!button) {
    return;
  }
  const id = Number(button.dataset.id);
  const order = state.orderMap.get(id);
  if (!order) {
    return;
  }
  if (button.dataset.action === 'order-view') {
    openOrderModal(order);
  }
  if (button.dataset.action === 'order-ship') {
    openShipModal(order);
  }
}

function openOrderModal(order) {
  const items = (order.items || [])
    .map((item) => `${escapeHtml(item.productName)} x${item.quantity}`)
    .join('<br>') || '-';
  openModal(`Order #${order.id}`, `
    <div class="small">
      <div><strong>Status:</strong> ${escapeHtml(order.status)}</div>
      <div><strong>Total:</strong> ${formatMoney(order.totalAmount)}</div>
      <div><strong>Receiver:</strong> ${escapeHtml(order.receiver || '-')}</div>
      <div><strong>Phone:</strong> ${escapeHtml(order.phone || '-')}</div>
      <div><strong>Address:</strong> ${escapeHtml(order.address || '-')}</div>
      <div><strong>Items:</strong><br>${items}</div>
      <div><strong>Express:</strong> ${escapeHtml(order.expressCompany || '-')} ${escapeHtml(order.expressNo || '')}</div>
    </div>
  `);
}

function openShipModal(order) {
  openModal(`Ship order #${order.id}`, `
    <form id="shipForm" class="small">
      <label>
        <span>Express company *</span>
        <input type="text" name="company" required />
      </label>
      <label>
        <span>Express number *</span>
        <input type="text" name="number" required />
      </label>
      <button type="submit" class="primary">Ship</button>
    </form>
  `, (container) => {
    const shipForm = container.querySelector('#shipForm');
    shipForm.addEventListener('submit', async (event) => {
      event.preventDefault();
      const company = shipForm.company.value.trim();
      const number = shipForm.number.value.trim();
      if (!company || !number) {
        showNotice('error', 'Express company and number are required.');
        return;
      }
      try {
        await apiRequest(`/admin/orders/${order.id}/ship`, {
          method: 'POST',
          body: { expressCompany: company, expressNo: number }
        });
        showNotice('', 'Order shipped.');
        closeModal();
        await loadOrders();
      } catch (error) {
        showNotice('error', error.message || 'Failed to ship order.');
      }
    });
  });
}

async function loadTraces() {
  el.traceTable.innerHTML = '<tr><td colspan="6">Loading...</td></tr>';
  try {
    const query = buildQuery({
      page: state.traces.page,
      size: state.traces.size,
      keyword: state.traces.keyword || undefined
    });
    const data = await apiRequest(`/admin/trace${query}`);
    state.traces.total = data.total || 0;
    renderTraces(data.items || []);
  } catch (error) {
    showNotice('error', error.message || 'Failed to load trace batches.');
  }
}

function renderTraces(items) {
  state.traceMap = new Map(items.map((item) => [item.id, item]));
  if (!items.length) {
    el.traceTable.innerHTML = '<tr><td colspan="6">No trace batches found.</td></tr>';
  } else {
    const rows = items
      .map((item) => {
        return `
          <tr>
            <td>${escapeHtml(item.id)}</td>
            <td>${escapeHtml(item.traceCode)}</td>
            <td>${escapeHtml(item.productName || item.productId)}</td>
            <td>${escapeHtml(item.origin || '-')}</td>
            <td>${escapeHtml(item.producer || '-')}</td>
            <td>
              <div class="table-actions">
                <button class="ghost" data-action="trace-edit" data-id="${item.id}">Edit</button>
                <button class="ghost" data-action="trace-use" data-id="${item.id}">Use</button>
                <button class="ghost" data-action="trace-qr" data-code="${escapeHtml(item.traceCode)}">QR</button>
                <button class="ghost" data-action="trace-delete" data-id="${item.id}">Delete</button>
              </div>
            </td>
          </tr>
        `;
      })
      .join('');
    el.traceTable.innerHTML = rows;
  }
  updatePagination(state.traces, el.tracePageInfo, el.tracePrev, el.traceNext);
}

function onTraceTableClick(event) {
  const button = event.target.closest('button[data-action]');
  if (!button) {
    return;
  }
  const action = button.dataset.action;
  if (action === 'trace-edit') {
    const trace = state.traceMap.get(Number(button.dataset.id));
    if (trace) {
      fillTraceForm(trace);
    }
  }
  if (action === 'trace-use') {
    const trace = state.traceMap.get(Number(button.dataset.id));
    if (trace) {
      el.logisticsFields.traceCode.value = trace.traceCode || '';
      showNotice('', 'Trace code added to logistics form.');
    }
  }
  if (action === 'trace-qr') {
    fetchQr(button.dataset.code);
  }
  if (action === 'trace-delete') {
    const id = Number(button.dataset.id);
    confirmDelete('Delete this trace batch?', async () => {
      await apiRequest(`/admin/trace/${id}`, { method: 'DELETE' });
      showNotice('', 'Trace batch deleted.');
      await loadTraces();
    });
  }
}

function fillTraceForm(trace) {
  state.editingTraceId = trace.id;
  el.traceFormTitle.textContent = `Edit trace #${trace.id}`;
  el.traceFormCancel.hidden = false;
  el.traceFields.productId.value = trace.productId || '';
  el.traceFields.batchNo.value = trace.batchNo || '';
  el.traceFields.origin.value = trace.origin || '';
  el.traceFields.producer.value = trace.producer || '';
  el.traceFields.harvestDate.value = toDateInput(trace.harvestDate);
  el.traceFields.processInfo.value = trace.processInfo || '';
  el.traceFields.testOrg.value = trace.testOrg || '';
  el.traceFields.testDate.value = toDateInput(trace.testDate);
  el.traceFields.testResult.value = trace.testResult || '';
  el.traceFields.reportUrl.value = trace.reportUrl || '';
}

function resetTraceForm() {
  state.editingTraceId = null;
  el.traceFormTitle.textContent = 'Create trace batch';
  el.traceFormCancel.hidden = true;
  el.traceForm.reset();
}

async function onTraceSubmit(event) {
  event.preventDefault();
  const payload = {
    productId: toInteger(el.traceFields.productId.value),
    batchNo: cleanValue(el.traceFields.batchNo.value),
    origin: el.traceFields.origin.value.trim(),
    producer: el.traceFields.producer.value.trim(),
    harvestDate: cleanValue(el.traceFields.harvestDate.value),
    processInfo: cleanValue(el.traceFields.processInfo.value),
    testOrg: cleanValue(el.traceFields.testOrg.value),
    testDate: cleanValue(el.traceFields.testDate.value),
    testResult: cleanValue(el.traceFields.testResult.value),
    reportUrl: cleanValue(el.traceFields.reportUrl.value)
  };
  if (!payload.productId || !payload.origin || !payload.producer) {
    showNotice('error', 'Product ID, origin, and producer are required.');
    return;
  }
  const id = state.editingTraceId;
  try {
    if (id) {
      await apiRequest(`/admin/trace/${id}`, { method: 'PUT', body: payload });
      showNotice('', 'Trace batch updated.');
    } else {
      await apiRequest('/admin/trace', { method: 'POST', body: payload });
      showNotice('', 'Trace batch created.');
    }
    resetTraceForm();
    await loadTraces();
  } catch (error) {
    showNotice('error', error.message || 'Failed to save trace batch.');
  }
}

async function onLogisticsSubmit(event) {
  event.preventDefault();
  const traceCode = el.logisticsFields.traceCode.value.trim();
  const nodeTime = normalizeDateTime(el.logisticsFields.nodeTime.value);
  const location = el.logisticsFields.location.value.trim();
  const statusDesc = el.logisticsFields.statusDesc.value.trim();
  if (!traceCode || !nodeTime || !location || !statusDesc) {
    showNotice('error', 'All logistics fields are required.');
    return;
  }
  try {
    await apiRequest(`/admin/trace/${encodeURIComponent(traceCode)}/logistics`, {
      method: 'POST',
      body: { nodeTime, location, statusDesc }
    });
    showNotice('', 'Logistics node added.');
    el.logisticsForm.reset();
  } catch (error) {
    showNotice('error', error.message || 'Failed to add logistics node.');
  }
}

async function onTraceQrClick() {
  const traceCode = el.logisticsFields.traceCode.value.trim();
  if (!traceCode) {
    showNotice('error', 'Trace code is required for QR.');
    return;
  }
  await fetchQr(traceCode);
}

async function fetchQr(traceCode) {
  try {
    const blob = await apiBlob(`/admin/trace/${encodeURIComponent(traceCode)}/qrcode`);
    if (state.qrUrl) {
      URL.revokeObjectURL(state.qrUrl);
    }
    state.qrUrl = URL.createObjectURL(blob);
    el.qrImage.src = state.qrUrl;
    el.qrDownload.href = state.qrUrl;
    el.qrResult.hidden = false;
    showNotice('', 'QR code loaded.');
  } catch (error) {
    showNotice('error', error.message || 'Failed to load QR code.');
  }
}

function onSettingsSubmit(event) {
  event.preventDefault();
  const baseUrl = normalizeBaseUrl(el.settingsBaseUrl.value.trim());
  if (!baseUrl) {
    showNotice('error', 'Base URL is required.');
    return;
  }
  setBaseUrl(baseUrl);
  showNotice('', 'Settings saved.');
}

function openModal(title, content, onReady) {
  el.modalTitle.textContent = title;
  el.modalBody.innerHTML = content;
  el.modal.hidden = false;
  if (onReady) {
    onReady(el.modalBody);
  }
}

function closeModal() {
  el.modal.hidden = true;
  el.modalBody.innerHTML = '';
}

function confirmDelete(message, onConfirm) {
  openModal('Confirm', `
    <p>${escapeHtml(message)}</p>
    <div class="split-actions">
      <button id="confirmYes" class="primary">Yes</button>
      <button id="confirmNo" class="ghost">Cancel</button>
    </div>
  `, (container) => {
    container.querySelector('#confirmYes').addEventListener('click', async () => {
      closeModal();
      try {
        await onConfirm();
      } catch (error) {
        showNotice('error', error.message || 'Action failed.');
      }
    });
    container.querySelector('#confirmNo').addEventListener('click', closeModal);
  });
}

function buildQuery(params) {
  const search = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      search.set(key, String(value));
    }
  });
  const query = search.toString();
  return query ? `?${query}` : '';
}

function escapeHtml(value) {
  return String(value ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

function cleanValue(value) {
  const trimmed = String(value || '').trim();
  return trimmed ? trimmed : null;
}

function toNumber(value) {
  const num = Number(value);
  return Number.isFinite(num) ? num : null;
}

function toInteger(value) {
  const num = Number.parseInt(value, 10);
  return Number.isFinite(num) ? num : null;
}

function formatMoney(value) {
  if (value === null || value === undefined || value === '') {
    return '-';
  }
  const num = Number(value);
  if (!Number.isFinite(num)) {
    return escapeHtml(value);
  }
  return num.toFixed(2);
}

function summarizeItems(items) {
  if (!items || !items.length) {
    return '-';
  }
  const slice = items.slice(0, 2).map((item) => `${item.productName} x${item.quantity}`);
  if (items.length > 2) {
    slice.push(`+${items.length - 2} more`);
  }
  return slice.join(', ');
}

function toDateInput(value) {
  if (!value) {
    return '';
  }
  const text = String(value);
  return text.split('T')[0];
}

function normalizeDateTime(value) {
  if (!value) {
    return null;
  }
  if (value.length === 16) {
    return `${value}:00`;
  }
  return value;
}

document.addEventListener('DOMContentLoaded', () => {
  initElements();
  initEvents();
  const fallbackBaseUrl = window.location.protocol.startsWith('http')
    ? window.location.origin
    : 'http://localhost:8080';
  const storedBaseUrl = localStorage.getItem('admin.baseUrl') || fallbackBaseUrl;
  setBaseUrl(storedBaseUrl);
  state.token = localStorage.getItem('admin.token') || '';
  if (state.token) {
    loadUser();
  } else {
    showLogin();
  }
});
