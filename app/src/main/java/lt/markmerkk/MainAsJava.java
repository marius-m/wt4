package lt.markmerkk;

import static javafx.application.Application.launch;

public class MainAsJava {

    static {
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.jnu.encoding", "UTF-8");
        System.setProperty("java.io.tmpdir", "C:\\installer\\tmp");
    }

    public static void main(String[] args) {
        launch(Main.class);
    }
}
