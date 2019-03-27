package lt.markmerkk;

import com.vinumeris.updatefx.AppDirectory;
import com.vinumeris.updatefx.UpdateFX;

import static javafx.application.Application.launch;

public class Main2 {
    public static void main(String[] args) throws Exception{
        AppDirectory.initAppDir("WT4");
        UpdateFX.bootstrap(Main2.class, AppDirectory.dir(), args);
    }

    public static void realMain(String[] args) {
        launch(Main.class);
    }

}
