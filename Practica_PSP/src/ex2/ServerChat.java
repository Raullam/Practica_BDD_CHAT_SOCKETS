package ex2;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerChat {
    private static final int PORT = 12345;
    private static final String ARXIU_LOG = "chat_log.txt"; // Archivo donde se guardarán las conversaciones

    // Llista concurrent per manejar múltiples clients
    public static final CopyOnWriteArrayList<PrintWriter> clientWriters = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        // Crea l'arxiu log si no existeix
        try {
            new File(ARXIU_LOG).createNewFile();
        } catch (IOException e) {
            System.err.println("Error al crear l'arxiu log: " + e.getMessage());
        }

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor de xat actiu al port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nou client connectat: " + clientSocket.getInetAddress());

                // Cra un fil per manejar cada client
                new Thread(new FilClient(clientSocket, ARXIU_LOG)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
