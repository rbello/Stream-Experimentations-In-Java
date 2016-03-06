package streams.java;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.stream.Stream;

public class Movie {

	private static final String PATTERN = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
	
	public final String title;
	public final String studio;
	public final String released;
	public final String status;
	public final String sound;
	public final String versions;
	public final double cost;
	public final String rating;
	public final int year;
	public final String genre;
	public final String aspect;

	public Movie(String line) {
		String[] tokens = line.split(PATTERN, -1);
		title 		= format(tokens[0]);
		studio 		= format(tokens[1]);
		released 	= format(tokens[2]);
		status 		= format(tokens[3]);
		sound 		= format(tokens[4]);
		versions 	= format(tokens[5]);
		cost 		= formatDouble(tokens[6]);
		rating 		= format(tokens[7]);
		year 		= formatInt(tokens[8]);
		genre 		= format(tokens[9]);
		aspect 		= format(tokens[10]);
	}
	
	private static int formatInt(String string) {
		string = format(string);
		try {
			return Integer.parseInt(string);
		}
		catch (Exception ex) {
			return -1;
		}
	}
	
	private static double formatDouble(String string) {
		string = format(string);
		try {
			return Double.parseDouble(string);
		}
		catch (Exception ex) {
			return -1;
		}
	}

	private static String format(String string) {
		if ("".equals(string)) return string;
		if ("\"".equals(string.substring(0, 1))) {
			if (string.length() < 3) return "";
			return string.substring(1, string.length() - 1);
		}
		return string;
	}

	@Override
	public String toString() {
		return String.format("%s, %s (%s)", title, studio, status);
	}
	
	/**
	 * Ne pas oublier de fermer le flux après utilisation !
	 * Ou bien l'ouvrir dans un bloc try :
	 * <code>
	 * 	 try (Stream&lt;Movie&gt; s = Movie.stream().parallel()) { ... }
	 * </code>
	 */
	public static Stream<Movie> stream() {
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("data/dvd_csv.txt"));
			return reader
					.lines()
					.onClose(() -> {
						System.out.println("Stream<Movie> closed");
						try {
							reader.close();
						}
						catch (IOException e) {
							e.printStackTrace();
							throw new UncheckedIOException(e);
						}
					})
					.map(line -> new Movie(line));
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}

	}

}
