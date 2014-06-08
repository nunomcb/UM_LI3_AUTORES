package autores;

import java.util.Map;

/**
 * Main class of the project, responsible for UI and delegation of user commands to the correct handlers
 *
 */

public class AuthorNetwork {

	private MenuOption[] mainMenu;
	private boolean isActive;
	private Lobby lobby;
	
	
	private static final String[] mainMenuStrings = {
		"Exit", "Read from file", "Count repeated lines", "Get statistics", "Year Table"
	};
	
	
	/**
	 * Empty constructor
	 */
	public AuthorNetwork() {
		this.isActive = false;
		this.mainMenu = null;
		this.lobby = new Lobby();
		this.generateMainMenu(); 
	}
	
	/* ##### Query methods ##### */
	
	/**
	 * Scans the user for a filename, reading from it
	 */
	private void readFromFile() {
		String filename = Scan.scanString("Enter a filename, please");
		System.out.println("Yea... I'm going to read from a file, now: " + filename);
	}
	
	/**
	 * Scans the user for a file name.<br>
	 * Proceeds to count the repeated lines.
	 */
	private void countLines() {
		String filename = Scan.scanString("Enter a filename: ");
		int count = this.lobby.countRepeatedLines(filename);
		System.out.println("Number of repeated lines: " + count);
		Scan.pressEnterToContinue();
	}
	
	/**
	 * Prints the statistics for the read file
	 */
	private void getStatistics() {
		StringBuilder sb = new StringBuilder();
		sb.append("\nStatistics for ");
		sb.append( this.lobby.getCurrentFile() );
		sb.append("\nTotal number of articles: ");
		sb.append( this.lobby.getTotalPublications() );
		sb.append("\nTotal number of authors: ");
		sb.append( this.lobby.getTotalAuthors() );
		sb.append("\nTotal number of solo authors: ");
		sb.append("INFORMATION PENDING"); // replace this
		sb.append("\nTotal number of non-solo authors: ");
		sb.append("INFORMATION PENDING"); // replace this
		sb.append("\nTotal number of different authors: ");
		sb.append("INFORMATION PENDING"); // replace this
		sb.append("\nTotal number of solo publications: ");
		sb.append( this.lobby.getSoloPublications() );
		sb.append("\nYear interval: ");
		Tuple<Integer, Integer> interval = this.lobby.getYearInterval();
		sb.append("[" + interval.getFirst() + ", " + interval.getSecond() + "]");
		System.out.println( sb.toString() );
		Scan.pressEnterToContinue();
	}
	
	/**
	 * Prints a table of year-number of publications pair
	 */
	private void getYearTable() {
		for(Map.Entry<Integer, Integer> pair : this.lobby.getYearTable().entrySet() )
			System.out.println(pair.getKey() + ": " + pair.getValue() );
		Scan.pressEnterToContinue();
	}
	
	/* ##### UI methods ##### */
	
	/**
	 * Print a friendly welcome message
	 */
	private static void greet() {
		System.out.println("Hello and welcome to the Author Network.");
	}
	
	/**
	 * Print a friendly goodbye message, setting the app to inactive 
	 */
	private void shutdown() {
		System.out.println("Bye bye.");
		this.isActive = false;
	}
	
	/**
	 * Generates the main menu option to be selected
	 */
	private void generateMainMenu() {
		final AuthorNetwork app = this; // Put the app in context to generate its Menu Options
		this.mainMenu = new MenuOption[] {
				new MenuOption() { public void exec() { app.shutdown(); } },
				new MenuOption() { public void exec() { app.readFromFile(); } },
				new MenuOption() { public void exec() { app.countLines(); } },
				new MenuOption() { public void exec() { app.getStatistics(); } },
				new MenuOption() { public void exec() { app.getYearTable(); } }
		};
	}
	
	/**
	 * Prints the main menu options
	 */
	private static void printMainMenu() {
		int i = 0;
		for(String s : AuthorNetwork.mainMenuStrings)
			System.out.println( (i++) + ". " + s);
	}
	
	private void bootstrap() {
		this.isActive = true;
		this.lobby.readFromFile("test/publicx.txt");
	}
	
	/**
	 * Runs a command interpreter, printing the options and scanning the user.
	 * It then proceeds to call the selected functionality 
	 */
	public void commandInterpreter() {
		AuthorNetwork.printMainMenu();
		
		int option = Scan.intInRange(
				"Please select an option",
				0,
				AuthorNetwork.mainMenuStrings.length - 1
				);
		
		this.mainMenu[option].exec();
	}
	
	/**
	 * Keeps the app in a cycle while it is active
	 * The user should select the shutdown option to mark it as "inactive"
	 */
	public void run() {
		this.bootstrap();
		while(this.isActive)
			this.commandInterpreter();
	}
	
	/**
	 * Let the party start
	 * @param args
	 */
	public static void main(String[] args) {
		AuthorNetwork.greet();
		new AuthorNetwork().run();
	}

}