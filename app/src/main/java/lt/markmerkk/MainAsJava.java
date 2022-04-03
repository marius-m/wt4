package lt.markmerkk;

import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.logging.LogManager;

import static javafx.application.Application.launch;

public class MainAsJava {
    static {
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.install();
    }

    public static void main(String[] args) {
        launch(Main.class);
    }
}
