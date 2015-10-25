package lt.markmerkk.controllers;

import javafx.scene.Scene;
import javafx.stage.Stage;
import lt.markmerkk.controllers.interfaces.IStageWrapper;

/**
 * Created by mariusmerkevicius on 10/25/15.
 * Wraps {@link javafx.stage.Stage} using {@link StageWrapper} interface.
 *
 * Mainly used for better testing functionality.
 */
public class StageWrapper implements IStageWrapper {
    Stage stage;

    public StageWrapper(Stage stage) {
        if (stage == null)
            throw new IllegalArgumentException("No stage provided!");
        this.stage = stage;
    }

    @Override
    public Stage getStage() {
        return this.stage;
    }

    @Override
    public void setScene(Scene scene) {
        this.stage.setScene(scene);
    }

    @Override
    public void setTitle(String title) {
        this.stage.setTitle(title);
    }

    @Override
    public void setWidth(double width) {
        this.stage.setWidth(width);
    }

    @Override
    public void setHeight(double height) {
        this.stage.setHeight(height);
    }

    @Override
    public double getWidth() {
        return this.stage.getWidth();
    }

    @Override
    public double getHeight() {
        return this.stage.getHeight();
    }

    @Override
    public void show() {
        this.stage.show();
    }
}
