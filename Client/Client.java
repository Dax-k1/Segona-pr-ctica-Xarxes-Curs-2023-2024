import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5000);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                printMenu();
                int option = getOption(userInput);
                out.writeInt(option);
                out.flush();

                switch (option) {
                    case 1:
                        receiveAndPrintTitles(in);
                        break;
                    case 2:
                        searchAndPrintBookInfo(userInput, out, in);
                        break;
                    case 3:
                        addBook(userInput, out, in);
                        break;
                    case 4:
                        deleteBook(userInput, out, in);
                        break;
                    case 5:
                        quit(socket, out, in, userInput);
                        return;

                }
            }
        } catch (IOException ex) {
            System.err.println("Error: " + ex.getMessage());
        }
    }

    private static void printMenu() {
        System.out.println("Menú de opciones:");
        System.out.println("1 - Listar todos los títulos.");
        System.out.println("2 - Obtener información de un libro.");
        System.out.println("3 - Añadir un libro.");
        System.out.println("4 - Eliminar un libro.");
        System.out.println("5 - Salir.");
    }

    private static int getOption(BufferedReader userInput) {
        int option = 0;
        try {
            System.out.println("Elija una opción: ");
            option = Integer.parseInt(userInput.readLine());
        } catch (IOException | NumberFormatException ex) {
            System.err.println("Error: " + ex.getMessage());
        }
        return option;
    }

    private static void receiveAndPrintTitles(ObjectInputStream in) throws IOException {
        try {
            int numBooks = in.readInt();
            System.out.println("Número de libros: " + numBooks);
            for (int i = 0; i < numBooks; i++) {
                String title = (String) in.readObject();
                System.out.println(title);
            }
        } catch (ClassNotFoundException ex) {
            System.err.println("Class not found: " + ex.getMessage());
        }
    }

    private static void searchAndPrintBookInfo(BufferedReader userInput, ObjectOutputStream out, ObjectInputStream in) throws IOException {
        System.out.println("Escriba el título del libro: ");
        String title = userInput.readLine();
        out.writeObject(title);
        out.flush();

        try {
            BookInfo book = (BookInfo) in.readObject();
            if (book != null) {
                System.out.println(book);
            } else {
                System.out.println("Libro no encontrado.");
            }
        } catch (ClassNotFoundException ex) {
            System.err.println("Class not found: " + ex.getMessage());
        }
    }

    private static void addBook(BufferedReader userInput, ObjectOutputStream out, ObjectInputStream in) throws IOException {
        try {
            System.out.println("Escriba el título del libro a añadir: ");
            String title = userInput.readLine();
            System.out.println("Introduzca el número de páginas: ");
            int pages = Integer.parseInt(userInput.readLine());
            System.out.println("Indique el autor (deje en blanco si es anónimo): ");
            String author = userInput.readLine();
            System.out.println("Especifique la serie (deje en blanco si es un libro suelto): ");
            String series = userInput.readLine();

            BookInfo book = new BookInfo(title, pages, author, series);
            out.writeObject(book);
            out.flush();

            boolean success = in.readBoolean();
            if (!success) {
                System.out.println("¡Este libro ya estaba en la base de datos!");
            }
        } catch (NumberFormatException ex) {
            System.err.println("Error: " + ex.getMessage());
        }
    }

    private static void deleteBook(BufferedReader userInput, ObjectOutputStream out, ObjectInputStream in) throws IOException {
        System.out.println("Escriba el título del libro a eliminar: ");
        String title = userInput.readLine();
        out.writeObject(title);
        out.flush();

        try {
            boolean success = in.readBoolean();
            if (!success) {
                System.out.println("Libro no encontrado.");
            }
        } catch (IOException ex) {
            System.err.println("Error: " + ex.getMessage());
        }
    }

    private static void quit(Socket socket, ObjectOutputStream out, ObjectInputStream in, BufferedReader userInput) {
        try {
            socket.close();
            out.close();
            in.close();
            userInput.close();
            System.exit(0);
        } catch (IOException ex) {
            System.err.println("Error: " + ex.getMessage());
            System.exit(-1);
        }
    }
}
