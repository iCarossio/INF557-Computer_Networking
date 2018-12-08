package test;

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

public class TestExo7 extends Test {

    public TestExo7() throws SocketException {
        super();

        handlers.add(new HelloReceiver());
        handlers.add(new DebugReceiver());

        mxdmx = new MuxDemuxSimple(handlers.toArray(new IMessageHandler[0]), socket);

        StateManager.setSenderId("Ball");
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
        LoopMessageSender sender = new LoopMessageSender("HELLO;" + StateManager.getSenderId() + ";2;11;0", 500, 0);
        sender.setMuxDemux(mxdmx);
        sender.start();
        // Run another message sender
        LoopMessageSender sender2 = new LoopMessageSender("HELLO;" + StateManager.getSenderId() + ";1;5;0", 500, 0);
        sender2.setMuxDemux(mxdmx);
        sender2.start();


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

    }

}
