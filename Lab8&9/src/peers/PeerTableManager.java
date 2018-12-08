package peers;

import database.SynManager;
import message.HelloMessage;
import message.SynMessage;

import java.net.InetAddress;
import java.util.*;


/**
 * WARNING: Be careful when removing an element, to reset earliestExpirationTime to Long.MAX_VALUE
 */

public class PeerTableManager {

    private static final PeerTableManager instance = new PeerTableManager();

    public static PeerTableManager getInstance() {
        return instance;
    }

    private final HashMap<String, PeerRecord> peerTable; // No need for a concurrent hash map, synchronization will be ensured manually.
    private long earliestExpirationTime = Long.MAX_VALUE; // Optimization: keep track of the earliest expiration time, so no need to go through the whole table all the time

    private PeerTableManager() {
        peerTable = new HashMap<>(); // Fine-grained concurrent HashSet
    }

    /**
     * Updates the peer table after reception of a HELLO message
     * Does NOT update the peer sequence number !
     *
     * @param helloMessage has to be a non-self sent message
     * @return true iff a SYN message should be sent to the sender
     */
    public synchronized boolean updateTable(HelloMessage helloMessage, InetAddress senderAddress) {
        PeerRecord record = peerTable.get(helloMessage.getSenderId());

        long currentTime = System.currentTimeMillis();
        earliestExpirationTime = Math.min(currentTime + 1000 * helloMessage.getHelloInterval(), earliestExpirationTime);

        boolean sendSyn = SynManager.getInstance().isNotAwaitingListMessagesFrom(helloMessage.getSenderId(), helloMessage.getSequenceNb());

        if (record == null) { // If the sender is not in the table
            peerTable.put(helloMessage.getSenderId(), new PeerRecord(senderAddress, -1, currentTime + 1000 * helloMessage.getHelloInterval(), StateManager.State.HEARD));
        } else if (record.getExpirationTime() < currentTime) { // If the entry was expired
            peerTable.replace(helloMessage.getSenderId(), new PeerRecord(senderAddress, -1, currentTime + 1000 * helloMessage.getHelloInterval(), StateManager.State.HEARD));
        } else {
            record.update(helloMessage, senderAddress, currentTime);

            if (helloMessage.getSequenceNb() <= peerTable.get(helloMessage.getSenderId()).getPeerSequenceNumber()) {
                sendSyn = false;
                if (helloMessage.getSequenceNb() < peerTable.get(helloMessage.getSenderId()).getPeerSequenceNumber())
                    setPeerState(helloMessage.getSenderId(), StateManager.State.INCONSISTENT);
            }
        }

        return sendSyn;
    }


    public synchronized void setPeerState(String peer, StateManager.State state) {
        PeerRecord record = peerTable.get(peer);

        if (record != null && record.getPeerState() != StateManager.State.INCONSISTENT) {
            record.setPeerState(state);
        }
    }

    public synchronized void setPeerSequenceNb(String peer, int sequenceNb) {
        PeerRecord record = peerTable.get(peer);

        if (record != null)
            record.setPeerSequenceNumber(sequenceNb);
    }

    /**
     * Get all the peers in the table in a HashSet
     */
    public synchronized Set<String> getPeerIdsList() {
        cleanTable();
        return new HashSet<>(peerTable.keySet()); // Return a clone of the key set
    }

    /**
     * @return true <=> the peerId is in the peerTable
     */
    public synchronized boolean hasPeer(String peerId) {
        cleanTable();
        return peerTable.containsKey(peerId);
    }


    /**
     * @MUST have the lock before calling this function!
     */
    private void cleanTable() {
        long currentTime = System.currentTimeMillis();
        if (currentTime < earliestExpirationTime)
            return; // Nothing to be cleaned

        Iterator it = peerTable.entrySet().iterator();

        while (it.hasNext()) {
            @SuppressWarnings("unchecked")
            Map.Entry<String, PeerRecord> entry = (Map.Entry<String, PeerRecord>) it.next();

            if (entry.getValue().getExpirationTime() < currentTime) {
                it.remove();
                earliestExpirationTime = Long.MAX_VALUE;
            }
        }

    }

    public synchronized HashMap<String, PeerRecord> getPeerTableCopy() {
        cleanTable();
        return new HashMap<>(peerTable);
    }
}
