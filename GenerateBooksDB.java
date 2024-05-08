import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class GenerateBooksDB {

	private static final String BOOKS_DB_NAME = "booksDB.dat";
	private static final String BOOKS_FILES = "books.txt";
	private static final String BOOKS_DIR = "Books";
	private static BooksDB booksDB;

	public static void main (String[] args) {
		try {
			booksDB = new BooksDB (BOOKS_DB_NAME);
			loadFromFiles();
		} catch (IOException ex) {
			System.err.println ("Error generating database!");
		}
	}

	private static void loadFromFiles() throws IOException {
		booksDB.reset();
		BufferedReader input = new BufferedReader (new FileReader (BOOKS_FILES));
		String fileName = input.readLine();
		while (fileName != null) {
			BookInfo book = BookInfoReader.readBookFile (BOOKS_DIR, fileName);
			booksDB.appendBookInfo (book);
			fileName = input.readLine();
		}
		input.close();
	}

}
