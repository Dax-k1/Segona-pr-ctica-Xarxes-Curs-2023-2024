import java.io.RandomAccessFile;
import java.io.IOException;

public class BooksDB {

	private RandomAccessFile booksDB;
	private int numBooks;

	public BooksDB (String fileName) throws IOException {
		booksDB = new RandomAccessFile (fileName, "rw");
		numBooks = (int)booksDB.length() / BookInfo.SIZE;
	}

	public int getNumBooks() {
		return numBooks;
	}

	public void close() throws IOException {
		booksDB.close();
	}

	public void reset() throws IOException {
		booksDB.setLength (0);
		numBooks = 0;
	}

	public BookInfo readBookInfo (int n) throws IOException {
		booksDB.seek (n * BookInfo.SIZE);
		byte[] record = new byte[BookInfo.SIZE];
		booksDB.read (record);
		return BookInfo.fromBytes (record);
	}

	public int searchBookByTitle (String title) throws IOException {
		for (int i = 0; i < numBooks; i++) {
			BookInfo book = readBookInfo (i);
			if (title.equalsIgnoreCase (book.getTitle())) {
				return i;
			}
		}
		return -1;
	}

	public void writeBookInfo (int n, BookInfo book) throws IOException {
		booksDB.seek (n * BookInfo.SIZE);
		byte[] record = book.toBytes();
		booksDB.write (record);
	}

	public void appendBookInfo (BookInfo book) throws IOException {
		writeBookInfo (numBooks, book);
		numBooks++;
	}

	public boolean insertNewBook (BookInfo book) throws IOException {
		int n = searchBookByTitle (book.getTitle());
		if (n == -1) {
			appendBookInfo (book);
			return true;
		}
		return false;
	}

	private void deleteBook (int n) throws IOException {
		BookInfo lastBook = readBookInfo (numBooks - 1);
		writeBookInfo (n, lastBook);
		booksDB.setLength ((numBooks - 1) * BookInfo.SIZE);
		numBooks--;
	}

	public boolean deleteByTitle (String title) throws IOException {
		int n = searchBookByTitle (title);
		if (n != -1) {
			deleteBook (n);
			return true;
		}
		return false;
	}

}
