const params = new URLSearchParams(window.location.search);
const patientId = params.get('id');

const pageError = document.getElementById('page-error');
const patientName = document.getElementById('patient-name');
const patientIc = document.getElementById('patient-ic');
const patientPhone = document.getElementById('patient-phone');
const patientGender = document.getElementById('patient-gender');
const patientAddress = document.getElementById('patient-address');
const editLink = document.getElementById('edit-link');

const addVisitToggle = document.getElementById('add-visit-toggle');
const addVisitPanel = document.getElementById('add-visit-panel');
const visitForm = document.getElementById('visit-form');
const visitFormError = document.getElementById('visit-form-error');
const visitSubmitBtn = document.getElementById('visit-submit-btn');
const visitCancelBtn = document.getElementById('visit-cancel-btn');

const visitList = document.getElementById('visit-list');
const visitEmptyState = document.getElementById('visit-empty-state');
const visitCardTemplate = document.getElementById('visit-card-template');

const PAYMENT_METHODS = ['Cash', 'Card', 'Bank Transfer', 'E-Wallet'];

let medicinesCache = null; // loaded once, reused across every visit card's dispense dropdown

function showPageError(message) {
    pageError.textContent = message;
    pageError.style.display = 'block';
}

function escapeHtml(str) {
    if (str == null) return '';
    return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

function aggregateMedications(medications) {
    const map = new Map();
    medications.forEach(function (m) {
        if (map.has(m.medicineId)) {
            map.get(m.medicineId).quantityDispensed += m.quantityDispensed;
        } else {
            map.set(m.medicineId, {
                medicineName: m.medicineName,
                quantityDispensed: m.quantityDispensed,
                unit: m.unit
            });
        }
    });
    return Array.from(map.values());
}

function todayLocalDateString() {
    const d = new Date();
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return year + '-' + month + '-' + day;
}

// load patient info
async function loadPatient() {
    try {
        const patient = await apiGet('/patients/' + patientId);
        patientName.textContent = patient.name;
        patientIc.textContent = patient.icNo;
        patientPhone.textContent = patient.phoneNum || '—';
        patientGender.textContent = patient.gender === 'M' ? 'Male' : patient.gender === 'F' ? 'Female' : '—';
        patientAddress.textContent = patient.address || 'No address on file';
        editLink.href = 'patient-form.html?id=' + patient.id;
        document.title = 'Clinic Records — ' + patient.name;
    } catch (err) {
        showPageError('Could not load patient: ' + err.message);
    }
}

// load meds list
async function loadMedicines() {
    try {
        medicinesCache = await apiGet('/medicines');
    } catch (err) {
        medicinesCache = [];
    }
}

function populateMedicineSelect(selectEl) {
    if (!medicinesCache || medicinesCache.length === 0) {
        selectEl.innerHTML = '<option value="">No medicines in inventory</option>';
        return;
    }
    selectEl.innerHTML = medicinesCache.map(function (m) {
        return '<option value="' + m.id + '">' + escapeHtml(m.name) + ' (' + m.currentStock + m.unit + ')</option>';
    }).join('');
}

// load visit hist
function renderVisits(visits) {
    visitList.innerHTML = '';
    if (!visits || visits.length === 0) {
        visitEmptyState.style.display = 'block';
        return;
    }
    visitEmptyState.style.display = 'none';

    visits.forEach(function (visit) {
        const node = visitCardTemplate.content.cloneNode(true);
        const card = node.querySelector('.visit-card');
        card.setAttribute('data-visit-id', visit.id);
        if (visit.hasPayment) {
            card.classList.add('is-paid');
        }

        node.querySelector('.visit-date').textContent = visit.visitDate;

        const badge = node.querySelector('.appointment-badge');
        if (visit.nextAppointmentDate) {
            badge.textContent = 'Next appt: ' + visit.nextAppointmentDate;
            badge.style.display = 'inline-block';
        }

        if (visit.complaint) {
            node.querySelector('.complaint-field').style.display = 'block';
            node.querySelector('.complaint-value').textContent = visit.complaint;
        }
        if (visit.treatmentPlan) {
            node.querySelector('.treatment-field').style.display = 'block';
            node.querySelector('.treatment-value').textContent = visit.treatmentPlan;
        }

        const medList = node.querySelector('.med-list');
        const aggregatedMeds = aggregateMedications(visit.medications || []);
        if (aggregatedMeds.length > 0) {
            medList.innerHTML = aggregatedMeds.map(function (m) {
                return '<span class="med-chip">' + escapeHtml(m.medicineName) + ' \u00d7 ' + m.quantityDispensed + ' ' + escapeHtml(m.unit) + '</span>';
            }).join('');
        } else {
            medList.innerHTML = '<span style="color: var(--text-muted); font-size: 0.85rem;">None recorded</span>';
        }

        setupVisitCardActions(node, visit, card);

        visitList.appendChild(node);
    });
}

// 3 btns tab coor
function setupVisitCardActions(node, visit, card) {
    const editToggleBtn = node.querySelector('.edit-visit-toggle-btn');
    const editPanel = node.querySelector('.edit-visit-panel');
    const dispenseToggleBtn = node.querySelector('.dispense-toggle-btn');
    const dispensePanel = node.querySelector('.dispense-inline-form');
    const paymentToggleBtn = node.querySelector('.payment-toggle-btn');
    const paymentPanel = node.querySelector('.payment-block');

    const tabs = [
        { btn: editToggleBtn, panel: editPanel, onOpen: onOpenEdit },
        { btn: dispenseToggleBtn, panel: dispensePanel, onOpen: onOpenDispense },
        { btn: paymentToggleBtn, panel: paymentPanel, onOpen: onOpenPayment }
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

    function lockVisitActions() {
        card.classList.add('is-paid');
        dispenseToggleBtn.disabled = true;
        dispenseToggleBtn.style.opacity = '0.4';
        dispenseToggleBtn.style.cursor = 'not-allowed';
        dispenseToggleBtn.title = 'Locked — payment already recorded for this visit';
        dispensePanel.classList.remove('open'); // close it if it happened to be open already

        editToggleBtn.disabled = true;
        editToggleBtn.style.opacity = '0.4';
        editToggleBtn.style.cursor = 'not-allowed';
        editToggleBtn.title = 'Locked — payment already recorded for this visit';
        editPanel.classList.remove('open');
    }

    if (visit.hasPayment) {
        lockVisitActions();
    }

    // edit visit
    const editDateInput = node.querySelector('.edit-visitDate');
    const editComplaintInput = node.querySelector('.edit-complaint');
    const editTreatmentInput = node.querySelector('.edit-treatmentPlan');
    const editNextApptInput = node.querySelector('.edit-nextAppointmentDate');
    const editSaveBtn = node.querySelector('.edit-visit-save-btn');
    const editCancelBtn = node.querySelector('.edit-visit-cancel-btn');
    const editErrorEl = node.querySelector('.edit-visit-error');

    function onOpenEdit() {
        editDateInput.value = visit.visitDate || '';
        editComplaintInput.value = visit.complaint || '';
        editTreatmentInput.value = visit.treatmentPlan || '';
        editNextApptInput.value = visit.nextAppointmentDate || '';
        editErrorEl.style.display = 'none';
    }

    editCancelBtn.addEventListener('click', function () {
        editPanel.classList.remove('open');
    });

    editSaveBtn.addEventListener('click', async function () {
        editErrorEl.style.display = 'none';
        editSaveBtn.disabled = true;
        try {
            await apiPut('/visits/' + visit.id, {
                visitDate: editDateInput.value,
                complaint: editComplaintInput.value.trim(),
                treatmentPlan: editTreatmentInput.value.trim(),
                nextAppointmentDate: editNextApptInput.value || null
            });
            await refreshVisits();
        } catch (err) {
            editErrorEl.textContent = err.message;
            editErrorEl.style.display = 'block';
            editSaveBtn.disabled = false;
        }
    });

    // dispense meds
    const medicineSelect = node.querySelector('.dispense-medicine');
    const qtyInput = node.querySelector('.dispense-qty');
    const dispenseSubmitBtn = node.querySelector('.dispense-submit-btn');
    const dispenseError = dispensePanel.querySelector('.inline-error');

    function onOpenDispense() {
        populateMedicineSelect(medicineSelect);
    }

    dispenseSubmitBtn.addEventListener('click', async function () {
        dispenseError.style.display = 'none';
        const medicineId = medicineSelect.value;
        const qty = parseInt(qtyInput.value, 10);

        if (!medicineId) {
            dispenseError.textContent = 'Select a medicine first.';
            dispenseError.style.display = 'block';
            return;
        }
        if (!qty || qty <= 0) {
            dispenseError.textContent = 'Enter a quantity greater than 0.';
            dispenseError.style.display = 'block';
            return;
        }

        dispenseSubmitBtn.disabled = true;
        try {
            await apiPost('/medicines/dispense', {
                visitId: visit.id,
                medicineId: parseInt(medicineId, 10),
                quantityDispensed: qty
            });
            await loadMedicines();      // stock changed, refresh cache
            await refreshVisits();      // re-render so the new chip + updated stock show
        } catch (err) {
            dispenseError.textContent = err.message;
            dispenseError.style.display = 'block';
            dispenseSubmitBtn.disabled = false;
        }
    });

    // payment here
    let paymentLoaded = false;

    function onOpenPayment() {
        if (paymentLoaded) return;
        paymentLoaded = true;
        loadPayment(visit, paymentPanel, lockVisitActions);
    }
}

async function loadPayment(visit, paymentBlock, onPaymentCreated) {
    paymentBlock.innerHTML = '<div style="width: 100%; color: var(--text-muted); font-size: 0.85rem;">Loading…</div>';
    try {
        const payment = await apiGet('/payments/visit/' + visit.id);
        renderPaymentReceipt(paymentBlock, payment);
    } catch (err) {
        if (err.status === 404) {
            renderPaymentForm(paymentBlock, visit.id, onPaymentCreated);
        } else {
            paymentBlock.innerHTML = '<div class="inline-error" style="display: block; width: 100%;">Could not load payment: ' + escapeHtml(err.message) + '</div>';
        }
    }
}

function renderPaymentReceipt(paymentBlock, payment) {
    paymentBlock.innerHTML =
        '<div style="width: 100%;">' +
        '<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem;">' +
        '<span style="font-family: var(--font-display); font-weight: 700; font-size: 0.9rem;">Payment Receipt</span>' +
        '<div style="display: flex; align-items: center; gap: 0.6rem;">' +
        '<button class="link-btn payment-edit-btn" type="button" style="padding: 0;">Edit</button>' +
        '<span class="paid-badge">PAID</span>' +
        '</div>' +
        '</div>' +
        '<div class="payment-row"><span>Date</span><span class="val">' + escapeHtml(payment.paymentDate) + '</span></div>' +
        '<div class="payment-row"><span>Method</span><span class="val">' + escapeHtml(payment.paymentMethod) + '</span></div>' +
        '<div class="payment-row"><span>Treatment Fee</span><span class="val">' + Number(payment.treatmentFee).toFixed(2) + '</span></div>' +
        '<div class="payment-row"><span>Medicine Cost</span><span class="val">' + Number(payment.medicineCost).toFixed(2) + '</span></div>' +
        '<div class="payment-row total"><span>Total</span><span class="val">' + Number(payment.totalAmount).toFixed(2) + '</span></div>' +
        '</div>';

    paymentBlock.querySelector('.payment-edit-btn').addEventListener('click', function () {
        renderPaymentEditForm(paymentBlock, payment);
    });
}

function renderPaymentEditForm(paymentBlock, payment) {
    paymentBlock.innerHTML =
        '<div style="width: 100%;">' +
        '<div style="font-family: var(--font-display); font-weight: 700; font-size: 0.9rem; margin-bottom: 0.6rem;">Edit Payment</div>' +
        '<div style="display: flex; gap: 0.6rem; flex-wrap: wrap; align-items: flex-end;">' +
        '<div class="field" style="margin-bottom: 0; min-width: 0; flex: 1 1 140px;">' +
        '<label>Date</label>' +
        '<input type="date" class="payment-edit-date-input" style="min-width: 0; width: 100%;">' +
        '</div>' +
        '<div class="field" style="margin-bottom: 0; min-width: 0; flex: 1 1 140px;">' +
        '<label>Method</label>' +
        '<select class="payment-edit-method-input" style="min-width: 0; width: 100%;">' +
        PAYMENT_METHODS.map(function (m) { return '<option value="' + m + '">' + m + '</option>'; }).join('') +
        '</select>' +
        '</div>' +
        '<div class="field" style="margin-bottom: 0; min-width: 0; flex: 1 1 120px;">' +
        '<label>Treatment Fee</label>' +
        '<input type="number" class="payment-edit-fee-input" min="0" step="0.01" style="min-width: 0; width: 100%;">' +
        '</div>' +
        '<button class="btn btn-primary btn-small payment-edit-save-btn" type="button" style="flex: 0 0 auto; padding-top: 0.7rem; padding-bottom: 0.7rem;">Save</button>' +
        '<button class="btn btn-secondary btn-small payment-edit-cancel-btn" type="button" style="flex: 0 0 auto; padding-top: 0.7rem; padding-bottom: 0.7rem;">Cancel</button>' +
        '</div>' +
        '<div class="inline-error payment-edit-error" style="width: 100%;"></div>' +
        '</div>';

    const dateInput = paymentBlock.querySelector('.payment-edit-date-input');
    const methodInput = paymentBlock.querySelector('.payment-edit-method-input');
    const feeInput = paymentBlock.querySelector('.payment-edit-fee-input');
    const saveBtn = paymentBlock.querySelector('.payment-edit-save-btn');
    const cancelBtn = paymentBlock.querySelector('.payment-edit-cancel-btn');
    const errorEl = paymentBlock.querySelector('.payment-edit-error');

    dateInput.value = payment.paymentDate;
    methodInput.value = payment.paymentMethod;
    feeInput.value = payment.treatmentFee;

    cancelBtn.addEventListener('click', function () {
        renderPaymentReceipt(paymentBlock, payment);
    });

    saveBtn.addEventListener('click', async function () {
        errorEl.style.display = 'none';
        const fee = parseFloat(feeInput.value);

        if (!dateInput.value) {
            errorEl.textContent = 'Select a payment date.';
            errorEl.style.display = 'block';
            return;
        }
        if (feeInput.value === '' || isNaN(fee) || fee < 0) {
            errorEl.textContent = 'Enter a treatment fee of 0 or more.';
            errorEl.style.display = 'block';
            return;
        }

        saveBtn.disabled = true;
        try {
            const updated = await apiPut('/payments/' + payment.id, {
                paymentDate: dateInput.value,
                paymentMethod: methodInput.value,
                treatmentFee: fee
            });
            renderPaymentReceipt(paymentBlock, updated);
        } catch (err) {
            errorEl.textContent = err.message;
            errorEl.style.display = 'block';
            saveBtn.disabled = false;
        }
    });
}

function renderPaymentForm(paymentBlock, visitId, onPaymentCreated) {
    paymentBlock.innerHTML =
        '<div style="width: 100%;">' +
        '<div style="font-family: var(--font-display); font-weight: 700; font-size: 0.9rem; margin-bottom: 0.6rem;">Record Payment</div>' +
        '<div style="display: flex; gap: 0.6rem; flex-wrap: wrap; align-items: flex-end;">' +
        '<div class="field" style="margin-bottom: 0; min-width: 0; flex: 1 1 140px;">' +
        '<label>Date</label>' +
        '<input type="date" class="payment-date-input">' +
        '</div>' +
        '<div class="field" style="margin-bottom: 0; min-width: 0; flex: 1 1 140px;">' +
        '<label>Method</label>' +
        '<select class="payment-method-input">' +
        PAYMENT_METHODS.map(function (m) { return '<option value="' + m + '">' + m + '</option>'; }).join('') +
        '</select>' +
        '</div>' +
        '<div class="field" style="margin-bottom: 0; min-width: 0; flex: 1 1 120px;">' +
        '<label>Treatment Fee</label>' +
        '<input type="number" class="payment-fee-input" min="0" step="0.01">' +
        '</div>' +
        '<button class="btn btn-primary btn-small payment-submit-btn" type="button" style="flex: 0 0 auto; padding-top: 0.7rem; padding-bottom: 0.7rem;">Record Payment</button>' +
        '</div>' +
        '<div class="inline-error payment-form-error"></div>' +
        '</div>';

    const dateInput = paymentBlock.querySelector('.payment-date-input');
    const methodInput = paymentBlock.querySelector('.payment-method-input');
    const feeInput = paymentBlock.querySelector('.payment-fee-input');
    const submitBtn = paymentBlock.querySelector('.payment-submit-btn');
    const errorEl = paymentBlock.querySelector('.payment-form-error');

    dateInput.value = todayLocalDateString();

    submitBtn.addEventListener('click', async function () {
        errorEl.style.display = 'none';
        const fee = parseFloat(feeInput.value);

        if (!dateInput.value) {
            errorEl.textContent = 'Select a payment date.';
            errorEl.style.display = 'block';
            return;
        }
        if (feeInput.value === '' || isNaN(fee) || fee < 0) {
            errorEl.textContent = 'Enter a treatment fee of 0 or more.';
            errorEl.style.display = 'block';
            return;
        }

        submitBtn.disabled = true;
        try {
            const payment = await apiPost('/payments', {
                visitId: visitId,
                paymentDate: dateInput.value,
                paymentMethod: methodInput.value,
                treatmentFee: fee
            });
            renderPaymentReceipt(paymentBlock, payment);
            if (onPaymentCreated) onPaymentCreated(); // lock Edit Visit / Add medication immediately, no page refresh needed
        } catch (err) {
            errorEl.textContent = err.message;
            errorEl.style.display = 'block';
            submitBtn.disabled = false;
        }
    });
}

async function refreshVisits() {
    try {
        const visits = await apiGet('/visits/patient/' + patientId);
        renderVisits(visits);
    } catch (err) {
        showPageError('Could not load visit history: ' + err.message);
    }
}

// visit form
addVisitToggle.addEventListener('click', function () {
    const opening = !addVisitPanel.classList.contains('open');
    addVisitPanel.classList.toggle('open');
    if (opening) {
        document.getElementById('visitDate').value = todayLocalDateString();
    }
});

visitCancelBtn.addEventListener('click', function () {
    addVisitPanel.classList.remove('open');
    visitForm.reset();
    visitFormError.style.display = 'none';
});

visitForm.addEventListener('submit', async function (e) {
    e.preventDefault();
    visitFormError.style.display = 'none';
    visitSubmitBtn.disabled = true;
    visitSubmitBtn.textContent = 'Saving…';

    const dto = {
        visitDate: document.getElementById('visitDate').value,
        complaint: document.getElementById('complaint').value.trim(),
        treatmentPlan: document.getElementById('treatmentPlan').value.trim(),
        nextAppointmentDate: document.getElementById('nextAppointmentDate').value || null
    };

    try {
        await apiPost('/visits/patient/' + patientId, dto);
        visitForm.reset();
        addVisitPanel.classList.remove('open');
        await refreshVisits();
    } catch (err) {
        visitFormError.textContent = err.message;
        visitFormError.style.display = 'block';
    } finally {
        visitSubmitBtn.disabled = false;
        visitSubmitBtn.textContent = 'Save Visit';
    }
});

// init
if (!patientId) {
    showPageError('No patient specified.');
} else {
    loadPatient();
    loadMedicines().then(refreshVisits);
}