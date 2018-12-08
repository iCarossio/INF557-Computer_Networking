package handler;

import database.SynManager;
import javafx.util.Pair;
import message.HelloMessage;
import message.SynMessage;
import peers.PeerTableManager;
import peers.StateManager;

import java.net.InetAddress;

public class HelloReceiver extends MessageHandler {

    private static int CAPACITY = 40960;

    public HelloReceiver() {
        super();
    }

    @Override
    public int getCapacity() {
        return CAPACITY;
    }

    /**
     * Handle new messages in and update peer state with to their current information
     */
    public void run() {
        try {

            while (!Thread.currentThread().isInterrupted()) {
                // If this is a self-sent message, drop it
                try {
                    Pair<String, InetAddress> nextMessage = incoming.take();

                    HelloMessage helloMessage = new HelloMessage(nextMessage.getKey());

                    if (helloMessage.getSenderId().equals(StateManager.getSenderId()))
                        continue;

                    // Update the peer table, and send a SYN request if necessary
                    if(PeerTableManager.getInstance().updateTable(helloMessage, nextMessage.getValue())) { // If SYN request should be sent

                        PeerTableManager.getInstance().setPeerState(helloMessage.getSenderId(), StateManager.State.HEARD);

                        myMuxDemux.send(new SynMessage(StateManager.getSenderId(), helloMessage.getSenderId(), helloMessage.getSequenceNb()).getSynMessageAsEncodedString());
                        SynManager.getInstance().setSynPeer(helloMessage.getSenderId(), helloMessage.getSequenceNb(), SynManager.getInstance());
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleMessage(String m, InetAddress senderAddress) {
        if (m.split(";")[0].toUpperCase().equals("HELLO"))
            super.handleMessage(m, senderAddress);
    }
}