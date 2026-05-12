// Theme management
function loadTheme() {
    const theme = localStorage.getItem('theme') || 'light';
    document.documentElement.setAttribute('data-theme', theme);
    updateThemeBtn(theme);
}

function toggleTheme() {
    const currentTheme = document.documentElement.getAttribute('data-theme') || 'light';
    const newTheme = currentTheme === 'light' ? 'dark' : 'light';
    document.documentElement.setAttribute('data-theme', newTheme);
    localStorage.setItem('theme', newTheme);
    updateThemeBtn(newTheme);
}

function updateThemeBtn(theme) {
    document.getElementById('themeBtn').textContent = theme === 'light' ? '🌙' : '☀️';
}

// Tab switching
function switchTab(tabName) {
    const tabs = document.querySelectorAll('.tab-content');
    const btns = document.querySelectorAll('.tab-btn');

    tabs.forEach(tab => tab.classList.remove('active'));
    btns.forEach(btn => btn.classList.remove('active'));

    document.getElementById(tabName).classList.add('active');
    event.target.closest('.tab-btn')?.classList.add('active');
    
    // Properly handle tab btn active state
    btns.forEach(btn => {
        if (btn.getAttribute('data-tab') === tabName) {
            btn.classList.add('active');
        }
    });
}

// Form submission handlers
document.getElementById('loginForm')?.addEventListener('submit', function(e) {
    e.preventDefault();
    
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    // Send to backend
    fetch('/qcm/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
            email: email,
            motdepasse: password
        })
    })
    .then(response => {
        if (response.ok) {
            window.location.href = '/qcm/dashboard';
        } else {
            alert('Identifiants invalides');
        }
    })
    .catch(error => {
        console.error('[v0] Login error:', error);
        alert('Erreur lors de la connexion');
    });
});

document.getElementById('registerForm')?.addEventListener('submit', function(e) {
    e.preventDefault();
    
    const studentId = document.getElementById('student-id').value;
    const name = document.getElementById('reg-name').value;
    const firstname = document.getElementById('reg-firstname').value;
    const email = document.getElementById('reg-email').value;
    const level = document.getElementById('level').value;

    // Send to backend
    fetch('/qcm/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
            numeroetudia: studentId,
            nom: name,
            prenoms: firstname,
            email: email,
            niveau: level
        })
    })
    .then(response => {
        if (response.ok) {
            alert('Inscription réussie ! Vous pouvez maintenant vous connecter.');
            switchTab('login');
            document.getElementById('loginForm').reset();
        } else {
            alert('Erreur lors de l\'inscription');
        }
    })
    .catch(error => {
        console.error('[v0] Register error:', error);
        alert('Erreur lors de l\'inscription');
    });
});

// Initialize
document.addEventListener('DOMContentLoaded', function() {
    loadTheme();
    
    // Tab button click handlers
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            const tabName = this.getAttribute('data-tab');
            
            document.querySelectorAll('.tab-content').forEach(tab => {
                tab.classList.remove('active');
            });
            document.querySelectorAll('.tab-btn').forEach(b => {
                b.classList.remove('active');
            });
            
            document.getElementById(tabName).classList.add('active');
            this.classList.add('active');
        });
    });
});
