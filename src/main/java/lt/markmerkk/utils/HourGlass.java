package lt.markmerkk.utils;

import java.util.Timer;
import java.util.TimerTask;
import org.joda.time.DateTimeUtils;

/**
 * Created by mariusm on 10/30/14.
 */
public class HourGlass {
    public static final int TICK_DELAY = 1000;
    private Timer timer = null;
    protected State state = State.STOPPED;
    private long startMillis = 0;
    private Listener listener;

    public HourGlass() {}

    // Public interfaces

    public boolean start() {
        if (state == State.STOPPED) {
            state = State.RUNNING;
            startMillis = DateTimeUtils.currentTimeMillis();
            TimerTask updateRunnable = new TimerTask() {
                @Override
                public void run() {
                    update();
                }
            };
            timer = new Timer();
            timer.scheduleAtFixedRate(updateRunnable, 1, TICK_DELAY);
            long delay = DateTimeUtils.currentTimeMillis() - startMillis;
            if (listener != null)
                listener.onStart(startMillis, DateTimeUtils.currentTimeMillis(), delay);
            return true;
        }
        return false;
    }

    public boolean stop() {
        if (state == State.RUNNING) {
            state = State.STOPPED;
            timer.cancel();
            timer.purge();
            update();
            long delay = DateTimeUtils.currentTimeMillis() - startMillis;
            if (listener != null)
                listener.onStop(startMillis, DateTimeUtils.currentTimeMillis(), delay);
            startMillis = 0;
            return true;
        }
        return false;
    }

    public boolean restart() {
        stop();
        start();
        return true;
    }

    // Core update methods

    private void update() {
        long delay = DateTimeUtils.currentTimeMillis() - startMillis;
        if (listener != null)
            listener.onTick(startMillis, DateTimeUtils.currentTimeMillis(), delay);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public State getState() {
        return state;
    }

    public long getStartMillis() {
        return startMillis;
    }

    public void appendMinute() {
        if ((startMillis + 1000 * 60) < DateTimeUtils.currentTimeMillis())
            startMillis += 1000 * 60; // Adding 60 seconds
        update();
    }

    public void subractMinute() {
        startMillis -= 1000 * 60; // Adding 60 seconds
        update();
    }

    public interface Listener {
        public void onStart(long start, long end, long duration);
        public void onStop(long start, long end, long duration);
        public void onTick(long start, long end, long duration);
    }

    public enum State {
        STOPPED(0),
        RUNNING(1);
        private int value;

        private State(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static State parseState(int status) {
            switch (status) {
                case 0: return STOPPED;
                case 1: return RUNNING;
                default: return STOPPED;
            }
        }

    }

}
