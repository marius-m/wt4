package lt.markmerkk.storage.entities;

import java.util.HashMap;
import java.util.List;
import lt.markmerkk.storage.entities.annotations.Column;
import lt.markmerkk.storage.entities.annotations.FieldType;
import lt.markmerkk.storage.entities.annotations.Table;
import lt.markmerkk.storage.entities.annotations.TableIndex;
import lt.markmerkk.storage.entities.core.DatabaseEntity;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;

/**
 * Created by mariusm on 10/25/14.
 */
@Table(name = "Task")
@TableIndex(name = "titleIndex", createQuery = "CREATE INDEX titleIndex ON Task(title)")
public class Task extends DatabaseEntity {

    @Column(value = FieldType.TEXT, canBeNull = false)
    protected String title;
    @Column(value = FieldType.TEXT, canBeNull = true)
    protected String detail;
    @Column(value = FieldType.TEXT, canBeNull = true)
    protected String link;

    public Task() {}

    public Task(String title) {
        this.title = title;
    }

    public Task(String title, String detail, String link) {
        this.title = title;
        this.detail = detail;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public String getLink() {
        return link;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public static Task getTaskWithTitle(List<Task> tasks, String title) {
        for (Task task : tasks)
            if (task.getTitle().equals(title))
                return task;
        return null;
    }


    @Override
    public void read(ISqlJetCursor cursor) throws SqlJetException {
        super.read(cursor);
        this.title = cursor.getString("title");
        this.detail = cursor.getString("detail");
        this.link = cursor.getString("link");
    }

    @Override
    protected HashMap<String, Object> packFields(HashMap<String, Object> fieldMap) {
        fieldMap.put("title", title);
        fieldMap.put("detail", detail);
        fieldMap.put("link", link);
        return fieldMap;
    }

    @Override
    public String toString() {
        return "Task{" +
                ", title='" + title + '\'' +
                ", detail='" + detail + '\'' +
                ", link='" + link + '\'' +
                '}'+super.toString();
    }
}
