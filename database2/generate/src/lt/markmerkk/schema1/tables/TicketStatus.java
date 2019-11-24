/*
 * This file is generated by jOOQ.
 */
package lt.markmerkk.schema1.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import lt.markmerkk.schema1.DefaultSchema;
import lt.markmerkk.schema1.Keys;
import lt.markmerkk.schema1.tables.records.TicketStatusRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


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
public class TicketStatus extends TableImpl<TicketStatusRecord> {

    private static final long serialVersionUID = -968709220;

    /**
     * The reference instance of <code>ticket_status</code>
     */
    public static final TicketStatus TICKET_STATUS = new TicketStatus();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TicketStatusRecord> getRecordType() {
        return TicketStatusRecord.class;
    }

    /**
     * The column <code>ticket_status.id</code>.
     */
    public final TableField<TicketStatusRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>ticket_status.name</code>.
     */
    public final TableField<TicketStatusRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR(100).nullable(false).defaultValue(org.jooq.impl.DSL.field("''", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * Create a <code>ticket_status</code> table reference
     */
    public TicketStatus() {
        this(DSL.name("ticket_status"), null);
    }

    /**
     * Create an aliased <code>ticket_status</code> table reference
     */
    public TicketStatus(String alias) {
        this(DSL.name(alias), TICKET_STATUS);
    }

    /**
     * Create an aliased <code>ticket_status</code> table reference
     */
    public TicketStatus(Name alias) {
        this(alias, TICKET_STATUS);
    }

    private TicketStatus(Name alias, Table<TicketStatusRecord> aliased) {
        this(alias, aliased, null);
    }

    private TicketStatus(Name alias, Table<TicketStatusRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> TicketStatus(Table<O> child, ForeignKey<O, TicketStatusRecord> key) {
        super(child, key, TICKET_STATUS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<TicketStatusRecord, Integer> getIdentity() {
        return Keys.IDENTITY_TICKET_STATUS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<TicketStatusRecord> getPrimaryKey() {
        return Keys.PK_TICKET_STATUS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<TicketStatusRecord>> getKeys() {
        return Arrays.<UniqueKey<TicketStatusRecord>>asList(Keys.PK_TICKET_STATUS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TicketStatus as(String alias) {
        return new TicketStatus(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TicketStatus as(Name alias) {
        return new TicketStatus(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public TicketStatus rename(String name) {
        return new TicketStatus(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TicketStatus rename(Name name) {
        return new TicketStatus(name, null);
    }
}
