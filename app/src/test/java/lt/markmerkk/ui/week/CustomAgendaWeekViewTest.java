package lt.markmerkk.ui.week;

import java.time.LocalDate;
import jfxtras.scene.control.agenda.Agenda;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;

/**
 * Created by mariusmerkevicius on 3/5/16.
 */
public class CustomAgendaWeekViewTest {
  @Test
  public void test_inputBetween_shouldBeTrue() throws Exception {
    // Arrange
    CustomAgendaWeekView view = mock(CustomAgendaWeekView.class);
    view.startDate = LocalDate.of(2007, 06, 10);
    view.endDate = LocalDate.of(2007, 06, 16);
    doCallRealMethod().when(view).isTargetBetween(any(DateTime.class));

    // Act
    boolean result = view.isTargetBetween(new DateTime(2007, 06, 13, 12, 23, 10));

    // Assert
    assertThat(result).isTrue();
  }

  @Test
  public void test_inputBeforeStart_shouldBeFalse() throws Exception {
    // Arrange
    CustomAgendaWeekView view = mock(CustomAgendaWeekView.class);
    view.startDate = LocalDate.of(2007, 06, 10);
    view.endDate = LocalDate.of(2007, 06, 16);
    doCallRealMethod().when(view).isTargetBetween(any(DateTime.class));

    // Act
    boolean result = view.isTargetBetween(new DateTime(2007, 06, 9, 12, 23, 10));

    // Assert
    assertThat(result).isFalse();
  }

  @Test
  public void test_inputEqualToStart_shouldBeFalse() throws Exception {
    // Arrange
    CustomAgendaWeekView view = mock(CustomAgendaWeekView.class);
    view.startDate = LocalDate.of(2007, 06, 10);
    view.endDate = LocalDate.of(2007, 06, 16);
    doCallRealMethod().when(view).isTargetBetween(any(DateTime.class));

    // Act
    boolean result = view.isTargetBetween(new DateTime(2007, 06, 10, 12, 23, 10));

    // Assert
    assertThat(result).isTrue();
  }

  @Test
  public void test_inputEqualToEnd_shouldBeFalse() throws Exception {
    // Arrange
    CustomAgendaWeekView view = mock(CustomAgendaWeekView.class);
    view.startDate = LocalDate.of(2007, 06, 10);
    view.endDate = LocalDate.of(2007, 06, 16);
    doCallRealMethod().when(view).isTargetBetween(any(DateTime.class));

    // Act
    boolean result = view.isTargetBetween(new DateTime(2007, 06, 16, 12, 23, 10));

    // Assert
    assertThat(result).isTrue();
  }

  @Test
  public void test_inputAfterEnd_shouldBeFalse() throws Exception {
    // Arrange
    CustomAgendaWeekView view = mock(CustomAgendaWeekView.class);
    view.startDate = LocalDate.of(2007, 06, 10);
    view.endDate = LocalDate.of(2007, 06, 16);
    doCallRealMethod().when(view).isTargetBetween(any(DateTime.class));

    // Act
    boolean result = view.isTargetBetween(new DateTime(2007, 06, 17, 12, 23, 10));

    // Assert
    assertThat(result).isFalse();
  }

  @Test
  public void test_inputNullTarget_shouldThrow() throws Exception {
    // Arrange
    CustomAgendaWeekView view = mock(CustomAgendaWeekView.class);
    view.startDate = LocalDate.of(2007, 06, 10);
    view.endDate = LocalDate.of(2007, 06, 16);
    doCallRealMethod().when(view).isTargetBetween(any(DateTime.class));

    // Act
    boolean result = false;
    try {
      result = view.isTargetBetween(null);
    } catch (Exception e) {
      assertThat(e).hasMessage("target == null");
    }

    // Assert
    assertThat(result).isFalse();
  }

  @Test
  public void test_startNull_shouldReturnFalse() throws Exception {
    // Arrange
    CustomAgendaWeekView view = mock(CustomAgendaWeekView.class);
    //view.startDate = LocalDate.of(2007, 06, 10);
    view.endDate = LocalDate.of(2007, 06, 16);
    doCallRealMethod().when(view).isTargetBetween(any(DateTime.class));

    // Act
    boolean result = view.isTargetBetween(new DateTime(2007, 06, 13, 12, 23, 10));

    // Assert
    assertThat(result).isFalse();
  }

  @Test
  public void test_endNull_shouldReturnFalse() throws Exception {
    // Arrange
    CustomAgendaWeekView view = mock(CustomAgendaWeekView.class);
    view.startDate = LocalDate.of(2007, 06, 10);
//    view.endDate = LocalDate.of(2007, 06, 16);
    doCallRealMethod().when(view).isTargetBetween(any(DateTime.class));

    // Act
    boolean result = view.isTargetBetween(new DateTime(2007, 06, 13, 12, 23, 10));

    // Assert
    assertThat(result).isFalse();
  }

}