package lt.markmerkk.storage.entities.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import lt.markmerkk.storage.entities.annotations.Column;
import lt.markmerkk.storage.entities.annotations.FieldType;
import lt.markmerkk.storage.entities.annotations.Table;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;

/**
 * Created by mariusm on 27/08/14.
 */
@Table(name = "DatabaseEntity")
public abstract class DatabaseEntity {

    @Column(value = FieldType.INTEGER, isPrimary = true)
    protected long id;
    @Column(value = FieldType.INTEGER, canBeNull = true)
    protected long create;
    @Column(value = FieldType.INTEGER, canBeNull = true)
    protected long modify;

    public void read(ISqlJetCursor cursor) throws SqlJetException {
        if (cursor == null)
            throw new SqlJetException("Cursor is null!");
        id = cursor.getInteger("id");
        create = cursor.getInteger("create");
        modify = cursor.getInteger("modify");
    }

    public HashMap<String, Object> getFields() {
        HashMap<String, Object> fieldMap = new HashMap<String, Object>();
        fieldMap = packFields(fieldMap);
        return fieldMap;
    }

    protected abstract HashMap<String, Object> packFields(HashMap<String, Object> fieldMap);

    public long getId() {
        return id;
    }

    public long getCreate() {
        return create;
    }

    public long getModify() {
        return modify;
    }

    public void setCreate(long create) {
        this.create = create;
    }

    public void setModify(long modify) {
        this.modify = modify;
    }

    /**
     * Caches paths
     * @return cached paths to byte array
     */
    protected byte[] cacheBlob(ArrayList arrayList) {
        if (arrayList == null)
            arrayList = new ArrayList();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(arrayList);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {}
            try {
                bos.close();
            } catch (IOException ex) {}
        }
        return null;
    }

    /**
     * uncaches paths
     * @param cache byte cache
     * @return
     */
    protected ArrayList uncacheBlob(byte[] cache) {
        if (cache == null)
            return null;
        ByteArrayInputStream bis = new ByteArrayInputStream(cache);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            return (ArrayList) in.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bis.close();
            } catch (IOException ex) {}
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {}
        }
        return null;
    }



    @Override
    public String toString() {
        return "DatabaseEntity{" +
                "id=" + id +
                ", create=" + create +
                ", modify=" + modify +
                '}';
    }

    //    public HashMap<String, Object> getFields() {
//        HashMap<String, Object> fieldHash = new HashMap<String, Object>();
//        Field[] fields = getClass().getDeclaredFields();
//        for (Field field : fields) {
//            Column columnAnnotation = field.getAnnotation(Column.class);
//            if (columnAnnotation != null && !columnAnnotation.isPrimary()) {
//                fieldHash.put(field.getName(), parseField(field));
//            }
//        }
//        return fieldHash;
//    }
//    private Object parseField(Field field) {
//        try {
//            field.setAccessible(true);
//            if (field.getType().isPrimitive()) {
//                if (field.getType().equals(int.class)) {
//                    return field.getInt(int.class);
//                } else if (field.getType().equals(double.class)) {
//                    return field.getDouble(double.class);
//                } else if (field.getType().equals(boolean.class)) {
//                    return field.getBoolean(boolean.class);
//                } else if (field.getType().equals(float.class)) {
//                    return field.getFloat(float.class);
//                } else if (field.getType().equals(long.class)) {
//                    return field.getLong(long.class);
//                }
//            } else if (field.getType().equals(String.class)) {
//                return field.get(String.class);
//            } else if (field.getType().equals(DateTime.class)) {
//                DateTime object = (DateTime)field.get(DateTime.class);
//                return object.getMillis();
//            }
//            field.setAccessible(false);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

}
