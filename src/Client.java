import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.Scanner;

public class Client {
    private String server;
    private int port;
    private Scanner scanner;

    public Client(String server, int port) {
        this.server = server;
        this.port = port;
        this.scanner = new Scanner(System.in);
    }

    public void start() throws Exception {
        System.out.println("Введите имя");
        String name = scanner.nextLine();
        String messageText;
        try (Connection connection = new Connection(new Socket(server, port))) {
            Thread getMessage = new Thread(new GetMessage(connection));
            getMessage.start();
            while (true) {
                System.out.println("Введите сообщение");
                messageText = scanner.nextLine();
                try {
                    sendMessage(name, messageText, connection);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendMessage(String name, String messageText, Connection connection) throws Exception {
            Message message = new Message(name, messageText, connection.getSocket());
            connection.sendMessage(message);
    }

    class GetMessage implements Runnable {
        Message message;
        Connection connection;

        public GetMessage(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
                while (true) {
                    try {
                        message = connection.readMessage();
                        System.out.println(message.getDate() + " " + message.getSender() + " : " + message.getText());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

        }
    }

    public static void main(String[] args) {
        try (InputStream inputStream = Client.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);
            String server = properties.getProperty("server");
            int port = Integer.parseInt(properties.getProperty("port"));
            Client client = new Client(server, port);
            client.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
