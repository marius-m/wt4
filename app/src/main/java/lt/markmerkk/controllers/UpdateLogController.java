package lt.markmerkk.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lt.markmerkk.storage2.SimpleLog;
import lt.markmerkk.storage2.SimpleLogBuilder;
import lt.markmerkk.storage2.jobs.UpdateJob;

public class UpdateLogController extends BaseController {

    @FXML TextField taskInput;
    @FXML TextField startInput;
    @FXML TextField endInput;
    @FXML TextArea commentInput;
    @FXML TextField durationInput;
    @FXML TextField outputError;
    @FXML Button buttonOk;
    @FXML Button buttonCancel;

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
        outputError.setText((updateLog.isError()) ? updateLog.getErrorMessage() : "");
        update();
        updateLock();
    }

    /**
     * Updates locking mechanism. Temoprarily, tasks that have been uploaded cant be edited.
     */
    void updateLock() {
        if (updateLog.getId() == 0) return; // Its local
        taskInput.setDisable(true);
        startInput.setDisable(true);
        endInput.setDisable(true);
        commentInput.setDisable(true);
        durationInput.setDisable(true);
        outputError.setDisable(true);
        buttonOk.setDisable(true);
        outputError.setText("Can't update logs that were uploaded!");
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
