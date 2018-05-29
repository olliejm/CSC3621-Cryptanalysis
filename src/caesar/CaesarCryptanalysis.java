package caesar;

import java.util.Map;

import utilities.FrequencyAnalysis;

/**
 * A class to perform automated cryptanalysis on String of potentially caesar-shifted text
 * @author Oliver McNally (130072007)
 *
 */
public final class CaesarCryptanalysis {
	
	/**
	 * A method to attempt to guess the caesar-offset of a given String by using a 
	 * chi-squared fitness test
	 * @param ciphertext the String you want to try and guess the caesar-offset of
	 * @return an (int) estimation of what the offset would be
	 */
	public static int guessOffset(final String ciphertext) {
		if (ciphertext == null || ciphertext.length() == 0) 
			throw new IllegalArgumentException("Ciphertext cannot be null or empty");
		
		// Set fitness to max to begin with, offset to 0
		double fitness = Integer.MAX_VALUE;
		int offset = 0;
		
		// For each (of 26 inc. 0) possible offsets
		for (int i = 0; i < FrequencyAnalysis.ALPHABET_SIZE; i++) {
			// Calculate the fitness of the text decrypted by that offset
			final double tempFitness = chiSquaredFitness(CaesarCipher.decrypt(ciphertext, i));
			
			// If it was lower than the previous value, update the lowest value and likely offset
			if (tempFitness < fitness) {
				fitness = tempFitness;
				offset = i;
			}
		} 
		
		// Return the most likely offset
		return offset;
	}
	
	/**
	 * A private method to actually compute the chi-squared fitness value of a given String, it does
	 * this by comparing character frequencies from the input to known english character-frequencies
	 * @param candidate the String containing potentially decrypted text
	 * @return a (double) value representing the likely similarity to plain english (smaller value -> more similar)
	 */
	private static double chiSquaredFitness(final String candidate) {
		// Calculate the frequencies of the input, initialise fitness
		final Map<Character, Double> observedFrequencies = FrequencyAnalysis.characterFrequencies(candidate);
		double fitness = 0;
		
		// For each character calculate the chi-squared fitness
		for (char c : observedFrequencies.keySet()) {
			fitness += 
					Math.pow(
							observedFrequencies.get(c) 
							- FrequencyAnalysis.KNOWN_ENG_FREQUENCIES.get(c), 2)
					/ FrequencyAnalysis.KNOWN_ENG_FREQUENCIES.get(c);
		} 
		
		// Return the average fitness for the input
		return fitness / FrequencyAnalysis.ALPHABET_SIZE;
	}
	
}