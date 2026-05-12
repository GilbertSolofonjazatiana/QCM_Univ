package models;

public class Login {
    private String codeLog;
    private int numEtudiant;
    private String passwordHash;

    public Login() {}

    public Login(String codeLog, int numEtudiant, String passwordHash) {
        this.codeLog = codeLog;
        this.numEtudiant = numEtudiant;
        this.passwordHash = passwordHash;
    }

    // Getters and Setters
    public String getCodeLog() {
        return codeLog;
    }

    public void setCodeLog(String codeLog) {
        this.codeLog = codeLog;
    }

    public int getNumEtudiant() {
        return numEtudiant;
    }

    public void setNumEtudiant(int numEtudiant) {
        this.numEtudiant = numEtudiant;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
