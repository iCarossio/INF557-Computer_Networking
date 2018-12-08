package test;

import handler.DebugReceiver;
import handler.IMessageHandler;
import multiplexing.MuxDemuxSimple;
import sender.DatagramSender;
import sender.HelloSender;
import sender.LoopMessageSender;

import java.net.SocketException;

public class TestExo5 extends Test {

    public TestExo5() throws SocketException {
        super();

        handlers.add(new DebugReceiver());

        mxdmx = new MuxDemuxSimple(handlers.toArray(new IMessageHandler[0]), socket);
    }

    @Override
    public void runTest() {
        // Run all the handler threads
        for(IMessageHandler handler: handlers) {
            new Thread(handler).start();
        }

        // Run the MxDmx thread
        new Thread(mxdmx).start();

        // Run a message sender
        LoopMessageSender sender = new LoopMessageSender("HELLO;Bob;42;60;0", 500, 0);
        sender.setMuxDemux(mxdmx);
        sender.start();

        // Run the datagram sender
        DatagramSender dgSender = new DatagramSender(socket, mxdmx);
        new Thread(dgSender).start();
    }
}
