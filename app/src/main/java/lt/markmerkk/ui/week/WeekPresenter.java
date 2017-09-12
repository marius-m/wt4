package lt.markmerkk.ui.week;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import jfxtras.scene.control.agenda.Agenda;
import lt.markmerkk.*;
import lt.markmerkk.entities.SimpleLog;
import lt.markmerkk.ui.interfaces.UpdateListener;
import lt.markmerkk.ui_2.LogStatusView;
import lt.markmerkk.utils.tracker.ITracker;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.schedulers.JavaFxScheduler;
import rx.schedulers.Schedulers;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by mariusmerkevicius on 12/5/15.
 * Represents the presenter to display the log list
 */
public class WeekPresenter implements Initializable, AgendaView {
  public static final Logger logger = LoggerFactory.getLogger(WeekPresenter.class);
  @Inject LogStorage storage;
  @Inject ITracker tracker;

  @FXML StackPane jfxWeekContainer;
  Agenda agenda;

  Agenda.AppointmentImplLocal[] appointments;
  UpdateListener updateListener;

  // fixme : VERY VERY WEIRD AND DIRTY IMPLEMENTATION OF SKIN WORKAROUND :/
  public static DateTime targetDate = null;
  private CustomAgendaWeekView weekSkin;
  private LogStatusView logStatusView;

  private AgendaPresenter agendaPresenter;

  public WeekPresenter() {

  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    Main.Companion.getComponent().presenterComponent().inject(this);
    tracker.sendView(GAStatics.INSTANCE.getVIEW_WEEK());
    storage.register(storageListener);

    logStatusView = new LogStatusView(jfxWeekContainer);

    targetDate = new DateTime(storage.getTargetDate());
    agenda = new Agenda();
    agenda.setLocale(new java.util.Locale("en"));
    weekSkin = new CustomAgendaWeekView(agenda);
    agenda.setSkin(weekSkin);
		agenda.setAllowDragging(false);
		agenda.setAllowResize(false);
    agenda.editAppointmentCallbackProperty().set(agendaCallbackListener);
    jfxWeekContainer.getChildren().add(agenda);
    agendaPresenter = new AgendaPresenterImpl(
            this,
            agenda.appointmentGroups().get(0),
            agenda.appointmentGroups().get(10),
            agenda.appointmentGroups().get(13),
            Schedulers.computation(),
            JavaFxScheduler.getInstance()
    );
    agendaPresenter.onAttach();
    agendaPresenter.reloadView(storage.getData());
    agenda.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        if (agenda.selectedAppointments().size() > 0) {
          SimpleLog selectedLog = ((AppointmentSimpleLog) agenda.selectedAppointments().get(0)).getSimpleLog();
          StackPane.setAlignment(logStatusView.getView(), logStatusView.suggestGravityByLogWeekDay(selectedLog));
          logStatusView.showLogWithId(selectedLog.get_id());
        } else {
          logStatusView.showLogWithId(null);
        }
      }
    });
  }

  public void setUpdateListener(UpdateListener updateListener) {
    this.updateListener = updateListener;
  }

  @PreDestroy
  public void destroy() {
    agendaPresenter.onDetatch();
    storage.unregister(storageListener);
  }

  //region Listeners

  IDataListener<SimpleLog> storageListener = new IDataListener<SimpleLog>() {
    @Override
    public void onDataChange(@NotNull List<? extends SimpleLog> data) {
      targetDate = new DateTime(storage.getTargetDate());
      agendaPresenter.reloadView(storage.getData());
    }
  };

  Callback<Agenda.Appointment, Void> agendaCallbackListener = new Callback<Agenda.Appointment, Void>() {
    @Override
    public Void call(final Agenda.Appointment appointment) {
      if (updateListener == null) return null;
      if (!(appointment instanceof AppointmentSimpleLog)) return null;
      ContextMenu contextMenu = new ContextMenu();
      MenuItem updateItem = new MenuItem(Translation.getInstance().getString("general_update"),
          new ImageView(new Image(getClass().getResource("/update_2.png").toString())));
      updateItem.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent e) {
          if (updateListener == null) return;
          updateListener.onUpdate(((AppointmentSimpleLog) appointment).getSimpleLog());
        }
      });
      MenuItem deleteItem = new MenuItem(Translation.getInstance().getString("general_delete"),
          new ImageView(new Image(getClass().getResource("/delete_2.png").toString())));
      deleteItem.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent e) {
          if (updateListener == null) return;
          updateListener.onDelete(((AppointmentSimpleLog) appointment).getSimpleLog());
        }
      });
      MenuItem cloneItem = new MenuItem(Translation.getInstance().getString("general_clone"),
          new ImageView(new Image(getClass().getResource("/clone_2.png").toString())));
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

  @Override
  public void updateAgenda(@NotNull List<? extends Agenda.AppointmentImplLocal> appointments) {
    boolean needRefresh = !weekSkin.isTargetBetween(storage.getTargetDate());
    agenda.appointments().clear();
    agenda.appointments().addAll(appointments);
    if (needRefresh) {
      weekSkin = new CustomAgendaWeekView(agenda);
      agenda.setSkin(weekSkin);
    }
  }

  //endregion

}
