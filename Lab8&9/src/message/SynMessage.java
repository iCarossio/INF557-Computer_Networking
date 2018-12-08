package message;

public class SynMessage extends Message {

    public static final String TYPE = "SYN";

    private String peerId;

    public SynMessage(String message) throws IllegalArgumentException {
        parseMessage(message);
    }

    public SynMessage(String senderID, String peerID, int sequenceNo) throws IllegalArgumentException {
        setSenderId(senderID);
        setPeerId(peerID);
        setSequenceNb(sequenceNo);
    }

    ////////////////////////////////////////////// HELPER FUNCTIONS /////////////////////////////////////////////

    /**
     * Constructs the message.SynMessage from the string
     */
    protected void parseMessage(String message) throws IllegalArgumentException {

        if (message == null)
            throw new IllegalArgumentException("Cannot parse message.HelloMessage from null string.");

        String[] splitMessage = message.split(";");

        if (!splitMessage[0].toUpperCase().equals(TYPE))
            throw new IllegalArgumentException("Packet '" + message + "' is not a " + TYPE + " message!");

        if (splitMessage.length < 4)
            throw new IllegalArgumentException("Packet '" + message + "' is too short. 4 fields are expected, but only " + splitMessage.length + " given!");

        String senderId, peerId;
        int sequenceNb;

        senderId = splitMessage[1];
        peerId = splitMessage[2];
        sequenceNb = convertSafe(splitMessage[3], "sequenceNb");

        setSenderId(senderId);
        setPeerId(peerId);
        setSequenceNb(sequenceNb);
    }

    @Override
    public String toString() {

        return "#################################################" +
                "\n\t ### <Raw Message> : " + getSynMessageAsEncodedString() +
                "\n\t ### SenderID      : " + getSenderId() +
                "\n\t ### PeerID        : " + getPeerId() +
                "\n\t ### SequenceNb    : " + getSequenceNb() +
                "\n#################################################";

    }


    /**
     * @return the formatted message as SYN;senderID;peerID;sequence#;
     */
    public String getSynMessageAsEncodedString() {
        return TYPE + ";" + getSenderId() + ";" + getPeerId() + ";" + getSequenceNb() + ";";
    }

    ////////////////////////////////////////////// GETTERS & SETTERS /////////////////////////////////////////////


    public String getPeerId() {
        return peerId;
    }

    private void setPeerId(String peerId) throws IllegalArgumentException {
        if (checkPeerId(peerId))
            this.peerId = peerId;
        else
            throw new IllegalArgumentException("The peerID '" + peerId + "' is not formatted properly.");
    }

}
