package multiplexing;

public interface IMuxDemux extends Runnable {
    void send(String s);

    /**
     * Blocking
     */
    String takeNextOutgoingMessage() throws InterruptedException;
}
