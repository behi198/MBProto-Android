package com.blackshadowsgroup.mbproto.encryption.encrypt.diffiehellman;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created by Behi198 on 6/10/2017.
 */

public class DiffieHellman {
    /// <summary>
    /// Represents the Diffie-Hellman algorithm.
    /// </summary>
    static Random _strongRng = new Random();
    /// <summary>
    /// The number of bits to generate.
    /// </summary>
    private int bits = 256;

    /// <summary>
    /// The shared prime.
    /// </summary>
    BigInteger prime;
    /// <summary>
    /// The shared base.
    /// </summary>
    BigInteger g;
    /// <summary>
    /// The private prime.
    /// </summary>
    BigInteger mine;

    /// <summary>
    /// The final key.
    /// </summary>
    byte[] key;
    /// <summary>
    /// The string representation/packet.
    /// </summary>
    String representation;
    private int radix = 36;

    /// <summary>
    /// Gets the final key to use for encryption.
    /// </summary>
    public byte[] getKey() {
        return key;
    }


    public DiffieHellman(int bits) {
        this.bits = bits;
    }

    /// <summary>
    /// Generates a request packet.
    /// </summary>
    /// <returns></returns>
    public DiffieHellman GenerateRequest() {
        // Generate the parameters.
//        prime = BigInteger.GenPseudoPrime(bits, 30, _strongRng);
        prime = BigInteger.probablePrime(bits, _strongRng);
        mine = BigInteger.probablePrime(bits, _strongRng);
        g = new BigInteger("5");

        // Gemerate the string.
        String rep = "";
        rep += prime.toString(radix);
        rep += "|";
        rep += g.toString(radix);
        rep += "|";

        // Generate the send BigInt.
        BigInteger send = g.modPow(mine, prime);
        rep += send.toString(radix);

        representation = rep.toString();
        return this;
    }

    /// <summary>
    /// Generate a response packet.
    /// </summary>
    /// <param name="request">The string representation of the request.</param>
    /// <returns></returns>
    public DiffieHellman GenerateResponse(String request) {
        String[] parts = request.split("\\|");

        // Generate the would-be fields.
        BigInteger prime = new BigInteger(parts[0], radix);
        BigInteger g = new BigInteger(parts[1], radix);
//        BigInteger mine = BigInteger.GenPseudoPrime(bits, 30, _strongRng);
//        BigInteger mine = BigInteger.probablePrime(bits, _strongRng);
        BigInteger mine = new BigInteger(bits, 30, _strongRng);
        {
            // Generate the key.
            BigInteger given = new BigInteger(parts[2], radix);
            BigInteger key = given.modPow(mine, prime);

                this.key = key.toByteArray();

            // Generate the response.
            BigInteger send = g.modPow(mine, prime);
                this.representation = send.toString(radix);
        }

        return this;
    }

    /// <summary>
    /// Generates the key after a response is received.
    /// </summary>
    /// <param name="response">The string representation of the response.</param>
    public void HandleResponse(String response) {
        // Get the response and modpow it with the stored prime.
        BigInteger given = new BigInteger(response, radix);
        BigInteger key = given.modPow(mine, prime);
            this.key = key.toByteArray();
        Dispose();
    }

    @Override
    public String toString() {
        return representation;
    }

    /// <summary>
    /// Ends the calculation. The key will still be available.
    /// </summary>
    public void Dispose() {
        if (prime != null)
            prime = null;
        if (mine != null)
            mine= null;
        if (g != null)
            g = null;

        prime = null;
        mine = null;
        g = null;

        representation = null;
    }

    public BigInteger getPrime() {
        return prime;
    }

    public DiffieHellman(BigInteger prime, BigInteger mine) {
        this.prime = prime;
        this.mine = mine;
        this.g = new BigInteger("5");
    }

    public BigInteger getMine() {

        return mine;
    }
}
