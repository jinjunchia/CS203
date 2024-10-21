package com.cs203.cs203system.config;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

/**
 * Utility class for generating RSA key pairs.
 * This class is used to generate public and private keys for cryptographic operations.
 */
public class KeyGenerator {

    /**
     * Generates an RSA key pair with a key size of 2048 bits.
     *
     * @return A {@link KeyPair} containing the generated public and private keys.
     * @throws IllegalStateException if the key generation process encounters an error.
     */
    public static KeyPair generateRsaKey() {

        KeyPair keyPair;

        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException();
        }

        return keyPair;
    }

}