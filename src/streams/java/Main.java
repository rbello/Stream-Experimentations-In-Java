package streams.java;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

	public static void main(String[] args) {

		displayCountedByYears();
		
		displayTitlesWordsCountedAndOrdered();
		
		displayStudiosProductionsOutCounted();
	
		displayDistinctVersions();
		
		displayAverageMovieCostByYears();
		
	}

	protected static void displayAverageMovieCostByYears() {
		long startTime = System.nanoTime();
		final NumberFormat formatter = NumberFormat.getNumberInstance();
		
		// On ouvre un flux de traitement parall�lisable
		try (Stream<Movie> stream = Movie.stream().parallel()) {
			stream
			
				// On �limine le bruit
				.filter(movie -> movie.year > 1000)
			
				// On associe � chaque ann�e la liste des films parus cette ann�e
				// C'est � dire une Map<Integer, List<Movie>>
				.collect(Collectors.groupingBy(movie -> movie.year))
				
				// On parcours la map
				.forEach((year, movies) -> {
					
					// On calcule la moyenne du prix
					double averageCost = movies.stream()
						.filter(movie -> movie.cost > -1)
						.collect(Collectors.averagingDouble(movie -> movie.cost));
					
					// Affichage en euros
					System.out.println(String.format("%s = %s �", year, formatter.format(averageCost * 10000)));
					
				});
		}
		System.out.println("That took " + ((System.nanoTime() - startTime) / 1000000l) + " milliseconds");
	}

	/**
	 * Affiche le nombre de films parus par ann�es, class�s par nombre de parutions croissant
	 */
	protected static void displayCountedByYears() {
		
		long startTime = System.nanoTime();
		
		// Le fait d'ouvrir un flux dans un try permet d'automatiquement
		// appeler le close() dessus � la fin du bloc
		try (Stream<Movie> stream = Movie.stream().parallel()) {
			
			Map<Integer, Long> countedByYears = stream
					.collect(Collectors.groupingBy(movie -> movie.year, Collectors.counting()));
			
			new TreeMap<Integer, Long>(countedByYears)
				.forEach((year, count) -> System.out.println(year + " = " + count));
			
		}
		
		System.out.println("That took " + ((System.nanoTime() - startTime) / 1000000l) + " milliseconds");
		
	}
	
	/**
	 * Affiche l'ensemble des diff�rents mots utilis�s dans les titres des films, et compte
	 * leurs occurences. Les mots cl�s sont tri�s par nombre croissant d'occurences.
	 */
	protected static void displayTitlesWordsCountedAndOrdered() {
		
		long startTime = System.nanoTime();
		
		// On ouvre un flux de traitement parall�lisable
		try (Stream<Movie> stream = Movie.stream().parallel()) {
			
			stream
			
				// Pour chaque film on recup�re la liste des mots du titre, sous forme de String[]
				.map(movie -> movie.title.trim().split("\\s+"))
				
				// On refait une liste unique � partir des diff�rents tableaux de string
				.flatMap(words -> Arrays.asList(words).stream())
				
				// On collecte les donn�es sous forme de map
				.collect(Collectors.groupingBy(
						// Les cl�s sont les mots des noms des films, avec un petit nettoyage
						word -> word.replaceAll("[^A-Za-z0-9 ]", ""),
						// Les valeurs sont les nombres d'occurences de ces mots
						Collectors.counting()))
				
				// On va travailler maintenant sur le flux de EntrySet et non plus sur la map produite
				.entrySet().stream()
				
				// On filtre les mots dont la taille est sup�rieure � 2
				.filter(entry -> entry.getKey().length() > 2)
				
				// On trie par nombre d'occurence
				.sorted(Map.Entry.comparingByValue())
				
				// On affiche enfin le r�sultat
				.forEach((entry) -> System.out.println(entry.getKey() + " = " + entry.getValue()));
			
		}
		
		System.out.println("That took " + ((System.nanoTime() - startTime) / 1000000l) + " milliseconds");
		
	}
	
	/**
	 * Affiche les 50 premiers studios en nombre de parutions de films
	 */
	protected static void displayStudiosProductionsOutCounted() {
		long startTime = System.nanoTime();
		
		// On ouvre un flux de traitement parall�lisable
		try (Stream<Movie> stream = Movie.stream().parallel()) {
			
			final String status = "Out";
			
			stream
			
				// On filtre les films sortis uniquement
				.filter(movie -> Objects.equals(movie.status, status))
				
				// On groupe par nom de studio 
				.collect(Collectors.groupingBy(movie -> movie.studio, Collectors.counting()))
				
				// On travail sur le flux d'EntrySet
				.entrySet().stream()
				
				// On classe par nombre de films croissant
				.sorted((a, b) -> a.getValue().compareTo(b.getValue()) * -1)
				
				// Limit� aux 50 premiers
				.limit(50)
				
				// On affiche
				.forEachOrdered(entry -> System.out.println(entry.getKey() + " = " + entry.getValue()));
		}
		
		System.out.println("That took " + ((System.nanoTime() - startTime) / 1000000l) + " milliseconds");
	}
	
	/**
	 * Affiche les diff�rentes versions existantes
	 */
	protected static void displayDistinctVersions() {
		long startTime = System.nanoTime();
		try (Stream<Movie> stream = Movie.stream().parallel()) {
			
			stream
				// On recup�re les diff�rentes versions, pour cela on transforme le flux
				.flatMap(movie -> Arrays.asList(movie.versions.split(",")).stream().map(version -> version.trim()))
				// On supprime les doublons
				.distinct()
				// On affiche
				.forEach(System.out::println);
			
		}
		System.out.println("That took " + ((System.nanoTime() - startTime) / 1000000l) + " milliseconds");
	}

}