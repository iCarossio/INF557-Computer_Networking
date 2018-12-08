package database;

import message.ListMessage;
import peers.PeerTableManager;
import peers.StateManager;

import java.util.HashMap;

public class ListReceptionManager {

    public class ListBuffer {
        public int partsRemaining;
        public String[] buffer;

        public ListBuffer(int nbExpectedMessages) {
            buffer = new String[nbExpectedMessages];
            partsRemaining = nbExpectedMessages;
        }
    }

    private static ListReceptionManager instance = new ListReceptionManager();

    public static ListReceptionManager getInstance() {
        return instance;
    }

    // Does not need inherent synchronization. Concurrency is managed manually in the functions below.
    private HashMap<String, ListBuffer> buffers;

    public synchronized void purgeBufferFrom(String peer) {
        buffers.remove(peer);
    }

    private ListReceptionManager() {
        buffers = new HashMap<>();
    }

    /**
     * Checks if the message comes from self
     *
     * @param message a freshly received ListMessage, that wants to be added as a part of the database
     */
    public synchronized void receiveListMessage(ListMessage message) throws IllegalStateException, IllegalArgumentException, ArrayIndexOutOfBoundsException {

        ListBuffer currentBuffer = buffers.get(message.getSenderId());

        // Check if this is a message from oneself
        if (message.getSenderId().equals(StateManager.getSenderId()))
            return;

        // Check if a ListMessage is awaited from this sender at this sequence number. This will discard older ListMessages that are not wanted anymore
        if (SynManager.getInstance().isNotAwaitingListMessagesFrom(message.getSenderId(), message.getSequenceNb()))
            throw new IllegalStateException(String.format("Received a ListMessage (from %s) without having sent a still alive SYN (maybe SYN already expired). Dropping the packet.", message.getSenderId()));

        // Check if the list message is the first received in the bundle
        if (currentBuffer == null) { // This is the first message from the list


            currentBuffer = new ListBuffer(message.getTotalParts());
            if(message.getTotalParts() == 0) {
                DatabaseManager.getInstance().updateDatabase(message.getSenderId(), currentBuffer.buffer);
                SynManager.getInstance().resetSynPeer(message.getSenderId());
                PeerTableManager.getInstance().setPeerSequenceNb(message.getSenderId(), message.getSequenceNb());
                return;
            }
            buffers.put(message.getSenderId(), currentBuffer); // All strings in the buffer are initialized to null
        }

        // Put the data into the buffer
        if (currentBuffer.buffer[message.getPartNb()] != null)
            throw new IllegalStateException(String.format("Received twice the same ListMessage senderId (%s) and part (partNb: %d, totalParts: %d). Message dropped.", message.getSenderId(), message.getPartNb(), message.getTotalParts()));
        currentBuffer.buffer[message.getPartNb()] = message.getData();

        // If all the parts have been received, update the database and reset the bundle
        currentBuffer.partsRemaining--;
        if (currentBuffer.partsRemaining == 0) {
            DatabaseManager.getInstance().updateDatabase(message.getSenderId(), currentBuffer.buffer);
            SynManager.getInstance().resetSynPeer(message.getSenderId());
            PeerTableManager.getInstance().setPeerSequenceNb(message.getSenderId(), message.getSequenceNb());
            buffers.remove(message.getSenderId());
        }
    }

}
