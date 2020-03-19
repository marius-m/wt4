/*
 * This file is generated by jOOQ.
 */
package lt.markmerkk.schema1.tables.records;


import javax.annotation.Generated;

import lt.markmerkk.schema1.tables.Worklog;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record15;
import org.jooq.Row15;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class WorklogRecord extends UpdatableRecordImpl<WorklogRecord> implements Record15<Integer, Long, Long, Long, String, String, String, String, Long, Byte, Byte, Byte, String, Long, String> {

    private static final long serialVersionUID = 1466754332;

    /**
     * Setter for <code>worklog.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>worklog.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>worklog.start</code>.
     */
    public void setStart(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>worklog.start</code>.
     */
    public Long getStart() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>worklog.end</code>.
     */
    public void setEnd(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>worklog.end</code>.
     */
    public Long getEnd() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>worklog.duration</code>.
     */
    public void setDuration(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>worklog.duration</code>.
     */
    public Long getDuration() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>worklog.code</code>.
     */
    public void setCode(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>worklog.code</code>.
     */
    public String getCode() {
        return (String) get(4);
    }

    /**
     * Setter for <code>worklog.comment</code>.
     */
    public void setComment(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>worklog.comment</code>.
     */
    public String getComment() {
        return (String) get(5);
    }

    /**
     * Setter for <code>worklog.system_note</code>.
     */
    public void setSystemNote(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>worklog.system_note</code>.
     */
    public String getSystemNote() {
        return (String) get(6);
    }

    /**
     * Setter for <code>worklog.author</code>.
     */
    public void setAuthor(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>worklog.author</code>.
     */
    public String getAuthor() {
        return (String) get(7);
    }

    /**
     * Setter for <code>worklog.remote_id</code>.
     */
    public void setRemoteId(Long value) {
        set(8, value);
    }

    /**
     * Getter for <code>worklog.remote_id</code>.
     */
    public Long getRemoteId() {
        return (Long) get(8);
    }

    /**
     * Setter for <code>worklog.is_deleted</code>.
     */
    public void setIsDeleted(Byte value) {
        set(9, value);
    }

    /**
     * Getter for <code>worklog.is_deleted</code>.
     */
    public Byte getIsDeleted() {
        return (Byte) get(9);
    }

    /**
     * Setter for <code>worklog.is_dirty</code>.
     */
    public void setIsDirty(Byte value) {
        set(10, value);
    }

    /**
     * Getter for <code>worklog.is_dirty</code>.
     */
    public Byte getIsDirty() {
        return (Byte) get(10);
    }

    /**
     * Setter for <code>worklog.is_error</code>.
     */
    public void setIsError(Byte value) {
        set(11, value);
    }

    /**
     * Getter for <code>worklog.is_error</code>.
     */
    public Byte getIsError() {
        return (Byte) get(11);
    }

    /**
     * Setter for <code>worklog.error_message</code>.
     */
    public void setErrorMessage(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>worklog.error_message</code>.
     */
    public String getErrorMessage() {
        return (String) get(12);
    }

    /**
     * Setter for <code>worklog.fetchTime</code>.
     */
    public void setFetchtime(Long value) {
        set(13, value);
    }

    /**
     * Getter for <code>worklog.fetchTime</code>.
     */
    public Long getFetchtime() {
        return (Long) get(13);
    }

    /**
     * Setter for <code>worklog.URL</code>.
     */
    public void setUrl(String value) {
        set(14, value);
    }

    /**
     * Getter for <code>worklog.URL</code>.
     */
    public String getUrl() {
        return (String) get(14);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record15 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row15<Integer, Long, Long, Long, String, String, String, String, Long, Byte, Byte, Byte, String, Long, String> fieldsRow() {
        return (Row15) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row15<Integer, Long, Long, Long, String, String, String, String, Long, Byte, Byte, Byte, String, Long, String> valuesRow() {
        return (Row15) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Worklog.WORKLOG.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field2() {
        return Worklog.WORKLOG.START;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field3() {
        return Worklog.WORKLOG.END;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field4() {
        return Worklog.WORKLOG.DURATION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return Worklog.WORKLOG.CODE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return Worklog.WORKLOG.COMMENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return Worklog.WORKLOG.SYSTEM_NOTE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return Worklog.WORKLOG.AUTHOR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field9() {
        return Worklog.WORKLOG.REMOTE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field10() {
        return Worklog.WORKLOG.IS_DELETED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field11() {
        return Worklog.WORKLOG.IS_DIRTY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field12() {
        return Worklog.WORKLOG.IS_ERROR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field13() {
        return Worklog.WORKLOG.ERROR_MESSAGE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field14() {
        return Worklog.WORKLOG.FETCHTIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field15() {
        return Worklog.WORKLOG.URL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component2() {
        return getStart();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component3() {
        return getEnd();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component4() {
        return getDuration();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component5() {
        return getCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component6() {
        return getComment();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component7() {
        return getSystemNote();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component8() {
        return getAuthor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component9() {
        return getRemoteId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component10() {
        return getIsDeleted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component11() {
        return getIsDirty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component12() {
        return getIsError();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component13() {
        return getErrorMessage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component14() {
        return getFetchtime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component15() {
        return getUrl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value2() {
        return getStart();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value3() {
        return getEnd();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value4() {
        return getDuration();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getComment();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value7() {
        return getSystemNote();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getAuthor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value9() {
        return getRemoteId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value10() {
        return getIsDeleted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value11() {
        return getIsDirty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value12() {
        return getIsError();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value13() {
        return getErrorMessage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value14() {
        return getFetchtime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value15() {
        return getUrl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorklogRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorklogRecord value2(Long value) {
        setStart(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorklogRecord value3(Long value) {
        setEnd(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorklogRecord value4(Long value) {
        setDuration(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorklogRecord value5(String value) {
        setCode(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorklogRecord value6(String value) {
        setComment(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorklogRecord value7(String value) {
        setSystemNote(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorklogRecord value8(String value) {
        setAuthor(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorklogRecord value9(Long value) {
        setRemoteId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorklogRecord value10(Byte value) {
        setIsDeleted(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorklogRecord value11(Byte value) {
        setIsDirty(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorklogRecord value12(Byte value) {
        setIsError(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorklogRecord value13(String value) {
        setErrorMessage(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorklogRecord value14(Long value) {
        setFetchtime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorklogRecord value15(String value) {
        setUrl(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorklogRecord values(Integer value1, Long value2, Long value3, Long value4, String value5, String value6, String value7, String value8, Long value9, Byte value10, Byte value11, Byte value12, String value13, Long value14, String value15) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        value15(value15);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached WorklogRecord
     */
    public WorklogRecord() {
        super(Worklog.WORKLOG);
    }

    /**
     * Create a detached, initialised WorklogRecord
     */
    public WorklogRecord(Integer id, Long start, Long end, Long duration, String code, String comment, String systemNote, String author, Long remoteId, Byte isDeleted, Byte isDirty, Byte isError, String errorMessage, Long fetchtime, String url) {
        super(Worklog.WORKLOG);

        set(0, id);
        set(1, start);
        set(2, end);
        set(3, duration);
        set(4, code);
        set(5, comment);
        set(6, systemNote);
        set(7, author);
        set(8, remoteId);
        set(9, isDeleted);
        set(10, isDirty);
        set(11, isError);
        set(12, errorMessage);
        set(13, fetchtime);
        set(14, url);
    }
}
