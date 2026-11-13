package clienthandler;

import commandprocessor.CommandProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler {
    private final CommandProcessor commandProcessor = new CommandProcessor();

    public void handleClient(Socket client) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {

            String message;
            List<String> inputData = new ArrayList<>();
            int expectedArguments = 0;
            int commandLength = 0;

            while ((message = in.readLine()) != null) {
                if (message.startsWith("*")) {
                    expectedArguments = Integer.parseInt(message.substring(1));
                    continue;
                }

                if (message.startsWith("+") | message.startsWith("$")) {
                    commandLength = Integer.parseInt(message.substring(1));
                    continue;
                }

                inputData.add(readInput(commandLength, message));

                if (inputData.size() == expectedArguments) {
                    commandProcessor.processCommand(inputData, out);
                    inputData.clear();
                }
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }

    private String readInput(int commandLength, String message) {
        return message.substring(0, commandLength);
    }
}
