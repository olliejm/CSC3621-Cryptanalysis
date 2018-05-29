package vigenere;

import java.util.Scanner;

import caesar.CaesarCipher;

/**
 * A utility class providing static methods to encrypt and decrypt given String 
 * inputs via use of the Vigenere cipher
 * @author Oliver McNally (130072007)
 *
 */
public final class VigenereCipher {
	
	/**
	 * A wrapper method for the vigenere program to perform encryption of a given
	 * plain text String using the given key
	 * @param plaintext the String to be encrypted
	 * @param key the key for the vigenere encryption
	 * @return the encrypted plaintext from the vigenere method
	 */
	public static String encrypt(final String plaintext, final String key) {
		// true = yes encrypt
		return run(plaintext, key, true);
	}
	
	/**
	 * An overload/wrapper method for the decrypt function which will make use of the automatic
	 * cryptanalysis program for guessing the key of a vigenere-encrypted text, if
	 * they call the decrypt method with only an input and no key
	 * @param ciphertext the ciphertext you are attempting to decrypt
	 * @return the text decrypted using the best-guess key
	 */
	public static String decrypt(final String ciphertext) {
		return decrypt(ciphertext, VigenereCryptanalysis.guessKey(ciphertext));
	}
	
	/**
	 * A wrapper method for the vigenere program to perform decryption of a given
	 * ciphertext String using the given key
	 * @param ciphertext the ciphertext you want to decrypt
	 * @param key the key to attempt decryption with
	 * @return the decrypted plaintext from the vigenere method
	 */
	public static String decrypt(final String ciphertext, final String key) {
		// false = don't encrypt (decrypt)
		return run(ciphertext, key, false);
	}
	
	/**
	 * A private method to actually run vigenere encryption and decryption. As the only difference
	 * between these operations is the calling of a caesar-cipher encrypt vs decrypt, we can combine
	 * both operations into one method provided we indicate when calling which operation is desired
	 * @param input the String to be encrypted or decrypted
	 * @param key the key with which to perform the encryption or decryption
	 * @param encrypt a boolean telling the method whether we want to encrypt or not (decrypt)
	 * @return a String containing the processed output text
	 */
	private static String run(final String input, final String key, final boolean encrypt) {
		// We can't do anything if the inputs are null, empty or our key contains characters other than a-z
		if (input == null || input.length() == 0 
				|| key == null || key.length() == 0 || !key.matches("[a-z]+")) 
			throw new IllegalArgumentException("Input / key is null, empty or invalid (![a-z]+)");
		
		// Use a StringBuilder to construct the result, a scanner to read the input
		final StringBuilder result = new StringBuilder();
		final Scanner read = new Scanner(input);
		
		// Step one of Vigenere encryption - repeat key to length of input text
		final String repeatedKey = repeatKey(key, input.length());
		
		// Begin a count so we can index into the repeated key to calculate the shift
		int count = 0;
		
		// Read character by character
		read.useDelimiter("");
		
		// While there are more chars...
		while(read.hasNext()) {
			// Get the next character
			final String s = read.next().toLowerCase();
			// Calculate Caesar shift for this character
			final int shift = repeatedKey.charAt(count) - 'a';
			
			// If we are encrypting...
			if (encrypt)
				// Encrypt via caesar the current char, given the shift from current char of the key
				result.append(CaesarCipher.encrypt(s, shift));
			// Or if decrypting...
			else
				// Decrypt via caesar the current char, given the shift from current char of the key
				result.append(CaesarCipher.decrypt(s, shift));
			
			// Don't forget to increment count
			count++;
		}
		
		read.close();
		// Return the string from the builder
		return result.toString();
	}
	
	/**
	 * A private helper function to repeat the key to the length given from the input text
	 * @param key the String containing the key to be repeated
	 * @param length the length which we want to repeat it to
	 * @return a String containing the key repeated to the given length
	 */
	private static String repeatKey(final String key, final int length) {
		// We need to know how many whole repetitions of the key will fit
		final int wholeRepetitions = length / key.length();
		// Then we need to see what the remainder is so we can append a partial final repetition
		final int remainder = length - (key.length() * wholeRepetitions);
		
		// A StringBuilder to construct the repeated key
		final StringBuilder repeated = new StringBuilder();
		
		// Keep appending the key until the max number of whole repetitions is reached
		for (int i = 0; i < wholeRepetitions; i++)
			repeated.append(key);
		
		// Now append what we can up to end of the remainder
		repeated.append(key.substring(0, remainder));
		
		// Return the string from the builder
		return repeated.toString();
	}

}