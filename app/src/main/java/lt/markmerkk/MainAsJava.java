package lt.markmerkk;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import static java.nio.charset.StandardCharsets.*;

import static javafx.application.Application.launch;

public class MainAsJava {

    static {
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.jnu.encoding", "UTF-8");
        final String tmpPath = System.getProperty("java.io.tmpdir");
        System.out.println("Tmp path: " + tmpPath);
        byte[] ptext = tmpPath.getBytes(ISO_8859_1);
        String newTmpPath = new String(ptext, UTF_8);
        System.setProperty("java.io.tmpdir", newTmpPath);
        System.out.println("Rebind tmp path: " + System.getProperty("java.io.tmpdir"));
    }

    public static void main(String[] args) {
        launch(Main.class);
    }
}
