package lt.markmerkk.utils;

import com.airhacks.afterburner.views.FXMLView;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import lt.markmerkk.afterburner.InjectorNoDI;
import lt.markmerkk.listeners.Destroyable;

/**
 * Created by mariusmerkevicius on 1/6/16.
 * Handles tab hiding/showing with new views
 */
public class HiddenTabsController {

  TabPane tabPane;

  /**
   * Method to prepare tabs for hidden magic (nicer name for workaround)
   * Source: https://gist.github.com/twasyl/7fc08b5843964823e36b
   * @param tabPane provided tab pane
   */
  public void prepare(TabPane tabPane) {
    this.tabPane = tabPane;
    if (tabPane == null) return;
    Platform.runLater(() -> {
      tabPane.getTabs().addListener((ListChangeListener) change -> {
        final StackPane header = (StackPane) tabPane.lookup(".tab-header-area");
        if (header != null) {
          if (tabPane.getTabs().size() == 1) header.setPrefHeight(0);
          else header.setPrefHeight(-1);
        }
      });
      Tab mockTab = new Tab();
      tabPane.getTabs().add(mockTab);
      tabPane.getTabs().remove(mockTab);
    });
  }

  /**
   * Adds a view to the closeable tab
   * @param view provided view
   */
  public void addCloseableTab(FXMLView view, String title) {
    if (view == null) return;
    if (tabPane == null) throw new IllegalArgumentException("Prepare was not called!");
    Tab newTab = new Tab(title);
    newTab.setContent(view.getView());
    tabPane.getTabs().add(newTab);
    tabPane.getSelectionModel().select(newTab);
    newTab.setOnClosed(new EventHandler<Event>() {
      @Override public void handle(Event event) {
        InjectorNoDI.forget(view.getPresenter());
      }
    });
  }

}
