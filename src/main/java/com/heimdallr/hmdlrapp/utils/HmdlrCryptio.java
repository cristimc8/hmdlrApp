package com.heimdallr.hmdlrapp.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

/**
 * Static class holding methods useful with password hashing and passowrd
 * comparing
 */
public class HmdlrCryptio {
    /**
     * Simple md5 hashing method used as a POC of a password hashing mechanism.
     * @param plainTextPassword The password as a plain text string
     * @return The hashed password
     */
    public static String hashPassword(String plainTextPassword) {
        try {
            byte[] plainTextPasswordBytes = plainTextPassword.getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md.digest(plainTextPasswordBytes);
            return Base64.getEncoder().encodeToString(md5Bytes);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Boolean method returning whether the password provided by a user is equal
     * with the hash found in the DB associated with the searched User account.
     * @param plainTextPassword The password as a plain text string
     * @param hash The hashed password in DB
     * @return Boolean indicating if the password is good.
     */
    public static boolean comparePasswords(String plainTextPassword, String hash) {
        String hashedProvided = hashPassword(plainTextPassword);
        return Objects.equals(hashedProvided, hash);
    }
}
