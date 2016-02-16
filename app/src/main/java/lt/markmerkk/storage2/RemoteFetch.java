package lt.markmerkk.storage2;

/**
 * Created by mariusmerkevicius on 2/16/16. Responsible for fetching remote objects and inserting /
 * updating them in database
 */
public abstract class RemoteFetch<LocalEntityType, RemoteEntityType> {

  /**
   * Core method for merging remote object to local database
   */
  public boolean merge(RemoteEntityType remoteEntity) {
    LocalEntityType localEntity = localEntity(localEntityId(remoteEntity));
    if (localEntity == null)
      return entityNew(remoteEntity);
    return entityUpdate(localEntity, remoteEntity);
  }

  //region Convenience

  /**
   * Adds a new entity to the database
   */
  protected abstract boolean entityNew(RemoteEntityType remoteEntity);

  /**
   * Updates an old entity with new data
   */
  protected abstract boolean entityUpdate(LocalEntityType localEntity, RemoteEntityType remoteEntity);

  /**
   * Pulls local entity id from the remote object
   */
  protected abstract String localEntityId(RemoteEntityType remoteEntity);

  /**
   * Pulls old entity from database
   * @param remoteId
   */
  protected abstract LocalEntityType localEntity(String remoteId);

  //endregion

}
