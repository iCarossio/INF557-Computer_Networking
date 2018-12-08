package peers;

import message.HelloMessage;

public class StateManager {

    /**
     * All the possible states of the peer
     */
    public enum State {
        DYING, HEARD, SYNCHRONISED, INCONSISTENT;
    }

    private static String senderId = "UNSET";

    public static void setSenderId(String newSenderId) {
        senderId = newSenderId;
    }
    public static String getSenderId() {
        return senderId;
    }


    /**
     * @return constant 2 for the moment
     */
    public static int getHelloInterval() {
        return 5;
    }
}
