/*
 * This file is generated by jOOQ.
 */
package lt.markmerkk.schema1;


import javax.annotation.Generated;

import lt.markmerkk.schema1.tables.Ticket;
import lt.markmerkk.schema1.tables.TicketStatus;
import lt.markmerkk.schema1.tables.TicketUseHistory;
import lt.markmerkk.schema1.tables.Worklog;
import lt.markmerkk.schema1.tables.records.TicketRecord;
import lt.markmerkk.schema1.tables.records.TicketStatusRecord;
import lt.markmerkk.schema1.tables.records.TicketUseHistoryRecord;
import lt.markmerkk.schema1.tables.records.WorklogRecord;

import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code></code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<TicketRecord, Integer> IDENTITY_TICKET = Identities0.IDENTITY_TICKET;
    public static final Identity<TicketStatusRecord, Integer> IDENTITY_TICKET_STATUS = Identities0.IDENTITY_TICKET_STATUS;
    public static final Identity<TicketUseHistoryRecord, Integer> IDENTITY_TICKET_USE_HISTORY = Identities0.IDENTITY_TICKET_USE_HISTORY;
    public static final Identity<WorklogRecord, Integer> IDENTITY_WORKLOG = Identities0.IDENTITY_WORKLOG;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<TicketRecord> PK_TICKET = UniqueKeys0.PK_TICKET;
    public static final UniqueKey<TicketStatusRecord> PK_TICKET_STATUS = UniqueKeys0.PK_TICKET_STATUS;
    public static final UniqueKey<TicketUseHistoryRecord> PK_TICKET_USE_HISTORY = UniqueKeys0.PK_TICKET_USE_HISTORY;
    public static final UniqueKey<WorklogRecord> PK_WORKLOG = UniqueKeys0.PK_WORKLOG;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 {
        public static Identity<TicketRecord, Integer> IDENTITY_TICKET = Internal.createIdentity(Ticket.TICKET, Ticket.TICKET.ID);
        public static Identity<TicketStatusRecord, Integer> IDENTITY_TICKET_STATUS = Internal.createIdentity(TicketStatus.TICKET_STATUS, TicketStatus.TICKET_STATUS.ID);
        public static Identity<TicketUseHistoryRecord, Integer> IDENTITY_TICKET_USE_HISTORY = Internal.createIdentity(TicketUseHistory.TICKET_USE_HISTORY, TicketUseHistory.TICKET_USE_HISTORY.ID);
        public static Identity<WorklogRecord, Integer> IDENTITY_WORKLOG = Internal.createIdentity(Worklog.WORKLOG, Worklog.WORKLOG.ID);
    }

    private static class UniqueKeys0 {
        public static final UniqueKey<TicketRecord> PK_TICKET = Internal.createUniqueKey(Ticket.TICKET, "pk_ticket", Ticket.TICKET.ID);
        public static final UniqueKey<TicketStatusRecord> PK_TICKET_STATUS = Internal.createUniqueKey(TicketStatus.TICKET_STATUS, "pk_ticket_status", TicketStatus.TICKET_STATUS.ID);
        public static final UniqueKey<TicketUseHistoryRecord> PK_TICKET_USE_HISTORY = Internal.createUniqueKey(TicketUseHistory.TICKET_USE_HISTORY, "pk_ticket_use_history", TicketUseHistory.TICKET_USE_HISTORY.ID);
        public static final UniqueKey<WorklogRecord> PK_WORKLOG = Internal.createUniqueKey(Worklog.WORKLOG, "pk_worklog", Worklog.WORKLOG.ID);
    }
}
