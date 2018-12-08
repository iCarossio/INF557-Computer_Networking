package sender;

import multiplexing.IMuxDemux;

public abstract class MessageSender {

    protected IMuxDemux myMuxDemux = null;

    public void setMuxDemux(IMuxDemux mxdmx) {
        myMuxDemux = mxdmx;
    }

    protected void sendMessage(String message) {
        if(myMuxDemux != null) {
/*            System.out.println();
            System.out.println("Sending message :");
            System.out.println(message);
            System.out.println();*/

            myMuxDemux.send(message);
        }
    }
}
