package no.hvl.dat110.util;

/**
 * exercise/demo purpose in dat110
 * @author tdoy
 *
 */

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class Hash {

	public static BigInteger hashOf(String entity) throws NoSuchAlgorithmException {

		BigInteger hashint = null;

		// Task: Hash a given string using MD5 and return the result as a BigInteger.
		// we use MD5 with 128 bits digest

		// compute the hash of the input 'entity'
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(entity.getBytes());
		byte[] digest = md.digest();

		// convert the hash into hex format
		String hex = toHex(digest);

		// convert the hex into BigInteger
		// return the BigInteger
		return new BigInteger(hex);
	}

	public static BigInteger addressSize() {

		// Task: compute the address size of MD5

		// get the digest length (Note: make this method independent of the class variables)

		// compute the number of bits = digest length * 8

		// compute the address size = 2 ^ number of bits

		// return the address size

		return null;
	}

	public static String toHex(byte[] digest) {
		StringBuilder strbuilder = new StringBuilder();
		for(byte b : digest) {
			strbuilder.append(String.format("%02x", b&0xff));
		}
		return strbuilder.toString();
	}

}
