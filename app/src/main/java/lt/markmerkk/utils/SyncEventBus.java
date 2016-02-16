package lt.markmerkk.utils;

import com.google.common.eventbus.EventBus;

/**
 * Created by mariusmerkevicius on 2/16/16.
 * Synchronization event bus as a single instance
 */
public class SyncEventBus {
  public static final String IDENTIFIER = "SYNC_BUS";

  private static SyncEventBus sInstance;
  private EventBus eventBus;

  private SyncEventBus() {
    eventBus = new EventBus(IDENTIFIER);
  }

  public static SyncEventBus getInstance() {
    if (sInstance == null)
      sInstance = new SyncEventBus();
    return sInstance;
  }

  public EventBus getEventBus() {
    return eventBus;
  }
}
