package controllers;

import models.Etudiant;
import models.Examen;
import models.QCM;
import dao.EtudiantDAO;
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
import java.util.List;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Vérifier que l'utilisateur est un admin
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            String action = request.getParameter("action");
            
            if ("students".equals(action)) {
                handleStudentsList(request, response);
            } else if ("questions".equals(action)) {
                handleQuestionsList(request, response);
            } else if ("ranking".equals(action)) {
                handleRanking(request, response);
            } else {
                // Afficher le dashboard par défaut
                displayDashboard(request, response);
            }

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

    /**
     * Affiche le dashboard admin principal
     */
    private void displayDashboard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        String currentYear = String.valueOf(java.time.Year.now().getValue());
        
        // Récupérer les statistiques
        List<Examen> exams = ExamenDAO.getExamensByAnnee(currentYear);
        List<QCM> questions = QCMDAO.getAllQCM();
        List<Etudiant> etudiants = EtudiantDAO.getAllEtudiants();

        request.setAttribute("totalExams", exams.size());
        request.setAttribute("totalQuestions", questions.size());
        request.setAttribute("totalStudents", etudiants.size());
        request.setAttribute("completedExams", 
            exams.stream().filter(e -> "termine".equals(e.getStatut())).count());

        request.getRequestDispatcher("/views/admin/dashboard.jsp").forward(request, response);
    }

    /**
     * Affiche la liste des étudiants filtrée par niveau
     */
    private void handleStudentsList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        String niveau = request.getParameter("niveau");
        List<Etudiant> etudiants;

        if (niveau != null && !niveau.isEmpty()) {
            etudiants = EtudiantDAO.getEtudiantsByNiveau(niveau);
        } else {
            etudiants = EtudiantDAO.getAllEtudiants();
        }

        request.setAttribute("etudiants", etudiants);
        request.setAttribute("niveau", niveau);
        request.getRequestDispatcher("/views/admin/students.jsp").forward(request, response);
    }

    /**
     * Affiche la liste des questions
     */
    private void handleQuestionsList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        String niveau = request.getParameter("niveau");
        List<QCM> questions;

        if (niveau != null && !niveau.isEmpty()) {
            questions = QCMDAO.getQCMByNiveau(niveau);
        } else {
            questions = QCMDAO.getAllQCM();
        }

        request.setAttribute("questions", questions);
        request.setAttribute("niveau", niveau);
        request.getRequestDispatcher("/views/admin/questions.jsp").forward(request, response);
    }

    /**
     * Affiche le classement par mérite (tri par note décroissante)
     */
    private void handleRanking(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        String currentYear = String.valueOf(java.time.Year.now().getValue());
        List<Examen> ranking = ExamenDAO.getRankingByAnnee(currentYear);

        request.setAttribute("ranking", ranking);
        request.getRequestDispatcher("/views/admin/ranking.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("create_question".equals(action)) {
                handleCreateQuestion(request, response);
            } else if ("update_question".equals(action)) {
                handleUpdateQuestion(request, response);
            } else if ("delete_question".equals(action)) {
                handleDeleteQuestion(request, response);
            }
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

    /**
     * Crée une nouvelle question
     */
    private void handleCreateQuestion(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        String question = request.getParameter("question");
        String reponse1 = request.getParameter("reponse1");
        String reponse2 = request.getParameter("reponse2");
        String reponse3 = request.getParameter("reponse3");
        String reponse4 = request.getParameter("reponse4");
        int bonneReponse = Integer.parseInt(request.getParameter("bonne_reponse"));
        String niveau = request.getParameter("niveau");

        QCM qcm = new QCM();
        qcm.setQuestion(question);
        qcm.setReponse1(reponse1);
        qcm.setReponse2(reponse2);
        qcm.setReponse3(reponse3);
        qcm.setReponse4(reponse4);
        qcm.setBonneReponse(bonneReponse);
        qcm.setQcmNiveau(niveau);

        QCMDAO.createQCM(qcm);
        
        response.sendRedirect(request.getContextPath() + "/admin/dashboard?action=questions");
    }

    /**
     * Met à jour une question
     */
    private void handleUpdateQuestion(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        int numQuestion = Integer.parseInt(request.getParameter("num_question"));
        String question = request.getParameter("question");
        String reponse1 = request.getParameter("reponse1");
        String reponse2 = request.getParameter("reponse2");
        String reponse3 = request.getParameter("reponse3");
        String reponse4 = request.getParameter("reponse4");
        int bonneReponse = Integer.parseInt(request.getParameter("bonne_reponse"));
        String niveau = request.getParameter("niveau");

        QCM qcm = new QCM();
        qcm.setNumQuestion(numQuestion);
        qcm.setQuestion(question);
        qcm.setReponse1(reponse1);
        qcm.setReponse2(reponse2);
        qcm.setReponse3(reponse3);
        qcm.setReponse4(reponse4);
        qcm.setBonneReponse(bonneReponse);
        qcm.setQcmNiveau(niveau);

        QCMDAO.updateQCM(qcm);
        
        response.sendRedirect(request.getContextPath() + "/admin/dashboard?action=questions");
    }

    /**
     * Supprime une question
     */
    private void handleDeleteQuestion(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        int numQuestion = Integer.parseInt(request.getParameter("num_question"));
        QCMDAO.deleteQCM(numQuestion);
        
        response.sendRedirect(request.getContextPath() + "/admin/dashboard?action=questions");
    }
}
