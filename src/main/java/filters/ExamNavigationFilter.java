package filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Examen;

import java.io.IOException;

/**
 * Filtre pour bloquer la navigation quand un examen est en cours
 * Redirige l'utilisateur vers la page d'examen si son statut est 'en_cours'
 */
@WebFilter({"/student/*", "/admin/*"})
public class ExamNavigationFilter implements Filter {

    private static final String EXAM_SESSION_KEY = "currentExam";

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        // Si pas de session, laisser passer (le servlet gérera la redirection)
        if (session == null) {
            chain.doFilter(request, response);
            return;
        }

        // Récupérer l'examen actuel
        Examen examen = (Examen) session.getAttribute(EXAM_SESSION_KEY);

        // Si un examen est en cours et l'utilisateur essaie d'accéder à une autre page
        // que la page d'examen, le rediriger
        if (examen != null && "en_cours".equals(examen.getStatut())) {
            String requestURI = httpRequest.getRequestURI();
            
            // Autoriser l'accès à la page d'examen et au logout
            if (!requestURI.contains("/student/exam") && !requestURI.contains("/logout")) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/student/exam");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
