// Theme Management
function loadTheme() {
    const theme = localStorage.getItem('theme') || 'light';
    document.documentElement.setAttribute('data-theme', theme);
    updateThemeBtns(theme);
}

function toggleTheme() {
    const currentTheme = document.documentElement.getAttribute('data-theme') || 'light';
    const newTheme = currentTheme === 'light' ? 'dark' : 'light';
    document.documentElement.setAttribute('data-theme', newTheme);
    localStorage.setItem('theme', newTheme);
    updateThemeBtns(newTheme);
}

function updateThemeBtns(theme) {
    document.querySelectorAll('.theme-toggle').forEach(btn => {
        btn.textContent = theme === 'light' ? '🌙' : '☀️';
    });
}

// Logout Handler
document.addEventListener('DOMContentLoaded', function() {
    loadTheme();
    
    // Theme button listeners
    document.querySelectorAll('.theme-toggle').forEach(btn => {
        btn.addEventListener('click', toggleTheme);
    });

    // Logout button
    const logoutBtns = document.querySelectorAll('.logout-btn, .logout-link');
    logoutBtns.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            fetch('/qcm/logout', {
                method: 'POST'
            }).then(() => {
                window.location.href = '/qcm/login.html';
            }).catch(error => {
                console.error('[v0] Logout error:', error);
                window.location.href = '/qcm/login.html';
            });
        });
    });

    // Load student/admin info if available
    loadUserInfo();
});

// Load user information from session
function loadUserInfo() {
    fetch('/qcm/user-info')
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                document.getElementById('studentName') && (document.getElementById('studentName').textContent = data.nom);
                document.getElementById('firstName') && (document.getElementById('firstName').textContent = data.prenoms || data.nom);
                document.getElementById('studentId') && (document.getElementById('studentId').textContent = data.numeroetudia);
                document.getElementById('studentLevel') && (document.getElementById('studentLevel').textContent = data.niveau);
                document.getElementById('levelHighlight') && (document.getElementById('levelHighlight').textContent = data.niveau);
                document.getElementById('emailHighlight') && (document.getElementById('emailHighlight').textContent = data.email);
            }
        })
        .catch(error => console.error('[v0] Error loading user info:', error));
}

// Navigation
function navigateTo(url) {
    window.location.href = url;
}

// Add event listeners to nav items
document.addEventListener('DOMContentLoaded', function() {
    const navItems = document.querySelectorAll('.nav-item');
    navItems.forEach(item => {
        item.addEventListener('click', function(e) {
            e.preventDefault();
            const href = this.getAttribute('href');
            if (href && href !== '#') {
                window.location.href = href;
            }
        });
    });
});
