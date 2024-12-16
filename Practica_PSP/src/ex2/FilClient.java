package ex2;


import java.io.*;
import java.net.Socket;

public class FilClient implements Runnable {
    private final Socket socket;
    private final String ARXIU_LOG;
    private PrintWriter out;

    public FilClient(Socket socket, String logFile) {
        this.socket = socket;
        this.ARXIU_LOG = logFile;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out = new PrintWriter(socket.getOutputStream(), true);
            ServerChat.clientWriters.add(out); // Afegim el client a la llista

            String missatgeRebut;
            while ((missatgeRebut = in.readLine()) != null) {
                System.out.println("Missatge rebut: " + missatgeRebut);

                // Guardem el missatge a l'arxiu
                guardarMissatge(missatgeRebut);

                broadcast(missatgeRebut);
            }
        } catch (IOException e) {
            System.err.println("Error amb el client: " + e.getMessage());
        } finally {
            if (out != null) {
                ServerChat.clientWriters.remove(out); // Eliminar el client quan es desconnecta
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Client desconnectat.");
        }
    }

    private void broadcast(String missatge) {
        for (PrintWriter writer : ServerChat.clientWriters) {
            writer.println(missatge);
            writer.flush();
        }
    }

    // Guardar un missatge a l'arxiu log
    private void guardarMissatge(String missatge) {
        synchronized (ARXIU_LOG) { // Sincronizar el acceso al archivo
            try (FileWriter writer = new FileWriter(ARXIU_LOG, true)) {
                writer.write(missatge + System.lineSeparator());
            } catch (IOException e) {
                System.err.println("Error al guardar el mensaje en el archivo: " + e.getMessage());
            }
        }
    }
}
