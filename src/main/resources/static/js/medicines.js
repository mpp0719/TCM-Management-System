const pageError = document.getElementById('page-error');

const addMedicineToggle = document.getElementById('add-medicine-toggle');
const addMedicinePanel = document.getElementById('add-medicine-panel');
const medicineForm = document.getElementById('medicine-form');
const medicineFormError = document.getElementById('medicine-form-error');
const medicineSubmitBtn = document.getElementById('medicine-submit-btn');
const medicineCancelBtn = document.getElementById('medicine-cancel-btn');

const medicineList = document.getElementById('medicine-list');
const medicineEmptyState = document.getElementById('medicine-empty-state');
const medicineCardTemplate = document.getElementById('medicine-card-template');
const medicineSearchInput = document.getElementById('medicine-search-input');

let allMedicinesCache = []; // unfiltered list from the backend; search filters this client-side

function showPageError(message) {
    pageError.textContent = message;
    pageError.style.display = 'block';
}

function escapeHtml(str) {
    if (str == null) return '';
    return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

function todayLocalDateString() {
    const d = new Date();
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return year + '-' + month + '-' + day;
}

// load meds list
function renderMedicines(medicines) {
    medicineList.innerHTML = '';
    if (!medicines || medicines.length === 0) {
        const searching = medicineSearchInput.value.trim().length > 0;
        medicineEmptyState.querySelector('.big').textContent = searching
            ? 'No medicines match your search'
            : 'No medicines in inventory yet';
        medicineEmptyState.querySelector('div:last-child').textContent = searching
            ? 'Try a different name.'
            : 'Add the first one using the button above.';
        medicineEmptyState.style.display = 'block';
        return;
    }
    medicineEmptyState.style.display = 'none';

    medicines.forEach(function (medicine) {
        const node = medicineCardTemplate.content.cloneNode(true);
        const card = node.querySelector('.medicine-card');
        card.setAttribute('data-medicine-id', medicine.id);

        node.querySelector('.medicine-name').textContent = medicine.name;
        node.querySelector('.medicine-unit').textContent = 'unit: ' + medicine.unit;
        node.querySelector('.stock-value').textContent = medicine.currentStock;
        node.querySelector('.price-value').textContent = Number(medicine.sellingPrice).toFixed(2);

        setupMedicineCardActions(node, medicine);

        medicineList.appendChild(node);
    });
}

// restokc
function setupMedicineCardActions(node, medicine) {
    const restockToggleBtn = node.querySelector('.restock-toggle-btn');
    const restockPanel = node.querySelector('.restock-inline-form');
    const historyToggleBtn = node.querySelector('.history-toggle-btn');
    const historyPanel = node.querySelector('.restock-history-list');
    const editToggleBtn = node.querySelector('.edit-medicine-toggle-btn');
    const editPanel = node.querySelector('.edit-medicine-inline-form');

    const tabs = [
        { btn: restockToggleBtn, panel: restockPanel, onOpen: onOpenRestock },
        { btn: historyToggleBtn, panel: historyPanel, onOpen: onOpenHistory },
        { btn: editToggleBtn, panel: editPanel, onOpen: onOpenEdit }
    ];

    tabs.forEach(function (tab) {
        tab.btn.addEventListener('click', function () {
            const wasOpen = tab.panel.classList.contains('open');
            tabs.forEach(function (t) { t.panel.classList.remove('open'); });
            if (!wasOpen) {
                tab.panel.classList.add('open');
                tab.onOpen();
            }
        });
    });

    const qtyInput = node.querySelector('.restock-qty');
    const dateInput = node.querySelector('.restock-date');
    const priceInput = node.querySelector('.restock-price');
    const restockSubmitBtn = node.querySelector('.restock-submit-btn');
    const restockError = restockPanel.querySelector('.inline-error');

    function onOpenRestock() {
        dateInput.value = todayLocalDateString();
        restockError.style.display = 'none';
    }

    restockSubmitBtn.addEventListener('click', async function () {
        restockError.style.display = 'none';
        const qty = parseInt(qtyInput.value, 10);
        const date = dateInput.value;
        const price = parseFloat(priceInput.value);

        if (!qty || qty <= 0) {
            restockError.textContent = 'Enter a quantity greater than 0.';
            restockError.style.display = 'block';
            return;
        }
        if (!date) {
            restockError.textContent = 'Select a restock date.';
            restockError.style.display = 'block';
            return;
        }
        if (!price || price <= 0) {
            restockError.textContent = 'Enter a unit price greater than 0.';
            restockError.style.display = 'block';
            return;
        }

        restockSubmitBtn.disabled = true;
        try {
            await apiPost('/medicines/restock', {
                medicineId: medicine.id,
                restockQty: qty,
                restockDate: date,
                unitPrice: price
            });
            qtyInput.value = '';
            priceInput.value = '';
            restockPanel.classList.remove('open');
            await refreshMedicines(); // stock changed, re-render everything
        } catch (err) {
            restockError.textContent = err.message;
            restockError.style.display = 'block';
        } finally {
            restockSubmitBtn.disabled = false;
        }
    });

    // restock hist
    let historyLoaded = false;

    function onOpenHistory() {
        if (historyLoaded) return;
        historyLoaded = true;
        loadHistory();
    }

    async function loadHistory() {
        historyPanel.innerHTML = '<div class="restock-history-empty">Loading…</div>';
        try {
            const history = await apiGet('/medicines/' + medicine.id + '/restock-history');
            if (!history || history.length === 0) {
                historyPanel.innerHTML = '<div class="restock-history-empty">No restocks recorded yet.</div>';
                return;
            }
            historyPanel.innerHTML =
                '<div style="border: 1px solid var(--border); border-radius: 10px; overflow: hidden; margin-top: 0.6rem;">' +
                '<table style="width: 100%; border-collapse: collapse; font-size: 0.85rem;">' +
                '<thead>' +
                '<tr style="background: var(--bg);">' +
                '<th style="padding: 0.65rem 0.75rem; text-align: left; color: var(--text-muted); font-weight: 600; font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.04em;">Date</th>' +
                '<th style="padding: 0.65rem 0.75rem; text-align: left; color: var(--text-muted); font-weight: 600; font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.04em;">Amount</th>' +
                '<th style="padding: 0.65rem 0.75rem; text-align: left; color: var(--text-muted); font-weight: 600; font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.04em;">Unit Price</th>' +
                '<th style="padding: 0.65rem 0.75rem; text-align: right; color: var(--text-muted); font-weight: 600; font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.04em;">Total Price</th>' +
                '</tr>' +
                '</thead>' +
                '<tbody>' +
                history.map(function (r, i) {
                    const total = (r.restockQty * r.unitPrice).toFixed(2);
                    const stripe = i % 2 === 1 ? ' background: color-mix(in srgb, var(--bg) 55%, transparent);' : '';
                    return (
                        '<tr style="border-top: 1px solid var(--border);' + stripe + '">' +
                        '<td style="padding: 0.65rem 0.75rem; color: var(--text-muted);">' + r.restockDate + '</td>' +
                        '<td style="padding: 0.65rem 0.75rem; font-family: var(--font-mono);">' + r.restockQty + ' ' + escapeHtml(medicine.unit) + '</td>' +
                        '<td style="padding: 0.65rem 0.75rem; font-family: var(--font-mono);">' + Number(r.unitPrice).toFixed(2) + '</td>' +
                        '<td style="padding: 0.65rem 0.75rem; text-align: right; font-family: var(--font-mono); font-weight: 600; color: var(--text);">' + total + '</td>' +
                        '</tr>'
                    );
                }).join('') +
                '</tbody>' +
                '</table>' +
                '</div>';
        } catch (err) {
            historyPanel.innerHTML = '<div class="restock-history-empty">Could not load history: ' + escapeHtml(err.message) + '</div>';
        }
    }

    // edit meds
    const editNameInput = node.querySelector('.edit-medicine-name');
    const editPriceInput = node.querySelector('.edit-medicine-price');
    const editSaveBtn = node.querySelector('.edit-medicine-save-btn');
    const editError = editPanel.querySelector('.edit-medicine-error');

    function onOpenEdit() {
        editNameInput.value = medicine.name;
        editPriceInput.value = medicine.sellingPrice;
        editError.style.display = 'none';
    }

    editSaveBtn.addEventListener('click', async function () {
        editError.style.display = 'none';
        const name = editNameInput.value.trim();
        const price = parseFloat(editPriceInput.value);

        if (!name) {
            editError.textContent = 'Name is required.';
            editError.style.display = 'block';
            return;
        }
        if (!price || price <= 0) {
            editError.textContent = 'Enter a selling price greater than 0.';
            editError.style.display = 'block';
            return;
        }

        editSaveBtn.disabled = true;
        try {
            await apiPut('/medicines/' + medicine.id, {
                name: name,
                sellingPrice: price
            });
            await refreshMedicines();
        } catch (err) {
            editError.textContent = err.message;
            editError.style.display = 'block';
            editSaveBtn.disabled = false;
        }
    });
}

async function refreshMedicines() {
    try {
        allMedicinesCache = await apiGet('/medicines');
        applyMedicineSearch();
    } catch (err) {
        showPageError('Could not load medicines: ' + err.message);
    }
}

function applyMedicineSearch() {
    const term = medicineSearchInput.value.trim().toLowerCase();
    const filtered = term
        ? allMedicinesCache.filter(function (m) { return m.name.toLowerCase().includes(term); })
        : allMedicinesCache;
    renderMedicines(filtered);
}

medicineSearchInput.addEventListener('input', applyMedicineSearch);

// med form
addMedicineToggle.addEventListener('click', function () {
    addMedicinePanel.classList.toggle('open');
});

medicineCancelBtn.addEventListener('click', function () {
    addMedicinePanel.classList.remove('open');
    medicineForm.reset();
    medicineFormError.style.display = 'none';
});

medicineForm.addEventListener('submit', async function (e) {
    e.preventDefault();
    medicineFormError.style.display = 'none';
    medicineSubmitBtn.disabled = true;
    medicineSubmitBtn.textContent = 'Saving…';

    const dto = {
        name: document.getElementById('medName').value.trim(),
        unit: document.getElementById('medUnit').value.trim(),
        sellingPrice: parseFloat(document.getElementById('medPrice').value)
    };

    try {
        await apiPost('/medicines', dto);
        medicineForm.reset();
        addMedicinePanel.classList.remove('open');
        await refreshMedicines();
    } catch (err) {
        medicineFormError.textContent = err.message;
        medicineFormError.style.display = 'block';
    } finally {
        medicineSubmitBtn.disabled = false;
        medicineSubmitBtn.textContent = 'Save Medicine';
    }
});

// init
refreshMedicines();