package sender;

import java.util.Timer;
import java.util.TimerTask;

public class LoopMessageSender extends MessageSender {

    private Timer timer;
    private final String message;
    private long period;
    private long delay;

    public LoopMessageSender(String message, long period, long delay) {
        this.message = message;
        this.period = period;
        this.delay = delay;
        timer = new Timer();
    }

    /**
     * Initialize the sending period of this sending machine
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
                sendMessage(message);
            }
        }, delay, period);
    };

    /**
     * Stop the local machine sending messages
     */
    public void stop() {
        timer.cancel();
        timer.purge();
    };
}
