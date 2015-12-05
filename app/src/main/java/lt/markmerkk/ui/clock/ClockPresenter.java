package lt.markmerkk.ui.clock;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javax.inject.Inject;
import lt.markmerkk.DBProdExecutor;
import lt.markmerkk.RocketService;
import lt.markmerkk.storage2.SimpleIssue;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.storage2.SimpleLogBuilder;
import lt.markmerkk.storage2.jobs.InsertJob;
import lt.markmerkk.utils.hourglass.HourGlass;
import org.joda.time.DateTime;

/**
 * Created by mariusmerkevicius on 12/5/15.
 * Represents the presenter for the clock for logging info.
 */
public class ClockPresenter implements Initializable {

  @Inject DBProdExecutor dbExecutor;
  @Inject HourGlass hourGlass;

  @FXML TextField inputTo;
  @FXML TextField inputFrom;
  @FXML TextField outputDuration;
  //@FXML TextField inputTask;
  @FXML TextArea inputComment;
  @FXML Button buttonClock;
  @FXML Button buttonEnter;
  @FXML Button buttonOpen;
  @FXML Button buttonNew;
  @FXML TextArea outputLogger;
  @FXML ComboBox<SimpleIssue> inputTaskCombo;

  @Override public void initialize(URL location, ResourceBundle resources) {

    // Initializing listeners
    inputFrom.textProperty().addListener(new ChangeListener<String>() {
      @Override public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        //hourGlass.updateTimers(filterDate, inputFrom.getText(), inputTo.getText());
      }
    });

    inputTo.textProperty().addListener(new ChangeListener<String>() {
      @Override public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        //hourGlass.updateTimers(filterDategst, inputFrom.getText(), inputTo.getText());
      }
    });

    // Comment event listeners
    inputComment.setText("");
    inputComment.setOnKeyReleased(new EventHandler<KeyEvent>() {
      public void handle(KeyEvent t) {
        if (t.getCode() == KeyCode.ENTER) {
          logWork();
        }
      }
    });

    // Timer configuration
    buttonClock.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override public void handle(MouseEvent mouseEvent) {
        if (hourGlass.getState() == HourGlass.State.STOPPED) hourGlass.start();
        else hourGlass.stop();
        updateUI();
      }
    });
    buttonEnter.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override public void handle(MouseEvent mouseEvent) {
        logWork();
      }
    });
  }

  //region Convenience

  /**
   * Updates UI depending on {@link HourGlass} state
   */
  private void updateUI() {
    boolean disableElement = (hourGlass.getState() == HourGlass.State.STOPPED);
    inputFrom.setDisable(disableElement);
    inputTo.setDisable(disableElement);
    inputTaskCombo.setDisable(disableElement);
    inputComment.setDisable(disableElement);
    outputDuration.setDisable(disableElement);
    buttonEnter.setDisable(disableElement);
    buttonOpen.setDisable(disableElement);
    buttonNew.setDisable(disableElement);
  }

  /**
   * Gathers data and logs work to a database
   */
  private void logWork() {
    try {
      if (hourGlass.getState() == HourGlass.State.STOPPED)
        throw new IllegalArgumentException("Please run timer first!");
      if (!hourGlass.isValid())
        throw new IllegalArgumentException("Error calculating time!");
      SimpleLog log = new SimpleLogBuilder(DateTime.now().getMillis())
          .setStart(hourGlass.reportStart().getMillis())
          .setEnd(hourGlass.reportEnd().getMillis())
          .setTask(inputTaskCombo.getEditor().getText())
          .setComment(inputComment.getText()).build();
      dbExecutor.execute(new InsertJob(SimpleLog.class, log));
      //this.log.info("Saving: "+log.toString());

      // Resetting controls
      inputComment.setText("");
      hourGlass.restart();
      //notifyLogsChanged();
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }

  //endregion

}
