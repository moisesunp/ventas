package pe.edu.unp.ventas.service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHasher {
    private static final int ITERACIONES = 120_000;
    private static final int LONGITUD_SALT = 16;
    private static final int LONGITUD_HASH = 256;
    private final SecureRandom random = new SecureRandom();

    public String hash(String password) {
        validarPassword(password);
        byte[] salt = new byte[LONGITUD_SALT];
        random.nextBytes(salt);
        byte[] hash = derivar(password.toCharArray(), salt, ITERACIONES);
        return ITERACIONES + ":" + Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    public boolean verificar(String password, String almacenado) {
        validarPassword(password);
        String[] partes = almacenado.split(":");
        if (partes.length != 3) {
            return false;
        }
        int iteraciones = Integer.parseInt(partes[0]);
        byte[] salt = Base64.getDecoder().decode(partes[1]);
        byte[] esperado = Base64.getDecoder().decode(partes[2]);
        byte[] actual = derivar(password.toCharArray(), salt, iteraciones);
        return MessageDigest.isEqual(esperado, actual);
    }

    private byte[] derivar(char[] password, byte[] salt, int iteraciones) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iteraciones, LONGITUD_HASH);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo procesar la contraseña", e);
        } finally {
            spec.clearPassword();
        }
    }

    private void validarPassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
    }
}
