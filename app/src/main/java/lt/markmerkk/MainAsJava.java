package lt.markmerkk;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static javafx.application.Application.launch;

public class MainAsJava {

    static {
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.jnu.encoding", "UTF-8");
        final String tmpPath = System.getProperty("java.io.tmpdir");
        System.out.println("Tmp path: " + tmpPath);
        final ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(tmpPath);
        System.setProperty("java.io.tmpdir", byteBuffer.toString());
        System.out.println("Rebind tmp path: " + System.getProperty("java.io.tmpdir"));
    }

    public static void main(String[] args) {
        launch(Main.class);
    }
}
