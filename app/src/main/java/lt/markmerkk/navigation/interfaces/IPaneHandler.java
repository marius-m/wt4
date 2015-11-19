package lt.markmerkk.navigation.interfaces;

import javafx.scene.layout.Pane;

/**
 * Created by mariusmerkevicius on 10/25/15.
 * Responsible for creating and showing view elements in {@link IViewNavigationController}
 */
public interface IPaneHandler {
    void init(Pane pane);
    void show();
}
