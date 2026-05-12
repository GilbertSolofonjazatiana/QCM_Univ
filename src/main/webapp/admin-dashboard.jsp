<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="models.Etudiant"%>

<%
    Etudiant admin = (Etudiant) session.getAttribute("admin");
    if (admin == null || !admin.getRole().equals("ADMIN")) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Administration - Plateforme QCM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body class="light-theme">
    <!-- Sidebar -->
    <aside class="sidebar">
        <div class="sidebar-header">
            <h2>Administration</h2>
            <p class="user-id">Tableau de bord</p>
        </div>
        <nav class="nav-menu">
            <a href="?page=home" class="nav-item active">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
                    <polyline points="9 22 9 12 15 12 15 22"/>
                </svg>
                Accueil
            </a>
            <a href="?page=qcm" class="nav-item">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/>
                    <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
                </svg>
                Gestion QCM
            </a>
            <a href="?page=students" class="nav-item">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                    <circle cx="9" cy="7" r="4"/>
                    <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
                    <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
                </svg>
                Étudiants
            </a>
            <a href="?page=results" class="nav-item">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="12" y1="5" x2="12" y2="19"/>
                    <polyline points="19 12 12 19 5 12"/>
                </svg>
                Résultats
            </a>
            <a href="?page=ranking" class="nav-item">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M6 9h12M6 9a3 3 0 0 1 3-3h6a3 3 0 0 1 3 3M6 9v10a3 3 0 0 0 3 3h6a3 3 0 0 0 3-3V9"/>
                </svg>
                Classement
            </a>
        </nav>

        <div class="admin-profile">
            <span class="admin-label">ADMINISTRATEUR</span>
            <p class="admin-name"><%=admin.getNom()%> <%=admin.getPrenom()%></p>
            <p class="admin-email"><%=admin.getEmail()%></p>
        </div>
    </aside>

    <!-- Main Content -->
    <main class="main-content">
        <!-- Top Bar -->
        <header class="top-bar">
            <div class="breadcrumb">
                <span>Tableau de bord</span>
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

        <!-- Dashboard Content -->
        <div class="content">
            <h1>Tableau de bord administrateur</h1>

            <!-- Stats Cards -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon" style="background: #E5E7EB;">
                        <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                            <circle cx="9" cy="7" r="4"/>
                            <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
                            <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
                        </svg>
                    </div>
                    <div class="stat-content">
                        <p class="stat-label">Total Étudiants</p>
                        <p class="stat-value" id="totalStudents">124</p>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon" style="background: #F3E8FF;">
                        <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/>
                            <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
                        </svg>
                    </div>
                    <div class="stat-content">
                        <p class="stat-label">Questions QCM</p>
                        <p class="stat-value" id="totalQuestions">87</p>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon" style="background: #E0F2FE;">
                        <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M9 11l3 3L22 4"/>
                            <path d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
                        </svg>
                    </div>
                    <div class="stat-content">
                        <p class="stat-label">Examens Passés</p>
                        <p class="stat-value" id="totalExams">98</p>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon" style="background: #F0F9FF;">
                        <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <line x1="12" y1="5" x2="12" y2="19"/>
                            <line x1="5" y1="12" x2="19" y2="12"/>
                        </svg>
                    </div>
                    <div class="stat-content">
                        <p class="stat-label">Moyenne Globale</p>
                        <p class="stat-value" id="averageScore">7.8</p>
                    </div>
                </div>
            </div>

            <!-- Charts Section -->
            <div class="charts-grid">
                <div class="chart-container">
                    <h3>Répartition des étudiants</h3>
                    <div id="distribution-chart"></div>
                </div>

                <div class="chart-container">
                    <h3>Taux de réussite par niveau</h3>
                    <div id="success-rate-chart"></div>
                </div>
            </div>

            <!-- Recent Activity -->
            <div class="recent-activity">
                <h3>Activités récentes</h3>
                <div id="activity-list">
                    <!-- Activities will be loaded here -->
                </div>
            </div>
        </div>
    </main>

    <script src="${pageContext.request.contextPath}/js/layout.js"></script>
    <script src="${pageContext.request.contextPath}/js/admin-dashboard.js"></script>
</body>
</html>
