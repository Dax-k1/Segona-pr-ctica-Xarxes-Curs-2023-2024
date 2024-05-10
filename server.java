import java.io.*;
import java.net.*;

public class Server {
    private static final String BOOKS_DB_NAME = "booksDB.dat";
    private static BooksDB booksDB;

    public static void main(String[] args) {
        try {
            booksDB = new BooksDB(BOOKS_DB_NAME);
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Server started, waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                Thread clientHandler = new Thread(() -> handleClient(clientSocket));
                clientHandler.start();
            }
        } catch (IOException ex) {
            System.err.println("Error starting server: " + ex.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            for (;;) {
                int option = in.readInt();
                switch (option) {
                    case 1:
                        sendTitles(out);
                        break;
                    case 2:
                        sendBookInfo(in, out);
                        break;
                    case 3:
                        receiveAndAddBook(in, out);
                        break;
                    case 4:
                        receiveAndDeleteBook(in, out);
                        break;
                    case 5:
                        quit(clientSocket, out, in);
                        return;
                }
            }
        } catch (IOException ex) {
            System.err.println("Error handling client: " + ex.getMessage());
        }
    }

    private static void sendTitles(ObjectOutputStream out) throws IOException {
        int numBooks = booksDB.getNumBooks();
        out.writeInt(numBooks);
        out.flush();

        for (int i = 0; i < numBooks; i++) {
            try {
                BookInfo book = booksDB.readBookInfo(i);
                out.writeObject(book.getTitle());
                out.flush();
            } catch (IOException ex) {
                System.err.println("Database error!");
            }
        }
    }

    private static void sendBookInfo(ObjectInputStream in, ObjectOutputStream out) throws IOException {
        String title = null;
        try {
            title = (String) in.readObject();
            int n = booksDB.searchBookByTitle(title);
            if (n != -1) {
                BookInfo book = booksDB.readBookInfo(n);
                out.writeObject(book);
                out.flush();
            } else {
                out.writeObject(null);
                out.flush();
            }
        } catch (ClassNotFoundException ex) {
            System.err.println("Class not found: " + ex.getMessage());
        }
    }

    private static void receiveAndAddBook(ObjectInputStream in, ObjectOutputStream out) throws IOException {
        try {
            BookInfo book = (BookInfo) in.readObject();
            boolean success = booksDB.insertNewBook(book);
            out.writeBoolean(success);
            out.flush();
        } catch (ClassNotFoundException ex) {
            System.err.println("Class not found: " + ex.getMessage());
        }
    }

    private static void receiveAndDeleteBook(ObjectInputStream in, ObjectOutputStream out) throws IOException {
        try {
            String title = (String) in.readObject();
            boolean success = booksDB.deleteByTitle(title);
            out.writeBoolean(success);
            out.flush();
        } catch (ClassNotFoundException ex) {
            System.err.println("Class not found: " + ex.getMessage());
        }
    }

    private static void quit(Socket clientSocket, ObjectOutputStream out, ObjectInputStream in) {
        try {
            clientSocket.close();
            booksDB.close();
            System.out.println("Client disconnected.");
            out.close();
            in.close();
            System.exit(0);
        } catch (IOException ex) {
            System.err.println("Error closing resources: " + ex.getMessage());
            System.exit(-1);
        }
    }
}
