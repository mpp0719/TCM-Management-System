const pageError = document.getElementById('page-error');
const instructionHint = document.getElementById('instruction-hint');
const monthLabel = document.getElementById('month-label');
const calendarGrid = document.getElementById('calendar-grid');
const prevMonthBtn = document.getElementById('prev-month-btn');
const nextMonthBtn = document.getElementById('next-month-btn');
const clearRangeBtn = document.getElementById('clear-range-btn');
const dayDetailPanel = document.getElementById('day-detail-panel');
const dayDetailHeading = document.getElementById('day-detail-heading');
const dayDetailList = document.getElementById('day-detail-list');

const MONTH_NAMES = ['January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December'];

const today = new Date();
let viewYear = today.getFullYear();
let viewMonth = today.getMonth(); // 0-indexed

let rangeStart = null;   // date string "YYYY-MM-DD"
let rangeEnd = null;     // date string "YYYY-MM-DD"
let selectionState = 'idle'; // 'idle' | 'selecting' | 'selected'
let appointmentsMap = {};    // dateStr -> array of visit DTOs
let openDetailDate = null;

function pad(n) {
    return String(n).padStart(2, '0');
}

function dateStr(year, month, day) {
    return year + '-' + pad(month + 1) + '-' + pad(day);
}

function escapeHtml(str) {
    if (str == null) return '';
    return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

function showPageError(message) {
    pageError.textContent = message;
    pageError.style.display = 'block';
}

function updateHint() {
    if (selectionState === 'idle') {
        instructionHint.textContent = 'Click a day to start a range, then click another day to finish it.';
    } else if (selectionState === 'selecting') {
        instructionHint.textContent = 'Now click the end date.';
    } else {
        instructionHint.textContent = 'Click a highlighted day to see who\u2019s booked, or click any other day to start a new range.';
    }
}

// build grid as days for calendar 7x5
function buildCells() {
    const firstOfMonth = new Date(viewYear, viewMonth, 1);
    const startWeekday = firstOfMonth.getDay(); // 0 = Sun
    const daysInMonth = new Date(viewYear, viewMonth + 1, 0).getDate();
    const daysInPrevMonth = new Date(viewYear, viewMonth, 0).getDate();

    const cells = [];

    // leading days from previous month
    const prevMonth = viewMonth === 0 ? 11 : viewMonth - 1;
    const prevYear = viewMonth === 0 ? viewYear - 1 : viewYear;
    for (let i = 0; i < startWeekday; i++) {
        const day = daysInPrevMonth - startWeekday + 1 + i;
        cells.push({ year: prevYear, month: prevMonth, day: day, muted: true });
    }

    // current month days
    for (let day = 1; day <= daysInMonth; day++) {
        cells.push({ year: viewYear, month: viewMonth, day: day, muted: false });
    }

    // trailing days from next month, pad to 42 cells (6 full weeks)
    const nextMonth = viewMonth === 11 ? 0 : viewMonth + 1;
    const nextYear = viewMonth === 11 ? viewYear + 1 : viewYear;
    let nextDay = 1;
    while (cells.length < 42) {
        cells.push({ year: nextYear, month: nextMonth, day: nextDay, muted: true });
        nextDay++;
    }

    return cells;
}

function renderGrid() {
    monthLabel.textContent = MONTH_NAMES[viewMonth] + ' ' + viewYear;
    const cells = buildCells();
    const todayStr = dateStr(today.getFullYear(), today.getMonth(), today.getDate());

    calendarGrid.innerHTML = cells.map(function (cell) {
        const ds = dateStr(cell.year, cell.month, cell.day);
        const classes = ['calendar-cell'];
        if (cell.muted) classes.push('muted');
        if (ds === todayStr) classes.push('is-today');

        const inRange = rangeStart && rangeEnd && ds >= rangeStart && ds <= rangeEnd;
        if (inRange) classes.push('in-range');
        if (ds === rangeStart || ds === rangeEnd) classes.push('range-endpoint');

        const appts = appointmentsMap[ds];
        let badge = '';
        if (appts && appts.length > 0) {
            classes.push('has-appt');
            badge = '<span class="cell-badge">' + appts.length + '</span>';
        }

        return (
            '<div class="' + classes.join(' ') + '" data-date="' + ds + '">' +
            '<span class="cell-daynum">' + cell.day + '</span>' +
            badge +
            '</div>'
        );
    }).join('');

    Array.prototype.forEach.call(calendarGrid.querySelectorAll('.calendar-cell'), function (cellEl) {
        cellEl.addEventListener('click', function () {
            onCellClick(cellEl.getAttribute('data-date'));
        });
    });
}

// selection logic
function onCellClick(ds) {
    if (selectionState === 'idle') {
        rangeStart = ds;
        rangeEnd = null;
        selectionState = 'selecting';
        closeDayDetail();
    } else if (selectionState === 'selecting') {
        let s = rangeStart, e = ds;
        if (e < s) { const tmp = s; s = e; e = tmp; }
        rangeStart = s;
        rangeEnd = e;
        selectionState = 'selected';
        fetchAppointments(s, e);
    } else {
        // selectionState === 'selected'
        if (appointmentsMap[ds] && appointmentsMap[ds].length > 0) {
            showDayDetail(ds);
            updateHint();
            renderGrid();
            return;
        } else {
            rangeStart = ds;
            rangeEnd = null;
            appointmentsMap = {};
            selectionState = 'selecting';
            closeDayDetail();
        }
    }
    updateHint();
    renderGrid();
}

async function fetchAppointments(start, end) {
    pageError.style.display = 'none';
    try {
        appointmentsMap = await apiGet('/visits/appointments?start=' + start + '&end=' + end);
        renderGrid();
    } catch (err) {
        showPageError('Could not load appointments: ' + err.message);
        appointmentsMap = {};
        renderGrid();
    }
}

// day detail pan
function showDayDetail(ds) {
    openDetailDate = ds;
    const appts = appointmentsMap[ds] || [];
    dayDetailHeading.textContent = 'Appointments on ' + ds + ' (' + appts.length + ')';
    dayDetailList.innerHTML = appts.map(function (visit) {
        return (
            '<div class="day-detail-item">' +
            '<div class="day-detail-name">' + escapeHtml(visit.patientName) + '</div>' +
            '<div class="day-detail-meta mono">' + escapeHtml(visit.patientPhoneNum || '\u2014') + '</div>' +
            (visit.complaint ? '<div class="day-detail-meta">' + escapeHtml(visit.complaint) + '</div>' : '') +
            '<a class="day-detail-link" href="patient.html?id=' + visit.patientId + '">View patient &rarr;</a>' +
            '</div>'
        );
    }).join('');
}

function closeDayDetail() {
    openDetailDate = null;
    dayDetailHeading.textContent = 'No day selected';
    dayDetailList.innerHTML = '<div style="color: var(--text-muted); font-size: 0.85rem;">Select a date range, then click a highlighted day to see who\u2019s booked.</div>';
}

// month navi btn
prevMonthBtn.addEventListener('click', function () {
    viewMonth--;
    if (viewMonth < 0) { viewMonth = 11; viewYear--; }
    renderGrid();
});

nextMonthBtn.addEventListener('click', function () {
    viewMonth++;
    if (viewMonth > 11) { viewMonth = 0; viewYear++; }
    renderGrid();
});

clearRangeBtn.addEventListener('click', function () {
    rangeStart = null;
    rangeEnd = null;
    appointmentsMap = {};
    selectionState = 'idle';
    closeDayDetail();
    updateHint();
    renderGrid();
});

// init
updateHint();
renderGrid();