package controllers;

import models.Examen;
import models.QCM;
import dao.ExamenDAO;
import dao.QCMDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/student/exam")
public class ExamServlet extends HttpServlet {

    private static final String EXAM_SESSION_KEY = "currentExam";
    private static final String QUESTIONS_SESSION_KEY = "examQuestions";
    private static final String START_TIME_SESSION_KEY = "examStartTime";
    private static final int EXAM_DURATION_MINUTES = 20;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Vérifier que l'utilisateur est connecté
        Integer numEtudiant = (Integer) session.getAttribute("numEtudiant");
        if (numEtudiant == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            String niveau = (String) session.getAttribute("niveau");
            String currentYear = String.valueOf(java.time.Year.now().getValue());

            // Récupérer l'examen actuel de l'étudiant
            Examen examen = ExamenDAO.getExamenByEtudiantAndAnnee(numEtudiant, currentYear);

            // Si aucun examen n'existe ou il est terminé, créer un nouveau
            if (examen == null || "termine".equals(examen.getStatut())) {
                // Récupérer 10 questions aléatoires
                List<QCM> questions = QCMDAO.getRandomQuestionsByNiveau(niveau, 10);
                
                if (questions.isEmpty()) {
                    request.setAttribute("error", "Aucune question disponible pour votre niveau");
                    request.getRequestDispatcher("/views/error.jsp").forward(request, response);
                    return;
                }

                // Créer un nouvel examen
                examen = new Examen();
                examen.setNumEtudiant(numEtudiant);
                examen.setAnneeUniv(currentYear);
                examen.setNote(0.0);
                examen.setStatut("en_cours");
                examen.setDebutExamen(new Timestamp(System.currentTimeMillis()));
                
                int numExamen = ExamenDAO.createExamen(examen);
                examen.setNumExamen(numExamen);

                // Stocker dans la session
                session.setAttribute(EXAM_SESSION_KEY, examen);
                session.setAttribute(QUESTIONS_SESSION_KEY, questions);
                session.setAttribute(START_TIME_SESSION_KEY, examen.getDebutExamen());
            } else if ("en_cours".equals(examen.getStatut())) {
                // L'examen est en cours, rediriger vers la page d'examen
                List<QCM> questions = (List<QCM>) session.getAttribute(QUESTIONS_SESSION_KEY);
                if (questions == null) {
                    // Récupérer les questions si elles ne sont pas en session
                    niveau = (String) session.getAttribute("niveau");
                    questions = QCMDAO.getRandomQuestionsByNiveau(niveau, 10);
                    session.setAttribute(QUESTIONS_SESSION_KEY, questions);
                }
                session.setAttribute(EXAM_SESSION_KEY, examen);
                if (session.getAttribute(START_TIME_SESSION_KEY) == null) {
                    session.setAttribute(START_TIME_SESSION_KEY, examen.getDebutExamen());
                }
            } else if ("termine".equals(examen.getStatut())) {
                // L'examen est déjà terminé, afficher le résultat
                request.setAttribute("examen", examen);
                request.getRequestDispatcher("/views/result.jsp").forward(request, response);
                return;
            }

            // Afficher la page d'examen
            List<QCM> questions = (List<QCM>) session.getAttribute(QUESTIONS_SESSION_KEY);
            request.setAttribute("questions", questions);
            request.setAttribute("examen", examen);
            request.getRequestDispatcher("/views/exam.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur de base de données");
            try {
                request.getRequestDispatcher("/views/error.jsp").forward(request, response);
            } catch (ServletException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Vérifier que l'utilisateur est connecté
        Integer numEtudiant = (Integer) session.getAttribute("numEtudiant");
        if (numEtudiant == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        
        if ("submit".equals(action)) {
            handleExamSubmission(request, response, session);
        }
    }

    /**
     * Traite la soumission de l'examen
     */
    private void handleExamSubmission(HttpServletRequest request, HttpServletResponse response, 
                                     HttpSession session) throws ServletException, IOException {
        try {
            Examen examen = (Examen) session.getAttribute(EXAM_SESSION_KEY);
            List<QCM> questions = (List<QCM>) session.getAttribute(QUESTIONS_SESSION_KEY);
            Integer numEtudiant = (Integer) session.getAttribute("numEtudiant");

            if (examen == null || questions == null) {
                request.setAttribute("error", "Erreur: Examen non trouvé");
                request.getRequestDispatcher("/views/error.jsp").forward(request, response);
                return;
            }

            // Calculer la note
            int correctAnswers = 0;
            for (QCM question : questions) {
                String answerParam = request.getParameter("answer_" + question.getNumQuestion());
                if (answerParam != null) {
                    try {
                        int answer = Integer.parseInt(answerParam);
                        if (answer == question.getBonneReponse()) {
                            correctAnswers++;
                        }
                    } catch (NumberFormatException e) {
                        // Ignore invalid answers
                    }
                }
            }

            // Calculer la note sur 10
            double note = (correctAnswers * 10.0) / questions.size();

            // Mettre à jour l'examen
            examen.setNote(note);
            examen.setStatut("termine");
            ExamenDAO.updateExamen(examen);

            // Nettoyer la session
            session.removeAttribute(EXAM_SESSION_KEY);
            session.removeAttribute(QUESTIONS_SESSION_KEY);
            session.removeAttribute(START_TIME_SESSION_KEY);

            // Rediriger vers la page de résultat
            request.setAttribute("examen", examen);
            request.getRequestDispatcher("/views/result.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors de la sauvegarde de l'examen");
            try {
                request.getRequestDispatcher("/views/error.jsp").forward(request, response);
            } catch (ServletException ex) {
                ex.printStackTrace();
            }
        }
    }
}
