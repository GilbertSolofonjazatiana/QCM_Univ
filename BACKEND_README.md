# Application QCM - Architecture Backend MVC

## 📋 Structure du Projet

```
src/main/java/
├── models/              # Entités métier
│   ├── Etudiant.java
│   ├── QCM.java
│   ├── Examen.java
│   └── Login.java
├── controllers/         # Servlets (Contrôleurs)
│   ├── LoginServlet.java
│   ├── ExamServlet.java
│   ├── AdminDashboardServlet.java
│   └── LogoutServlet.java
├── dao/                 # Data Access Objects (Modèle)
│   ├── EtudiantDAO.java
│   ├── QCMDAO.java
│   ├── ExamenDAO.java
│   └── LoginDAO.java
├── filters/             # Filtres (Sécurité)
│   ├── AuthenticationFilter.java
│   └── ExamNavigationFilter.java
└── util/                # Utilitaires
    ├── DatabaseConnection.java
    └── PasswordUtil.java
```

## 🏗️ Architecture MVC

### **Model** (Modèle)
- **Classes métier** (`models/`): Représentent les données de l'application
- **DAO** (`dao/`): Couche d'accès aux données PostgreSQL

### **View** (Présentation)
- Pages JSP (à créer côté frontend):
  - `/views/login.jsp` - Connexion et inscription
  - `/views/exam.jsp` - Page d'examen avec chronomètre
  - `/views/result.jsp` - Affichage des résultats
  - `/views/admin/` - Interface administrateur

### **Controller** (Contrôleur)
- **Servlets** (`controllers/`): Gèrent la logique métier et le flux
- **Filtres** (`filters/`): Authentification et contrôle de navigation

## 🔧 Configuration Requise

### Dépendances Maven
```xml
<!-- PostgreSQL Driver -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.0</version>
</dependency>

<!-- Jakarta Servlet API -->
<dependency>
    <groupId>jakarta.servlet</groupId>
    <artifactId>jakarta.servlet-api</artifactId>
    <version>6.0.0</version>
    <scope>provided</scope>
</dependency>
```

### Base de Données PostgreSQL
```bash
# Créer la base de données
createdb qcm_db

# Importer le schéma
psql qcm_db < src/main/resources/schema.sql
```

### Fichier `DatabaseConnection.java`
Modifiez les paramètres de connexion selon votre environnement:
```java
private static final String DB_URL = "jdbc:postgresql://localhost:5432/qcm_db";
private static final String DB_USER = "postgres";
private static final String DB_PASSWORD = "postgres";
```

## 📝 Workflow des Servlets

### 1. **LoginServlet** (`/login`)
- **GET**: Affiche la page de connexion
- **POST** `action=signin`: Authentification utilisateur
  - Validation email/mot de passe
  - Vérification avec BCrypt
  - Création de session avec rôle (ADMIN/ETUDIANT)
  
- **POST** `action=register`: Inscription
  - Validation des données
  - Hachage du mot de passe avec salt
  - Création étudiant + entrée LOGIN

### 2. **ExamServlet** (`/student/exam`)
- **GET**: 
  - Crée un nouvel examen si nécessaire
  - Récupère 10 questions aléatoires du niveau de l'étudiant
  - Initialise le chronomètre côté serveur (dans la session)
  - Affiche la page d'examen
  
- **POST** `action=submit`: Soumet l'examen
  - Calcule le score (/10)
  - Met à jour le statut → 'termine'
  - Affiche le résultat

### 3. **AdminDashboardServlet** (`/admin/dashboard`)
- **GET** `action=students`: Liste des étudiants (filtrable par niveau)
- **GET** `action=questions`: Liste des QCM (CRUD)
- **GET** `action=ranking`: Classement par mérite (notes décroissantes)
- **POST** CRUD complet sur les questions

### 4. **LogoutServlet** (`/logout`)
- Invalide la session
- Redirige vers la page de connexion

## 🔐 Sécurité

### Filtres
1. **AuthenticationFilter**
   - Vérifie que l'utilisateur est connecté
   - Contrôle d'accès par rôle (ADMIN/ETUDIANT)

2. **ExamNavigationFilter**
   - Bloque la navigation si examen en cours
   - Force la redirection vers `/student/exam`

### Hachage des Mots de Passe
- Utilise SHA-256 avec salt aléatoire
- Classe `PasswordUtil`:
  - `hashPassword()`: Hache avec salt
  - `verifyPassword()`: Vérification sécurisée

### HttpSession
- Temps restant d'examen stocké côté serveur (non localStorage)
- Impossible à falsifier côté client
- Clés de session:
  - `numEtudiant`: Numéro de l'étudiant
  - `currentExam`: Examen en cours
  - `examQuestions`: Liste des 10 questions
  - `examStartTime`: Timestamp de démarrage

## 📊 Flux d'Examen

```
1. Étudiant se connecte
   └─> Vérifié par LoginServlet
   
2. Accès à /student/exam
   └─> ExamServlet crée/vérifie l'examen
   └─> Génère 10 questions aléatoires (ORDER BY RANDOM())
   └─> Initialise debut_examen (Timestamp)
   └─> Stocke en session
   
3. Chronomètre (JavaScript côté frontend)
   └─> window.setInterval() chaque seconde
   └─> Calcule temps_restant = 20min - (now - debut_examen)
   └─> Soumission auto à T=0
   
4. Blocage de navigation
   └─> ExamNavigationFilter redirection si statut='en_cours'
   └─> Impossible de quitter la page d'examen
   
5. Soumission de l'examen
   └─> POST avec réponses
   └─> ExamServlet calcule le score
   └─> Mise à jour EXAMEN: note + statut='termine'
   └─> Affichage du résultat
```

## 🗄️ Schéma Base de Données

### ETUDIANT
```
num_etudiant (SERIAL PK)
nom (VARCHAR)
prenoms (VARCHAR)
niveau (VARCHAR) - ex: "L1", "L2", "L3"
adr_email (VARCHAR, UNIQUE)
```

### QCM
```
num_question (SERIAL PK)
question (TEXT)
reponse1, reponse2, reponse3, reponse4 (VARCHAR)
bonne_reponse (INT: 1-4)
qcm_niveau (VARCHAR)
```

### LOGIN
```
code_log (VARCHAR PK) - email
num_etudiant (INT FK)
password_hash (VARCHAR) - SHA-256 avec salt
```

### EXAMEN
```
num_examen (SERIAL PK)
num_etudiant (INT FK)
annee_univ (VARCHAR) - ex: "2024"
note (DECIMAL 0-10)
statut (VARCHAR) - 'non_passe' | 'en_cours' | 'termine'
debut_examen (TIMESTAMP)
```

## 🚀 Points Clés à Implémenter Côté Frontend

### JavaScript Chronomètre
```javascript
// Synchronisé avec debut_examen du serveur
setInterval(() => {
    const elapsed = (Date.now() - serverStartTime) / 1000;
    const remaining = 1200 - elapsed; // 20 * 60 secondes
    
    if (remaining <= 0) {
        // Soumission automatique
        document.getElementById('examForm').submit();
    }
}, 1000);
```

### Form Soumission
```html
<form id="examForm" method="POST" action="/student/exam">
    <input type="hidden" name="action" value="submit">
    
    <!-- Pour chaque question -->
    <div>
        <p>Question 1: ...</p>
        <input type="radio" name="answer_1" value="1"> Réponse 1
        <input type="radio" name="answer_1" value="2"> Réponse 2
        <!-- etc -->
    </div>
</form>
```

## 📌 Notes Importantes

1. **Timestamp du serveur**: Utilisé pour la source de vérité du chronomètre
2. **Rôle ADMIN**: Actuellement fixe à un compte par défaut
3. **10 questions aléatoires**: `ORDER BY RANDOM() LIMIT 10` PostgreSQL
4. **Scoring**: (réponses_correctes / 10) * 10
5. **Email**: Actuellement pas d'envoi automatique (à implémenter avec JavaMail)

## 🔗 Routes Disponibles

```
GET  /login                      - Page de connexion
POST /login                      - SignIn ou Register
GET  /logout                     - Déconnexion
GET  /student/exam               - Page examen
POST /student/exam               - Soumettre réponses
GET  /admin/dashboard            - Dashboard admin
GET  /admin/dashboard?action=... - Sous-pages admin
POST /admin/dashboard            - CRUD questions
```

## ✅ Checklist d'Implémentation Frontend

- [ ] Page login.jsp (connexion + inscription)
- [ ] Page exam.jsp (affichage questions + chronomètre)
- [ ] JavaScript chronomètre (synchronisé serveur)
- [ ] Page result.jsp (affichage note)
- [ ] Admin dashboard (listes + CRUD)
- [ ] CSS/Design

---

**Backend généré selon l'architecture MVC stricte. Frontend à créer comme demandé.**
