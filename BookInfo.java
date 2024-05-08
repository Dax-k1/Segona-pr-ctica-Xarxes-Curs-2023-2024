public class BookInfo {

	private String  title;
	private   int   pages;
	private String author;
	private String series;

	private static final int  TITLE_LIMIT = 32;
	private static final int AUTHOR_LIMIT = 32;
	private static final int SERIES_LIMIT = 32;

	public static final int SIZE = 2 * TITLE_LIMIT
	                             + 4 // pages
	                             + 2 * AUTHOR_LIMIT
	                             + 2 * SERIES_LIMIT;

	public BookInfo (String title, int pages, String author, String series) {
		this.title = title;
		this.pages = pages;
		this.author = author;
		this.series = series;
	}

	// Getters
	public String getTitle () { return title;  }
	public   int  getPages () { return pages;  }
	public String getAuthor() { return author; }
	public String getSeries() { return series; }

	public byte[] toBytes() {
		byte[] record = new byte[SIZE];
		int offset = 0;
		PackUtils.packLimitedString (title,  TITLE_LIMIT,  record, offset);
		offset += 2 * TITLE_LIMIT;
		PackUtils.packInt (pages, record, offset);
		offset += 4;
		PackUtils.packLimitedString (author, AUTHOR_LIMIT, record, offset);
		offset += 2 * AUTHOR_LIMIT;
		PackUtils.packLimitedString (series, SERIES_LIMIT, record, offset);
		// offset += 2 * SERIES_LIMIT;
		return record;
	}

	public static BookInfo fromBytes (byte[] record) {
		int offset = 0;
		String  title = PackUtils.unpackLimitedString (TITLE_LIMIT, record, offset);
		offset += 2 * TITLE_LIMIT;
		int pages = PackUtils.unpackInt (record, offset);
		offset += 4;
		String author = PackUtils.unpackLimitedString (AUTHOR_LIMIT, record, offset);
		offset += 2 * AUTHOR_LIMIT;
		String series = PackUtils.unpackLimitedString (SERIES_LIMIT, record, offset);
		// offset += 2 * SERIES_LIMIT;
		return new BookInfo (title, pages, author, series);
	}

	public String toString() {
		String result = title;
		if (series != null && !series.isEmpty()) {
			result += " (" + series + ")";
		}
		if (author != null && !author.isEmpty()) {
			result += ", per " + author;
		} else {
			result += ", anònim";
		}
		result += ", que té " + pages + " pàgines.";

		return result;
	}

}
