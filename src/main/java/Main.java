import org.java_websocket.client.WebSocketClient;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {

        File tokenFile = new File(".WINDToken");
        boolean isTokenSaved = tokenFile.exists();

        Scanner scanner = new Scanner(System.in);
        String token = null;

        if (!isTokenSaved) {
            System.out.print("Input Auth Token: ");
            token = scanner.nextLine();

            System.out.print("Token is not saved. Save Token? [Y/N]: ");
            if (scanner.next().toLowerCase().charAt(0) == 'y') {
                if (tokenFile.createNewFile()) {
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tokenFile));
                    bufferedWriter.write(token);
                    bufferedWriter.close();
                    System.out.println("Saved Token at .WINDToken File.\n");
                } else
                    System.out.println("E: Token was not saved successfully.");
            }
        } else {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(tokenFile))) {
                token = bufferedReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        WebSocketClient webSocketClient = new WINDWebSocketClient(new URI("ws://192.168.35.1:9000/ws"), scanner, token);
        webSocketClient.connect();
    }
}
