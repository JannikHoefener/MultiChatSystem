package Client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientRunMain {
    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Geben Sie ihren Benutzernamen ein: ");
        String username = scanner.nextLine();

        Socket socket = new Socket("localhost", 12345);
        Client client = new Client(socket, username); // Username wird an Client weiter gegeben und ClientHandler zieht sich diesen auch als firstLine
        client.listenForMessage();
        client.sendMessage();
    }
}
