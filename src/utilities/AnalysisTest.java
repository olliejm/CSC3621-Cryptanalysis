package utilities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map.Entry;

import caesar.CaesarCipher;
import caesar.CaesarCryptanalysis;
import vigenere.VigenereCipher;
import vigenere.VigenereCryptanalysis;

/**
 * A class to perform the exercises outlined in the coursework specification and write
 * the output to a file for inspection to ensure the program is functioning as required
 * @author Oliver McNally (130072007)
 *
 */
public class AnalysisTest {
	
	// Set some constants for the file locations in case we want to easily change them
	public static final String PLAIN_TEXT_URI = "pg1661.txt";
	public static final String ALT_PLAIN_TEXT_URI = "xld-ripping-guide.txt";
	public static final String CIPHER_TEXT_1_URI = "Exercise1Ciphertext.txt";
	public static final String CIPHER_TEXT_2_URI = "Exercise2Ciphertext.txt";
	public static final String OUTPUT_URI = "output.txt";
	
	public static void main(String[] args) {
		// Use sb to construct output
		final StringBuilder output = new StringBuilder();
		
		// Declare our Strings for analysis
		String plainText = null;
		String altPlainText = null;
		String cipherText1 = null;
		String cipherText2 = null;
		
		// Attempt to read the files into the strings
		try {
			plainText = new String(Files.readAllBytes(Paths.get(PLAIN_TEXT_URI)));
			altPlainText = new String(Files.readAllBytes(Paths.get(ALT_PLAIN_TEXT_URI)));
			cipherText1 = new String(Files.readAllBytes(Paths.get(CIPHER_TEXT_1_URI)));
			cipherText2 = new String(Files.readAllBytes(Paths.get(CIPHER_TEXT_2_URI)));
		} catch (IOException e) {
			// Fail if IO errors
			System.out.println("One or more required files not found, printing stack trace: \n");
			e.printStackTrace();
		}
		
		// Do our initial frequency analyses
		final List<Entry<Character, Double>> plainByValue = FrequencyAnalysis.frequenciesByValue(plainText);
		final List<Entry<Character, Double>> altPlainByValue = FrequencyAnalysis.frequenciesByValue(altPlainText);
		final List<Entry<Character, Double>> cipher1ByValue = FrequencyAnalysis.frequenciesByValue(cipherText1);
		
		// Write the results to the output in an understandable format
		output.append("\nPrinting frequency analysis of " + PLAIN_TEXT_URI + " ordered by percentage...\n\n");
		output.append(plainByValue + "\n");
		output.append("\nPrinting frequency analysis of " + ALT_PLAIN_TEXT_URI + " ordered by percentage...\n\n");
		output.append(altPlainByValue + "\n");
		output.append("\nPrinting frequency analysis of " + CIPHER_TEXT_1_URI + " ordered by percentage...\n\n");
		output.append(cipher1ByValue + "\n");
		
		// Try our caesar offset guessing...
		final int guessedOffset = CaesarCryptanalysis.guessOffset(cipherText1);
		final String decryptedCaesar = CaesarCipher.decrypt(cipherText1, guessedOffset);
		
		// Write the results...
		output.append("\nGuessed caesar cipher offset of " + CIPHER_TEXT_1_URI + " using frequency analysis: " + guessedOffset + "\n");
		output.append("\nPrinting decryption of " + CIPHER_TEXT_1_URI + " using guessed offset of " + guessedOffset + "...\n\n");
		output.append(decryptedCaesar + "\n");
		
		// Use key ncl to encrypt pg1661.txt with vigenere, then try to decrypt that encryption back to the original
		final String key = "ncl";
		final String plainEncrypted = VigenereCipher.encrypt(plainText, key);
		final String plainDecrypted = VigenereCipher.decrypt(plainEncrypted, key);
		
		// Do a frequency analysis of the encrypted
		final List<Entry<Character, Double>> vigenereEncryptedFreq = FrequencyAnalysis.frequenciesByValue(plainEncrypted);
		
		// Append...
		output.append("\nPrinting " + PLAIN_TEXT_URI + " encrypted by Vigenere cipher with key '" + key + "'...\n\n");
		output.append(plainEncrypted);
		output.append("\nPrinting decryption of Vigenere-encrypted " + PLAIN_TEXT_URI + " using key '" + key + "'...\n\n");
		output.append(plainDecrypted);
		output.append("\nPrinting frequency analysis of Vigenere-encrypted" + PLAIN_TEXT_URI + " ordered by percentage...\n\n");
		output.append(vigenereEncryptedFreq + "\n");
		
		// Quick check against coursemates' results
		final String test = "newcastleuniversity";
		final String testEncrypted = VigenereCipher.encrypt(test, key);
		final String testDecrypted = VigenereCipher.decrypt(testEncrypted, key);
		
		output.append("\nPrinting '" + test + "' encrypted with key '" + key + "'...\n\n");
		output.append(testEncrypted);
		output.append("\n\nPrinting decryption of '" + testEncrypted + "' using key '" + key + "'...\n\n");
		output.append(testDecrypted);
		
		// Try our vignere guessing programs
		final int lengthGuess = VigenereCryptanalysis.guessKeyLength(cipherText2);
		final String keyGuess = VigenereCryptanalysis.guessKey(cipherText2, lengthGuess);
		final String textGuess = VigenereCipher.decrypt(cipherText2, keyGuess);
		
		output.append("\n\nGuessed key length of " + CIPHER_TEXT_2_URI + " via cryptanalysis: " + lengthGuess + "\n");
		output.append("\nGiven key length of " + lengthGuess + ", guessed key: " + keyGuess + "\n");
		output.append("\nPrinting decryption of " + CIPHER_TEXT_2_URI + " using guessed key " + keyGuess + "...\n\n");
		output.append(textGuess);
		
		// Finally attempt to write to file, or fail
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(OUTPUT_URI))) {
			final String content = output.toString();
			bw.write(content);
			System.out.println("Tasks completed without error - output written to " + OUTPUT_URI);
		} catch (IOException e) {
			System.out.println(e + "\nPrinting stack trace...\n");
			e.printStackTrace();
		}
		
		System.out.println(VigenereCipher.decrypt("LWWIIQIEJTZBFOFVWNJMAXEAGLPNAPSMQUZLW", "ifitevenlasts"));
	}
	
}