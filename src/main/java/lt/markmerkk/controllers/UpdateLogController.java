package lt.markmerkk.controllers;

import java.util.ArrayList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lt.markmerkk.storage.entities.Log;
import lt.markmerkk.storage.entities.Project;
import lt.markmerkk.storage.entities.table.LogTable;
import lt.markmerkk.utils.TaskController;
import org.joda.time.DateTime;

public class UpdateLogController extends BaseController {

    @FXML TextField taskInput;
    @FXML TextField startInput;
    @FXML TextField endInput;
    @FXML TextArea commentInput;
    //@FXML ListView gitInput;
    @FXML TextField durationInput;
    @FXML HBox durationContainer;
    private ObservableList<String> gitLogs;
    private LogTable logToUpdate;
    private ArrayList<Project> projects;

    public UpdateLogController() {
        super();
    }

    @Override
    public void create(Object data) {
        super.create(data);
        projects = projectStorage.readAll();

        logToUpdate = (LogTable) data;
        startInput.setText(logToUpdate.getLongVerbalStart());
        startInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                String newValue) {
                updateDuration();
            }
        });
        endInput.setText(logToUpdate.getLongVerbalEnd());
        endInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                String newValue) {
                updateDuration();
            }
        });
        commentInput.setText(logToUpdate.getComment());
        taskInput.setText(logToUpdate.getCategory());
        updateDuration();
    }

    private void updateDuration() {
        try {
            DateTime startTime = logToUpdate.getLongFormat().parseDateTime(startInput.getText());
            DateTime endTime = logToUpdate.getLongFormat().parseDateTime(endInput.getText());
            if (startTime.isAfter(endTime))
                throw new IllegalArgumentException("Start time cannot be after end time!");
            durationInput.setText(Log.formatDuration(endTime.getMillis() - startTime.getMillis()));
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
            logToUpdate.setComment(commentInput.getText());
            DateTime startTime = logToUpdate.getLongFormat().parseDateTime(startInput.getText());
            DateTime endTime = logToUpdate.getLongFormat().parseDateTime(endInput.getText());
            if (startTime.isAfter(endTime))
                throw new IllegalArgumentException("Start time cannot be after end time!");
            logToUpdate.setStart(startTime.getMillis());
            logToUpdate.setEnd(endTime.getMillis());
            String newTaskName = TaskController.inspectAndFormTitle(taskInput.getText());
            if (newTaskName != null)
                logToUpdate.setCategory(newTaskName);
            logStorage.update(logToUpdate);
            mMasterListener.popScene();
        } catch (IllegalArgumentException e) {
            System.out.println("Cannot save!"+e.getMessage());
        }
    }

    @FXML public void pop() {
        mMasterListener.popScene();
    }

    @FXML public void delete() {
        logStorage.delete(logToUpdate.getId());
        mMasterListener.popScene();
    }

}
