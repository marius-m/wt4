package lt.markmerkk.storage2;

import lt.markmerkk.DBProdExecutor;
import lt.markmerkk.storage2.database.interfaces.IQueryJob;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by mariusmerkevicius on 12/13/15.
 */
public class BasicLogStorageInsertTest {
  @Test public void testValid() throws Exception {
    // Arrange
    BasicLogStorage storage = new BasicLogStorage();
    storage.executor = mock(DBProdExecutor.class);

    // Act
    storage.insert(new SimpleLog());

    // Assert
    verify(storage.executor).execute(any(IQueryJob.class));
  }

  @Test public void testNullJob() throws Exception {
    // Arrange
    BasicLogStorage storage = new BasicLogStorage();
    storage.executor = mock(DBProdExecutor.class);

    // Act
    storage.insert(null);

    // Assert
    verify(storage.executor, never()).execute(any(IQueryJob.class));
  }
}