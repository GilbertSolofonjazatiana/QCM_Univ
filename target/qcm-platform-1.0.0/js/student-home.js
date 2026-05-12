// Navigation handlers
document.addEventListener('DOMContentLoaded', function() {
    const navItems = document.querySelectorAll('.nav-item');
    
    navItems.forEach((item, index) => {
        item.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Remove active class from all
            navItems.forEach(nav => nav.classList.remove('active'));
            
            // Add active class to clicked
            this.classList.add('active');
            
            // Navigate based on index
            const routes = [
                '/qcm/student-home.html',
                '/qcm/exam.html',
                '#'
            ];
            
            if (routes[index] && routes[index] !== '#') {
                window.location.href = routes[index];
            }
        });
    });

    // Start exam button (if needed on this page)
    const startExamBtn = document.querySelector('[data-action="start-exam"]');
    if (startExamBtn) {
        startExamBtn.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Request exam from backend
            fetch('/qcm/exam-start', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            })
            .then(response => {
                if (response.ok) {
                    window.location.href = '/qcm/exam.html';
                } else {
                    alert('Impossible de démarrer l\'examen');
                }
            })
            .catch(error => {
                console.error('[v0] Error starting exam:', error);
                alert('Erreur lors du démarrage de l\'examen');
            });
        });
    }
});

// Load dynamic content if needed
function loadStudentData() {
    fetch('/qcm/student-data')
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // Update exam status
                const examSection = document.querySelector('.alert-box');
                if (data.examStarted) {
                    examSection.classList.remove('alert-warning');
                    examSection.classList.add('alert-success');
                    examSection.innerHTML = `
                        <div class="alert-icon">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <polyline points="20 6 9 17 4 12"></polyline>
                            </svg>
                        </div>
                        <div class="alert-content">
                            <h3>Examen en cours</h3>
                            <p>Vous avez un examen en cours. Continuez votre session.</p>
                        </div>
                    `;
                }
            }
        })
        .catch(error => console.error('[v0] Error loading student data:', error));
}

// Initialize
document.addEventListener('DOMContentLoaded', function() {
    loadStudentData();
});
