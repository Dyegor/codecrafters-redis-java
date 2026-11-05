import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

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
        String responseMessage = "+PONG\r\n";

        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {
            String message;

            while ((message = in.readLine()) != null) {
                if (!message.startsWith("*")) {
                    break;
                }

                if (isNull(in.readLine()) || isNull(in.readLine())) {
                    break;
                }

                out.print(responseMessage);
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}
