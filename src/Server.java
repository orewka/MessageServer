import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

public class Server {
    private int port;
    private Connection connection;

    public Server(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        LinkedBlockingDeque<Message> messageLinkedBlockingDeque = new LinkedBlockingDeque<>();
        Map<String, Connection> stringConnectionMap = new HashMap<>();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Старт");
            Thread sendMessages = new Thread(new SendMessages(connection, messageLinkedBlockingDeque, stringConnectionMap));
            sendMessages.start();
            while (true) {
                Socket clientSocket = serverSocket.accept();
                connection = new Connection(clientSocket);
                Thread readMessage = new Thread(new ReadMessage(connection, messageLinkedBlockingDeque, stringConnectionMap));
                readMessage.start();
            }
        }
    }

    class ReadMessage implements Runnable {
        private Connection connection;
        private LinkedBlockingDeque<Message> messageLinkedBlockingDeque;
        private Map<String, Connection> stringConnectionMap;

        public ReadMessage(Connection connection, LinkedBlockingDeque<Message> messageLinkedBlockingDeque, Map<String, Connection> stringConnectionMap) {
            this.connection = connection;
            this.messageLinkedBlockingDeque = messageLinkedBlockingDeque;
            this.stringConnectionMap = stringConnectionMap;
        }

        @Override
        public void run() {
            Message message;
            while (true) {
                try {
                    message = connection.readMessage();
                    messageLinkedBlockingDeque.add(message);
                    stringConnectionMap.put(message.getSender(), connection);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class SendMessages implements Runnable {
        private Connection connection;
        private LinkedBlockingDeque<Message> messageLinkedBlockingDeque;
        private Map<String, Connection> stringConnectionMap;

        public SendMessages(Connection connection, LinkedBlockingDeque<Message> messageLinkedBlockingDeque, Map<String, Connection> stringConnectionMap) {
            this.connection = connection;
            this.messageLinkedBlockingDeque = messageLinkedBlockingDeque;
            this.stringConnectionMap = stringConnectionMap;
        }

        @Override
        public void run() {
            Message sendMessage;
            while (true) {
                try {
                    sendMessage = messageLinkedBlockingDeque.take();
                    for (Map.Entry<String, Connection> entry: stringConnectionMap.entrySet()) {
                        if (!entry.getValue().getSocket().equals(sendMessage.getSocket())) {
                            entry.getValue().sendMessage(sendMessage);
                        }
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        int port = 8090;
        Server server = new Server(port);
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
