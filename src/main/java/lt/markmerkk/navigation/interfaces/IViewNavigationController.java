package lt.markmerkk.navigation.interfaces;

/**
 * An interface that helps set up controller and its core navigation events
 */
public interface IViewNavigationController {

    /**
     * Pushes new controller forward in the stack
     * @param sceneXml new controller scene layout
     * @param data additional data passed in. This is optional, null passing is valid.
     * @return new scene
     */
    IViewController pushScene(String sceneXml, Object data);

    /**
     * Pops scene out of the controller stack
     */
    void popScene();
}
