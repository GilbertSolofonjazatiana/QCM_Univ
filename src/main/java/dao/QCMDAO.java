package dao;

import models.QCM;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QCMDAO {

    /**
     * Récupère une question par son numéro
     */
    public static QCM getQCMByNum(int numQuestion) throws SQLException {
        String query = "SELECT * FROM QCM WHERE num_question = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, numQuestion);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToQCM(rs);
            }
        }
        return null;
    }

    /**
     * Récupère 10 questions aléatoires selon le niveau
     */
    public static List<QCM> getRandomQuestionsByNiveau(String niveau, int limit) throws SQLException {
        List<QCM> questions = new ArrayList<>();
        String query = "SELECT * FROM QCM WHERE qcm_niveau = ? ORDER BY RANDOM() LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, niveau);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                questions.add(mapResultSetToQCM(rs));
            }
        }
        return questions;
    }

    /**
     * Récupère toutes les questions d'un niveau
     */
    public static List<QCM> getQCMByNiveau(String niveau) throws SQLException {
        List<QCM> questions = new ArrayList<>();
        String query = "SELECT * FROM QCM WHERE qcm_niveau = ? ORDER BY num_question";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, niveau);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                questions.add(mapResultSetToQCM(rs));
            }
        }
        return questions;
    }

    /**
     * Récupère toutes les questions
     */
    public static List<QCM> getAllQCM() throws SQLException {
        List<QCM> questions = new ArrayList<>();
        String query = "SELECT * FROM QCM ORDER BY num_question";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                questions.add(mapResultSetToQCM(rs));
            }
        }
        return questions;
    }

    /**
     * Crée une nouvelle question
     */
    public static boolean createQCM(QCM qcm) throws SQLException {
        String query = "INSERT INTO QCM (question, reponse1, reponse2, reponse3, reponse4, bonne_reponse, qcm_niveau) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, qcm.getQuestion());
            stmt.setString(2, qcm.getReponse1());
            stmt.setString(3, qcm.getReponse2());
            stmt.setString(4, qcm.getReponse3());
            stmt.setString(5, qcm.getReponse4());
            stmt.setInt(6, qcm.getBonneReponse());
            stmt.setString(7, qcm.getQcmNiveau());
            stmt.executeUpdate();
            return true;
        }
    }

    /**
     * Met à jour une question
     */
    public static boolean updateQCM(QCM qcm) throws SQLException {
        String query = "UPDATE QCM SET question = ?, reponse1 = ?, reponse2 = ?, reponse3 = ?, reponse4 = ?, " +
                      "bonne_reponse = ?, qcm_niveau = ? WHERE num_question = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, qcm.getQuestion());
            stmt.setString(2, qcm.getReponse1());
            stmt.setString(3, qcm.getReponse2());
            stmt.setString(4, qcm.getReponse3());
            stmt.setString(5, qcm.getReponse4());
            stmt.setInt(6, qcm.getBonneReponse());
            stmt.setString(7, qcm.getQcmNiveau());
            stmt.setInt(8, qcm.getNumQuestion());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Supprime une question
     */
    public static boolean deleteQCM(int numQuestion) throws SQLException {
        String query = "DELETE FROM QCM WHERE num_question = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, numQuestion);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Mappe un ResultSet à une entité QCM
     */
    private static QCM mapResultSetToQCM(ResultSet rs) throws SQLException {
        return new QCM(
            rs.getInt("num_question"),
            rs.getString("question"),
            rs.getString("reponse1"),
            rs.getString("reponse2"),
            rs.getString("reponse3"),
            rs.getString("reponse4"),
            rs.getInt("bonne_reponse"),
            rs.getString("qcm_niveau")
        );
    }
}
