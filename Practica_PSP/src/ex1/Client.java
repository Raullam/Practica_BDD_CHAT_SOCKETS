package ex1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345);
             BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Connectat al servidor!");

            String command;
            while (true) {
                System.out.print("Introdueix comanda (insert/select/delete): ");
                command = consoleInput.readLine();

                if (command == null || command.equalsIgnoreCase("exit")) {
                    System.out.println("Sortint...");
                    break;
                }

                out.println(command);
                String response = in.readLine();
                System.out.println("Resposta del servidor: " + response);
            }
        } catch (IOException e) {
            System.out.println("Error al connectar amb el servidor: " + e.getMessage());
        }
    }
}
