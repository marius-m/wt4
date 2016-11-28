package lt.markmerkk.entities;

import net.rcarz.jiraclient.Issue;

import java.util.Date;

/**
 * Created by mariusmerkevicius on 11/22/15.
 * A helper class to validate and build {@link LocalIssue} object
 */
public class LocalIssueBuilder {
    private String project;
    private String key;
    private String description;
    private long createDate;
    private long updateDate;

    private long _id;
    private long id;
    private String uri;
    private boolean dirty = true;
    private boolean error = false;
    private boolean deleted = false;
    private String errorMessage = null;
    private long downloadMillis = 0L;

    public LocalIssueBuilder() {
    }

    public LocalIssueBuilder(LocalIssue oldIssue) {
        this._id = oldIssue._id;
        this.project = oldIssue.project;
        this.key = oldIssue.key;
        this.description = oldIssue.description;

        this.uri = oldIssue.uri;
        this.id = oldIssue.id;
        this.deleted = oldIssue.deleted;
        this.dirty = true;
        this.error = false;
        this.errorMessage = null;
    }

    public LocalIssueBuilder(Issue remoteIssue) {
        this.project = remoteIssue.getProject().getKey();
        this.key = remoteIssue.getKey();
        if (remoteIssue.getIssueType().isSubtask()) {
            this.description = String.format(
                    "%s: %s > %s",
                    remoteIssue.getParent().getKey(),
                    remoteIssue.getParent().getSummary(),
                    remoteIssue.getSummary()
            );
        } else {
            this.description = remoteIssue.getSummary();
        }
        this.createDate = extractDate(remoteIssue.getCreatedDate());
        this.updateDate = extractDate(remoteIssue.getUpdatedDate());

        this.uri = remoteIssue.getSelf().toString();
        this.id = SimpleLogBuilder.parseUri(remoteIssue.getId());
        this.deleted = false;
        this.dirty = false;
        this.error = false;
        this.errorMessage = null;
    }

    public LocalIssueBuilder(LocalIssue oldIssue, Issue remoteIssue) {
        this._id = oldIssue._id;
        this.project = remoteIssue.getProject().getKey();
        this.key = remoteIssue.getKey();
        this.description = remoteIssue.getSummary();
        this.createDate = extractDate(remoteIssue.getCreatedDate());
        this.updateDate = extractDate(remoteIssue.getUpdatedDate());

        this.uri = remoteIssue.getSelf().toString();
        this.id = SimpleLogBuilder.parseUri(remoteIssue.getId());
        this.deleted = false;
        this.dirty = false;
        this.error = false;
        this.errorMessage = null;
    }

    public LocalIssueBuilder setProject(String project) {
        this.project = project;
        return this;
    }

    public LocalIssueBuilder setKey(String key) {
        this.key = key;
        return this;
    }

    public LocalIssueBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public LocalIssueBuilder setDownloadMillis(long downloadMillis) {
        this.downloadMillis = downloadMillis;
        return this;
    }

    public LocalIssue build() {
        LocalIssue newIssue = new LocalIssue();
        if (this.project == null)
            throw new IllegalArgumentException("Project must be provided!");
        if (this.key == null)
            throw new IllegalArgumentException("Key must be provided!");
//    if (downloadMillis <= 0)
//      throw new IllegalArgumentException("downloadMillis == 0");
        newIssue.project = this.project;
        newIssue.key = this.key;
        newIssue.description = this.description;
        newIssue.createDate = this.createDate;
        newIssue.updateDate = this.updateDate;

        newIssue.uri = this.uri;
        newIssue.id = this.id;
        newIssue.dirty = this.dirty;
        newIssue.error = this.error;
        newIssue.errorMessage = this.errorMessage;
        newIssue.deleted = this.deleted;
        newIssue.download_millis = this.downloadMillis;
        if (this._id > 0)
            newIssue._id = this._id;
        return newIssue;
    }

    public LocalIssue buildWithError(String errorMessage) {
        LocalIssue issue = build();
        issue.dirty = false;
        issue.error = true;
        issue.errorMessage = errorMessage;
        return issue;
    }

    //region Convenience

    /**
     * Extracts date from the {@link Issue} field
     *
     * @param date
     * @return
     */
    long extractDate(Date date) {
        if (date == null) return 0L;
        return date.getTime();
    }


    //endregion

}
