// theme toggle
(function () {
    const STORAGE_KEY = 'clinic-theme';

    function applyTheme(theme) {
        if (theme === 'dark') {
            document.documentElement.setAttribute('data-theme', 'dark');
        } else {
            document.documentElement.removeAttribute('data-theme');
        }
    }

    function getStoredTheme() {
        return localStorage.getItem(STORAGE_KEY) || 'light';
    }

    // Apply immediately (before DOMContentLoaded) to avoid a flash of the
    // wrong theme on page load.
    applyTheme(getStoredTheme());

    function initToggleButton() {
        const btn = document.getElementById('theme-toggle-btn');
        if (!btn) return;

        function render() {
            const current = getStoredTheme();
            const icon = btn.querySelector('.icon');
            const label = btn.querySelector('.label');
            if (current === 'dark') {
                icon.textContent = '☀';
                label.textContent = 'Light';
            } else {
                icon.textContent = '☾';
                label.textContent = 'Dark';
            }
        }

        btn.addEventListener('click', function () {
            const next = getStoredTheme() === 'dark' ? 'light' : 'dark';
            localStorage.setItem(STORAGE_KEY, next);
            applyTheme(next);
            render();
        });

        render();
    }

    document.addEventListener('DOMContentLoaded', initToggleButton);
})();