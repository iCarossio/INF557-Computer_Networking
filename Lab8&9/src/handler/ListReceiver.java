package handler;

import database.ListReceptionManager;
import javafx.util.Pair;
import message.ListMessage;
import peers.StateManager;

import java.net.InetAddress;

public class ListReceiver extends MessageHandler {

    private static int CAPACITY = 40960;

    public ListReceiver() {
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

                    ListMessage listMessage = new ListMessage(nextMessage.getKey());

                    if (listMessage.getSenderId().equals(StateManager.getSenderId()))
                        continue;

                    ListReceptionManager.getInstance().receiveListMessage(listMessage);

                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleMessage(String m, InetAddress senderAddress) {
        if (m.split(";")[0].toUpperCase().equals("LIST"))
            super.handleMessage(m, senderAddress);
    }

}
