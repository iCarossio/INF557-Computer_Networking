package handler;

import javafx.util.Pair;
import multiplexing.MuxDemuxSimple;

import java.net.InetAddress;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class MessageHandler implements IMessageHandler {

    public static final int BUF_LEN = 8192;

    protected LinkedBlockingQueue<Pair<String, InetAddress>> incoming;
    protected MuxDemuxSimple myMuxDemux = null;

    public MessageHandler() {
        incoming = new LinkedBlockingQueue<>(getCapacity());
    }

    /**
     * Set the MuxDemux with which this handler communicate
     */
    public void setMuxDemux(MuxDemuxSimple md){
        myMuxDemux = md;
    }

    /**
     * Handle the message
     */
    public void handleMessage(String m, InetAddress senderAddress){
        // Do not block, even if the capacity is exceeded
        incoming.offer(new Pair<>(m, senderAddress));
    }

    /**
     * @return the constant capacity of the queue, depending on the handler
     */
    public abstract int getCapacity();

    public abstract void run();
}
