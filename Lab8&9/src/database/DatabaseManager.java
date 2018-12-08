package database;

import peers.PeerTableManager;
import peers.StateManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class DatabaseManager {

    private static DatabaseManager instance = new DatabaseManager();

    public static DatabaseManager getInstance() {
        return instance;
    }

    // A database entry per peer, no synchronization required
    private HashMap<String, List<String>> databases;

    private int sequenceNb;

    private DatabaseManager() {
        databases = new HashMap<>();

        databases.put(StateManager.getSenderId(), new ArrayList<>());

        sequenceNb = 0;
    }

    public synchronized void updateDatabase(String from, String[] newVersion) throws IllegalArgumentException {
        databases.put(from, new ArrayList<>(Arrays.asList(newVersion)));
        PeerTableManager.getInstance().setPeerState(from, StateManager.State.SYNCHRONISED);
    }

    /**
     * @return null if the requested sequence number is not the sequence number of the current database, else a reference to the local database
     */
    public List<String> getData(int requestedSequenceNb) {
        if(requestedSequenceNb == sequenceNb)
            return databases.get(StateManager.getSenderId());

        // Some debug information
        if(requestedSequenceNb > sequenceNb)
            System.err.println("Received database request for a future database version (requestedSequenceNb > sequenceNb).");

        return null;
    }

    public synchronized int getDatabaseSequenceNb(){
        return sequenceNb;
    }

    /**
     * Modifies local database and increments the sequence number
     */
    public synchronized void addInMyDatabase(String element) throws IllegalArgumentException {

        if(element.length() > 255)
            throw new IllegalArgumentException(String.format("String too long (max 255 characters, here %d characters)", element.length()));

        sequenceNb++;

        databases.get(StateManager.getSenderId()).add(element);
    }

    /**
     * Modifies local database and increments the sequence number
     */
    public synchronized void removeInMyDatabase(String element) {

        databases.get(StateManager.getSenderId()).remove(element);

        sequenceNb++;
    }

    public synchronized HashMap<String, List<String>> getDatabases() {
        return new HashMap<>(databases);
    }

}
