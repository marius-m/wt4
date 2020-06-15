package lt.markmerkk;

import static javafx.application.Application.launch;

public class MainAsJava {

    static {
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.jnu.encoding", "UTF-8");
    }

    public static void main(String[] args) {
        launch(Main.class);
    }
}
