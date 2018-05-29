package vigenere;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import caesar.CaesarCryptanalysis;
import utilities.FrequencyAnalysis;


/**
 * A utility class providing static methods for determining canditate keys and key lengths for text that
 * has potentially been encrypted by caesar cipher, by performing index of coincidence calculations
 * @author Oliver McNally (130072007)
 *
 */
public final class VigenereCryptanalysis {
	
	/**
	 * A wrapper / overload method for guessing the key without providing a determined key length,
	 * it will instead just try to guess the key length first before guessing the key
	 * @param ciphertext the text you are trying to determine the key for
	 * @return the potential vigenere key for the ciphertext
	 */
	public static String guessKey(final String ciphertext) {
		return guessKey(ciphertext, guessKeyLength(ciphertext));
	}
	
	/**
	 * A method to guess the most likely vigenere key for an input ciphertext given a known or candidate key length
	 * @param ciphertext the String you are attempting to guess the key of
	 * @param keyLength a known or expected key length for the input text
	 * @return a candidate for the vigenere key of the input text
	 */
	public static String guessKey(final String ciphertext, final int keyLength) {
		// No use for null or empty strings, and if length = -1 it was probably a failed guess attempt
		if (ciphertext == null || ciphertext.length() == 0 || keyLength == -1) 
			throw new IllegalArgumentException("Input cannot be null or empty (or could not calculate key length)");
		
		// A StringBuilder to hold the key
		final StringBuilder key = new StringBuilder(keyLength);
		// Split the text into all the groups at the key length interval, giving us each caesar to solve
		final List<String> caesarGroups = allGroupsAtInterval(ciphertext, keyLength);
		
		// For each string
		for (String s : caesarGroups) {
			// Guess the caesar offset for the string
			int shift = CaesarCryptanalysis.guessOffset(s);
			// Append the char that would have caused that offset
			key.append((char) (shift + 'a'));
		}
		
		// Return the string from the builder
		return key.toString();
	}
	
	/**
	 * A method to guess the length of the vigenere key of the given input using index
	 * of coincidence calculations
	 * @param ciphertext the text you want to try and guess the key length of
	 * @return the most likely length of a vigenere key for the input
	 */
	public static int guessKeyLength(final String ciphertext) {
		if (ciphertext == null || ciphertext.length() == 0) 
			throw new IllegalArgumentException("Input cannot be null or empty");
		
		// For a potential key length from 0 to the length of the input...
		for (int i = 0; i < ciphertext.length(); i++) {
			// Get all the caesar strings joined by that key length
			final List<String> joins = allGroupsAtInterval(ciphertext, i);
			// Initialise a sum for our IOC average
			double sumIOC = 0;
			
			// For every string generated, add the index of coincidence to our sum
			for (String s : joins)
				sumIOC += indexOfCoincidence(s);
			
			// Find the average IOC for this potential length
			final double averageIOC = sumIOC / joins.size();
			
			// If it is close enough to the expected english IOC, return it as favourite candidate
			if (averageIOC >= FrequencyAnalysis.IOC_ENG_LOWER_BOUND 
					&& averageIOC <= FrequencyAnalysis.IOC_ENG_UPPER_BOUND) 
				return i;
		} 
		
		// If we get here, nothing seemed likely enough - attempt failure
		return -1;
	}
	
	/**
	 * A private method for the key length guessing program to calculate the 'index of coincidence'
	 * of the input string
	 * @param input the string we want the IOC of
	 * @return the calculated IOC of the input
	 */
	private static double indexOfCoincidence(final String input) {
		// Do a frequency analysis of the input
		final Map<Character, Integer> counts = FrequencyAnalysis.characterCounts(input);
		// Initialise numerator of IOC
		double num = 0;
		
		// Calculate numerator
		for (char c : counts.keySet()) 
			num += counts.get(c) * (counts.get(c) - 1);
		
		// Calculate denominator
		final double denom = (input.length() * (input.length() - 1)) 
				/ FrequencyAnalysis.ALPHABET_SIZE;
		
		// Return the IOC
		return num / denom;
	}
	
	/**
	 * A private method for use by both the key length and key guessing methods to split a string
	 * into the set of strings produced when you join all chars at a given interval
	 * @param input the String you want to split
	 * @param interval the interval you want to split it at
	 * @return a List of all the strings produced by the split at the given interval
	 */
	private static List<String> allGroupsAtInterval(final String input, final int interval) {
		final List<String> results = new ArrayList<String>();
		
		// Loop round until every offset is covered
		for (int i = 0; i < Math.min(interval, input.length()); i++)
			// Add the result of splitting at the offset i
			results.add(groupByInterval(input, interval, i));
		
		// No reason this should be mutable
		return Collections.unmodifiableList(results);
	}
	
	/**
	 * A private method to group chars in a string at a given interval and at a given offset
	 * @param input the text you want to split
	 * @param interval the interval you want to join chars from
	 * @param offset the offset to apply to the split
	 * @return the string joined at the given interval and offset
	 */
	private static String groupByInterval(final String input, final int interval, final int offset) {
		if (interval <= 0 || offset < 0) 
			return input;
		
		final StringBuilder result = new StringBuilder();
		
		// For the whole input, if the char will fit then append each one at every interval
		for (int i = 0; i < input.length(); i += interval) {
			if (i + offset < input.length())
				result.append(input.charAt(i + offset));
		}
		
		// Return the string from the builder 
		return result.toString();
	}
	
}