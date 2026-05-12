// Admin Dashboard
document.addEventListener('DOMContentLoaded', function() {
    loadDashboardData();
    setupNavigation();
});

// Load dashboard data
function loadDashboardData() {
    fetch('/qcm/admin-dashboard-data')
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                updateStats(data);
                updateCharts(data);
            }
        })
        .catch(error => console.error('[v0] Error loading dashboard data:', error));
}

// Update statistics cards
function updateStats(data) {
    const stats = data.stats;
    
    document.querySelectorAll('.stat-card').forEach((card, index) => {
        const values = [
            stats.totalEtudiants,
            stats.totalQuestions,
            stats.totalExamens,
            parseFloat(stats.moyenneGlobale).toFixed(1)
        ];
        
        if (values[index] !== undefined) {
            const numberEl = card.querySelector('.stat-number');
            numberEl.textContent = values[index];
        }
    });
}

// Update charts
function updateCharts(data) {
    if (data.distribution) {
        updateDistributionChart(data.distribution);
    }
    
    if (data.performance) {
        updatePerformanceChart(data.performance);
    }
}

// Update student distribution chart
function updateDistributionChart(distribution) {
    const chartItems = document.querySelectorAll('.chart-content .chart-item');
    const levels = Object.keys(distribution).sort();
    const maxValue = Math.max(...Object.values(distribution));

    chartItems.forEach((item, index) => {
        const level = levels[index];
        if (level && distribution[level] !== undefined) {
            const percentage = (distribution[level] / maxValue) * 100;
            const barFill = item.querySelector('.bar-fill');
            const valueEl = item.querySelector('.chart-value');
            
            barFill.style.width = percentage + '%';
            valueEl.textContent = `${distribution[level]} étudiant${distribution[level] > 1 ? 's' : ''}`;
        }
    });
}

// Update performance chart
function updatePerformanceChart(performance) {
    const performanceList = document.querySelector('.performance-list');
    if (!performanceList) return;

    performanceList.innerHTML = '';

    Object.entries(performance).forEach(([level, data]) => {
        const item = document.createElement('div');
        item.className = 'performance-item';

        const percent = data.successRate || 0;
        let percentClass = 'green';
        if (percent < 60) percentClass = 'red';
        else if (percent < 75) percentClass = 'orange';

        item.innerHTML = `
            <div class="performance-header">
                <span class="level-badge">${level}</span>
                <span class="performance-stat">Moyenne: ${(data.moyenne || 0).toFixed(1)}/10</span>
            </div>
            <span class="performance-percent ${percentClass}">${percent.toFixed(0)}% de réussite</span>
        `;

        performanceList.appendChild(item);
    });
}

// Setup navigation
function setupNavigation() {
    const navItems = document.querySelectorAll('.nav-item');
    
    navItems.forEach((item, index) => {
        item.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Remove active class from all
            navItems.forEach(nav => nav.classList.remove('active'));
            
            // Add active class to clicked
            this.classList.add('active');
            
            // Navigate
            const routes = [
                '/qcm/admin-dashboard.html',
                '/qcm/admin-qcm.html',
                '/qcm/admin-students.html',
                '/qcm/admin-results.html',
                '/qcm/admin-ranking.html'
            ];
            
            if (routes[index]) {
                window.location.href = routes[index];
            }
        });
    });
}

// Refresh dashboard every 30 seconds
setInterval(function() {
    loadDashboardData();
}, 30000);
