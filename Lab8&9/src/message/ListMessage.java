package message;

public class ListMessage extends Message {

    public static final String TYPE = "LIST";

    private String peerId;
    private String data;
    private int totalParts;
    private int partNb;

    public ListMessage(String message) throws IllegalArgumentException {
        parseMessage(message);
    }

    public ListMessage(String senderID, String peerID, int sequenceNo, int totalParts, int partNb, String payload) throws IllegalArgumentException {
        setSenderId(senderID);
        setPeerId(peerID);
        setSequenceNb(sequenceNo);
        setTotalParts(totalParts);
        setPartNb(partNb);
        setData(payload);
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

        if (splitMessage.length < 6)
            throw new IllegalArgumentException("Packet '" + message + "' is too short. 4 fields are expected, but only " + splitMessage.length + " given!");

        String senderId, peerId, data;
        int sequenceNb, totalParts, partNb;

        senderId = splitMessage[1];
        peerId = splitMessage[2];
        sequenceNb = convertSafe(splitMessage[3], "sequenceNb");
        totalParts = convertSafe(splitMessage[4], "totalParts");
        partNb = convertSafe(splitMessage[5], "partNb");

        if(splitMessage.length < 7)
            data = "";
        else
            data = splitMessage[6];

        setSenderId(senderId);
        setPeerId(peerId);
        setSequenceNb(sequenceNb);
        setTotalParts(totalParts);
        setPartNb(partNb);
        setData(data);

    }

    @Override
    public String toString() {

        StringBuilder retBuilder = new StringBuilder("#################################################" +
                "\n\t ### <Raw Message> : " + getListMessageAsEncodedString() +
                "\n\t ### SenderID      : " + getSenderId() +
                "\n\t ### PeerID        : " + getPeerId() +
                "\n\t ### SequenceNb    : " + getSequenceNb() +
                "\n\t ### TotalParts    : " + getTotalParts() +
                "\n\t ### PartNb        : " + getPartNb() +
                "\n\t ### Data          : " + getData()
        );
        retBuilder.append("\n#################################################");

        return retBuilder.toString();
    }


    /**
     * @return the formatted message as SYN;senderID;peerID;sequence#;
     */
    public String getListMessageAsEncodedString() {
        StringBuilder resBuilder = new StringBuilder(TYPE + ";" + getSenderId() + ";" + getPeerId() + ";" + getSequenceNb() + ";" + getTotalParts() + ";" + getPartNb() + ";" + getData() + ";");
        return resBuilder.toString();
    }

    ////////////////////////////////////////////// GETTERS & SETTERS /////////////////////////////////////////////


    public String getPeerId() {
        return peerId;
    }

    public String getData() {
        return data;
    }

    public int getTotalParts() {
        return totalParts;
    }

    public int getPartNb() {
        return partNb;
    }

    private void setPeerId(String peerId) throws IllegalArgumentException {
        if (checkPeerId(peerId))
            this.peerId = peerId;
        else
            throw new IllegalArgumentException("The peerID '" + peerId + "' is not formatted properly.");
    }

    private void setData(String data) {
        if (data.length() > 255) {
            System.err.println("WARNING: Too much data transfered (" + data.length() + "). Everything after 255 will be ignored.");
            data = data.substring(0, 255);
        }
        this.data = data;
    }

    private void setTotalParts(int totalParts) {
        if (totalParts < 0) {
            System.err.println("WARNING: Negative totalParts, considered as 0");
            totalParts = 0;
        }
        this.totalParts = totalParts;
    }

    private void setPartNb(int partNb) {
        if (partNb >= totalParts && totalParts != 0)
            throw new IllegalArgumentException("partNb = " + partNb + " >= " + totalParts + " = totalParts");
        else if (partNb < 0) {
            throw new IllegalArgumentException("Negative partNb = " + partNb);
        }
        this.partNb = partNb;

    }


}
