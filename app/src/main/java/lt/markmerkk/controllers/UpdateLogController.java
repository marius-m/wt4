package lt.markmerkk.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lt.markmerkk.storage2.SimpleLogBuilder;
import lt.markmerkk.storage2.entities.SimpleLog;
import lt.markmerkk.storage2.jobs.UpdateJob;
import lt.markmerkk.utils.Utils;
import org.joda.time.DateTime;

public class UpdateLogController extends BaseController {

    @FXML TextField taskInput;
    @FXML TextField startInput;
    @FXML TextField endInput;
    @FXML TextArea commentInput;
    @FXML TextField durationInput;

    private SimpleLog updateLog;

    public UpdateLogController() {
        super();
    }

    @Override
    public void create(Object data) {
        super.create(data);
        updateLog = (SimpleLog) data;

        startInput.setText(updateLog.getLongStart());
        startInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                String newValue) {
                update();
            }
        });
        endInput.setText(updateLog.getLongEnd());
        endInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                String newValue) {
                update();
            }
        });
        commentInput.setText(updateLog.getComment());
        taskInput.setText(updateLog.getTask());
        update();
    }

    @Override void onInternalOutput(String message) { }

    private void update() {
        try {
            updateLog = new SimpleLogBuilder(updateLog)
                .setStart(SimpleLog.longFormat.parseDateTime(startInput.getText()).getMillis())
                .setEnd(SimpleLog.longFormat.parseDateTime(endInput.getText()).getMillis())
                .setTask(taskInput.getText())
                .setComment(commentInput.getText())
                .build();
            durationInput.setText(updateLog.getPrettyDuration());
        } catch (IllegalArgumentException e) {
            durationInput.setText("Error: " + e.getMessage());
        }
    }

    @Override
    public void setupController(BaseControllerDelegate listener, Scene scene, Stage primaryStage) {
        super.setupController(listener, scene, primaryStage);
    }

    @FXML public void saveAndExit() {
        try {
            update();
            executor.execute(new UpdateJob(SimpleLog.class, updateLog));
            UpdateLogController.this.log.info("Updated log! "+updateLog);
            masterListener.popScene();
        } catch (IllegalArgumentException e) {
            UpdateLogController.this.log.info("Error updating."+e.getMessage());
            System.out.println("Cannot save!"+e.getMessage());
        }
    }

    @FXML public void pop() {
        masterListener.popScene();
    }

    @FXML public void delete() {
        //logStorage.delete(updateLog.getId());
        masterListener.popScene();
    }

}
