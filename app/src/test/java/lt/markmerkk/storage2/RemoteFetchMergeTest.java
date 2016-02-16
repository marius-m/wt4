package lt.markmerkk.storage2;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by mariusmerkevicius on 2/16/16.
 */
public class RemoteFetchMergeTest {

  @Test
  public void test_noObject_shouldCreateNew() throws Exception {
    // Arrange
    MockMerger merger = spy(new MockMerger());
    doReturn(null).when(merger).localEntity(anyString());

    // Act
    boolean result = merger.merge(new Object());

    // Assert
    verify(merger).entityNew(anyObject());
  }

  @Test
  public void test_objectExist_shouldUpdate() throws Exception {
    // Arrange
    MockMerger merger = spy(new MockMerger());
    doReturn(new Object()).when(merger).localEntity(anyString());

    // Act
    boolean result = merger.merge(new Object());

    // Assert
    verify(merger).entityUpdate(anyObject(), anyObject());
  }

  //region Classes

  private class MockMerger extends RemoteFetch<Object, Object> {

    @Override
    protected boolean entityNew(Object remoteEntity) {
      return false;
    }

    @Override
    protected boolean entityUpdate(Object localEntity, Object remoteEntity) {
      return false;
    }

    @Override
    protected String localEntityId(Object remoteEntity) {
      return null;
    }

    @Override
    protected Object localEntity(String remoteId) {
      return null;
    }
  }

  //endregion

}