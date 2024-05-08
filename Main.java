import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

public class Main {

	private static final String BOOKS_DB_NAME = "booksDB.dat";
	private static BooksDB booksDB;

	public static void main (String[] args) {
		try {
			booksDB = new BooksDB (BOOKS_DB_NAME);
		} catch (IOException ex) {
			System.err.println ("Error opening database!");
			System.exit (-1);
		}
		for (;;) {
			printMenu();
			int option = getOption();
			switch (option) {
				case 1:
					listTitles();
					break;
				case 2:
					infoFromOneBook();
					break;
				case 3:
					addBook();
					break;
				case 4:
					deleteBook();
					break;
				case 5:
					quit();
					break;
			}
			System.out.println();
		}
	}

	private static void printMenu() {
		System.out.println ("Menú d'opcions:");
		System.out.println ("1 - Llista tots els títols.");
		System.out.println ("2 - Obté la informació d'un llibre.");
		System.out.println ("3 - Afegeix un llibre.");
		System.out.println ("4 - Elimina un llibre.");
		System.out.println ("5 - Sortir.");
	}

	private static int getOption() {
		for (;;) {
			try {
				BufferedReader in = new BufferedReader (new InputStreamReader (System.in));
				System.out.println ("Escull una opció: ");
				String optionStr = in.readLine();
				int option = Integer.parseInt (optionStr);
				if (0 < option && option <= 5) {
					return option;
				}
			} catch (Exception ex) {
				System.err.println ("Error reading option.");
			}
		}
	}

	private static void listTitles() {
		int numBooks = booksDB.getNumBooks();
		System.out.println();
		try {
			for (int i = 0; i < numBooks; i++) {
				BookInfo book = booksDB.readBookInfo (i);
				System.out.println (book.getTitle());
			}
		} catch (IOException ex) {
			System.err.println ("Database error!");
		}
	}

	private static void infoFromOneBook() {
		BufferedReader in = new BufferedReader (new InputStreamReader (System.in));
		System.out.println ("Escriu el títol del llibre: ");
		String title;
		try {
			title = in.readLine();
		} catch (IOException ex) {
			System.err.println ("Error while reading title!");
			return;
		}
		try {
			int n = booksDB.searchBookByTitle (title);
			if (n != -1) {
				BookInfo book = booksDB.readBookInfo (n);
				System.out.println (book);
			} else {
				System.out.println ("Llibre no trobat.");
			}
		} catch (IOException ex) {
			System.err.println ("Database error!");
		}
	}

	private static void addBook() {
		BufferedReader in = new BufferedReader (new InputStreamReader (System.in));
		BookInfo book;
		try {
			System.out.println ("Escriu el títol del llibre a afegir: ");
			String title = in.readLine();
			while (title == null || title.isEmpty())
			{
				System.out.println ("El títol del llibre no pot ser buit.");
				System.out.println ("Escriu el títol del llibre a afegir: ");
				title = in.readLine();
			}
			int pages = -1;
			while (pages < 0) {
				System.out.println ("Introdueix el nombre de pàgines: ");
				String pagesStr = in.readLine();
				if (pagesStr != null) {
					try {
						pages = Integer.parseInt (pagesStr);
					} catch (NumberFormatException ex) {
						// Ignore
					}
				}
			}
			System.out.println ("Indica l'autor (deixa'l buit si és anònim).");
			String author = in.readLine();
			if (author == null) {
				author = "";
			}
			System.out.println ("Especifica la sèrie (buida si és un llibre solt).");
			String series = in.readLine();
			if (series == null) {
				series = "";
			}
			book = new BookInfo (title, pages, author, series);
		} catch (IOException ex) {
			System.err.println ("Error while reading book information!");
			return;
		}
		try {
			boolean success = booksDB.insertNewBook (book);
			if (!success) {
				System.out.println ("Aquest llibre ja estava a la base de dades.");
			}
		} catch (IOException ex) {
			System.err.println ("Database error!");
		}
	}

	private static void deleteBook() {
		BufferedReader in = new BufferedReader (new InputStreamReader (System.in));
		System.out.println ("Escriu el títol del llibre a eliminar: ");
		String title;
		try {
			title = in.readLine();
		} catch (IOException ex) {
			System.err.println ("Error while reading title!");
			return;
		}
		try {
			boolean success = booksDB.deleteByTitle (title);
			if (!success) {
				System.out.println ("Llibre no trobat.");
			}
		} catch (IOException ex) {
			System.err.println ("Database error!");
		}
	}

	private static void quit() {
		try {
			booksDB.close();
			System.exit (0);
		} catch (IOException ex) {
			System.err.println ("Error closing database!");
			System.exit (-1);
		}
	}

}
