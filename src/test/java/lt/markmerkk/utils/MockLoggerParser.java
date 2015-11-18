package lt.markmerkk.utils;

import org.joda.time.DateTime;

/**
 * Created by mariusm on 10/27/14.
 */
public class MockLoggerParser implements Logger.Listener {
    DateTime startTime, endTime;
    String comment, taskTitle;

    @Override
    public void onParse(DateTime startTime, DateTime endTime, String comment, String task) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.comment = comment;
        this.taskTitle = task;
    }
}
