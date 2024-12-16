package ex2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientChat {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner sc = new Scanner(System.in)) {

            System.out.print("Indica el teu número: ");
            String origen = sc.nextLine();

            System.out.print("Indica el número del receptor: ");
            String desti = sc.nextLine();

            // Fil per escoltar els missatges del servidor
            Thread fil = new Thread(() -> {
                try {
                    String resposta;
                    boolean primerMissatge = true; // Indica si es el primer missatge del block
                    while ((resposta = in.readLine()) != null) {
                        if (!resposta.trim().isEmpty()) {
                            if (primerMissatge) {
                                System.out.println("\nResposta del servidor:");
                                primerMissatge = false;
                            }
                            System.out.println(resposta);
                        } else {
                            primerMissatge = true; // Reiniciar per el proxim block
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error al llegir del servidor: " + e.getMessage());
                }
            });

            fil.start();

            String missatge;
            while (true) {
                missatge = sc.nextLine();

                // Enviam el missatge al servidor
                out.println(origen + ";" + desti + ";" + missatge);

                // Finalitzar si el missatge és "adeu"
                if (missatge.equalsIgnoreCase("adeu")) {
                    System.out.println("Has finalitzat la connexió.");
                    break;
                }
            }

            fil.join(); // Esperar a que el hilo termine antes de cerrar
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
