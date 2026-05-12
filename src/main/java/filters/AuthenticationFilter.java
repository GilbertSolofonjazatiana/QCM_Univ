package filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filtre d'authentification pour vérifier que l'utilisateur est connecté
 */
@WebFilter({"/student/*", "/admin/*"})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        Integer numEtudiant = null;
        String role = null;

        if (session != null) {
            numEtudiant = (Integer) session.getAttribute("numEtudiant");
            role = (String) session.getAttribute("role");
        }

        // Si pas connecté, rediriger vers login
        if (numEtudiant == null || role == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        // Vérifier les droits d'accès
        String requestURI = httpRequest.getRequestURI();
        
        // Seuls les ADMINs peuvent accéder à /admin/*
        if (requestURI.contains("/admin/") && !"ADMIN".equals(role)) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Seuls les ETUDIANTs peuvent accéder à /student/*
        if (requestURI.contains("/student/") && !"ETUDIANT".equals(role)) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
