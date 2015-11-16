package lt.markmerkk.storage.entities.core;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import lt.markmerkk.storage.entities.annotations.Column;
import lt.markmerkk.storage.entities.annotations.Table;
import lt.markmerkk.storage.entities.annotations.TableIndex;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTransaction;
import org.tmatesoft.sqljet.core.table.SqlJetDb;
import org.tmatesoft.sqljet.core.table.SqlJetScope;

/**
 * Created by mariusm on 02/09/14.
 */
public abstract class BaseStorage<Type extends DatabaseEntity> {

    protected SqlJetDb db;
    protected Class<Type> mEntityType;

    public BaseStorage(Class<Type> typeClass) {
        mEntityType = typeClass;
    }

    public Object update(Object o) {
        return runTransaction(StorageType.UPDATE, o);
    }

    public Object insert(Object o) {
        return runTransaction(StorageType.INSERT, o);
    }

    public Object delete(long rowId) {
        return runTransaction(StorageType.DELETE, rowId);
    }

    public ArrayList<Type> readAll() {
        Object readObject = runTransaction(StorageType.READ_ALL, null);
        if (readObject != null)
            return (ArrayList<Type>)readObject;
        return null;
    }

    public ArrayList<Type> readWithScope(String name, SqlJetScope scope) {
        Object readObject = runTransaction(name, scope);
        if (readObject != null)
            return (ArrayList<Type>)readObject;
        return null;
    }

    public ArrayList<Type> readWithLookup(String name, String lookup) {
        Object readObject = runTransaction(name, lookup);
        if (readObject != null)
            return (ArrayList<Type>)readObject;
        return null;
    }

    public Type read(long rowId) {
        throw new IllegalArgumentException("Unsupported functionality");
//        Object readObject = runTransaction(StorageType.READ_ALL, null);
//        if (readObject != null)
//            return (ArrayList<Type>)readObject;
    }

    protected Object runTransaction(StorageType type, Object o) {
        Object object = null;
        try {
            open();
            switch (type) {
                case UPDATE:
                    object = db.runWriteTransaction(updateTransaction((DatabaseEntity) o));
                    break;
                case INSERT:
                    object = db.runWriteTransaction(insertTransaction((DatabaseEntity) o));
                    break;
                case DELETE:
                    object = db.runWriteTransaction(deleteTransaction((Long) o));
                    break;
                case READ_ALL:
                    db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
                    try {
                        return parseAllCursor(db.getTable(mEntityType.getAnnotation(Table.class).name()).open());
                    } finally {
                        db.commit();
                    }
            }
        } catch (SqlJetException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return object;
    }

    protected Object runTransaction(String index, SqlJetScope scope) {
        Object object = null;
        try {
            open();
            db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
            try {
                return parseAllCursor(db.getTable(mEntityType.getAnnotation(Table.class).name()).scope(index, scope));
            } finally {
                db.commit();
            }
        } catch (SqlJetException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return object;
    }

    protected Object runTransaction(String index, String lookup) {
        Object object = null;
        try {
            open();
            db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
            try {
                return parseAllCursor(db.getTable(mEntityType.getAnnotation(Table.class).name()).lookup(index, lookup));
            } finally {
                db.commit();
            }
        } catch (SqlJetException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return object;
    }

    protected abstract Class[] getTables();
    protected abstract String getDatabaseName();
    protected abstract ISqlJetTransaction updateTransaction(DatabaseEntity databaseEntity);
    protected abstract ISqlJetTransaction insertTransaction(DatabaseEntity databaseEntity);
    protected abstract ISqlJetTransaction deleteTransaction(long rowId);

    protected ArrayList parseAllCursor(ISqlJetCursor cursor) throws SqlJetException {
        ArrayList<Type> tasks = new ArrayList<Type>();
        try {
            if (!cursor.eof()) {
                do {
                    try {
                        try {
                            Object databaseEntity = mEntityType.newInstance();
                            ((DatabaseEntity) databaseEntity).read(cursor);
                            tasks.add((Type) databaseEntity);
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } catch (SqlJetException e) {
                        e.printStackTrace();
                    }
                } while (cursor.next());
            }
        } finally {
            cursor.close();
        }
        return tasks;
    }

    protected void open() throws SqlJetException {
        if ((db != null && !db.isOpen()) || db == null) {
            db = SqlJetDb.open(new File(getDatabaseName()), true);
            // Creating tables
            for (final Class column : getTables()) {
                if (db.getSchema().getTable(((Table) column.getAnnotation(Table.class)).name()) == null) {
                    db.runWriteTransaction(new ISqlJetTransaction() {
                        public Object run(SqlJetDb arg0) throws SqlJetException {
                            db.createTable(formCreateQueryFromClass(column));
                            // Creating indexes
                            TableIndex tableIndex = (TableIndex)column.getAnnotation(TableIndex.class);
                            if (tableIndex != null)
                                db.createIndex(tableIndex.createQuery());
                            return null;
                        }
                    });
                }
            }
        }
    }

    protected void close() {
        try {
            if (db != null)
                db.close();
            db = null;
        } catch (SqlJetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a table in sqlite create annotated Entities.
     * Table must be annotated as {@link Table}
     * and all table columns must be identified as {@link Column}
     * @param clazz
     * @return
     */
    private String formCreateQueryFromClass(Class clazz) {
        String query = "CREATE TABLE ";
        Annotation tableAnnotation = clazz.getAnnotation(Table.class);
        if (tableAnnotation != null) {
            query += ((Table) tableAnnotation).name() + " ";
            query += "(";
            query += recursiveColumnPuller(clazz);
            query += ");";
            return query;
        }
        return null;
    }

    private String recursiveColumnPuller(Class clazz) {
        if (clazz.getAnnotation(Table.class) == null)
            return null;
        StringBuilder query = new StringBuilder();
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.getAnnotation(Column.class) != null) {
                Column columnAnnotation = field.getAnnotation(Column.class);
                query.append(field.getName());
                query.append(" "+columnAnnotation.value().name());
                if (!columnAnnotation.canBeNull())
                    query.append(" NOT NULL");
                if (columnAnnotation.isPrimary())
                    query.append(" PRIMARY KEY");
                if (columnAnnotation.defaultValue().length() > 0)
                    query.append(" default "+columnAnnotation.defaultValue());
                if (i < fields.length-1)
                    query.append(",");
            }
        }
        if (clazz.getSuperclass() != null) {
            String superColumns = recursiveColumnPuller(clazz.getSuperclass());
            if (superColumns != null) {
                query.insert(0, superColumns+", ");
            }
        }
        return query.toString();
    }

    public enum StorageType {
        UPDATE,
        INSERT,
        DELETE,
        READ_ALL,
        READ_PARTIAL
    }

}
