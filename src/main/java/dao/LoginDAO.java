package dao;

import models.Login;
import util.DatabaseConnection;
import java.sql.*;

public class LoginDAO {

    /**
     * Crée une nouvelle entrée LOGIN
     */
    public static boolean createLogin(Login login) throws SQLException {
        String query = "INSERT INTO LOGIN (code_log, num_etudiant, password_hash) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, login.getCodeLog());
            stmt.setInt(2, login.getNumEtudiant());
            stmt.setString(3, login.getPasswordHash());
            stmt.executeUpdate();
            return true;
        }
    }

    /**
     * Récupère une entrée LOGIN par code_log (email)
     */
    public static Login getLoginByCodeLog(String codeLog) throws SQLException {
        String query = "SELECT * FROM LOGIN WHERE code_log = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, codeLog);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToLogin(rs);
            }
        }
        return null;
    }

    /**
     * Récupère une entrée LOGIN par numéro d'étudiant
     */
    public static Login getLoginByEtudiant(int numEtudiant) throws SQLException {
        String query = "SELECT * FROM LOGIN WHERE num_etudiant = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, numEtudiant);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToLogin(rs);
            }
        }
        return null;
    }

    /**
     * Met à jour le mot de passe
     */
    public static boolean updatePassword(String codeLog, String newPasswordHash) throws SQLException {
        String query = "UPDATE LOGIN SET password_hash = ? WHERE code_log = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newPasswordHash);
            stmt.setString(2, codeLog);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Supprime une entrée LOGIN
     */
    public static boolean deleteLogin(String codeLog) throws SQLException {
        String query = "DELETE FROM LOGIN WHERE code_log = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, codeLog);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Mappe un ResultSet à une entité Login
     */
    private static Login mapResultSetToLogin(ResultSet rs) throws SQLException {
        return new Login(
            rs.getString("code_log"),
            rs.getInt("num_etudiant"),
            rs.getString("password_hash")
        );
    }
}
