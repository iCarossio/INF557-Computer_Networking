package message;

import database.DatabaseManager;
import peers.PeerTableManager;
import peers.StateManager;

import java.util.HashSet;
import java.util.Set;

public class HelloMessage extends Message {

    public static final String TYPE = "HELLO";

    private int helloInterval;
    private HashSet<String> peerIds;

    public HelloMessage(String message) throws IllegalArgumentException {
        peerIds = new HashSet<>();
        parseMessage(message);
    }

    public HelloMessage(String senderID, int sequenceNo, int HelloInterval) throws IllegalArgumentException {
        setSenderId(senderID);
        setSequenceNb(sequenceNo);
        setHelloInterval(helloInterval);
        peerIds = new HashSet<>();
    }

    ////////////////////////////////////////////// HELPER FUNCTIONS /////////////////////////////////////////////

    /**
     * Constructs the message.HelloMessage from the string
     */
    protected void parseMessage(String message) throws IllegalArgumentException {

        if (message == null)
            throw new IllegalArgumentException("Cannot parse message.HelloMessage from null string.");

        String[] splitMessage = message.split(";");

        if (!splitMessage[0].toUpperCase().equals(TYPE))
            throw new IllegalArgumentException("Packet '" + message + "' is not a " + TYPE + " message!");

        if (splitMessage.length < 5)
            throw new IllegalArgumentException("Packet '" + message + "' is too short. 5 fields are expected, but only " + splitMessage.length + " given!");

        int nbPeers, sequenceNb, helloInterval;
        sequenceNb = convertSafe(splitMessage[2], "sequenceNb");
        helloInterval = convertSafe(splitMessage[3], "helloInterval");
        nbPeers = convertSafe(splitMessage[4], "numPeers");

        nbPeers = check8Bits(nbPeers, "numPeers");
        int realNbPeers = splitMessage.length - 5;
        if (realNbPeers != nbPeers) {
            int diff = splitMessage.length - 5;
            System.err.println("WARNING: numPeers indicates " + nbPeers + " peers, but in fact " + diff + " peers are given. numPeers ignored.");
        }

        if (realNbPeers > 255) {
            System.err.println("WARNING: " + realNbPeers + " peers are given. Only the first 255 are handled.");
            realNbPeers = 255;
        }

        setSequenceNb(sequenceNb);
        setSenderId(splitMessage[1]);
        setHelloInterval(helloInterval);

        peerIds = new HashSet<>();
        for (int i = 0; i < realNbPeers; i++)
            addPeer(splitMessage[i + 5]);

    }

    /**
     * @return the formatted message as HELLO;senderID;sequence#;HelloInterval;NumPeers;peer1;peer2;….;peerN
     */
    public String getHelloMessageAsEncodedString() {
        StringBuilder resBuilder = new StringBuilder(TYPE + ";" + getSenderId() + ";" + getSequenceNb() + ";" + getHelloInterval() + ";" + getNumPeers());
        if (peerIds != null) {
            for (String peerId : peerIds) {
                resBuilder.append(";");
                resBuilder.append(peerId);
            }
        }
        return resBuilder.toString();
    }

    /**
     * @return the current hello message according to the peer table
     */
    public static String generateCurrentHelloMessage() {
        Set<String> currentPeerIds = PeerTableManager.getInstance().getPeerIdsList();

        StringBuilder retBuilder = new StringBuilder("HELLO;" + StateManager.getSenderId() + ";" + DatabaseManager.getInstance().getDatabaseSequenceNb() + ";" + StateManager.getHelloInterval() + ";");

        retBuilder.append(currentPeerIds.size());

        for (String peerId : currentPeerIds) {
            retBuilder.append(";");
            retBuilder.append(peerId);
        }

        return retBuilder.toString();
    }

    @Override
    public String toString() {

        StringBuilder retBuilder = new StringBuilder("#################################################" +
                "\n\t ### <Raw Message> : " + getHelloMessageAsEncodedString() +
                "\n\t ### SenderID      : " + getSenderId() +
                "\n\t ### SequenceNb    : " + getSequenceNb() +
                "\n\t ### HelloInterval : " + getHelloInterval() +
                "\n\t ### NumPeers      : " + getNumPeers());

        int i = 0;

        for (String peerId : peerIds) {
            if (i++ % TO_STRING_BLOCK_SIZE == 0)
                retBuilder.append("\n\t\t > ");
            else
                retBuilder.append(' ');
            retBuilder.append(peerId);
        }

        retBuilder.append("\n#################################################");

        return retBuilder.toString();
    }

    public boolean containsPeerId(String peerId) {
        return peerIds.contains(peerId);
    }

    ////////////////////////////////////////////// GETTERS & SETTERS /////////////////////////////////////////////

    /** GETTERS **
     * - getSenderId() is needed HelloReceiver() - see abstract class Message.java
     * - getSequenceNb(), getHelloInterval() are used in the PeerTableManager and the PeerRecord classes
     * - getNumPeers() is not used yet
     */

    public int getHelloInterval() {
        return helloInterval;
    }

    private int getNumPeers() {
        return peerIds.size();
    }

    /** SETTERS **
     * For now, setters are only used locally for initialization
     */

    /**
     * @param peer Peer to add to the set of peers if there is room remaining.
     * @return true if the peer could be added to the set, else false (already full, or duplicate).
     */
    private boolean addPeer(String peer) {
        if (checkPeerId(peer))
            return peerIds.add(peer);
        System.err.println("WARNING: The peerID '" + peer + "' is not formatted correctly. Ignored.");
        return false;
    }

    private void setHelloInterval(int helloInterval) {
        this.helloInterval = check8Bits(helloInterval, "helloInterval");
    }

}
