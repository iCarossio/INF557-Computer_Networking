package peers;

import message.HelloMessage;

import java.net.InetAddress;

public class PeerRecord {

    private InetAddress peerAddress;
    private int peerSequenceNumber;
    private long expirationTime;
    private StateManager.State peerState;

    public PeerRecord(InetAddress peerAddress, int peerSequenceNumber, long expirationTime, StateManager.State peerState) {
        this.peerAddress = peerAddress;
        this.peerSequenceNumber = peerSequenceNumber;
        this.expirationTime = expirationTime;
        this.peerState = peerState;
    }

    /**
     * This method does not check for expiration time
     *
     * DOES NOT update the sequence number
     */
    void update(HelloMessage helloMessage, InetAddress senderAddress, long currentTime) {
        expirationTime = currentTime + helloMessage.getHelloInterval() * 1000;

        if(!helloMessage.containsPeerId(StateManager.getSenderId()))
            return;

        peerAddress = senderAddress; // Update the IP address

        if(peerSequenceNumber != helloMessage.getSequenceNb())
            peerState = StateManager.State.INCONSISTENT;
        else if(peerState == StateManager.State.SYNCHRONISED) // The else statement guarantees that peerSequenceNumber == helloMessage.getSequenceNb()
            peerState = StateManager.State.SYNCHRONISED; // Weird instruction, following the protocol specification
    }

    /**
     * GETTERS & SETTERS
     */

    public InetAddress getPeerAddress() {
        return peerAddress;
    }

    public int getPeerSequenceNumber() {
        return peerSequenceNumber;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public StateManager.State getPeerState() {
        return peerState;
    }

    public void setPeerSequenceNumber(int peerSequenceNumber) {
        this.peerSequenceNumber = peerSequenceNumber;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public void setPeerState(StateManager.State peerState) {
        this.peerState = peerState;
    }

}
