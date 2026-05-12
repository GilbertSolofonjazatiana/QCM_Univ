# Structure des Fichiers JSP

## Fichiers JSP créés

Les fichiers JSP sont situés dans : **`src/main/webapp/`**

```
src/main/webapp/
├── login.jsp                 ← Page de connexion/inscription
├── student-home.jsp          ← Accueil étudiant
├── exam.jsp                  ← Interface d'examen avec questions QCM
├── admin-dashboard.jsp       ← Tableau de bord administrateur
```

## Ressources Statiques

Les fichiers CSS et JavaScript sont dans : **`public/`**

```
public/
├── css/
│   ├── login.css             ← Styles page connexion
│   ├── layout.css            ← Styles partagés (sidebar, top bar)
│   ├── student-home.css      ← Styles page accueil étudiant
│   ├── exam.css              ← Styles page examen
│   └── admin.css             ← Styles panel admin
├── js/
│   ├── login.js              ← Logique connexion/inscription
│   ├── layout.js             ← Logique partagée (navigation, thème)
│   ├── student-home.js       ← Logique accueil étudiant
│   ├── exam.js               ← Logique examen + chronomètre
│   └── admin-dashboard.js    ← Logique dashboard admin
└── html/
    ├── login.html
    ├── student-home.html
    ├── exam.html
    └── admin-dashboard.html
```

## Configuration Tomcat

Pour que Tomcat serve ces fichiers, assurez-vous que votre `web.xml` contient :

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee 
    http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
    version="4.0">

    <display-name>QCM Platform</display-name>

    <!-- Servlets -->
    <servlet>
        <servlet-name>LoginServlet</servlet-name>
        <servlet-class>controllers.LoginServlet</servlet-class>
    </servlet>

    <servlet-mapping>
        <servlet-name>LoginServlet</servlet-name>
        <url-pattern>/login</url-pattern>
    </servlet-mapping>

    <servlet>
        <servlet-name>LogoutServlet</servlet-name>
        <servlet-class>controllers.LogoutServlet</servlet-class>
    </servlet>

    <servlet-mapping>
        <servlet-name>LogoutServlet</servlet-name>
        <url-pattern>/logout</url-pattern>
    </servlet-mapping>

    <servlet>
        <servlet-name>ExamServlet</servlet-name>
        <servlet-class>controllers.ExamServlet</servlet-class>
    </servlet>

    <servlet-mapping>
        <servlet-name>ExamServlet</servlet-name>
        <url-pattern>/exam</url-pattern>
    </servlet-mapping>

    <servlet>
        <servlet-name>AdminDashboardServlet</servlet-name>
        <servlet-class>controllers.AdminDashboardServlet</servlet-class>
    </servlet>

    <servlet-mapping>
        <servlet-name>AdminDashboardServlet</servlet-name>
        <url-pattern>/admin</url-pattern>
    </servlet-mapping>

    <!-- Filtres de sécurité -->
    <filter>
        <filter-name>AuthenticationFilter</filter-name>
        <filter-class>filters.AuthenticationFilter</filter-class>
    </filter>

    <filter-mapping>
        <filter-name>AuthenticationFilter</filter-name>
        <url-pattern>/admin/*</url-pattern>
    </filter-mapping>

    <filter>
        <filter-name>ExamNavigationFilter</filter-name>
        <filter-class>filters.ExamNavigationFilter</filter-class>
    </filter>

    <filter-mapping>
        <filter-name>ExamNavigationFilter</filter-name>
        <url-pattern>/exam</url-pattern>
    </filter-mapping>

    <!-- Session -->
    <session-config>
        <cookie-config>
            <secure>false</secure>
            <http-only>true</http-only>
        </cookie-config>
        <tracking-mode>COOKIE</tracking-mode>
    </session-config>

    <!-- Erreurs -->
    <error-page>
        <error-code>404</error-code>
        <location>/login.jsp</location>
    </error-page>

    <error-page>
        <error-code>500</error-code>
        <location>/error.jsp</location>
    </error-page>

</web-app>
```

## Flux des Pages

### Étudiant

1. **login.jsp** → Connexion avec email + password
2. **student-home.jsp** → Affichage des informations d'examen
3. **exam.jsp** → Interface d'examen avec 10 questions QCM + chronomètre

### Administrateur

1. **login.jsp** → Connexion avec rôle ADMIN
2. **admin-dashboard.jsp** → Tableau de bord avec statistiques

## Comment les JSP communiquent avec le Backend

### Request Flow

1. **JSP affiche le formulaire HTML** → utilisateur le remplit
2. **JavaScript le récupère** → valide côté client
3. **Soumission POST/GET** → vers le Servlet Java
4. **Servlet traite** → appelle DAO, accède BDD, prépare réponse
5. **Servlet forward/redirect** → vers la JSP suivante
6. **JSP reçoit les données** → via `request.getAttribute()` ou `session.getAttribute()`
7. **JSP affiche le résultat** → HTML + données dynamiques

### Exemple : Soumettre l'examen

```html
<!-- exam.jsp -->
<form id="submitForm" action="${pageContext.request.contextPath}/exam" method="POST">
    <input type="hidden" name="action" value="submit">
    <input type="hidden" id="answersInput" name="answers">
    <button type="submit">Soumettre l'examen</button>
</form>
```

```javascript
// exam.js
document.getElementById('submitForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const answers = JSON.stringify(studentAnswers);
    document.getElementById('answersInput').value = answers;
    this.submit();
});
```

```java
// ExamServlet.java
protected void doPost(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    String action = request.getParameter("action");
    if ("submit".equals(action)) {
        String answersJson = request.getParameter("answers");
        // Traiter les réponses, calculer la note
        // Envoyer un email avec le résultat
        // Rediriger vers page de résultat
    }
}
```

## Intégration CSS/JS

Dans les JSP, les chemins vers CSS/JS utilisent `${pageContext.request.contextPath}` :

```jsp
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
<script src="${pageContext.request.contextPath}/js/login.js"></script>
```

Cela fonctionne indépendamment du nom du contexte (qcm-platform, app, etc.)

## Déploiement

1. **Compiler le projet Maven** → `mvn clean package`
2. **Copier le WAR** → `target/qcm-platform.war` vers `tomcat/webapps/`
3. **Tomcat démarre** → déploie le WAR, extrait les fichiers JSP et statiques
4. **Accès** → `http://localhost:8080/qcm-platform/login`

Les JSP sont compilées à la première requête et cachées dans `tomcat/work/Catalina/localhost/qcm-platform/`
