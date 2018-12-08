package handler;

import javafx.util.Pair;
import message.HelloMessage;
import message.ListMessage;
import message.Message;
import message.SynMessage;
import peers.StateManager;

import java.net.InetAddress;

public class DebugReceiver extends MessageHandler {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private static int CAPACITY = 40960;

    public DebugReceiver() {
        super();
    }

    @Override
    public int getCapacity() {
        return CAPACITY;
    }


    /**
     * Handle the new messages, find its type, and pretty-prints it
     */

    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Pair<String, InetAddress> nextMessage = incoming.take();

                Message message;
                String color;

                String header = nextMessage.getKey().split(";")[0];

                try {
                    switch (header) {
                        case HelloMessage.TYPE:
                            message = new HelloMessage(nextMessage.getKey());
                            break;
                        case SynMessage.TYPE:
                            message = new SynMessage(nextMessage.getKey());
                            break;
                        case ListMessage.TYPE:
                            message = new ListMessage(nextMessage.getKey());
                            break;
                        default:
                            System.err.println("Invalid message received : " + nextMessage.getKey());
                            continue;
                    }

                    color = (message.getSenderId().equals(StateManager.getSenderId())) ? ANSI_WHITE : ANSI_BLUE;

                    System.out.println(color + "DebugReceiver received following message from IP " + nextMessage.getValue() + ":" +
                            "\n" + message.toString() + ANSI_RESET + "\n");
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }

            }
        } catch (
                InterruptedException e) {
            e.printStackTrace();
        }
    }
}
