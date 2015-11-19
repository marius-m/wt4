package lt.markmerkk.storage.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lt.markmerkk.storage.entities.annotations.Column;
import lt.markmerkk.storage.entities.annotations.FieldType;
import lt.markmerkk.storage.entities.annotations.Table;
import lt.markmerkk.storage.entities.core.DatabaseEntity;
import lt.markmerkk.utils.Utils;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;

/**
 * Created by mariusm on 10/25/14.
 */
@Table(name = "Project")
public class Project extends DatabaseEntity {

    @Column(value = FieldType.TEXT, canBeNull = true)
    protected String title;
    @Column(value = FieldType.TEXT, canBeNull = true)
    protected String detail;
    @Column(value = FieldType.BLOB, canBeNull = true)
    protected ArrayList<String> paths;

    public Project() {
        paths = new ArrayList();
    }

    public Project(String title) {
        this.title = title;
        paths = new ArrayList();
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public ArrayList<String> getPaths() {
        return paths;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setPaths(ArrayList<String> paths) {
        this.paths = paths;
    }

    public static Project getProjectWithTitle(List<Project> projects, String title) {
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i) instanceof Project) {
                Project project = projects.get(i);
                if (project.getTitle().equals(title))
                    return project;
            }
        }
        return null;
    }

    @Override
    public void read(ISqlJetCursor cursor) throws SqlJetException {
        super.read(cursor);
        this.title = cursor.getString("title");
        this.detail = cursor.getString("detail");
        this.paths = uncacheBlob(cursor.getBlobAsArray("paths"));
    }

    @Override
    protected HashMap<String, Object> packFields(HashMap<String, Object> fieldMap) {
        fieldMap.put("title", title);
        fieldMap.put("detail", detail);
        fieldMap.put("paths", cacheBlob(paths));
        return fieldMap;
    }

    @Override
    public String toString() {
        return title+((!Utils.isEmpty(detail))?" ("+detail+")":"");
    }
}
