package message;

abstract public class Message {

    static final int TO_STRING_BLOCK_SIZE = 10;
    static final String peerIdPattern = "([a-zA-Z0-9]{1,16})";

    protected String senderId;
    protected int sequenceNb;

    ////////////////////////////////////////////// HELPER FUNCTIONS /////////////////////////////////////////////

    /**
     * @return The cast value of String toCast
     */
    public int convertSafe(String toCast, String varName) {
        try {
            return Integer.parseInt(toCast);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The " + varName + " '" + toCast + "' is not a valid integer.");
        }
    }

    /**
     * Check if the peerID is valid (corresponds to the pattern and length)
     */
    public boolean checkPeerId(String peerID) {
        return peerID.matches(peerIdPattern) && peerID.length() > 0 && peerID.length() <= 16;
    }


    /**
     * Check that the int toCheck in [0,255], else force it to live in this range
     */
    public int check8Bits(int toCheck, String varName) {
        int res = toCheck;
        if (res < 0) { // If negative, set it to 0
            System.err.println("WARNING: The negative " + varName + " '" + res + "' is considered as a '0'.");
            res = 0;
        } else if (res > 255) { // If >255, set it to 255
            System.err.println("WARNING: The " + varName + " '" + res + "' is considered as a '255'.");
            res = 255;
        }
        return res;
    }

    ////////////////////////////////////////////// GETTERS & SETTERS /////////////////////////////////////////////

    public int getSequenceNb() {
        return sequenceNb;
    }

    public void setSequenceNb(int sequenceNb) throws IllegalArgumentException {
        this.sequenceNb = sequenceNb;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) throws IllegalArgumentException {
        if (checkPeerId(senderId))
            this.senderId = senderId;
        else
            throw new IllegalArgumentException("The senderID '" + senderId + "' is not formatted properly.");
    }

    protected abstract void parseMessage(String message);

}
