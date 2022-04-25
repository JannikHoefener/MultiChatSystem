package Server;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerRunMain {

    public static void main(String[] args) throws IOException {

        // ServerSocket erstellen horch auf Port 1234 und erlaubt 4 Clients
        ServerSocket serverSocket = new ServerSocket( 12345, 4);
        // Erstellt ein Server welches das Serversocket verwendet
        Server server = new Server(serverSocket);
        // Startet den Server
        server.startServer();
    }

}
