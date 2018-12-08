package test;

import database.DatabaseManager;
import database.TCPDatabaseDumper;
import handler.DebugReceiver;
import handler.HelloReceiver;
import handler.IMessageHandler;
import multiplexing.MuxDemuxSimple;
import peers.StateManager;
import peers.TCPPeerDumper;
import sender.DatagramSender;
import sender.LoopMessageSender;

import java.io.IOException;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

public class TestDatabase extends Test {

    static int dataBaseStringIndex = 0;

    public TestDatabase() throws SocketException {
        super();
        StateManager.setSenderId("TestDB");
    }

    @Override
    public void runTest() {

        String[] forDb = new String[]{"Alpha", "Bravo", "Charlie", "Delta", "Echo", "Foxtrot", "Golf", "Hotel", "Indigo"};

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                DatabaseManager.getInstance().addInMyDatabase(forDb[dataBaseStringIndex++]);
            }
        }, 0, 1000);

        try {
            TCPDatabaseDumper.getInstance().initialize();
            new Thread(TCPDatabaseDumper.getInstance()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
