package lt.markmerkk.ui.week;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import javafx.util.Callback;
import javax.inject.Inject;
import jfxtras.internal.scene.control.skin.agenda.AgendaWeekSkin;
import jfxtras.scene.control.agenda.Agenda;
import jfxtras.util.NodeUtil;
import lt.markmerkk.storage2.BasicLogStorage;
import lt.markmerkk.storage2.IDataListener;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.ui.interfaces.UpdateListener;
import org.joda.time.DateTime;

/**
 * Created by mariusmerkevicius on 12/5/15.
 * Represents the presenter to display the log list
 */
public class WeekPresenter implements Initializable {
  @Inject BasicLogStorage storage;

  @FXML VBox mainContainer;
  Agenda agenda;

  UpdateListener updateListener;

  @Override public void initialize(URL location, ResourceBundle resources) {
    storage.register(new IDataListener<SimpleLog>() {
      @Override
      public void onDataChange(ObservableList<SimpleLog> data) {
        update();
      }
    });
    agenda = new Agenda();
    agenda.setLocale(new java.util.Locale("de"));
		agenda.setSkin(new AgendaWeekSkin(agenda));
//		agenda.setSkin(new AgendaDaySkin(agenda));
		agenda.setAllowDragging(false);
		agenda.setAllowResize(false);
    agenda.editAppointmentCallbackProperty().set(new Callback<Agenda.Appointment, Void>() {
      @Override
      public Void call(final Agenda.Appointment appointment) {
        if (updateListener == null) return null;
        if (!(appointment instanceof AppointmentSimpleLog)) return null;
        updateListener.onUpdate(((AppointmentSimpleLog) appointment).getSimpleLog());
        return null;
      }
    });


    mainContainer.getChildren().add(agenda);
    update();
  }

  private void update() {
    agenda.appointments().clear();
    for (final SimpleLog simpleLog : storage.getData()) {
      DateTime startTime = new DateTime(simpleLog.getStart());
      DateTime endTime = new DateTime(simpleLog.getEnd());
      agenda.appointments().add(
          new AppointmentSimpleLog(simpleLog)
              .withStartLocalDateTime(
                  LocalDateTime.of(
                      startTime.getYear(),
                      startTime.getMonthOfYear(),
                      startTime.getDayOfMonth(),
                      startTime.getHourOfDay(),
                      startTime.getMinuteOfHour()))
              .withEndLocalDateTime(
                  LocalDateTime.of(
                      endTime.getYear(),
                      endTime.getMonthOfYear(),
                      endTime.getDayOfMonth(),
                      endTime.getHourOfDay(),
                      endTime.getMinuteOfHour()))
              .withSummary(simpleLog.getComment())
      );
    }
  }

  public void setUpdateListener(UpdateListener updateListener) {
    this.updateListener = updateListener;
  }
}
