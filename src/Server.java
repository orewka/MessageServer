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
        Map<Socket, Connection> socketConnectionMap = new HashMap<>();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Старт");
            Thread sendMessages = new Thread(new SendMessages(messageLinkedBlockingDeque, socketConnectionMap));
            sendMessages.start();
            Socket clientSocket;
            while (true) {
                if (!socketConnectionMap.containsKey(clientSocket = serverSocket.accept())) {
                    socketConnectionMap.put(clientSocket, connection = new Connection(clientSocket));
                    Thread readMessage = new Thread(new ReadMessage(connection, messageLinkedBlockingDeque, socketConnectionMap));
                    readMessage.start();
                }
            }
        }
    }

    class ReadMessage implements Runnable {
        private Connection connection;
        private LinkedBlockingDeque<Message> messageLinkedBlockingDeque;
        private Map<Socket, Connection> socketConnectionMap;

        public ReadMessage(Connection connection, LinkedBlockingDeque<Message> messageLinkedBlockingDeque, Map<Socket, Connection> socketConnectionMap) {
            this.messageLinkedBlockingDeque = messageLinkedBlockingDeque;
            this.connection = connection;
            this.socketConnectionMap = socketConnectionMap;
        }

        @Override
        public void run() {
            Message message;
            System.out.println("Юзер +");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    message = connection.readMessage();
                    messageLinkedBlockingDeque.add(message);
                } catch (IOException e) {
                    socketConnectionMap.remove(connection.getSocket());
                    Thread.currentThread().interrupt();
                    System.out.println("Юзер -");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class SendMessages implements Runnable {
        private LinkedBlockingDeque<Message> messageLinkedBlockingDeque;
        private Map<Socket, Connection> stringConnectionMap;

        public SendMessages(LinkedBlockingDeque<Message> messageLinkedBlockingDeque, Map<Socket, Connection> stringConnectionMap) {
            this.messageLinkedBlockingDeque = messageLinkedBlockingDeque;
            this.stringConnectionMap = stringConnectionMap;
        }

        @Override
        public void run() {
            Message sendMessage;
            while (true) {
                try {
                    sendMessage = messageLinkedBlockingDeque.take();
                    for (Map.Entry<Socket, Connection> entry: stringConnectionMap.entrySet()) {
                        if (!entry.getKey().equals(sendMessage.getSocket())) {
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
