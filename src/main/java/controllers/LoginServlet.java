package controllers;

import models.Etudiant;
import models.Login;
import models.Examen;
import dao.LoginDAO;
import dao.EtudiantDAO;
import dao.ExamenDAO;
import util.PasswordUtil;
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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Afficher la page de connexion
        request.getRequestDispatcher("/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("signin".equals(action)) {
            handleSignIn(request, response);
        } else if ("register".equals(action)) {
            handleRegister(request, response);
        }
    }

    /**
     * Gère la connexion
     */
    private void handleSignIn(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String email = request.getParameter("email").trim();
            String password = request.getParameter("password");

            // Validation des paramètres
            if (email.isEmpty() || password == null || password.isEmpty()) {
                request.setAttribute("error", "Email et mot de passe sont requis");
                request.getRequestDispatcher("/views/login.jsp").forward(request, response);
                return;
            }

            // Récupérer les données de connexion
            Login login = LoginDAO.getLoginByCodeLog(email);
            if (login == null) {
                request.setAttribute("error", "Email ou mot de passe incorrect");
                request.getRequestDispatcher("/views/login.jsp").forward(request, response);
                return;
            }

            // Vérifier le mot de passe
            if (!PasswordUtil.verifyPassword(password, login.getPasswordHash())) {
                request.setAttribute("error", "Email ou mot de passe incorrect");
                request.getRequestDispatcher("/views/login.jsp").forward(request, response);
                return;
            }

            // Récupérer les informations de l'étudiant
            Etudiant etudiant = EtudiantDAO.getEtudiantByNum(login.getNumEtudiant());
            if (etudiant == null) {
                request.setAttribute("error", "Données étudiant introuvables");
                request.getRequestDispatcher("/views/login.jsp").forward(request, response);
                return;
            }

            // Créer la session
            HttpSession session = request.getSession(true);
            session.setAttribute("numEtudiant", etudiant.getNumEtudiant());
            session.setAttribute("email", etudiant.getAdrEmail());
            session.setAttribute("nom", etudiant.getNom());
            session.setAttribute("prenoms", etudiant.getPrenoms());
            session.setAttribute("niveau", etudiant.getNiveau());
            
            // Déterminer le rôle (ADMIN ou ETUDIANT)
            String role = "ETUDIANT";
            if ("ADMIN".equals(request.getParameter("role"))) {
                role = "ADMIN";
            }
            session.setAttribute("role", role);

            // Rediriger en fonction du rôle
            if ("ADMIN".equals(role)) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/student/exam");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur de base de données");
            try {
                request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            } catch (ServletException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Gère l'inscription
     */
    private void handleRegister(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String nom = request.getParameter("nom").trim();
            String prenoms = request.getParameter("prenoms").trim();
            String email = request.getParameter("email").trim();
            String password = request.getParameter("password");
            String passwordConfirm = request.getParameter("password_confirm");
            String niveau = request.getParameter("niveau");

            // Validations
            if (nom.isEmpty() || prenoms.isEmpty() || email.isEmpty() || 
                password == null || password.isEmpty() || niveau == null || niveau.isEmpty()) {
                request.setAttribute("error", "Tous les champs sont requis");
                request.getRequestDispatcher("/views/login.jsp").forward(request, response);
                return;
            }

            // Valider le format email
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                request.setAttribute("error", "Format d'email invalide");
                request.getRequestDispatcher("/views/login.jsp").forward(request, response);
                return;
            }

            // Vérifier la confirmation du mot de passe
            if (!password.equals(passwordConfirm)) {
                request.setAttribute("error", "Les mots de passe ne correspondent pas");
                request.getRequestDispatcher("/views/login.jsp").forward(request, response);
                return;
            }

            // Vérifier si l'email existe déjà
            if (EtudiantDAO.getEtudiantByEmail(email) != null) {
                request.setAttribute("error", "Cet email est déjà utilisé");
                request.getRequestDispatcher("/views/login.jsp").forward(request, response);
                return;
            }

            // Créer l'étudiant
            Etudiant etudiant = new Etudiant();
            etudiant.setNom(nom);
            etudiant.setPrenoms(prenoms);
            etudiant.setAdrEmail(email);
            etudiant.setNiveau(niveau);
            EtudiantDAO.createEtudiant(etudiant);

            // Récupérer l'étudiant créé
            Etudiant createdEtudiant = EtudiantDAO.getEtudiantByEmail(email);
            
            // Créer l'entrée LOGIN avec mot de passe hashé
            Login login = new Login();
            login.setCodeLog(email);
            login.setNumEtudiant(createdEtudiant.getNumEtudiant());
            login.setPasswordHash(PasswordUtil.hashPassword(password));
            LoginDAO.createLogin(login);

            request.setAttribute("success", "Inscription réussie ! Vous pouvez maintenant vous connecter.");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors de l'inscription");
            try {
                request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            } catch (ServletException ex) {
                ex.printStackTrace();
            }
        }
    }
}
