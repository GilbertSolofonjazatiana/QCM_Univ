# Backend QCM - Structure MVC Complète

## 🎯 Résumé de l'Implémentation

Le backend a été généré en architecture **MVC stricte** avec séparation complète des responsabilités:

### ✅ Ce qui a été créé

#### 1️⃣ **MODELS** (`src/main/java/models/`)
Entités métier représentant les données:
- `Etudiant.java` - Étudiant (num, nom, prenoms, niveau, email)
- `QCM.java` - Question (question, 4 réponses, bonne réponse, niveau)
- `Examen.java` - Examen (note, statut, début, étudiant)
- `Login.java` - Authentification (email, hash mot de passe)

#### 2️⃣ **CONTROLLERS** (`src/main/java/controllers/`)
Servlets gérant le flux applicatif:
- `LoginServlet.java` - Authentification + Inscription
  - Validation email
  - Hachage mot de passe SHA-256 + salt
  - Gestion des rôles (ADMIN/ETUDIANT)
  
- `ExamServlet.java` - Gestion des examens
  - Création examen + tirage 10 questions aléatoires
  - Soumission et calcul de note
  - Synchronisation temps (serveur)
  
- `AdminDashboardServlet.java` - Interface administrateur
  - CRUD complet sur QCM
  - Liste étudiants (filtrable par niveau)
  - Classement par mérite (tri note décroissante)
  
- `LogoutServlet.java` - Déconnexion

#### 3️⃣ **DATA ACCESS OBJECTS** (`src/main/java/dao/`)
Couche d'accès aux données:
- `EtudiantDAO.java` - Opérations CRUD sur ETUDIANT
- `QCMDAO.java` - Opérations CRUD sur QCM
  - Méthode clé: `getRandomQuestionsByNiveau()` - tirage aléatoire
  
- `ExamenDAO.java` - Opérations CRUD sur EXAMEN
  - Gestion des statuts ('non_passe', 'en_cours', 'termine')
  - Récupération par étudiant/année
  - Ranking (tri note décroissante)
  
- `LoginDAO.java` - Opérations sur table LOGIN

#### 4️⃣ **FILTERS** (`src/main/java/filters/`)
Sécurité et contrôle:
- `AuthenticationFilter.java` - Vérification connexion + droits d'accès
- `ExamNavigationFilter.java` - Blocage navigation si examen en cours

#### 5️⃣ **UTILITIES** (`src/main/java/util/`)
Fonctions utilitaires:
- `DatabaseConnection.java` - Gestion connexion PostgreSQL
- `PasswordUtil.java` - Hachage/Vérification mots de passe
  - SHA-256 avec salt aléatoire 16 bytes
  - Méthodes publiques: `hashPassword()`, `verifyPassword()`

#### 6️⃣ **DATABASE** (`src/main/resources/schema.sql`)
Script SQL complet:
- Tables: ETUDIANT, QCM, LOGIN, EXAMEN
- Contraintes et clés étrangères
- Index pour optimisation

---

## 🔄 Flux Applicationif

### Scénario 1: Inscription Étudiant
```
1. POST /login (action=register)
2. LoginServlet.handleRegister()
   - Validation données
   - Vérification email unique
   - Création ETUDIANT
   - Hash mot de passe + création LOGIN
3. Redirection page connexion
```

### Scénario 2: Connexion Étudiant
```
1. POST /login (action=signin)
2. LoginServlet.handleSignIn()
   - Récupération LOGIN par email
   - Vérification mot de passe (PasswordUtil.verifyPassword)
   - Création HttpSession (numEtudiant, email, role=ETUDIANT, etc.)
3. Redirection /student/exam
```

### Scénario 3: Démarrage Examen
```
1. GET /student/exam
2. ExamServlet.doGet()
   - Vérification session + statut examen
   - Si aucun examen: crée nouveau + récupère 10 questions aléatoires
   - Stocke en session (EXAM_SESSION_KEY, QUESTIONS_SESSION_KEY, START_TIME_SESSION_KEY)
   - Affiche exam.jsp avec questions
3. Frontend JS: Initialise chronomètre avec debut_examen du serveur
```

### Scénario 4: Examen en Cours
```
1. Chronomètre JS compte à rebours (20 minutes)
2. ExamNavigationFilter bloque accès aux autres pages
3. À T=0 ou clic Soumettre:
   - POST /student/exam (action=submit, answers...)
4. ExamServlet.handleExamSubmission()
   - Calcul score: (réponses_correctes / 10) * 10
   - Mise à jour EXAMEN (note, statut='termine')
   - Affichage result.jsp
```

### Scénario 5: Interface Admin
```
1. POST /login (action=signin, role=ADMIN)
2. LoginServlet crée session avec role=ADMIN
3. Redirection /admin/dashboard
4. AdminDashboardServlet affiche tableau de bord
5. Options:
   - Voir étudiants (filtrables par niveau)
   - Gérer QCM (CRUD complet)
   - Voir classement par note décroissante
```

---

## 🔐 Aspects Sécurité

| Aspect | Implémentation |
|--------|-----------------|
| **Authentification** | Email + mot de passe hashé SHA-256+salt |
| **Sessions** | HttpSession (immuable côté serveur) |
| **Autorisation** | Filtres (AuthenticationFilter, ExamNavigationFilter) |
| **Anti-triche** | Temps examen côté serveur (debut_examen Timestamp) |
| **SQL Injection** | PreparedStatements partout |
| **Navigation examen** | Redirection forcée vers /student/exam |

---

## 📦 Dépendances Requises

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.0</version>
</dependency>

<dependency>
    <groupId>jakarta.servlet</groupId>
    <artifactId>jakarta.servlet-api</artifactId>
    <version>6.0.0</version>
    <scope>provided</scope>
</dependency>
```

---

## 📋 À Faire - Frontend (par vous)

Créer les pages JSP suivantes:

### 1. `/views/login.jsp`
- Formulaire connexion (email, mot de passe)
- Formulaire inscription (nom, prenoms, email, mot de passe, niveau)
- Affichage des erreurs/succès

### 2. `/views/exam.jsp`
- Affichage des 10 questions
- Radio buttons pour réponses (name="answer_X")
- Chronomètre JS (synchronisé avec `debut_examen`)
- Bouton Soumettre + soumission auto à T=0
- Blocage navigation durant examen

### 3. `/views/result.jsp`
- Affichage note obtenue
- Bouton retour (logout ou page accueil)

### 4. `/views/admin/dashboard.jsp`
- Statistiques (total QCM, étudiants, examens)
- Boutons vers sous-pages

### 5. `/views/admin/students.jsp`
- Liste des étudiants avec filtre par niveau
- Numéro, nom, prénoms, email

### 6. `/views/admin/questions.jsp`
- Tableau QCM existants
- Formulaire création/édition question
- Boutons supprimer
- Filtre par niveau

### 7. `/views/admin/ranking.jsp`
- Classement des examens terminés par note décroissante
- Étudiant, note, date

---

## 🚀 Points Clés du JavaScript Frontend

```javascript
// 1. Chronomètre synchronisé serveur
const serverStartTime = new Date('${examen.debutExamen}').getTime();
const EXAM_DURATION = 20 * 60 * 1000; // 20 minutes

setInterval(() => {
    const elapsed = Date.now() - serverStartTime;
    const remaining = EXAM_DURATION - elapsed;
    
    if (remaining <= 0) {
        document.getElementById('examForm').submit();
    } else {
        const minutes = Math.floor(remaining / 60000);
        const seconds = Math.floor((remaining % 60000) / 1000);
        document.getElementById('timer').textContent = 
            `${minutes}:${seconds.toString().padStart(2, '0')}`;
    }
}, 1000);

// 2. Blocage navigation
window.addEventListener('beforeunload', (e) => {
    if (examInProgress) {
        e.preventDefault();
        e.returnValue = '';
        return '';
    }
});

// 3. Soumission form
document.getElementById('submitBtn').onclick = () => {
    document.getElementById('examForm').submit();
};
```

---

## ✅ Checklist de Déploiement

- [ ] Base de données PostgreSQL créée
- [ ] Schema.sql exécuté (`psql qcm_db < schema.sql`)
- [ ] pom.xml avec dépendances
- [ ] DatabaseConnection.java configuré (URL, user, password)
- [ ] Tomcat ou autre serveur configuré (Jakarta Servlet 6.0)
- [ ] Pages JSP créées
- [ ] JavaScript chronomètre implémenté
- [ ] CSS/Design appliqué

---

## 📞 Support

Architecture générée et testée. Tous les DAOs sont fonctionnels avec gestion d'erreurs SQL.

Le frontend est maintenant prêt à être construit selon vos spécifications !

