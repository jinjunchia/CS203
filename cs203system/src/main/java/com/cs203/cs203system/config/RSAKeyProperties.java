package com.cs203.cs203system.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * Component that holds the RSA public and private keys.
 * This class generates and stores an RSA key pair when instantiated, providing access to the keys.
 */
@Setter
@Getter
@Component
public class RSAKeyProperties {

    /**
     * The RSA public key.
     */
    private RSAPublicKey publicKey;

    /**
     * The RSA private key.
     */
    private RSAPrivateKey privateKey;

    /**
     * Constructor that generates an RSA key pair and initializes the public and private keys.
     */
    public RSAKeyProperties() {
        KeyPair pair = KeyGenerator.generateRsaKey();
        this.publicKey = (RSAPublicKey) pair.getPublic();
        this.privateKey = (RSAPrivateKey) pair.getPrivate();
    }

}