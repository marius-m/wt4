package lt.markmerkk;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

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
        String defaultCharacterEncoding = System.getProperty("file.encoding");
        System.out.println("defaultCharacterEncoding by property: " + defaultCharacterEncoding);
        System.out.println("defaultCharacterEncoding by code: " + getDefaultCharEncoding());
        System.out.println("defaultCharacterEncoding by charSet: " + Charset.defaultCharset());

        System.setProperty("file.encoding", "UTF-16");
        System.out.println("defaultCharacterEncoding by property after updating file.encoding : " + System.getProperty("file.encoding"));
        System.out.println("defaultCharacterEncoding by code after updating file.encoding : " + getDefaultCharEncoding());
        System.out.println("defaultCharacterEncoding by java.nio.Charset after updating file.encoding : " + Charset.defaultCharset());

        launch(Main.class);
    }

    public static String getDefaultCharEncoding(){
        byte [] bArray = {'w'};
        InputStream is = new ByteArrayInputStream(bArray);
        InputStreamReader reader = new InputStreamReader(is);
        final String defaultCharacterEncoding = reader.getEncoding();
        return defaultCharacterEncoding;
    }
}
