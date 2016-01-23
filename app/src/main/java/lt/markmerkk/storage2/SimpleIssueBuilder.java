package lt.markmerkk.storage2;

import java.net.URI;
import java.net.URISyntaxException;
import lt.markmerkk.utils.Utils;
import org.joda.time.DurationFieldType;

/**
 * Created by mariusmerkevicius on 11/22/15.
 * A helper class to validate and build {@link SimpleIssue} object
 */
public class SimpleIssueBuilder {
  private String project;
  private String key;
  private String description;

  private long _id;
  private long id;
  private String uri;
  private boolean dirty = true;
  private boolean error = false;
  private boolean deleted = false;
  private String errorMessage = null;

  public SimpleIssueBuilder() { }

  public SimpleIssueBuilder(SimpleIssue oldIssue) {
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

//  public SimpleIssueBuilder(Issue remoteIssue) {
//    this.project = remoteIssue.getProject().getKey();
//    this.key = remoteIssue.getKey();
//    this.description = remoteIssue.getSummary();
//
//    this.uri = remoteIssue.getSelf().toString();
//    this.id = remoteIssue.getId();
//    this.deleted = false;
//    this.dirty = false;
//    this.error = false;
//    this.errorMessage = null;
//  }

//  public SimpleIssueBuilder(SimpleIssue oldIssue, Issue remoteIssue) {
//    this._id = oldIssue._id;
//    this.project = remoteIssue.getProject().getKey();
//    this.key = remoteIssue.getKey();
//    this.description = remoteIssue.getSummary();
//
//    this.uri = remoteIssue.getSelf().toString();
//    this.id = remoteIssue.getId();
//    this.deleted = false;
//    this.dirty = false;
//    this.error = false;
//    this.errorMessage = null;
//  }

  public SimpleIssueBuilder setProject(String project) {
    this.project = project;
    return this;
  }

  public SimpleIssueBuilder setKey(String key) {
    this.key = key;
    return this;
  }

  public SimpleIssueBuilder setDescription(String description) {
    this.description = description;
    return this;
  }

  public SimpleIssue build() {
    SimpleIssue newIssue = new SimpleIssue();
    if (this.project == null)
      throw new IllegalArgumentException("Project must be provided!");
    if (this.key == null)
      throw new IllegalArgumentException("Key must be provided!");
    newIssue.project = this.project;
    newIssue.key = this.key;
    newIssue.description = this.description;

    newIssue.uri = this.uri;
    newIssue.id = this.id;
    newIssue.dirty = this.dirty;
    newIssue.error = this.error;
    newIssue.errorMessage = this.errorMessage;
    newIssue.deleted = this.deleted;
    if (this._id > 0)
      newIssue._id = this._id;
    return newIssue;
  }

}
