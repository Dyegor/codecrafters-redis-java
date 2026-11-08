import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

public class Main {
    static Map<String, String> cachedData = new HashMap<>();

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
                        processCommand(inputData, out);
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

    private static void processCommand(List<String> inputData, PrintWriter out) {
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
                printOutcome(argumentsToPrint.toString(), out);
            }
            case "SET" -> {
                cachedData.put(inputData.getFirst(), inputData.getLast());
                out.print("+OK\r\n");
            }
            case "GET" -> {
                printOutcome(cachedData.get(inputData.getFirst()), out);
            }
        }

        out.flush();
    }

    private static void printOutcome(String outcome, PrintWriter out) {
        if (nonNull(outcome)) {
            out.print("$" + outcome.length() + "\r\n" + outcome + "\r\n");
        } else {
            out.print("$-1\r\n");
        }
    }
}
