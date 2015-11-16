package lt.markmerkk.storage.entities;

import java.util.ArrayList;
import java.util.HashMap;
import lt.markmerkk.storage.entities.annotations.Column;
import lt.markmerkk.storage.entities.annotations.FieldType;
import lt.markmerkk.storage.entities.annotations.Table;
import lt.markmerkk.storage.entities.annotations.TableIndex;
import lt.markmerkk.storage.entities.core.DatabaseEntity;
import lt.markmerkk.storage.entities.table.LogTable;
import lt.markmerkk.utils.Utils;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;

/**
 * Created by mariusm on 10/25/14.
 * An object that hold data for logged comment.
 */
@Table(name = "Log")
@TableIndex(name = "startIndex", createQuery = "CREATE INDEX startIndex ON Log(start)")
public class Log extends DatabaseEntity {
    private final static DateTimeFormatter shortFormat = DateTimeFormat.forPattern("HH:mm");


    @Column(value = FieldType.TEXT, canBeNull = true)
    protected String category;
    @Column(value = FieldType.INTEGER, defaultValue = "0")
    protected long start;
    @Column(value = FieldType.INTEGER, defaultValue = "0")
    protected long end;
    protected long duration;
    @Column(value = FieldType.TEXT, canBeNull = true)
    protected String comment;
    @Column(value = FieldType.TEXT, canBeNull = true)
    protected ArrayList<String> git;

    // Jira server vars
    @Column(value = FieldType.TEXT, canBeNull = true)
    protected String serverUri;

    public Log() {
        git = new ArrayList<>();
    }

    // Contructor form local log
    public Log(String category, long start, long end, long duration, String comment, ArrayList<String> git) {
        this.category = category;
        this.start = start;
        this.end = end;
        this.duration = duration;
        this.comment = comment;
        this.git = git;
    }

    // Constructor for remote log
    public Log(String category, long start, long end, long duration, String comment, String serverUri) {
        this.category = category;
        this.start = start;
        this.end = end;
        this.duration = duration;
        this.comment = comment;
        this.serverUri = serverUri;
    }

    public String getCategory() {
        return category;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public long getDuration() {
        return duration;
    }

    public String getComment() {
        return comment;
    }

    /**
     * Reportes logged message. This is basicly used whenever message should be sent over the server.
     * @return formatted log
     * @throws IllegalArgumentException if both commend and git messages are empty
     */
    public String getFullComment() throws IllegalArgumentException {
        if (Utils.isEmpty(comment) && Utils.isArrayEmpty(git))
            throw new IllegalArgumentException("Cannot get comment message as commend and git are empty");
        StringBuilder sb = new StringBuilder();
        if (!Utils.isEmpty(comment))
            sb.append(comment);
        if (git != null && git.size() > 0) {
            if (!Utils.isEmpty(comment))
                sb.append(" (");
            sb.append("GIT: ");
            for (int i = 0; i < git.size(); i++) {
                sb.append(git.get(i));
                sb.append("; ");
            }
            sb.deleteCharAt(sb.length()-1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append(".");
            if (!Utils.isEmpty(comment))
                sb.append(")");
        }

        return sb.toString();
    }

    public ArrayList<String> getGit() {
        return git;
    }

    public String getServerUri() {
        return serverUri;
    }

    @Override
    public void read(ISqlJetCursor cursor) throws SqlJetException {
        super.read(cursor);
        this.category = cursor.getString("category");
        this.start = cursor.getInteger("start");
        this.end = cursor.getInteger("end");
        this.duration = this.end - this.start;
        this.comment = cursor.getString("comment");
        this.git = uncacheBlob(cursor.getBlobAsArray("git"));
        this.serverUri = cursor.getString("serverUri");
    }

    @Override
    protected HashMap<String, Object> packFields(HashMap<String, Object> fieldMap) {
        fieldMap.put("category", category);
        fieldMap.put("start", start);
        fieldMap.put("end", end);
        fieldMap.put("comment", comment);
        fieldMap.put("git", cacheBlob(git));
        fieldMap.put("serverUri", serverUri);
        return fieldMap;
    }

    /**
     * A builder class that will form and apply some rules when creating new Log object.
     */
    public static class Builder {
        private long now;

        private String category;
        private long start;
        private long end;
        private long duration;
        private String message;
        private ArrayList<String> gitLog;

        public Builder() {
            this.now = DateTimeUtils.currentTimeMillis();
        }

        public Builder setCategory(String category) {
            this.category = category;
            return this;
        }

        public Builder setStart(long start) {
            this.start = start;
            return this;
        }

        public Builder setEnd(long end) {
            this.end = end;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setGitMessage(ArrayList<String> gitLog) {
            this.gitLog = gitLog;
            return this;
        }

        public Log build() {
            // Applying some rules and default values;
            if (this.start == 0)
                this.start = this.now;
            if (this.end == 0)
                this.end = now;
            // Counting duration
            if (this.start > this.end)
                throw new IllegalArgumentException("\'start\' time cannot be higher than \'end\' time");
            duration = end-start;
            return new Log(
                    this.category,
                    this.start,
                    this.end,
                    this.duration,
                    this.message,
                    this.gitLog
            );
        }
    }

    /**
     * A builder class that will form and apply some rules when creating new Log object.
     */
    public static class RemoteBuilder {
        private String category;
        private long start;
        private long end;
        private int minutesSpent;
        private long duration;
        private String message;
        private long created, modified;
        private String serverUri;

        public RemoteBuilder() {}

        public RemoteBuilder setCategory(String category) {
            this.category = category;
            return this;
        }

        public RemoteBuilder setCreated(long created) {
            this.created = created;
            return this;
        }

        public RemoteBuilder setModified(long modified) {
            this.modified = modified;
            return this;
        }

        public RemoteBuilder setServerUri(String serverUri) {
            this.serverUri = serverUri;
            return this;
        }

        public RemoteBuilder setMinutesSpent(int minutesSpent) {
            this.minutesSpent = minutesSpent;
            return this;
        }

        public RemoteBuilder setStart(long start) {
            this.start = start;
            return this;
        }

        public RemoteBuilder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Log build() {
            if (serverUri == null)
                throw new IllegalArgumentException("Server uri must be provided creating remote log");
            DateTime startTime = new DateTime(start);
            DateTime endTime = startTime.withDurationAdded(Duration.standardMinutes(minutesSpent), 1);
            start = startTime.getMillis();
            end = endTime.getMillis();
            duration = end-start;
            Log log = new Log(
                    this.category,
                    this.start,
                    this.end,
                    this.duration,
                    this.message,
                    this.serverUri
            );
            log.setCreate(created);
            log.setModify(modified);
            return log;
        }
    }

    public static String formatDuration(long durationMillis) {
        if (durationMillis < 1000)
            return "0s";
        StringBuilder builder = new StringBuilder();
        PeriodType type = PeriodType.forFields(new DurationFieldType[]{
                DurationFieldType.hours(),
                DurationFieldType.minutes(),
                DurationFieldType.seconds()
        });

        Period period = new Period(durationMillis, type);
        if (period.getDays() != 0)
            builder.append(period.getDays()).append("d").append(" ");
        if (period.getHours() != 0)
            builder.append(period.getHours()).append("h").append(" ");
        if (period.getMinutes() != 0)
            builder.append(period.getMinutes()).append("m").append(" ");
        if (period.getSeconds() != 0)
            builder.append(period.getSeconds()).append("s").append(" ");
        if ((builder.length() > 0) && builder.charAt(builder.length()-1) == " ".charAt(0))
            builder.deleteCharAt(builder.length()-1);
        return builder.toString();
    }

    public static String formatShortDuration(long durationMillis) {
        if (durationMillis < (1000*60))
            return "0m";
        StringBuilder builder = new StringBuilder();
        PeriodType type = PeriodType.forFields(new DurationFieldType[]{
                DurationFieldType.hours(),
                DurationFieldType.minutes()
        });

        Period period = new Period(durationMillis, type);
        if (period.getDays() != 0)
            builder.append(period.getDays()).append("d").append(" ");
        if (period.getHours() != 0)
            builder.append(period.getHours()).append("h").append(" ");
        if (period.getMinutes() != 0)
            builder.append(period.getMinutes()).append("m").append(" ");
        if ((builder.length() > 0) && builder.charAt(builder.length()-1) == " ".charAt(0))
            builder.deleteCharAt(builder.length()-1);
        return builder.toString();
    }

    @Override
    public String toString() {
        return "Log{" +
                "category=" + category +
                ", start=" + start +
                ", end=" + end +
                ", duration=" + duration +
                ", comment='" + comment + '\'' +
                ", git='" + git + '\'' +
                '}'+super.toString();
    }

    public String toPrettyString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%9.9s / ", (category == null) ? "" : category));
        stringBuilder.append(
                String.format("%5.5s - %5.5s = %6.6s", shortFormat.print(start),
                    shortFormat.print(end), Log.formatDuration(duration))
        );
        stringBuilder.append(" / ");
        stringBuilder.append(String.format("%50.50s", comment));
        String gitLog = getJoinedArray(git);
        if (!Utils.isEmpty(gitLog)) {
            stringBuilder.append(" / ");
            stringBuilder.append(String.format("%50.50s", gitLog));
        }
        return stringBuilder.toString();
    }

    public String toExportString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%9.9s / ", (category == null) ? "" : category));
        stringBuilder.append(
                String.format("%5.5s - %5.5s = %6.6s", shortFormat.print(start),
                    shortFormat.print(end), Log.formatDuration(duration))
        );
        stringBuilder.append(" / ");
        stringBuilder.append(comment);
        String gitLog = getJoinedArray(git);
        if (!Utils.isEmpty(gitLog)) {
            stringBuilder.append(" / ");
            stringBuilder.append(gitLog);
        }
        return stringBuilder.toString();
    }
    /**
     * Returns pulled out git log
     * @return
     */
    public static String getJoinedArray(ArrayList<String> list) {
        String format = "";
        if (list == null)
            return format;
        if (list.size() == 0)
            return format;
        for (String s : list)
            format += s.replaceAll("\\n", "; ");
        return format;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setGit(ArrayList<String> git) {
        this.git = git;
    }

    public LogTable toTableEntity() {
        return new LogTable(this);
    }

}
