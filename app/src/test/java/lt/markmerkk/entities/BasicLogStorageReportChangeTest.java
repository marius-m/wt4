package lt.markmerkk.entities;

import java.util.ArrayList;
import javafx.collections.ObservableList;
import lt.markmerkk.entities.database.interfaces.IExecutor;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by mariusmerkevicius on 12/13/15.
 */
public class BasicLogStorageReportChangeTest {
  @Test public void testValidLogs() throws Exception {
    // Arrange
    BasicLogStorage storage = new BasicLogStorage(mock(IExecutor.class));
    storage.listeners = new ArrayList<IDataListener<SimpleLog>>() {{
      add(mock(IDataListener.class));
      add(mock(IDataListener.class));
    }};

    // Act
    storage.reportDataChange();

    // Assert
    verify(storage.listeners.get(0)).onDataChange(any(ObservableList.class));
    verify(storage.listeners.get(1)).onDataChange(any(ObservableList.class));
  }

  @Test public void testValidIssues() throws Exception {
    // Arrange
    BasicLogStorage storage = new BasicLogStorage(mock(IExecutor.class));
    storage.listeners = new ArrayList<IDataListener<SimpleLog>>() {{
      add(mock(IDataListener.class));
      add(mock(IDataListener.class));
    }};

    // Act
    storage.reportDataChange();

    // Assert
    verify(storage.listeners.get(0)).onDataChange(any(ObservableList.class));
    verify(storage.listeners.get(1)).onDataChange(any(ObservableList.class));
  }
}