package streams.java;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

	public static void main(String[] args) {

		displayCountedByYears();
		
		displayTitlesWordsCountedAndOrdered();
		
	}

	/**
	 * Affiche le nombre de films parus par années
	 */
	protected static void displayCountedByYears() {
		
		long startTime = System.nanoTime();
		
		// Le fait d'ouvrir un flux dans un try permet d'automatiquement
		// appeler le close() dessus à la fin du bloc
		try (Stream<Movie> s = Movie.stream().parallel()) {
			
			Map<Integer, Long> countedByYears = s
					.collect(Collectors.groupingBy(movie -> movie.year, Collectors.counting()));
			
			new TreeMap<Integer, Long>(countedByYears)
				.forEach((year, count) -> System.out.println(year + " = " + count));
			
		}
		
		System.out.println("That took " + ((System.nanoTime() - startTime) / 1000000l) + " milliseconds");
		
	}
	
	/**
	 * Affiche l'ensemble des différents mots utilisés dans les titres des films, et compte
	 * leurs occurences. Les mots clés sont triés par nombre croissant d'occurences.
	 */
	protected static void displayTitlesWordsCountedAndOrdered() {
		
		long startTime = System.nanoTime();
		
		// On ouvre un flux de traitement parallélisable
		try (Stream<Movie> stream = Movie.stream().parallel()) {
			
			stream
			
				// Pour chaque film on recupère la liste des mots du titre, sous forme de String[]
				.map(movie -> movie.title.trim().split("\\s+"))
				
				// On refait une liste unique à partir des différents tableaux de string
				.flatMap(words -> Arrays.asList(words).stream())
				
				// On collecte les données sous forme de map
				.collect(Collectors.groupingBy(
						// Les clés sont les mots des noms des films, avec un petit nettoyage
						word -> word.replaceAll("[^A-Za-z0-9 ]", ""),
						// Les valeurs sont les nombres d'occurences de ces mots
						Collectors.counting()))
				
				// On va travailler maintenant sur le flux de EntrySet et non plus sur la map produite
				.entrySet().stream()
				
				// On filtre les mots dont la taille est supérieure à 2
				.filter(entry -> entry.getKey().length() > 2)
				
				// On trie par nombre d'occurence
				.sorted(Map.Entry.comparingByValue())
				
				// On affiche enfin le résultat
				.forEach((entry) -> System.out.println(entry.getKey() + " = " + entry.getValue()));
			
		}
		
		System.out.println("That took " + ((System.nanoTime() - startTime) / 1000000l) + " milliseconds");
		
	}

}
