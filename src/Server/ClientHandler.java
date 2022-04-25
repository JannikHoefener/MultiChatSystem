package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientHandler implements Runnable{

    // Um Jedem Client die Nachricht eines Clients mitteilen zu können über die ClientHandler
    // ... Broadcast zu allen Clients
    public static ArrayList<ClientHandler> allClientHandlers = new ArrayList<>();
    public LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();


    private Socket clientHandlerSocket; // ServerSocket <---> clientHandlerSocket
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    // Konstruktor
    public ClientHandler(Socket clientHandlerSocket) {
        try {
            this.clientHandlerSocket = clientHandlerSocket;

            // BufferedWriter erstellen
            // Outputstream = Bytestream =>wrapping=> OutputStreamWriter = CharStream
            // Was "Wir" schicken
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientHandlerSocket.getOutputStream())); // Senden aktivierung bei .flush()
            // Was der Client schickt
            this.bufferedReader = new BufferedReader(new InputStreamReader(clientHandlerSocket.getInputStream())); // Lesen

            this.clientUsername = bufferedReader.readLine(); // Erste Line des Clientes bestimmt den clientUsername
            allClientHandlers.add(this); //client zum Array / Gruppenchat hinzufügen
            broadcastMessage("SERVER: " + clientUsername + " hat den Chat betreten");
        } catch (IOException e) {
            e.printStackTrace();
            closeEverything(clientHandlerSocket, bufferedReader, bufferedWriter);
        }
    }

    //Methoden
    @Override
    public void run() {

        // Auf Nachrichten warten und lesen
        // String messageFromClient;
        while (clientHandlerSocket.isConnected()) {
            try {
                queue.add(bufferedReader.readLine());
                System.out.println("DEBUG: "+ queue.size()); // Must be 1
                broadcastMessage(queue.take());
                System.out.println("DEBUG: "+ queue.size()); // Must be 0
            } catch (IOException e) {
                e.printStackTrace();
                closeEverything(clientHandlerSocket, bufferedReader, bufferedWriter);
                break;
            } catch (InterruptedException e) { // Für Queue
                throw new RuntimeException(e);
            }
        }

    }

    public void broadcastMessage(String messageToSend) {
        // Nachricht an alle Clients die sich in der ArrayList befinden senden
        for (ClientHandler clientHandler : allClientHandlers) {
            try {
                if (!clientHandler.clientUsername.equals(clientUsername)) { // Absender (sich selbst) ausschließen
                    clientHandler.bufferedWriter.write(messageToSend);//eingelesene Nachricht in bufferedWriter schreiben
                    clientHandler.bufferedWriter.newLine(); // quasi enter um Nachricht ready zu machen (Quasi wie schreib marschine)
                    clientHandler.bufferedWriter.flush(); // Übertragung der Nachricht (wie bei einer toilette wird alles herrein gegebene geflusht();) und löschen den lokalen Buffers
                }
            } catch (IOException e) {
                e.printStackTrace();
                closeEverything(clientHandlerSocket, bufferedReader, bufferedWriter);
            }
        }
    }
    public void removeClientHandler() {
        allClientHandlers.remove(this);
        broadcastMessage("SERVER: " +clientUsername+ " hat den Chat verlassen!");
    }

    public void closeEverything(Socket clientHandlerSocket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            // Null-Pointer Exception vermeiden
            if (bufferedReader != null){
                bufferedReader.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
            if (clientHandlerSocket != null){
                clientHandlerSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
