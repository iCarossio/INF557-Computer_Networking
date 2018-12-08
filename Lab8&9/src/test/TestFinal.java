package test;

import database.DatabaseManager;
import database.TCPDatabaseDumper;
import handler.*;
import message.HelloMessage;
import multiplexing.MuxDemuxSimple;
import peers.StateManager;
import peers.TCPPeerDumper;
import sender.DatagramSender;
import sender.HelloSender;
import sender.LoopMessageSender;

import java.io.IOException;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

public class TestFinal extends Test {

    static int dataBaseStringIndex = 0;

    public TestFinal() throws SocketException {
        super();

        handlers.add(new HelloReceiver());
        handlers.add(new SynReceiver());
        handlers.add(new ListReceiver());
        handlers.add(new DebugReceiver());

        mxdmx = new MuxDemuxSimple(handlers.toArray(new IMessageHandler[0]), socket);

        StateManager.setSenderId("Antoine");
    }

    @Override
    public void runTest() {
        // Run all the handler threads
        for (IMessageHandler handler : handlers) {
            new Thread(handler).start();
        }

        // Run the MxDmx thread
        new Thread(mxdmx).start();

        // Run a message sender

        HelloSender sender = new HelloSender(4000);
        sender.setMuxDemux(mxdmx);
        sender.start();

        /*

        LoopMessageSender sender2 = new LoopMessageSender("HELLO;Test;7;255;0", 255000, 0);
        sender2.setMuxDemux(mxdmx);
        sender2.start();

        LoopMessageSender sender3 = new LoopMessageSender("LIST;Test;Antoine;7;2;0;StringTest", 255000, 500);
        sender3.setMuxDemux(mxdmx);
        sender3.start();

        LoopMessageSender sender4 = new LoopMessageSender("LIST;Test;Antoine;7;2;1;StringTest2", 255000, 500);
        sender4.setMuxDemux(mxdmx);
        sender4.start();

        */

        String[] forDb = new String[]{"Alpha", "Bravo", "Charlie", "Delta", "Echo", "Foxtrot", "Golf", "Hotel", "Indigo"};

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                DatabaseManager.getInstance().addInMyDatabase(forDb[dataBaseStringIndex++ % forDb.length]);
            }
        }, 0, 5000);

        // Run the datagram sender
        DatagramSender dgSender = new DatagramSender(socket, mxdmx);
        new Thread(dgSender).start();

        // Run tcp peer table dumper
        try {
            TCPPeerDumper.getInstance().initialize();
            new Thread(TCPPeerDumper.getInstance()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            TCPDatabaseDumper.getInstance().initialize();
            new Thread(TCPDatabaseDumper.getInstance()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
