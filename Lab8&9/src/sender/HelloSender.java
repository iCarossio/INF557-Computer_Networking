package sender;

import message.HelloMessage;

import java.util.Timer;
import java.util.TimerTask;

public class HelloSender extends MessageSender {

    private Timer timer;
    private long period;

    public HelloSender(long period) {
        this.period = period;
        timer = new Timer();
    }

    /**
     * Initialize the sending period of this local machine
     */
    public void setPeriod(long period) {
        this.period = period;
    }

    /**
     * Send HELLO messages each period of time
     */
    public void start() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendMessage(HelloMessage.generateCurrentHelloMessage());
            }
        }, 0, period);
    }

    /**
     * Stop the local machine sending messages
     */
    public void stop() {
        timer.cancel();
        timer.purge();
    }

}
