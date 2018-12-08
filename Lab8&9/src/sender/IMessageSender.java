package sender;

public interface IMessageSender {
    void startSending();
    void stopSending();

    void setInterval();
}
