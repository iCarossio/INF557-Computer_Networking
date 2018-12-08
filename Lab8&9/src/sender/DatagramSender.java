package sender;

import multiplexing.IMuxDemux;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This Runnable takes the output queue of a MxDmx and
 * broadcasts it on the link.
 */

public class DatagramSender implements Runnable {

    private static final boolean _VERBOSE = true;

    private IMuxDemux mxdmx;
    private ReentrantLock mxDmxLock;
    private Condition mxDmxLockChanged;

    DatagramSocket socket;

    public DatagramSender(DatagramSocket socket) {
        init(socket);
        mxdmx = null;
    }

    public DatagramSender(DatagramSocket socket, IMuxDemux mxdmx) {
        init(socket);
        this.mxdmx = mxdmx;
    }

    private void init(DatagramSocket socket) {
        mxDmxLock = new ReentrantLock();
        mxDmxLockChanged = mxDmxLock.newCondition();
        this.socket = socket;
    }


    public void setMuxDemux(IMuxDemux mxdmx) {
        try {
            mxDmxLock.lock();
            this.mxdmx = mxdmx;
            mxDmxLockChanged.signalAll();
        } finally {
            mxDmxLock.unlock();
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {

                // If the MuxDemux is null, wait for it to be changed
                if (mxdmx == null) {

                    try {
                        mxDmxLock.lock();
                        while (mxdmx == null)
                            mxDmxLockChanged.await();
                    } finally {
                        mxDmxLock.unlock();
                    }
                }

                String nextMessage = mxdmx.takeNextOutgoingMessage();

                sendMessage(nextMessage);

            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            } catch (NullPointerException e) { // May happen if mxdmx is changed to null during the execution
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(String message) throws IOException {
        byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("255.255.255.255"), 4242);

        socket.send(packet);

        if (_VERBOSE)
            System.out.println("DatagramSender sent:" +
                    "\n\t" + message);
    }
}
