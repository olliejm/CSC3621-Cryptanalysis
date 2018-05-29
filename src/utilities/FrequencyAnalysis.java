package utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

/**
 * A utility class providing static methods to perform frequency analysis operations on String objects
 * @author Oliver McNally (130072007)
 *
 */
public final class FrequencyAnalysis {
	
	// Declare useful constants, decided on 10% bounds either way for IOC values
	public static final int ALPHABET_SIZE = 26;
	public static final double IOC_ENG_AVERAGE = 1.73;
	public static final double IOC_ENG_UPPER_BOUND = IOC_ENG_AVERAGE + (IOC_ENG_AVERAGE / 10);
	public static final double IOC_ENG_LOWER_BOUND = IOC_ENG_AVERAGE - (IOC_ENG_AVERAGE / 10);
	public static final Map<Character, Double> KNOWN_ENG_FREQUENCIES;
	
	// Initialise constant frequency map with known letter frequencies taken from Wikipedia
	static {
		final Map<Character, Double> knownFrequencies = new HashMap<Character, Double>();
		
		knownFrequencies.put('a', 0.08167);
		knownFrequencies.put('b', 0.01492);
		knownFrequencies.put('c', 0.02782);
		knownFrequencies.put('d', 0.04253);
		knownFrequencies.put('e', 0.12702);
		knownFrequencies.put('f', 0.02228);
		knownFrequencies.put('g', 0.02015);
		knownFrequencies.put('h', 0.06094);
		knownFrequencies.put('i', 0.06966);
		knownFrequencies.put('j', 0.00153);
		knownFrequencies.put('k', 0.00772);
		knownFrequencies.put('l', 0.04025);
		knownFrequencies.put('m', 0.02406);
		knownFrequencies.put('n', 0.06749);
		knownFrequencies.put('o', 0.07507);
		knownFrequencies.put('p', 0.01929);
		knownFrequencies.put('q', 0.00095);
		knownFrequencies.put('r', 0.05987);
		knownFrequencies.put('s', 0.06327);
		knownFrequencies.put('t', 0.09056);
		knownFrequencies.put('u', 0.02758);
		knownFrequencies.put('v', 0.00978);
		knownFrequencies.put('w', 0.02360);
		knownFrequencies.put('x', 0.00150);
		knownFrequencies.put('y', 0.01974);
		knownFrequencies.put('z', 0.00074);
		
		KNOWN_ENG_FREQUENCIES = Collections.unmodifiableMap(knownFrequencies);
	}
	
	/**
	 * A method which will take a String return a map of each Character to an Integer
	 * indicating the number of times that Character occurred within the String
	 * @param input the String you want to count characters of
	 * @return a Map of characters to their counts
	 */
	public static Map<Character, Integer> characterCounts(final String input) {
		// If the input is empty we can't do anything
		if (input == null || input.length() == 0)
			throw new IllegalArgumentException("Input cannot be null or empty");
		
		// Initialise variables required
		final Map<Character, Integer> counts = new HashMap<Character, Integer>();
		final Scanner read = new Scanner(input);
		
		// Tell the scanner to read the input character-by-character
		read.useDelimiter("");
		
		// While there are still chars to read...
		while(read.hasNext()) {
			// Get the next character, shift it to lower-case and get the char value
			final String s = read.next().toLowerCase();
			final char c = s.charAt(0);
			
			// We are only interested in counting characters between a-z
			if (c >= 'a' && c <= 'z') {
				// If this character already has an entry, update it...
				if (counts.containsKey(c)) 
					counts.replace(c, counts.get(c) + 1);
				// ...otherwise put a new entry in
				else 
					counts.put(c, 1);
			}
		}
		
		// If any characters didn't occur at all, put a 0 entry in for them to avoid errors if iterating over the map
		for (char c = 'a'; c <= 'z'; c++)
			if (!counts.containsKey(c)) 
				counts.put(c, 0);
		
		read.close();
		// If anyone was to modify the output it would no longer be a valid character count
		return Collections.unmodifiableMap(counts);
	}
	
	/**
	 * A method to get the relative character frequencies (as a proportion from 0-1), from the counts
	 * Useful if needing to compare with existing measures which will likely be in the same format
	 * @param input the String you want the relative frequencies of
	 * @return a Map of characters to their relative frequencies
	 */
	public static Map<Character,Double> characterFrequencies(final String input) {
		if (input == null || input.length() == 0) 
			throw new IllegalArgumentException("Input cannot be null or empty");
		
		// Get the counts first, initialise our output map and sum all the characters
		final Map<Character, Integer> counts = characterCounts(input);
		final Map<Character, Double> frequencies = new HashMap<Character, Double>();
		final int totalCharacters = sumCharacters(counts);
		
		// For each character, find the frequency as a proportion of the total
		for (Character c : counts.keySet()) 
			frequencies.put(c, ((double) counts.get(c) / totalCharacters));
		
		// Again, shouldn't be modifiable
		return Collections.unmodifiableMap(frequencies);
	}
	
	/**
	 * A method to get the frequencies ordered by largest value first, allows for quicker visualisation of the results
	 * if for example, testing outputs via the console
	 * @param input the String you want the ordered relative frequencies of
	 * @return a List of Map.Entry<Character, Double> ordered by descending relative frequencies
	 */
	public static List<Entry<Character, Double>> frequenciesByValue(final String input) {
		if (input == null || input.length() == 0)
			throw new IllegalArgumentException("Input cannot be null or empty");
		
		// We need the frequencies first, then make a list of the entry set
		final Map<Character, Double> frequencies = characterFrequencies(input);
		final List<Entry<Character, Double>> sorted = new ArrayList<Entry<Character, Double>>(frequencies.entrySet());
		
		// Define a sort method for this list
		Collections.sort(sorted, 
	            new Comparator<Entry<Character, Double>>() {
	                @Override
	                public int compare(Entry<Character, Double> one, Entry<Character, Double> two) {
	                    return two.getValue().compareTo(one.getValue());
	                }
	            }
	    );
		
		// And end...
		return Collections.unmodifiableList(sorted);
	}
	
	/**
	 * A method to sum the total characters of a given String input
	 * @param input the String to sum the characters of
	 * @return an int indicating the total number of characters in the input
	 */
	public static int sumCharacters(final String input) {
		if (input == null || input.length() == 0)
			throw new IllegalArgumentException("Input cannot be null or empty");
		// Figure out the counts and then return output of the private method
		final Map<Character, Integer> counts = characterCounts(input);
		return sumCharacters(counts);
	}
	
	/**
	 * The actual implementation of the summing method, which takes a predetermined count
	 * map rather than calculating its own. Private, for use within the class to prevent needless
	 * recalculation of the count map on every iteration of a loop
	 * @param counts the count map you want to sum
	 * @return an int indicating the total number of characters from the map's original input
	 */
	private static int sumCharacters(final Map<Character, Integer> counts) {
		if (counts == null || counts.isEmpty())
			throw new IllegalArgumentException("Count map cannot be null or empty");
		// Self-explanatory summation...
		int sum = 0;
		
		for (Character c : counts.keySet()) 
			sum += counts.get(c);
		
		return sum;
	}
	
}