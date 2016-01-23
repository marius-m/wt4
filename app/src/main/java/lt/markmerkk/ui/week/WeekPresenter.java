package lt.markmerkk.ui.week;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import jfxtras.internal.scene.control.skin.agenda.AgendaWeekSkin;
import jfxtras.scene.control.agenda.Agenda;
import lt.markmerkk.jira.TaskExecutor3;
import lt.markmerkk.listeners.Destroyable;
import lt.markmerkk.listeners.IPresenter;
import lt.markmerkk.storage2.BasicLogStorage;
import lt.markmerkk.storage2.IDataListener;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.ui.interfaces.UpdateListener;
import org.joda.time.DateTime;

/**
 * Created by mariusmerkevicius on 12/5/15.
 * Represents the presenter to display the log list
 */
public class WeekPresenter implements Initializable, Destroyable, IPresenter, TaskExecutor3.Listener {
  @Inject BasicLogStorage storage;

  @FXML VBox mainContainer;
  Agenda agenda;

  Agenda.AppointmentImplLocal[] appointments;
  UpdateListener updateListener;
  TaskExecutor3 asyncExecutor;

  @Override public void initialize(URL location, ResourceBundle resources) {
    storage.register(storageListener);
    agenda = new Agenda();
    agenda.setLocale(new java.util.Locale("de"));
		agenda.setSkin(new AgendaWeekSkin(agenda));
//		agenda.setSkin(new AgendaDaySkin(agenda));
		agenda.setAllowDragging(false);
		agenda.setAllowResize(false);
    agenda.editAppointmentCallbackProperty().set(agendaCallbackListener);
    mainContainer.getChildren().add(agenda);
    asyncExecutor = new TaskExecutor3();
    asyncExecutor.setListener(this);
    asyncExecutor.onStart();
    asyncExecutor.executeInBackground(updateRunnable);
  }

  public void setUpdateListener(UpdateListener updateListener) {
    this.updateListener = updateListener;
  }

  @PreDestroy
  @Override
  public void destroy() {
    asyncExecutor.onStop();
    storage.unregister(storageListener);
  }

  //region Listeners

  IDataListener<SimpleLog> storageListener = new IDataListener<SimpleLog>() {
    @Override
    public void onDataChange(ObservableList<SimpleLog> data) {
      if (asyncExecutor.isLoading())
        asyncExecutor.cancel();
      asyncExecutor.executeInBackground(updateRunnable);
    }
  };

  Callback<Agenda.Appointment, Void> agendaCallbackListener = new Callback<Agenda.Appointment, Void>() {
    @Override
    public Void call(final Agenda.Appointment appointment) {
      if (updateListener == null) return null;
      if (!(appointment instanceof AppointmentSimpleLog)) return null;
      ContextMenu contextMenu = new ContextMenu();
      MenuItem updateItem = new MenuItem("Update");
      updateItem.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent e) {
          if (updateListener == null) return;
          updateListener.onUpdate(((AppointmentSimpleLog) appointment).getSimpleLog());
        }
      });
      MenuItem deleteItem = new MenuItem("Delete");
      deleteItem.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent e) {
          if (updateListener == null) return;
          updateListener.onDelete(((AppointmentSimpleLog) appointment).getSimpleLog());
        }
      });
      MenuItem cloneItem = new MenuItem("Clone");
      cloneItem.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent e) {
          if (updateListener == null) return;
          updateListener.onClone(((AppointmentSimpleLog) appointment).getSimpleLog());
        }
      });
      contextMenu.getItems().addAll(updateItem, deleteItem, cloneItem);
      agenda.setContextMenu(contextMenu);
      return null;
    }
  };

  //endregion

  //region Runnables

  Runnable updateRunnable = new Runnable() {
    @Override
    public void run() {
      long start = System.currentTimeMillis();
      appointments = new Agenda.AppointmentImplLocal[storage.getData().size()];
      for (int i = 0; i < storage.getData().size(); i++) {
        SimpleLog simpleLog = storage.getData().get(i);
        DateTime startTime = new DateTime(simpleLog.getStart());
        DateTime endTime = new DateTime(simpleLog.getEnd());
        Agenda.AppointmentGroup group = null;
        switch (simpleLog.getStateImageUrl()) {
          case "/red.png":
            group = agenda.appointmentGroups().get(0);
            break;
          case "/yellow.png":
            group = agenda.appointmentGroups().get(10);
            break;
          case "/green.png":
            group = agenda.appointmentGroups().get(13);
            break;
        }
        appointments[i] = new AppointmentSimpleLog(simpleLog)
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
            .withAppointmentGroup(group)
            .withSummary(simpleLog.getTask()+"\n"+simpleLog.getComment());
      }
    }
  };

  @Override
  public void onLoadChange(boolean loading) {
    if (!loading) {
      agenda.appointments().clear();
      agenda.appointments().addAll(appointments);
    }
  }

  //endregion

}