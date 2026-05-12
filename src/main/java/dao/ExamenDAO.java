package dao;

import models.Examen;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExamenDAO {

    /**
     * Crée un nouvel examen
     */
    public static int createExamen(Examen examen) throws SQLException {
        String query = "INSERT INTO EXAMEN (num_etudiant, annee_univ, note, statut, debut_examen) " +
                      "VALUES (?, ?, ?, ?, ?) RETURNING num_examen";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, examen.getNumEtudiant());
            stmt.setString(2, examen.getAnneeUniv());
            stmt.setDouble(3, examen.getNote() != null ? examen.getNote() : 0.0);
            stmt.setString(4, examen.getStatut());
            stmt.setTimestamp(5, examen.getDebutExamen());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("num_examen");
            }
        }
        return -1;
    }

    /**
     * Récupère un examen par son numéro
     */
    public static Examen getExamenByNum(int numExamen) throws SQLException {
        String query = "SELECT * FROM EXAMEN WHERE num_examen = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, numExamen);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToExamen(rs);
            }
        }
        return null;
    }

    /**
     * Récupère l'examen actuel ou le dernier examen d'un étudiant pour l'année
     */
    public static Examen getExamenByEtudiantAndAnnee(int numEtudiant, String anneeUniv) throws SQLException {
        String query = "SELECT * FROM EXAMEN WHERE num_etudiant = ? AND annee_univ = ? " +
                      "ORDER BY debut_examen DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, numEtudiant);
            stmt.setString(2, anneeUniv);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToExamen(rs);
            }
        }
        return null;
    }

    /**
     * Récupère tous les examens d'un étudiant
     */
    public static List<Examen> getExamensByEtudiant(int numEtudiant) throws SQLException {
        List<Examen> examens = new ArrayList<>();
        String query = "SELECT * FROM EXAMEN WHERE num_etudiant = ? ORDER BY annee_univ DESC, debut_examen DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, numEtudiant);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                examens.add(mapResultSetToExamen(rs));
            }
        }
        return examens;
    }

    /**
     * Récupère tous les examens d'une année
     */
    public static List<Examen> getExamensByAnnee(String anneeUniv) throws SQLException {
        List<Examen> examens = new ArrayList<>();
        String query = "SELECT * FROM EXAMEN WHERE annee_univ = ? ORDER BY note DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, anneeUniv);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                examens.add(mapResultSetToExamen(rs));
            }
        }
        return examens;
    }

    /**
     * Met à jour un examen
     */
    public static boolean updateExamen(Examen examen) throws SQLException {
        String query = "UPDATE EXAMEN SET note = ?, statut = ?, debut_examen = ? WHERE num_examen = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, examen.getNote());
            stmt.setString(2, examen.getStatut());
            stmt.setTimestamp(3, examen.getDebutExamen());
            stmt.setInt(4, examen.getNumExamen());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Met à jour juste le statut d'un examen
     */
    public static boolean updateStatut(int numExamen, String statut) throws SQLException {
        String query = "UPDATE EXAMEN SET statut = ? WHERE num_examen = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, statut);
            stmt.setInt(2, numExamen);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Met à jour la note d'un examen
     */
    public static boolean updateNote(int numExamen, double note) throws SQLException {
        String query = "UPDATE EXAMEN SET note = ? WHERE num_examen = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, note);
            stmt.setInt(2, numExamen);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Récupère tous les examens terminés d'une année triés par note décroissante
     */
    public static List<Examen> getRankingByAnnee(String anneeUniv) throws SQLException {
        List<Examen> examens = new ArrayList<>();
        String query = "SELECT * FROM EXAMEN WHERE annee_univ = ? AND statut = 'termine' " +
                      "ORDER BY note DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, anneeUniv);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                examens.add(mapResultSetToExamen(rs));
            }
        }
        return examens;
    }

    /**
     * Supprime un examen
     */
    public static boolean deleteExamen(int numExamen) throws SQLException {
        String query = "DELETE FROM EXAMEN WHERE num_examen = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, numExamen);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Mappe un ResultSet à une entité Examen
     */
    private static Examen mapResultSetToExamen(ResultSet rs) throws SQLException {
        return new Examen(
            rs.getInt("num_examen"),
            rs.getInt("num_etudiant"),
            rs.getString("annee_univ"),
            rs.getDouble("note"),
            rs.getString("statut"),
            rs.getTimestamp("debut_examen")
        );
    }
}
