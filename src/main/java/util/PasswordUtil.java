package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utilitaire pour le hachage et la vérification des mots de passe
 * Utilise SHA-256 avec salt pour la sécurité
 */
public class PasswordUtil {
    private static final int SALT_LENGTH = 16;
    private static final String ALGORITHM = "SHA-256";

    /**
     * Hache un mot de passe avec un salt
     * @param password Le mot de passe en clair
     * @return Le hash encodé en Base64 (salt + hash)
     */
    public static String hashPassword(String password) {
        try {
            // Générer un salt aléatoire
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            // Hasher le mot de passe avec le salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());

            // Combiner le salt et le hash
            byte[] saltAndHash = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, saltAndHash, 0, salt.length);
            System.arraycopy(hashedPassword, 0, saltAndHash, salt.length, hashedPassword.length);

            // Encoder en Base64
            return Base64.getEncoder().encodeToString(saltAndHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithme SHA-256 non disponible", e);
        }
    }

    /**
     * Vérifie un mot de passe contre un hash stocké
     * @param password Le mot de passe en clair à vérifier
     * @param storedHash Le hash stocké en Base64
     * @return true si le mot de passe correspond, false sinon
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // Décoder le hash stocké
            byte[] saltAndHash = Base64.getDecoder().decode(storedHash);

            // Extraire le salt
            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(saltAndHash, 0, salt, 0, SALT_LENGTH);

            // Hasher le mot de passe fourni avec le même salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());

            // Comparer avec le hash stocké
            byte[] storedHashOnly = new byte[saltAndHash.length - SALT_LENGTH];
            System.arraycopy(saltAndHash, SALT_LENGTH, storedHashOnly, 0, storedHashOnly.length);

            return MessageDigest.isEqual(hashedPassword, storedHashOnly);
        } catch (IllegalArgumentException e) {
            return false;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithme SHA-256 non disponible", e);
        }
    }
}
