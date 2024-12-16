package ex1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final String BBDD_Simulacre = "src/ex1/bbdd.txt";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Servidor esperant connexions...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connectat!");
                
                // Gestionar la connexió amb el client
                controlClient(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Error al crear el servidor: " + e.getMessage());
        }
    }

    private static void controlClient(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String comanda;
            while ((comanda = in.readLine()) != null) {
                String resposta = controlComanda(comanda);
                out.println(resposta);
            }
        } catch (IOException e) {
            System.out.println("Error al gestionar el client: " + e.getMessage());
        }
    }

    private static String controlComanda(String command) {
        String[] parts = command.split(" ");
        String accio = parts[0];

        switch (accio) {
            case "insert":
                return controlInsert(parts);
            case "select":
                return controlSelect(parts);
            case "delete":
                return controlDelete(parts);
            default:
                return "Error: Comanda no reconeguda.";
        }
    }

    private static String controlInsert(String[] parts) {
        if (parts.length < 4) {
            return "Error: Dades insuficients per a 'insert'.";
        }
        String id = parts[1];
        String name = parts[2];
        String surname = parts[3];

        synchronized (BBDD_Simulacre) {
            try (BufferedReader reader = new BufferedReader(new FileReader(BBDD_Simulacre));
                 BufferedWriter writer = new BufferedWriter(new FileWriter(BBDD_Simulacre, true))) {

                // Comprovar si l'ID ja existeix
                String linia;
                while ((linia = reader.readLine()) != null) {
                    if (linia.startsWith(id + " ")) {
                        return "Error: ID " + id + " ja existeix.";
                    }
                }

                // Afegir la dada
                writer.write(id + " " + name + " " + surname);
                writer.newLine();
                return "Dada inserida: " + id + " " + name + " " + surname;
            } catch (IOException e) {
                return "Error al gestionar la base de dades: " + e.getMessage();
            }
        }
    }

    private static String controlSelect(String[] parts) {
        if (parts.length < 2) {
            return "Error: ID no especificat per a 'select'.";
        }
        String id = parts[1];

        synchronized (BBDD_Simulacre) {
            try (BufferedReader reader = new BufferedReader(new FileReader(BBDD_Simulacre))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(id + " ")) {
                        return "Dada trobada: " + line;
                    }
                }
                return "Error: ID " + id + " no trobat.";
            } catch (IOException e) {
                return "Error al gestionar la base de dades: " + e.getMessage();
            }
        }
    }

    private static String controlDelete(String[] parts) {
        if (parts.length < 2) {
            return "Error: ID no especificat per a 'delete'.";
        }
        String id = parts[1];

        synchronized (BBDD_Simulacre) {
            try {
                File BBDD = new File(BBDD_Simulacre);
                File arxiuTemporal = new File(BBDD.getParent(), "temp_bbdd.txt");

                BufferedReader reader = new BufferedReader(new FileReader(BBDD));
                BufferedWriter writer = new BufferedWriter(new FileWriter(arxiuTemporal));

                boolean trobat = false;
                String linia;
                while ((linia = reader.readLine()) != null) {
                    if (linia.startsWith(id + " ")) {
                        trobat = true; // Marcar com a trobat
                    } else {
                        writer.write(linia);
                        writer.newLine(); // Copiar línies que no coincideixin amb l'ID
                    }
                }

                writer.close();
                reader.close();

                // Substituir el fitxer original pel fitxer temporal
                if (trobat) {
                    if (!BBDD.delete()) {
                        return "Error: No s'ha pogut eliminar el fitxer original.";
                    }
                    if (!arxiuTemporal.renameTo(BBDD)) {
                        return "Error: No s'ha pogut substituir el fitxer original.";
                    }
                    return "Dada amb ID " + id + " eliminada.";
                } else {
                    // Si no s'ha trobat l'ID, elimina el fitxer temporal
                    arxiuTemporal.delete();
                    return "Error: ID " + id + " no trobat.";
                }
            } catch (IOException e) {
                return "Error al gestionar la base de dades: " + e.getMessage();
            }
        }
    }

}
