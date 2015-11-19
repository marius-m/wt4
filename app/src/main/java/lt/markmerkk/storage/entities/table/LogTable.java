package lt.markmerkk.storage.entities.table;

import javafx.beans.property.SimpleStringProperty;
import lt.markmerkk.storage.entities.Log;
import lt.markmerkk.storage.entities.annotations.Table;
import lt.markmerkk.utils.Utils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by mariusm on 11/6/14.
 */
@Table(name = "Log")
public class LogTable extends Log {
    private final DateTimeFormatter shortFormat = DateTimeFormat.forPattern("HH:mm");
    private final DateTimeFormatter longFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
    private transient SimpleStringProperty verbalStart;
    private transient SimpleStringProperty verbalEnd;
    private transient SimpleStringProperty verbalDuration;
    private transient String storeLocation;
    private transient String gitMessageIndicator;

    public LogTable(Log log) {
        this.id = log.getId();
        this.create = log.getCreate();
        this.modify = log.getModify();
        this.category = log.getCategory();
        this.start = log.getStart();
        this.end = log.getEnd();
        this.duration = log.getDuration();
        this.comment = log.getComment();
        this.git = log.getGit();
        this.serverUri = log.getServerUri();
        this.storeLocation = (log.getServerUri() == null) ? "L" : "R";
        this.gitMessageIndicator = (Utils.isArrayEmpty(git)) ? "A" : "B";
    }

    public String getGitMessageIndicator() {
        return gitMessageIndicator;
    }

    public String getStoreLocation() {
        return storeLocation;
    }

    public String getVerbalStart() {
        return shortFormat.print(getStart());
    }

    public String getLongVerbalStart() {
        return longFormat.print(getStart());
    }

    public String getVerbalEnd() {
        return shortFormat.print(getEnd());
    }

    public String getLongVerbalEnd() {
        return longFormat.print(getEnd());
    }

    public DateTimeFormatter getShortFormat() {
        return shortFormat;
    }

    public DateTimeFormatter getLongFormat() {
        return longFormat;
    }

    public String getVerbalDuration() {
        return Log.formatDuration(getDuration());
    }

}
