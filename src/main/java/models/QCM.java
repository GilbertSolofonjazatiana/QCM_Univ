package models;

public class QCM {
    private int numQuestion;
    private String question;
    private String reponse1;
    private String reponse2;
    private String reponse3;
    private String reponse4;
    private int bonneReponse; // 1, 2, 3, ou 4
    private String qcmNiveau;

    public QCM() {}

    public QCM(int numQuestion, String question, String reponse1, String reponse2,
               String reponse3, String reponse4, int bonneReponse, String qcmNiveau) {
        this.numQuestion = numQuestion;
        this.question = question;
        this.reponse1 = reponse1;
        this.reponse2 = reponse2;
        this.reponse3 = reponse3;
        this.reponse4 = reponse4;
        this.bonneReponse = bonneReponse;
        this.qcmNiveau = qcmNiveau;
    }

    // Getters and Setters
    public int getNumQuestion() {
        return numQuestion;
    }

    public void setNumQuestion(int numQuestion) {
        this.numQuestion = numQuestion;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getReponse1() {
        return reponse1;
    }

    public void setReponse1(String reponse1) {
        this.reponse1 = reponse1;
    }

    public String getReponse2() {
        return reponse2;
    }

    public void setReponse2(String reponse2) {
        this.reponse2 = reponse2;
    }

    public String getReponse3() {
        return reponse3;
    }

    public void setReponse3(String reponse3) {
        this.reponse3 = reponse3;
    }

    public String getReponse4() {
        return reponse4;
    }

    public void setReponse4(String reponse4) {
        this.reponse4 = reponse4;
    }

    public int getBonneReponse() {
        return bonneReponse;
    }

    public void setBonneReponse(int bonneReponse) {
        this.bonneReponse = bonneReponse;
    }

    public String getQcmNiveau() {
        return qcmNiveau;
    }

    public void setQcmNiveau(String qcmNiveau) {
        this.qcmNiveau = qcmNiveau;
    }
}
