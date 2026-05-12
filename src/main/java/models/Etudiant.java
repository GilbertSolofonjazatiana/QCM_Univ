package models;

public class Etudiant {
    private int numEtudiant;
    private String nom;
    private String prenoms;
    private String niveau;
    private String adrEmail;

    public Etudiant() {}

    public Etudiant(int numEtudiant, String nom, String prenoms, String niveau, String adrEmail) {
        this.numEtudiant = numEtudiant;
        this.nom = nom;
        this.prenoms = prenoms;
        this.niveau = niveau;
        this.adrEmail = adrEmail;
    }

    // Getters and Setters
    public int getNumEtudiant() {
        return numEtudiant;
    }

    public void setNumEtudiant(int numEtudiant) {
        this.numEtudiant = numEtudiant;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenoms() {
        return prenoms;
    }

    public void setPrenoms(String prenoms) {
        this.prenoms = prenoms;
    }

    public String getNiveau() {
        return niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    public String getAdrEmail() {
        return adrEmail;
    }

    public void setAdrEmail(String adrEmail) {
        this.adrEmail = adrEmail;
    }
}
