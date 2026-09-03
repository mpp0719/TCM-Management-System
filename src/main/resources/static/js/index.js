const searchInput = document.getElementById('search-input');
const searchShell = document.getElementById('search-shell');
const searchMeta = document.getElementById('search-meta');
const resultsTable = document.getElementById('results-table');
const resultsBody = document.getElementById('results-body');
const emptyState = document.getElementById('empty-state');

function escapeHtml(str) {
    if (str == null) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;');
}

function genderClass(gender) {
    if (gender === 'M') return 'male';
    if (gender === 'F') return 'female';
    return '';
}

function renderResults(patients) {
    if (!patients || patients.length === 0) {
        resultsTable.style.display = 'none';
        emptyState.style.display = 'block';
        return;
    }

    emptyState.style.display = 'none';
    resultsTable.style.display = 'table';

    // Server already sorts: upcoming appointment first (soonest), then most
    // recent past visit, then never-visited patients last — see PatientService.
    resultsBody.innerHTML = patients.map(function (p) {
        return (
            '<tr data-id="' + p.id + '">' +
            '<td>' + escapeHtml(p.name) + '</td>' +
            '<td class="mono">' + escapeHtml(p.icNo) + '</td>' +
            '<td class="mono">' + escapeHtml(p.phoneNum) + '</td>' +
            '<td><span class="gender-badge ' + genderClass(p.gender) + '">' + escapeHtml(p.gender || '—') + '</span></td>' +
            '</tr>'
        );
    }).join('');

    Array.prototype.forEach.call(resultsBody.querySelectorAll('tr'), function (row) {
        row.addEventListener('click', function () {
            window.location.href = 'patient.html?id=' + row.getAttribute('data-id');
        });
    });
}

async function runSearch(term) {
    try {
        const patients = await apiGet('/patients/search?term=' + encodeURIComponent(term || ''));
        searchMeta.textContent = patients.length + (patients.length === 1 ? ' patient' : ' patients') + (term ? ' matching “' + term + '”' : ' total');
        renderResults(patients);
    } catch (err) {
        searchMeta.textContent = 'Could not load patients: ' + err.message;
        resultsTable.style.display = 'none';
        emptyState.style.display = 'none';
    }
}

searchInput.addEventListener('input', function () {
    runSearch(searchInput.value.trim());
});

searchInput.addEventListener('focus', function () {
    searchShell.classList.add('focused');
});
searchInput.addEventListener('blur', function () {
    searchShell.classList.remove('focused');
});

// Initial load — empty term shows all patients (per requirement:
// empty search bar behaves like a normal search bar and shows everything).
runSearch('');

// export transaction within date range
const exportToggleBtn = document.getElementById('export-toggle-btn');
const exportPanel = document.getElementById('export-panel');
const exportStartInput = document.getElementById('export-start-date');
const exportEndInput = document.getElementById('export-end-date');
const exportDownloadBtn = document.getElementById('export-download-btn');
const exportError = document.getElementById('export-error');

exportToggleBtn.addEventListener('click', function () {
    exportPanel.classList.toggle('open');
});

exportDownloadBtn.addEventListener('click', function () {
    exportError.style.display = 'none';
    const start = exportStartInput.value;
    const end = exportEndInput.value;

    if (!start || !end) {
        exportError.textContent = 'Select both a start and end date.';
        exportError.style.display = 'block';
        return;
    }
    if (start > end) {
        exportError.textContent = 'Start date must not be after end date.';
        exportError.style.display = 'block';
        return;
    }

    window.location.href = '/api/payments/export?start=' + start + '&end=' + end;
});