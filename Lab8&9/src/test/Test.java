package test;

import handler.IMessageHandler;
import multiplexing.IMuxDemux;
import multiplexing.MuxDemuxSimple;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

public abstract class Test {

    public static final int PORT = 4242;

    protected DatagramSocket socket;

    protected ArrayList<IMessageHandler> handlers;
    protected IMuxDemux mxdmx;


    public Test() throws SocketException {
        handlers = new ArrayList<>();

        socket = new DatagramSocket(PORT);
        socket.setBroadcast(true);
    }

    public abstract void runTest();
}
