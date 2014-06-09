package autores;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Class responsible for:<br>
 * <ul>
 * 		<li>Reading from a file;</li>
 * 		<li>Populating the database accordingly;</li>
 * 		<li>Communicating with the structures</li>
 * </ul>
 *
 */

public class Lobby {
	private String currentFile;
	private Statistics stats;
	private AuthorIndex index;
	private GlobalAuthorNetwork network;
	
	/**
	 * Empty constructor
	 */
	public Lobby() {
		this.currentFile = "";
		this.stats = new Statistics();
		this.index = new AuthorIndex();
		this.network = new GlobalAuthorNetwork();
	}
	
	/**
	 * Sets the read filename
	 * @param filename
	 */
	public void setFilename(String filename) {
		this.currentFile = filename;
	}
	
	/**
	 * Returns the read filename
	 * @return
	 */
	public String getCurrentFile() {
		return this.currentFile;
	}
	
	/**
	 * Returns the total number of solo publications
	 * @return
	 */
	public int getSoloPublications() {
		return stats.getSoloArticles();
	}
	
	/**
	 * Returns the total number of authors
	 * @return
	 */
	public long getTotalAuthors() {
		return stats.getTotalNames();
	}
	
	/**
	 * Returns the total number of publications
	 * @return
	 */
	public int getTotalPublications() {
		return stats.getTotalArticles();
	}
	
	/**
	 * Returns a tuple containing minimum and maximum years that have publications
	 * @return
	 */
	public Tuple<Integer, Integer> getYearInterval() {
		return stats.getYearInterval();
	}
	
	/**
	 * Returns a navigable map of the year table. The table shall contain an association of year - number of publications
	 * @return
	 */
	public NavigableMap<Integer, Integer> getYearTable() {
		return this.stats.getYearTable();
	}
	
	public NavigableSet<Tuple<String, Integer>> topPublishersInInterval(int min, int max, int nrAuthors) {
		return this.network.topPublishers(min, max, nrAuthors);
	}
	
	public NavigableSet<String> authorsInInterval(int min, int max) throws NoAuthorsInIntervalException {
		return this.network.authorsInInterval(min, max);
	}
	
	/**
	 * Resets the statistics and sets the new filename
	 * @param filename
	 */
	private void reset(String filename) {
		this.stats = new Statistics();
		this.currentFile = filename;
	}
	/**
	 * Returns a navigable set of authors started by the given initial
	 * @param c
	 * @return
	 */
	public NavigableSet<String> getAuthorsBy(char c) {
		return this.index.getAuthorsBy(c);
	}
	
	/**
	 * Reads from a file, populating the database
	 * @param filename name of the file to be read
	 */
	public void readFromFile(String filename) throws IOException {
		this.reset(filename);
		
		BufferedReader br = new BufferedReader( new FileReader(filename) );
		String line = br.readLine();
		
		while(line != null) {
			if(line.length() > 1)
				processData( getLineArgs(line) );
			
			line = br.readLine();
		}
		
		br.close(); // I don't know if this won't give some exceptions
	}
	
	/**
	 * Receives a line, splitting it into valid information to be processed
	 * The information shall be returned as a Collection
	 * @param line
	 * @return
	 */
	private List<String> getLineArgs(String line) {
		ArrayList<String> args = new ArrayList<>();
		
		for( String s : line.split(",") )
			args.add( s.trim() );
		
		return args;
	}
	
	/**
	 * Processes the data, inserting to databases
	 * @param args
	 */
	private void processData(List<String> args) {
		int year = Integer.parseInt( args.get(args.size() - 1) );
		List<String> authorArgs = args.subList(0, args.size() - 1);
		
		for(String s : authorArgs)
			this.index.addAuthor(s);
		
		this.network.addPublication(year, authorArgs);
		
		this.stats.process(year, authorArgs);
	}
	
	/**
	 * Counts the number of repeated lines for a given file.
	 * @param filename
	 * @return
	 */
	public int countRepeatedLines(String filename) throws IOException {
		TreeSet<String> lineTree = new TreeSet<>();
		int repeatedLines = 0;
		
		BufferedReader br = new BufferedReader( new FileReader(filename) );
		String line = br.readLine();
		
		while(line != null) {
			if(line.length() > 1) {
				if( lineTree.contains(line) ) repeatedLines++;
				else lineTree.add(line);	
			}
			
			line = br.readLine();
		}
		
		br.close(); // I don't know if this won't give some exceptions
		
		return repeatedLines;
	}
	
	public NavigableSet<Tuple<Tuple<String, String>, Integer>> topPairs(int min, int max, int nrAuthors) {
		return this.network.topPairs(min, max, nrAuthors);
	}
	

	private class Statistics {
		private int totalArticles;
		private long totalNames;
		private int soloArticles;
		private TreeMap<Integer, Integer> yearTable;
		
		/**
		 * Empty constructor
		 */
		public Statistics() {
			this.totalArticles = 0;
			this.totalNames = 0;
			this.soloArticles = 0;
			this.yearTable = new TreeMap<>();
		}
		
		/**
		 * Returns total number of articles read
		 * @return
		 */
		public int getTotalArticles() {
			return this.totalArticles;
		}
		
		/**
		 * Returns total number of names read
		 * @return
		 */
		public long getTotalNames() {
			return this.totalNames;
		}
		
		/**
		 * Returns total number of solo articles
		 * @return
		 */
		public int getSoloArticles() {
			return this.soloArticles;
		}
		
		/**
		 * Returns a tuple containing the minimum and maximum years
		 * @return
		 */
		public Tuple<Integer, Integer> getYearInterval() {
			return new Tuple<>( this.yearTable.firstKey(), this.yearTable.lastKey() );			
		}
		
		/**
		 * Returns a navigable map of the year table. The table shall contain an association of year - number of publications
		 * @return
		 */
		public NavigableMap<Integer, Integer> getYearTable() {
			TreeMap<Integer, Integer> cpy = new TreeMap<>();
			for( Map.Entry<Integer, Integer> pair : this.yearTable.entrySet() )
				cpy.put( (Integer)pair.getKey(), (Integer)pair.getValue() );
			return cpy;
		}
		
		/**
		 * Processes a list containing publication info, updating the variables
		 * @param publication
		 */
		public void process(int year, List<String> publication) {
			updateTotals(publication);
			updateTable(year);
		}
		
		/**
		 * Updates the table for a given year
		 * @param year
		 */
		private void updateTable(int year) {
			
			if( this.yearTable.containsKey(year) ) {
				int oldTotal = this.yearTable.get(year);
				this.yearTable.put(year, oldTotal + 1);
			}
			
			else
				this.yearTable.put(year, 0);
		}
		
		/**
		 * Updates the totals of articles, names and solo articles
		 * @param publication Information of the publication
		 */
		private void updateTotals(List<String> publication) {
			this.totalArticles++;
			this.totalNames += publication.size();
			if(publication.size() == 1)	// if the publication only has one author
				this.soloArticles++;
		}
	}	
	
}
