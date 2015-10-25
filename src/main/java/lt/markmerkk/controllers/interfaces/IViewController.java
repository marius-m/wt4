package lt.markmerkk.controllers.interfaces;

import javafx.scene.Scene;

/**
 * Created by mariusmerkevicius on 10/25/15.
 * Represents basic logic for the controller.
 * <p/>
 * Provides life-cycle events.
 */
public interface IViewController {
    /**
     * A helper method to help set up base controller.
     * @param listener basic navigation listener, used to link navigation with basic controller.
     * @param scene scene instance passed down to controller
     */
    void setup(IViewNavigationController listener, Scene scene);

    /**
     * Returns scene that holds the controller.
     * This is mainly used for the {@link IViewNavigationController} controller
     * @return scene
     */
    Scene masterScene();

    /**
     * Callback when controller is created.
     * This is called on the first time when controller is initialized.
     *
     * @param data additional data passed in.
     *             Might be null!
     */
    void create(Object data);

    /**
     * Callback when resume is called.
     * This should occur when controller was in the stack sleeping, and old one was poped out.
     */
    void resume();

    /**
     * Callback when pause on controller is called.
     * This should occur, if the controller is active, and new one is pushed on the stack.
     */
    void pause();

    /**
     * Callback when controller is destroyed.
     * Should occur when controller is pushed out of the stack.
     */
    void destroy();
}
