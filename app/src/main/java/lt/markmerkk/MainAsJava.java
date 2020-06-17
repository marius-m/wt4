package lt.markmerkk;

import static javafx.application.Application.launch;

public class MainAsJava {

    static {
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.jnu.encoding", "UTF-8");
        final String tmpPath = System.getProperty("java.io.tmpdir");
//        System.out.println("Tmp path: " + tmpPath);
//        byte[] ptext = tmpPath.getBytes(ISO_8859_1);
//        String newTmpPath = new String(ptext, UTF_8);
//        System.setProperty("java.io.tmpdir", newTmpPath);
    }

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.jnu.encoding", "UTF-8");
        launch(Main.class);
    }
}
