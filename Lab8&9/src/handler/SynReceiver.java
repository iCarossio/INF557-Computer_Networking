package handler;

import javafx.util.Pair;
import message.ListMessage;
import message.SynMessage;
import peers.StateManager;
import database.DatabaseManager;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class SynReceiver extends MessageHandler {

    private static int CAPACITY = 40960;

    public SynReceiver() {
        super();
    }

    @Override
    public int getCapacity() {
        return CAPACITY;
    }

    /**
     * Handle new messages in and update peer state with to their current information
     *
     * For the moment, we decided to answer favorably even to inconsistent neighbors
     */
    public void run() {
        try {

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Pair<String, InetAddress> nextMessage = incoming.take();

                    SynMessage synMessage = new SynMessage(nextMessage.getKey());

                    int mySequenceNb = DatabaseManager.getInstance().getDatabaseSequenceNb();

                    if (!synMessage.getPeerId().equals(StateManager.getSenderId()) // If the message is not addressed to me…
                            || synMessage.getSequenceNb() != mySequenceNb) { // If the requested sequence number is not the current…
                        continue; // …drop it
                    }

                    // To make sure that the database has not been changed during the constitution of the packets, check the sequence number before and after their creation

                    List<String> data = DatabaseManager.getInstance().getData(synMessage.getSequenceNb());

                    // If the SYN is not relevant
                    if (data == null)
                        continue;

                    // Prepare the messages
                    String peerId = synMessage.getSenderId();
                    String senderId = StateManager.getSenderId();

                    int totalParts = data.size();
                    int partNb = 0;

                    List<String> messagesToSend = new ArrayList<>();

                    for (String payload : data) {
                        messagesToSend.add(new ListMessage(senderId, peerId, mySequenceNb, totalParts, partNb, payload).getListMessageAsEncodedString());
                        partNb++;
                    }

                    // If the local serial number has not changed during the message processing, we can send the database
                    if (DatabaseManager.getInstance().getDatabaseSequenceNb() == mySequenceNb) {
                        for (String encodedMessage : messagesToSend)
                            myMuxDemux.send(encodedMessage);
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
        if (m.split(";")[0].toUpperCase().equals(SynMessage.TYPE))
            super.handleMessage(m, senderAddress);
    }
}
