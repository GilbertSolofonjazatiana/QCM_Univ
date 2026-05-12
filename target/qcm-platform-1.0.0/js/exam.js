// Exam State
let examState = {
    questions: [],
    currentQuestion: 0,
    answers: {},
    timeLeft: 1200, // 20 minutes in seconds
    timerInterval: null
};

// Initialize exam
document.addEventListener('DOMContentLoaded', function() {
    loadExamQuestions();
    startTimer();
    setupEventListeners();
});

// Fetch exam questions from backend
function loadExamQuestions() {
    fetch('/qcm/exam-questions')
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                examState.questions = data.questions;
                displayQuestion();
            } else {
                alert('Erreur: Impossible de charger les questions');
                window.location.href = '/qcm/student-home.html';
            }
        })
        .catch(error => {
            console.error('[v0] Error loading questions:', error);
            alert('Erreur de connexion');
        });
}

// Display current question
function displayQuestion() {
    const question = examState.questions[examState.currentQuestion];
    if (!question) return;

    // Update question number and text
    document.getElementById('questionNum').textContent = examState.currentQuestion + 1;
    document.getElementById('questionText').textContent = question.texte;
    document.getElementById('progressText').textContent = 
        `${Object.keys(examState.answers).length}/${examState.questions.length}`;

    // Update remaining text
    const remaining = examState.questions.length - examState.currentQuestion - 1;
    const remainingText = remaining === 0 
        ? 'Dernière question' 
        : `${remaining} question${remaining > 1 ? 's' : ''} restante${remaining > 1 ? 's' : ''}`;
    document.getElementById('remainingText').textContent = remainingText;

    // Display options
    const optionsContainer = document.getElementById('optionsContainer');
    optionsContainer.innerHTML = '';

    question.options.forEach((option, index) => {
        const optionDiv = document.createElement('div');
        optionDiv.className = 'option';
        
        const savedAnswer = examState.answers[examState.currentQuestion];
        if (savedAnswer === index) {
            optionDiv.classList.add('selected');
        }

        optionDiv.innerHTML = `
            <input type="radio" id="option-${index}" name="answer" value="${index}" 
                   ${savedAnswer === index ? 'checked' : ''}>
            <label for="option-${index}">${option}</label>
        `;

        optionDiv.addEventListener('click', function(e) {
            // Remove selected class from all options
            document.querySelectorAll('.option').forEach(opt => opt.classList.remove('selected'));
            
            // Add selected class to this option
            this.classList.add('selected');
            
            // Save answer
            examState.answers[examState.currentQuestion] = index;
            
            console.log('[v0] Answer saved for question', examState.currentQuestion, ':', index);
        });

        optionsContainer.appendChild(optionDiv);
    });

    // Update exam title
    document.getElementById('examTitle').textContent = `Examen ${question.niveau}`;
}

// Timer management
function startTimer() {
    examState.timerInterval = setInterval(function() {
        examState.timeLeft--;
        updateTimerDisplay();

        if (examState.timeLeft <= 0) {
            clearInterval(examState.timerInterval);
            submitExamAuto();
        }
    }, 1000);
}

function updateTimerDisplay() {
    const minutes = Math.floor(examState.timeLeft / 60);
    const seconds = examState.timeLeft % 60;
    const timerDisplay = `${minutes}:${seconds.toString().padStart(2, '0')}`;
    
    const timerEl = document.getElementById('timer');
    timerEl.textContent = timerDisplay;

    // Change color based on time remaining
    if (examState.timeLeft <= 60) {
        timerEl.classList.add('danger');
        timerEl.classList.remove('warning');
    } else if (examState.timeLeft <= 300) {
        timerEl.classList.add('warning');
        timerEl.classList.remove('danger');
    }
}

// Event listeners
function setupEventListeners() {
    // Submit button
    document.getElementById('submitBtn')?.addEventListener('click', function() {
        openConfirm();
    });

    // Prevent leaving page without confirmation (optional)
    window.addEventListener('beforeunload', function(e) {
        if (examState.questions.length > 0) {
            e.preventDefault();
            e.returnValue = '';
        }
    });
}

// Confirmation dialog
function openConfirm() {
    document.getElementById('confirmModal').classList.add('active');
}

function closeConfirm() {
    document.getElementById('confirmModal').classList.remove('active');
}

// Submit exam
function submitExam() {
    closeConfirm();
    clearInterval(examState.timerInterval);

    // Prepare answers
    const answersArray = examState.questions.map((q, index) => ({
        questionId: q.id,
        selectedOption: examState.answers[index] !== undefined ? examState.answers[index] : -1
    }));

    console.log('[v0] Submitting exam with answers:', answersArray);

    // Send to backend
    fetch('/qcm/exam-submit', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            answers: answersArray,
            timeSpent: 1200 - examState.timeLeft
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            window.location.href = '/qcm/exam-result.html';
        } else {
            alert('Erreur lors de la soumission');
        }
    })
    .catch(error => {
        console.error('[v0] Error submitting exam:', error);
        alert('Erreur de connexion');
    });
}

// Auto submit on timeout
function submitExamAuto() {
    console.log('[v0] Auto-submitting exam due to timeout');
    document.getElementById('confirmModal').classList.remove('active');
    submitExam();
}

// Close modal on Escape key
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        closeConfirm();
    }
});

// Close modal when clicking outside
document.getElementById('confirmModal')?.addEventListener('click', function(e) {
    if (e.target === this) {
        closeConfirm();
    }
});
