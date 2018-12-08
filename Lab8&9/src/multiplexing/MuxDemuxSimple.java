package multiplexing;

import handler.IMessageHandler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.LinkedBlockingQueue;

public class MuxDemuxSimple implements IMuxDemux {

    public static final int MUXDEMUX_CAPACITY = 40960;
    public static final int RECV_BUF_LEN = 65536;

    private DatagramSocket socket;
    private IMessageHandler[] myMessageHandlers;
    private LinkedBlockingQueue<String> outgoing = new LinkedBlockingQueue<>(MUXDEMUX_CAPACITY);

    /**
     * Initialize the MuxDemux with a list of MessageHandlers and the communication socket
     */
    public MuxDemuxSimple(IMessageHandler[] h, DatagramSocket s) {
        socket = s;
        myMessageHandlers = h;
    }

    /**
     * Catch all in/out messages of the socket, dans dispatch them to all MessageHandlers
     */
    public void run() {
        for (int i = 0; i < myMessageHandlers.length; i++) {
            myMessageHandlers[i].setMuxDemux(this);
        }
        try {
            byte[] buffer = new byte[RECV_BUF_LEN];
            DatagramPacket packet = new DatagramPacket(buffer, RECV_BUF_LEN);

            while(!Thread.currentThread().isInterrupted()) {
                socket.receive(packet);

                String message = new String(buffer, 0, packet.getLength());
                for (int i = 0; i < myMessageHandlers.length; i++)
                    myMessageHandlers[i].handleMessage(message, packet.getAddress());

                packet.setLength(RECV_BUF_LEN);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket.close();
    }

    /**
     * Store a message which will be sent by SenderThread, into the queue queue
     */
    public void send(String s) {
        outgoing.offer(s); // .offers: Avoid blocking
        System.err.println("\nDEBUG: "+s+"\n");
    }

    /**
     * Get a message from the queue
     */
    @Override
    public String takeNextOutgoingMessage() throws InterruptedException {
        return outgoing.take();
    }

}
