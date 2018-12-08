package handler;

import multiplexing.MuxDemuxSimple;

import java.net.InetAddress;

public interface IMessageHandler extends Runnable{
    void handleMessage(String m, InetAddress senderAddress);
    void setMuxDemux(MuxDemuxSimple muxDemux);
}