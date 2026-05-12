<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="models.Etudiant, models.QCM, java.util.List"%>

<%
    Etudiant etudiant = (Etudiant) session.getAttribute("etudiant");
    if (etudiant == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    @SuppressWarnings("unchecked")
    List<QCM> questions = (List<QCM>) request.getAttribute("questions");
    int currentQuestion = request.getAttribute("currentQuestion") != null ? (int) request.getAttribute("currentQuestion") : 0;
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Examen - Plateforme QCM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/exam.css">
</head>
<body class="light-theme">
    <!-- Sidebar -->
    <aside class="sidebar">
        <div class="sidebar-header">
            <h2>Espace Étudiant</h2>
            <p class="user-id"><%=etudiant.getNom() + " " + etudiant.getPrenom()%></p>
        </div>
        <nav class="nav-menu">
            <a href="${pageContext.request.contextPath}/student-home" class="nav-item">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
                    <polyline points="9 22 9 12 15 12 15 22"/>
                </svg>
                Accueil
            </a>
            <a href="#" class="nav-item active">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/>
                    <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
                </svg>
                Session
            </a>
        </nav>

        <div class="student-info">
            <p><strong>Numéro étudiant</strong><br><%=etudiant.getNumeroEtudiant()%></p>
            <p><strong>Niveau</strong><br><%=etudiant.getNiveau()%></p>
        </div>
    </aside>

    <!-- Main Content -->
    <main class="main-content">
        <!-- Top Bar -->
        <header class="top-bar">
            <div class="exam-title">
                <h2>Examen L1</h2>
                <p>Test Étudiant</p>
            </div>
            <div class="exam-stats">
                <div class="stat">
                    <span class="label">Progression</span>
                    <span class="value" id="progress">0/10</span>
                </div>
                <div class="stat timer">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <circle cx="12" cy="12" r="10"/>
                        <polyline points="12 6 12 12 16 14"/>
                    </svg>
                    <div>
                        <span class="label">Temps restant</span>
                        <span class="value" id="timer">19:54</span>
                    </div>
                </div>
            </div>
            <div class="header-actions">
                <button id="themeBtn" class="theme-btn" onclick="toggleTheme()">🌙</button>
            </div>
        </header>

        <!-- Exam Content -->
        <div class="content exam-content">
            <c:if test="${not empty questions}">
                <div id="question-container">
                    <!-- Les questions seront chargées ici par JavaScript -->
                </div>

                <div class="exam-footer">
                    <span class="questions-remaining" id="remaining">10 questions restantes</span>
                    <form id="submitForm" action="${pageContext.request.contextPath}/exam" method="POST">
                        <input type="hidden" name="action" value="submit">
                        <input type="hidden" id="answersInput" name="answers">
                        <button type="submit" class="btn btn-primary">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"/>
                                <polyline points="17 21 17 13 7 13 7 21"/>
                                <polyline points="7 3 7 8 15 8"/>
                            </svg>
                            Soumettre l&apos;examen
                        </button>
                    </form>
                </div>
            </c:if>

            <c:if test="${empty questions}">
                <div class="alert alert-error">
                    <strong>Erreur :</strong> Impossible de charger les questions. Veuillez réessayer.
                </div>
            </c:if>
        </div>
    </main>

    <!-- Data for JavaScript -->
    <script>
        const questionsData = ${questionsJson};
        const studentNiveau = '<%=etudiant.getNiveau()%>';
        const contextPath = '${pageContext.request.contextPath}';
    </script>

    <script src="${pageContext.request.contextPath}/js/layout.js"></script>
    <script src="${pageContext.request.contextPath}/js/exam.js"></script>
</body>
</html>
