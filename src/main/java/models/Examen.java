package models;

import java.sql.Timestamp;

public class Examen {
    private int numExamen;
    private int numEtudiant;
    private String anneeUniv;
    private Double note;
    private String statut; // 'non_passe', 'en_cours', 'termine'
    private Timestamp debutExamen;

    public Examen() {}

    public Examen(int numExamen, int numEtudiant, String anneeUniv, Double note, 
                  String statut, Timestamp debutExamen) {
        this.numExamen = numExamen;
        this.numEtudiant = numEtudiant;
        this.anneeUniv = anneeUniv;
        this.note = note;
        this.statut = statut;
        this.debutExamen = debutExamen;
    }

    // Getters and Setters
    public int getNumExamen() {
        return numExamen;
    }

    public void setNumExamen(int numExamen) {
        this.numExamen = numExamen;
    }

    public int getNumEtudiant() {
        return numEtudiant;
    }

    public void setNumEtudiant(int numEtudiant) {
        this.numEtudiant = numEtudiant;
    }

    public String getAnneeUniv() {
        return anneeUniv;
    }

    public void setAnneeUniv(String anneeUniv) {
        this.anneeUniv = anneeUniv;
    }

    public Double getNote() {
        return note;
    }

    public void setNote(Double note) {
        this.note = note;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Timestamp getDebutExamen() {
        return debutExamen;
    }

    public void setDebutExamen(Timestamp debutExamen) {
        this.debutExamen = debutExamen;
    }
}
