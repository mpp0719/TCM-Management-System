const params = new URLSearchParams(window.location.search);
const editId = params.get('id');
const isEditMode = !!editId;
const pageTitle = document.getElementById('page-title');
const backLink = document.getElementById('back-link');
const cancelLink = document.getElementById('cancel-link');
const form = document.getElementById('patient-form');
const submitBtn = document.getElementById('submit-btn');
const errorBanner = document.getElementById('form-error');
const nameInput = document.getElementById('name');
const icNoInput = document.getElementById('icNo');
const genderInput = document.getElementById('gender');
const phoneInput = document.getElementById('phoneNum');
const addressInput = document.getElementById('address');

function showError(message) {
    errorBanner.textContent = message;
    errorBanner.style.display = 'block';
}

function hideError() {
    errorBanner.style.display = 'none';
}

async function loadForEdit() {
    try {
        const patient = await apiGet('/patients/' + editId);
        nameInput.value = patient.name || '';
        icNoInput.value = patient.icNo || '';
        genderInput.value = patient.gender || '';
        phoneInput.value = patient.phoneNum || '';
        addressInput.value = patient.address || '';
    } catch (err) {
        showError('Could not load patient: ' + err.message);
        submitBtn.disabled = true;
    }
}

if (isEditMode) {
    pageTitle.textContent = 'Edit Patient';
    document.title = 'Clinic Records — Edit Patient';
    backLink.href = 'patient.html?id=' + editId;
    cancelLink.href = 'patient.html?id=' + editId;
    loadForEdit();
}

form.addEventListener('submit', async function (e) {
    e.preventDefault();
    hideError();
    submitBtn.disabled = true;
    submitBtn.textContent = 'Saving…';

    const dto = {
        name: nameInput.value.trim(),
        icNo: icNoInput.value.trim(),
        gender: genderInput.value,
        phoneNum: phoneInput.value.trim(),
        address: addressInput.value.trim()
    };

    try {
        let saved;
        if (isEditMode) {
            saved = await apiPut('/patients/' + editId, dto);
        } else {
            saved = await apiPost('/patients', dto);
        }
        window.location.href = 'patient.html?id=' + saved.id;
    } catch (err) {
        showError(err.message);
        submitBtn.disabled = false;
        submitBtn.textContent = 'Save Patient';
    }
});