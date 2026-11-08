import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

public class Main {
    public static void main(String[] args) throws IOException {
        int port = 6379;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);

            while (true) {
                Socket client = serverSocket.accept();
                Thread.startVirtualThread(() -> handleClient(client));
            }
        }
    }

    public static void handleClient(Socket client) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {
            String message;
            List<String> inputData = new ArrayList<>();
            int expectedArguments = 0;
            int commandLength = 0;


            while ((message = in.readLine()) != null) {
                if (message.startsWith("*")) {
                    expectedArguments = Integer.parseInt(message.substring(1, 2));
                    continue;
                }

                if (message.startsWith("+") | message.startsWith("$")) {
                    commandLength = Integer.parseInt(message.substring(1, 2));
                    continue;
                }

                if (Character.isLetter(message.charAt(0))) {
                    inputData.add(readInput(commandLength, message));

                    if (inputData.size() == expectedArguments) {
                        printOutput(inputData, out);
                        inputData.clear();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }

    private static String readInput(int commandLength, String message) {
        return message.substring(0, commandLength);
    }

    private static void printOutput(List<String> inputData, PrintWriter out) {
        String command = inputData.removeFirst();

        switch (command) {
            case "PING" -> {
                out.print("+PONG\r\n");
            }
            case "ECHO" -> {
                StringBuilder argumentsToPrint = new StringBuilder();
                for (String argument : inputData) {
                    argumentsToPrint.append(argument);
                }
                out.print("$" + argumentsToPrint.length() + "\r\n" + argumentsToPrint + "\r\n");
            }
        }

        out.flush();
    }
}
