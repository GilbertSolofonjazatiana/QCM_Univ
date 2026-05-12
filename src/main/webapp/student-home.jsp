<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="models.Etudiant"%>

<%
    // Vérifier que l'utilisateur est connecté
    Etudiant etudiant = (Etudiant) session.getAttribute("etudiant");
    if (etudiant == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Espace Étudiant - Plateforme QCM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/student-home.css">
</head>
<body class="light-theme">
    <!-- Sidebar -->
    <aside class="sidebar">
        <div class="sidebar-header">
            <h2>Espace Étudiant</h2>
            <p class="user-id"><%=etudiant.getNom() + " " + etudiant.getPrenom()%></p>
        </div>
        <nav class="nav-menu">
            <a href="?page=home" class="nav-item active">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
                    <polyline points="9 22 9 12 15 12 15 22"/>
                </svg>
                Accueil
            </a>
            <a href="?page=session" class="nav-item">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/>
                    <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
                </svg>
                Session
            </a>
            <a href="?page=about" class="nav-item">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"/>
                    <path d="M12 16v-4m0-4h.01"/>
                </svg>
                À propos
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
            <div class="breadcrumb">
                <span>Accueil</span>
            </div>
            <div class="header-actions">
                <button id="themeBtn" class="theme-btn" onclick="toggleTheme()">🌙</button>
                <a href="${pageContext.request.contextPath}/logout" class="logout-btn">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
                        <polyline points="16 17 21 12 16 7"/>
                        <line x1="21" y1="12" x2="9" y2="12"/>
                    </svg>
                    Déconnexion
                </a>
            </div>
        </header>

        <!-- Content -->
        <div class="content">
            <h1>Bienvenue, <%=etudiant.getPrenom()%> !</h1>

            <c:if test="${not empty examInProgress}">
                <div class="alert alert-warning">
                    <div style="display: flex; gap: 10px;">
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                            <circle cx="12" cy="12" r="10"/>
                            <path d="M12 16v-4m0-4h.01" stroke="white" stroke-width="2" fill="none"/>
                        </svg>
                        <div>
                            <strong>Examen en attente</strong>
                            <p>Vous n&apos;avez pas encore passé l&apos;examen pour l&apos;année universitaire 2023-2024. Rendez-vous dans l&apos;onglet "Session" pour commencer.</p>
                        </div>
                    </div>
                </div>
            </c:if>

            <div class="info-cards">
                <div class="card">
                    <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <rect x="3" y="4" width="18" height="18" rx="2"/>
                        <path d="M16 2v4M8 2v4M3 10h18"/>
                    </svg>
                    <h3>Année universitaire</h3>
                    <p>2023-2024</p>
                </div>
                <div class="card">
                    <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <circle cx="12" cy="12" r="10"/>
                        <polyline points="12 6 12 12 16 14"/>
                    </svg>
                    <h3>Durée de l&apos;examen</h3>
                    <p>20 minutes</p>
                </div>
                <div class="card">
                    <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                    </svg>
                    <h3>Questions</h3>
                    <p>10 QCM</p>
                </div>
            </div>

            <div class="important-section">
                <h2>Informations importantes</h2>
                <ul class="info-list">
                    <li><strong>●</strong> Les questions sont adaptées à votre niveau actuel : <strong><%=etudiant.getNiveau()%></strong></li>
                    <li><strong>●</strong> Votre résultat sera envoyé automatiquement à votre email : <strong><%=etudiant.getEmail()%></strong></li>
                    <li><strong>●</strong> Une fois l&apos;examen commencé, vous ne pourrez plus quitter la session avant la fin</li>
                    <li><strong>●</strong> La soumission est automatique à la fin du temps imparti</li>
                </ul>
            </div>
        </div>
    </main>

    <script src="${pageContext.request.contextPath}/js/layout.js"></script>
    <script src="${pageContext.request.contextPath}/js/student-home.js"></script>
</body>
</html>
