import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection implements AutoCloseable {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    public Socket getSocket() {
        return socket;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public void sendMessage(Message message) throws IOException {
        out.writeObject(message);
        out.flush();
    }

    public Message readMessage() throws IOException, ClassNotFoundException {
        return (Message) in.readObject();
    }

    @Override
    public void close() throws Exception {
        out.close();
        in.close();
    }
}