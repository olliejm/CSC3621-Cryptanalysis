package caesar;

import java.util.Scanner;

import utilities.FrequencyAnalysis;

/**
 * A utility class providing static methods to encrypt and decrypt given String 
 * inputs via use of the Caesar cipher
 * @author Oliver McNally (130072007)
 *
 */
public final class CaesarCipher {
	
	/**
	 * A wrapper method for the caesar program to encrypt an input with a given offset
	 * @param plaintext the plain text input to be encrypted
	 * @param offset the (int) offset by which to encrypt the input
	 * @return the encrypted text as a String
	 */
	public static String encrypt(final String plaintext, final int offset) {
		// Tell the run method encrypt = 'true' as in 'I want to encrypt'
		return run(plaintext, offset, true);
	}
	
	/**
	 * An overload/wrapper method for caesar decryption which will automatically use the
	 * cryptanalysis shift-guessing method when you don't supply a given offset
	 * @param ciphertext the ciphertext String to be decrypted
	 * @return a String containing the ciphertext decrypted by a best-guess offset
	 */
	public static String decrypt(final String ciphertext) {
		return decrypt(ciphertext, CaesarCryptanalysis.guessOffset(ciphertext));
	}
	
	/**
	 * A wrapper method for the caesar program to decrypt an input using a given offset
	 * @param ciphertext the ciphertext String to be decrypted
	 * @param offset the (int) offset to be used in decrpytion
	 * @return a String containing the ciphertext decrypted by the given offset
	 */
	public static String decrypt(final String ciphertext, final int offset) {
		// Tell the run method encrypt = 'false' as in 'I don't want to encrypt...(I want to decrypt)'
		return run(ciphertext, offset, false);
	}
	
	/**
	 * The private method which does all caesar cipher encryption and decryption. Seeing as
	 * the only difference is that you negate the offset to decrypt, no point repeating the code
	 * @param input the String we are wanting to encrypt or decrypt using a caesar cipher
	 * @param offset the offset by which it needs to be encrypted (or was encrypted with)
	 * @param encrypt a boolean stating true for encrypt, or false for decrypt
	 * @return a String containing the input processed in the requested manner using the given offset 
	 */
	private static String run(final String input, final int offset, final boolean encrypt) {
		if (input == null || input.length() == 0)
			throw new IllegalArgumentException("Ciphertext cannot be null or empty");
		
		// If the offset has been set to 0 no point wasting processing time
		if (offset == 0)
			return input;
		
		// Use a StringBuilder to construct the output and a scanner reading the input
		final StringBuilder result = new StringBuilder();
		final Scanner read = new Scanner(input);
		
		// One character at a time
		read.useDelimiter("");
		
		// While there is still chars
		while (read.hasNext()) {
			// Get the next one, make lower-case...
			final String s = read.next().toLowerCase();
			final char c = s.charAt(0);
			
			// if alphabetical...
			if (c >= 'a' && c <= 'z') {
				// and if encrypting...
				if (encrypt)
					// shift it by the input
					result.append(shift(c, offset));
				// but if decrypting...
				else
					// reverse the shift
					result.append(shift(c, -offset));
			} else
				// and just leave spaces, punctuation etc as-is
				result.append(c);
		}
		
		read.close();
		// return the String from the StringBuilder
		return result.toString();
	}
	
	/**
	 * A private method to perform the required shift on a given char during encryption/decryption
	 * @param c the char to be shifted
	 * @param offset the (int) value by which to shift it
	 * @return the shifted char
	 */
	private static char shift(final char c, final int offset) {
		// Offset the char
		final int shifted = c + offset;
		
		// Loop it back round into an alphabetical value if it fell off either end, cast and return
		if (shifted < 'a') 
			return (char) (shifted + FrequencyAnalysis.ALPHABET_SIZE);
		if (shifted > 'z') 
			return (char) (shifted - FrequencyAnalysis.ALPHABET_SIZE);
		
		// Otherwise just cast to char and return
		return (char) shifted;
	}
	
}