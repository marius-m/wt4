package lt.markmerkk;

import com.threerings.getdown.data.EnvConfig;
import com.threerings.getdown.data.SysProps;
import com.threerings.getdown.launcher.Getdown;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.threerings.getdown.Log.log;

public class GetDownLocalApp {
    /**
     * The main entry point of the Getdown launcher application.
     */
    public static void main(String[] argv) {
        try {
            start(argv);
        } catch (Exception e) {
            log.warning("main() failed.", e);
        }
    }

    /**
     * Runs Getdown as an application, using the arguments supplie as {@code argv}.
     *
     * @return the {@code Getdown} instance that is running. {@link Getdown#start} will have been
     * called on it.
     * @throws Exception if anything goes wrong starting Getdown.
     */
    public static Getdown start(String[] argv) throws Exception {
        List<EnvConfig.Note> notes = new ArrayList<>();
        EnvConfig envc = EnvConfig.create(argv, notes);
        if (envc == null) {
            if (!notes.isEmpty()) for (EnvConfig.Note n : notes) System.err.println(n.message);
            else System.err.println("Usage: java -jar getdown.jar [app_dir] [app_id] [app args]");
            System.exit(-1);
        }

        // pipe our output into a file in the application directory
        if (!SysProps.noLogRedir()) {
            File logFile = new File(envc.appDir, "launcher.log");
            try {
                PrintStream logOut = new PrintStream(
                        new BufferedOutputStream(new FileOutputStream(logFile)), true);
                System.setOut(logOut);
                System.setErr(logOut);
            } catch (IOException ioe) {
                log.warning("Unable to redirect output to '" + logFile + "': " + ioe);
            }
        }

        // report any notes from reading our env config, and abort if necessary
        boolean abort = false;
        for (EnvConfig.Note note : notes) {
            switch (note.level) {
                case INFO:
                    log.info(note.message);
                    break;
                case WARN:
                    log.warning(note.message);
                    break;
                case ERROR:
                    log.error(note.message);
                    abort = true;
                    break;
            }
        }
        if (abort) System.exit(-1);

        // record a few things for posterity
        log.info("------------------ VM Info ------------------");
        log.info("-- OS Name: " + System.getProperty("os.name"));
        log.info("-- OS Arch: " + System.getProperty("os.arch"));
        log.info("-- OS Vers: " + System.getProperty("os.version"));
        log.info("-- Java Vers: " + System.getProperty("java.version"));
        log.info("-- Java Home: " + System.getProperty("java.home"));
        log.info("-- User Name: " + System.getProperty("user.name"));
        log.info("-- User Home: " + System.getProperty("user.home"));
        log.info("-- Cur dir: " + System.getProperty("user.dir"));
        log.info("---------------------------------------------");

        Getdown app = new GetDownLocal(envc);
        app.start();
        return app;
    }
}
