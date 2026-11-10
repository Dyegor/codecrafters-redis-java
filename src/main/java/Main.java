import clienthandler.ClientHandler;

import java.io.*;
        import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        int port = 6379;
        ClientHandler clientHandler = new ClientHandler();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);

            while (true) {
                Socket client = serverSocket.accept();
                Thread.startVirtualThread(() -> clientHandler.handleClient(client));
            }
        }
    }
}
