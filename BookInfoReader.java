import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class BookInfoReader {

	public static BookInfo readBookFile (String dirName, String fileName) throws IOException {
		File file = new File (dirName, fileName);
		BufferedReader input = new BufferedReader (new FileReader (file));

		String title = input.readLine();
		if (title == null) {
			title = "";
			System.err.println ("Empty title in file " + fileName);
		}

		int pages = -1;
		String pagesStr = input.readLine();
		if (pagesStr != null) {
			try {
				pages = Integer.parseInt (pagesStr);
			} catch (NumberFormatException ex) {
				System.err.println ("Pages error in file " + fileName);
			}
		} else {
			System.err.println ("Empty pages in file " + fileName);
		}

		String author = input.readLine();
		if (author == null) {
			author = "";
		}
		String series = input.readLine();
		if (series == null) {
			series = "";
		}

		return new BookInfo (title, pages, author, series);
	}

}
