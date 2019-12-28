import java.io.Serializable;
import java.net.Socket;
import java.time.LocalDateTime;

public class Message implements Serializable {
    private String sender;
    private String text;
    private LocalDateTime date;
    private Socket socket;

    public Message(String sender, String text, Socket socket) {
        this.sender = sender;
        this.text = text;
        date = LocalDateTime.now();
        this.socket = socket;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", text='" + text + '\'' +
                ", date=" + date +
                ", socket=" + socket +
                '}';
    }
}
