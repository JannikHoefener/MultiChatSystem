package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    // Attribute
    private ServerSocket serverSocket;

    // Konstruktor
    public Server (ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    // Methoden
        // Server Starten und Verbindungen von Clients erlauben
    public void startServer() {
        try {

            while (!serverSocket.isClosed()) { // solange das Server-Socket offen ist
                Socket socket = serverSocket.accept();
                System.out.println("Ein neuer Client hat sich verbunden");
                // Es wird ein neuer clientHandler erstellt um den neu verbundenen Client zu organisieren
                ClientHandler clientHandler = new ClientHandler(socket);

                // Das ClientHandler Objekt zu einem Thread machen und starten
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {

        }
    }

        // Server schließen
    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
