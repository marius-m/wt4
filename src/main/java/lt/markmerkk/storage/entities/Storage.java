package lt.markmerkk.storage.entities;

import java.util.HashMap;
import lt.markmerkk.storage.entities.annotations.Table;
import lt.markmerkk.storage.entities.core.BaseStorage;
import lt.markmerkk.storage.entities.core.DatabaseEntity;
import org.joda.time.DateTimeUtils;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTransaction;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

/**
 * Created by mariusm on 26/08/14.
 */
public class Storage<Type extends DatabaseEntity> extends BaseStorage {

    public Storage(Class<Type> entityType) {
        super(entityType);
    }

    @Override
    protected Class[] getTables() {
        return new Class[]{
                Log.class,
                Task.class,
                Project.class
        };
    }

    @Override
    protected String getDatabaseName() {
        return "test.db";
    }

    @Override
    protected ISqlJetTransaction updateTransaction(final DatabaseEntity databaseEntity) {
        return new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                ISqlJetCursor cursor = db.getTable(databaseEntity.getClass().getAnnotation(Table.class).name()).open();
                try {
                    if (cursor.goTo(databaseEntity.getId())) {
                        HashMap<String, Object> fieldMap = databaseEntity.getFields();
                        fieldMap.put("modify", DateTimeUtils.currentTimeMillis());
                        cursor.updateByFieldNames(fieldMap);
                    }
                } catch (SqlJetException e) {
                    System.out.println(e.getMessage());
                } finally {
                    cursor.close();
                }
                return null;
            }
        };
    }

    @Override
    protected ISqlJetTransaction insertTransaction(final DatabaseEntity databaseEntity) {
        return new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                HashMap<String, Object> fieldMap = databaseEntity.getFields();
                fieldMap.put("create", DateTimeUtils.currentTimeMillis());
                fieldMap.put("modify", DateTimeUtils.currentTimeMillis());
                return db.getTable(((Table) mEntityType.getAnnotation(Table.class)).name())
                        .insertByFieldNames(fieldMap);
            }
        };
    }

    @Override
    protected ISqlJetTransaction deleteTransaction(final long rowId) {
        return new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                ISqlJetCursor cursor = db.getTable(((Table) mEntityType.getAnnotation(Table.class)).name()).open();
                try {
                    if (cursor.goTo(rowId)) {
                        cursor.delete();
                    }
                } finally {
                    cursor.close();
                }
                return null;
            }
        };
    }

}
