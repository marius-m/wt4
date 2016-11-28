package lt.markmerkk.entities;

import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.IssueType;

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
        this.description = extractDescription(remoteIssue);
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
        this.description = extractDescription(remoteIssue);
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

    /**
     * Extracts possible description.
     * If task is a subtask and its parent is valid, parent infor should be appended
     *
     * For ex.: ([parent_key]:[parent_summary]):issue_summary
     *
     * @param remoteIssue remote issue provided.
     * @return always a valid result, in worst case, empty string
     */
    String extractDescription(Issue remoteIssue) {
        if (remoteIssue == null) return "";
        StringBuilder descriptionBuilder = new StringBuilder();
        if (remoteIssue.getDescription() != null) {
            descriptionBuilder.append(remoteIssue.getDescription());
        }
        IssueType issueType = remoteIssue.getIssueType();
        if (issueType == null) return descriptionBuilder.toString();
        if (!issueType.isSubtask()) return descriptionBuilder.toString();
        return descriptionBuilder
                .insert(0, extractParentPrefix(remoteIssue.getParent()))
                .toString();
    }

    /**
     * Convenience function for {@link #extractDescription(Issue)}
     * Will return prefix or an empty string
     * @return
     */
    String extractParentPrefix(Issue parent) {
        if (parent == null) return "";
        String parentSummary = parent.getSummary();
        String parentKey = parent.getKey();
        if ((parentKey == null || parentKey.isEmpty())
                && (parentSummary == null || parentSummary.isEmpty())) {
            return "(...):";
        }
        StringBuilder prefixBuilder = new StringBuilder();
        prefixBuilder.insert(0, ":");
        prefixBuilder.insert(0, ")");
        if (parentSummary != null && !parentSummary.isEmpty()) {
            prefixBuilder.insert(0, parentSummary);
        }
        if (parentKey != null) {
            if (parentSummary != null) {
                prefixBuilder.insert(0, ":");
            }
            prefixBuilder.insert(0, parentKey);
        }
        prefixBuilder.insert(0, "(");
        return prefixBuilder.toString();
    }

    //endregion

}
