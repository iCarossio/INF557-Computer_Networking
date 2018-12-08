package database;

import javafx.util.Pair;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class SynManager {

    public static final long SYN_TIMEOUT = 7000; // ms

    private static SynManager instance = new SynManager();

    public static SynManager getInstance() {
        return instance;
    }

    // Stores the peerId and the sequenceId prompted
    public HashMap<String, Pair<Integer, Timer>> timers;

    private SynManager() {
        timers = new HashMap<>();
    }

    /**
     * Declares that all the awaited ListMessages have been received, or that the timeout for this peer has been reached
     *
     * @param peer the peer from which the whole bundle has been received or that timed out
     */
    public void resetSynPeer(String peer) {
        timers.remove(peer);
    }

    /**
     * @param peer the peer from which we are maybe already be awaiting ListMessages
     * @return 1 iff not already awaiting ListMessages from peer
     */
    public boolean isNotAwaitingListMessagesFrom(String peer, int sequenceNb) {
        return !timers.containsKey(peer) || timers.get(peer).getKey() != sequenceNb;
    }

    /**
     * Declares that a SYN message has been transmitted to the peer
     *
     * @param peer the peer to which the SYN has been sent
     */
    public void setSynPeer(String peer, int sequenceNb, SynManager synManager) throws IllegalStateException {
        if (!isNotAwaitingListMessagesFrom(peer, sequenceNb))
            throw new IllegalStateException("Sending SYN but a non-fulfilled has not yet been timed out or fulfilled.");


        Timer newTimer = new Timer();

        newTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                synManager.resetSynPeer(peer);
            }
        }, SYN_TIMEOUT);
        newTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                synManager.resetSynPeer(peer);
            }
        }, SYN_TIMEOUT);

        synManager.timers.put(peer, new Pair<>(sequenceNb, newTimer));
    }
}
