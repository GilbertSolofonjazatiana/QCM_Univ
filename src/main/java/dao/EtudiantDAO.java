package dao;

import models.Etudiant;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EtudiantDAO {

    /**
     * Récupère un étudiant par son numéro
     */
    public static Etudiant getEtudiantByNum(int numEtudiant) throws SQLException {
        String query = "SELECT * FROM ETUDIANT WHERE num_etudiant = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, numEtudiant);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToEtudiant(rs);
            }
        }
        return null;
    }

    /**
     * Récupère un étudiant par son email
     */
    public static Etudiant getEtudiantByEmail(String email) throws SQLException {
        String query = "SELECT * FROM ETUDIANT WHERE adr_email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToEtudiant(rs);
            }
        }
        return null;
    }

    /**
     * Crée un nouvel étudiant
     */
    public static boolean createEtudiant(Etudiant etudiant) throws SQLException {
        String query = "INSERT INTO ETUDIANT (nom, prenoms, niveau, adr_email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, etudiant.getNom());
            stmt.setString(2, etudiant.getPrenoms());
            stmt.setString(3, etudiant.getNiveau());
            stmt.setString(4, etudiant.getAdrEmail());
            stmt.executeUpdate();
            return true;
        }
    }

    /**
     * Met à jour un étudiant
     */
    public static boolean updateEtudiant(Etudiant etudiant) throws SQLException {
        String query = "UPDATE ETUDIANT SET nom = ?, prenoms = ?, niveau = ?, adr_email = ? WHERE num_etudiant = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, etudiant.getNom());
            stmt.setString(2, etudiant.getPrenoms());
            stmt.setString(3, etudiant.getNiveau());
            stmt.setString(4, etudiant.getAdrEmail());
            stmt.setInt(5, etudiant.getNumEtudiant());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Récupère tous les étudiants
     */
    public static List<Etudiant> getAllEtudiants() throws SQLException {
        List<Etudiant> etudiants = new ArrayList<>();
        String query = "SELECT * FROM ETUDIANT ORDER BY nom, prenoms";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                etudiants.add(mapResultSetToEtudiant(rs));
            }
        }
        return etudiants;
    }

    /**
     * Récupère les étudiants par niveau
     */
    public static List<Etudiant> getEtudiantsByNiveau(String niveau) throws SQLException {
        List<Etudiant> etudiants = new ArrayList<>();
        String query = "SELECT * FROM ETUDIANT WHERE niveau = ? ORDER BY nom, prenoms";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, niveau);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                etudiants.add(mapResultSetToEtudiant(rs));
            }
        }
        return etudiants;
    }

    /**
     * Supprime un étudiant
     */
    public static boolean deleteEtudiant(int numEtudiant) throws SQLException {
        String query = "DELETE FROM ETUDIANT WHERE num_etudiant = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, numEtudiant);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Mappe un ResultSet à une entité Etudiant
     */
    private static Etudiant mapResultSetToEtudiant(ResultSet rs) throws SQLException {
        return new Etudiant(
            rs.getInt("num_etudiant"),
            rs.getString("nom"),
            rs.getString("prenoms"),
            rs.getString("niveau"),
            rs.getString("adr_email")
        );
    }
}
