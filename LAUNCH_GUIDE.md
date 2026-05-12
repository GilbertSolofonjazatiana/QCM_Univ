# Guide de Lancement - Plateforme QCM

## 📋 Prérequis

### Backend (Java)
- **JDK 11+** (OpenJDK ou Oracle JDK)
- **Maven 3.6+** (pour compiler le projet Java)
- **PostgreSQL 12+** (base de données)
- **Tomcat 9+** (serveur d'application)

### Frontend (HTML/CSS/JS)
- Navigateur moderne (Chrome, Firefox, Safari, Edge)
- Connexion internet (si déploiement distant)

---

## 🚀 Installation Complète (Mode Local)

### 1️⃣ Préparation de la Base de Données

#### a) Créer la base PostgreSQL
```bash
# Se connecter à PostgreSQL
psql -U postgres

# Créer la base de données
CREATE DATABASE qcm_platform;
CREATE USER qcm_user WITH PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE qcm_platform TO qcm_user;

# Se déconnecter
\q
```

#### b) Exécuter le script SQL
```bash
# Depuis le répertoire du projet
psql -U qcm_user -d qcm_platform -f src/main/resources/schema.sql
```

**Résultat attendu :** Les tables `etudiants`, `qcm_questions`, `examen_results`, `login` sont créées.

---

### 2️⃣ Configuration de la Base de Données (Backend)

Créer le fichier : `src/main/java/util/config.properties`

```properties
# Configuration PostgreSQL
db.url=jdbc:postgresql://localhost:5432/qcm_platform
db.username=qcm_user
db.password=your_secure_password
db.driver=org.postgresql.Driver

# Configuration serveur
server.port=8080
server.host=localhost
```

**OU** modifier directement dans `DatabaseConnection.java` (ligne 14-17)

---

### 3️⃣ Compilation du Backend Java

```bash
# Dans le répertoire racine du projet
mvn clean package

# Ou si vous n'avez pas Maven, compiler manuellement avec javac
# (attention : plus complexe)
```

**Résultat attendu :** Un fichier `target/qcm-platform.war` est généré

---

### 4️⃣ Déploiement sur Tomcat

#### Option A : Déploiement Automatique
```bash
# 1. Télécharger et installer Tomcat
# https://tomcat.apache.org/download-9.cgi

# 2. Copier le WAR généré
cp target/qcm-platform.war /chemin/vers/tomcat/webapps/

# 3. Démarrer Tomcat
/chemin/vers/tomcat/bin/startup.sh  # Linux/Mac
C:\chemin\tomcat\bin\startup.bat    # Windows
```

#### Option B : Configuration Manuelle
```bash
# 1. Créer le dossier d'extraction
mkdir /chemin/vers/tomcat/webapps/qcm

# 2. Copier les fichiers
cp -r public/* /chemin/vers/tomcat/webapps/qcm/

# 3. Copier les classes Java compilées
cp -r target/classes/* /chemin/vers/tomcat/webapps/qcm/WEB-INF/classes/
```

---

### 5️⃣ Vérifier les Logs Tomcat

```bash
# Vérifier que Tomcat démarre sans erreur
tail -f /chemin/vers/tomcat/logs/catalina.out

# Résultats attendus :
# - "Tomcat started on port 8080"
# - Pas d'erreurs de connexion DB
```

---

### 6️⃣ Accéder à l'Application

Ouvrir un navigateur et aller à :

```
http://localhost:8080/qcm-platform
```

**Ou si déploiement simplifié :**
```
http://localhost:8080/qcm
```

---

## 📱 Test Complet

### Connexion Étudiant
1. Accédez à : `http://localhost:8080/qcm-platform`
2. Onglet **Connexion**
3. Email : `yao.kouassi@example.com`
4. Mot de passe : `123456` (ou numéro d'étudiant)
5. Cliquez : **Se connecter**

**Résultat attendu :**
- Redirection vers l'espace étudiant
- Affichage des examens disponibles
- Chronomètre synchronisé

### Espace Étudiant
1. Cliquez sur **Session** (ou Examen)
2. Vérifiez :
   - Affichage des 10 questions
   - Chronomètre compte à rebours (20 min)
   - Sélection des réponses
3. Cliquez : **Soumettre l'examen**

**Résultat attendu :**
- Les réponses sont envoyées au backend
- La note est calculée
- Redirection vers les résultats

### Connexion Admin
1. Retour à la page de connexion
2. Email : `admin@qcm.com`
3. Mot de passe : `admin123`
4. Cliquez : **Se connecter**

**Résultat attendu :**
- Accès au **Tableau de Bord Admin**
- Visualisation des statistiques
- Gestion des QCM et étudiants

---

## 🔧 Configuration Avancée

### 1. Variables d'Environnement (Optional)

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=qcm_platform
export DB_USER=qcm_user
export DB_PASSWORD=your_secure_password
export TOMCAT_HOME=/chemin/vers/tomcat
```

### 2. SSL/HTTPS (Production)

```bash
# Générer certificat SSL
keytool -genkey -alias tomcat -keyalg RSA -keystore keystore.jks

# Configurer dans tomcat/conf/server.xml
# (voir documentation Tomcat)
```

### 3. Pool de Connexions (Production)

Modifier `DatabaseConnection.java` pour utiliser HikariCP :
```java
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:postgresql://localhost:5432/qcm_platform");
config.setUsername("qcm_user");
config.setPassword("password");
config.setMaximumPoolSize(20);

HikariDataSource ds = new HikariDataSource(config);
```

---

## 🐛 Dépannage

### ❌ Erreur : "Connection refused"
**Cause :** PostgreSQL n'est pas démarré  
**Solution :**
```bash
# Linux
sudo systemctl start postgresql

# Mac
brew services start postgresql

# Windows (Services)
services.msc → PostgreSQL → Démarrer
```

### ❌ Erreur : "ClassNotFoundException: org.postgresql.Driver"
**Cause :** JDBC driver PostgreSQL manquant  
**Solution :** Ajouter à `pom.xml` :
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.6.0</version>
</dependency>
```

### ❌ Erreur : "Permission denied" (Tomcat)
**Solution :**
```bash
chmod +x /chemin/vers/tomcat/bin/*.sh
chmod -R 755 /chemin/vers/tomcat/webapps
```

### ❌ Chronomètre désynchronisé
**Cause :** JavaScript et serveur pas en phase  
**Solution :** Vérifier synchronisation dans `exam.js` ligne 45

---

## 📊 Fichiers de Logs à Vérifier

```
/chemin/vers/tomcat/logs/catalina.out       # Logs Tomcat
/chemin/vers/tomcat/logs/catalina.err       # Erreurs
/var/log/postgresql/postgresql.log          # Logs PostgreSQL
```

---

## ✅ Checklist de Lancement

- [ ] PostgreSQL démarré et accessible
- [ ] Base de données créée et tables initialisées
- [ ] pom.xml contient tous les dépendances
- [ ] `DatabaseConnection.java` configuré
- [ ] Projet Maven compilé sans erreurs
- [ ] WAR déployé dans Tomcat
- [ ] Tomcat démarré sans erreurs
- [ ] Page de login accessible sur `http://localhost:8080`
- [ ] Connexion avec identifiants de test
- [ ] Test exam → soumission → résultats
- [ ] Admin accessible et dashboard fonctionne

---

## 🚀 Production (Déploiement)

Pour déployer en production :

1. **Utiliser un serveur Tomcat distant** (AWS, Heroku, DigitalOcean)
2. **Configurer HTTPS/SSL**
3. **Ajouter load balancer** (Nginx, HAProxy)
4. **Configurer backup base données**
5. **Activer monitoring** (New Relic, Datadog)
6. **Utiliser reverse proxy** pour frontend statique

---

## 📞 Assistance Supplémentaire

Consultez les fichiers :
- `BACKEND_README.md` - Détails API endpoints
- `FRONTEND_README.md` - Structure frontend
- `BACKEND_STRUCTURE.md` - Architecture MVC

Bon lancement ! 🎓
