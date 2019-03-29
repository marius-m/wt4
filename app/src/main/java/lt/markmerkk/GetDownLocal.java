package lt.markmerkk;

import com.threerings.getdown.data.Application;
import com.threerings.getdown.data.EnvConfig;
import com.threerings.getdown.launcher.Getdown;
import com.threerings.getdown.util.LaunchUtil;
import com.threerings.getdown.util.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.util.ArrayList;

import static com.threerings.getdown.Log.log;

public class GetDownLocal extends Getdown {

    public GetDownLocal(EnvConfig envConfig) {
        super(envConfig);
    }

    @Override
    protected void launch() {
        localLaunch();
    }

    protected void localLaunch() {
        setStep(Application.UpdateInterface.Step.LAUNCH);
        setStatusAsync("m.launching", stepToGlobalPercent(100), -1L, false);

        try {
            javafx.application.Application.launch(Main.class);

            // if we have a UI open and we haven't been around for at least 5 seconds (the default
            // for min_show_seconds), don't stick a fork in ourselves straight away but give our
            // lovely user a chance to see what we're doing
            long uptime = System.currentTimeMillis() - _startup;
            long minshow = _ifc.minShowSeconds * 1000L;
            if (_container != null && uptime < minshow) {
                try {
                    Thread.sleep(minshow - uptime);
                } catch (Exception e) {
                }
            }

            // pump the percent up to 100%
            setStatusAsync(null, 100, -1L, false);
            exit(0);

        } catch (Exception e) {
            log.warning("launch() failed.", e);
        }
    }

    @Override
    protected Container createContainer() {
        // create our user interface, and display it
        if (_frame == null) {
            _frame = new JFrame("");
            _frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent evt) {
                    handleWindowClose();
                }
            });
            // handle close on ESC
            String cancelId = "Cancel"; // $NON-NLS-1$
            _frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), cancelId);
            _frame.getRootPane().getActionMap().put(cancelId, new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    handleWindowClose();
                }
            });
            // this cannot be called in configureContainer as it is only allowed before the
            // frame has been displayed for the first time
            _frame.setUndecorated(_ifc.hideDecorations);
            _frame.setResizable(false);
        } else {
            _frame.getContentPane().removeAll();
        }
        _frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        return _frame.getContentPane();
    }

    @Override
    protected void configureContainer() {
        if (_frame == null) return;

        _frame.setTitle(_ifc.name);

        try {
            _frame.setBackground(new Color(_ifc.background, true));
        } catch (Exception e) {
            log.warning("Failed to set background", "bg", _ifc.background, e);
        }

        if (_ifc.iconImages != null) {
            ArrayList<Image> icons = new ArrayList<>();
            for (String path : _ifc.iconImages) {
                Image img = loadImage(path);
                if (img == null) {
                    log.warning("Error loading icon image", "path", path);
                } else {
                    icons.add(img);
                }
            }
            if (icons.isEmpty()) {
                log.warning("Failed to load any icons", "iconImages", _ifc.iconImages);
            } else {
                _frame.setIconImages(icons);
            }
        }
    }

    @Override
    protected void showContainer() {
        if (_frame != null) {
            _frame.pack();
            //SwingUtil.centerWindow(_frame); Cannot center
            _frame.setVisible(true);
        }
    }

    @Override
    protected void disposeContainer() {
        if (_frame != null) {
            _frame.dispose();
            _frame = null;
        }
    }

    @Override
    protected void showDocument(String url) {
        if (!StringUtil.couldBeValidUrl(url)) {
            // command injection would be possible if we allowed e.g. spaces and double quotes
            log.warning("Invalid document URL.", "url", url);
            return;
        }
        String[] cmdarray;
        if (LaunchUtil.isWindows()) {
            String osName = System.getProperty("os.name", "");
            if (osName.indexOf("9") != -1 || osName.indexOf("Me") != -1) {
                cmdarray = new String[]{
                        "command.com", "/c", "start", "\"" + url + "\""};
            } else {
                cmdarray = new String[]{
                        "cmd.exe", "/c", "start", "\"\"", "\"" + url + "\""};
            }
        } else if (LaunchUtil.isMacOS()) {
            cmdarray = new String[]{"open", url};
        } else { // Linux, Solaris, etc.
            cmdarray = new String[]{"firefox", url};
        }
        try {
            Runtime.getRuntime().exec(cmdarray);
        } catch (Exception e) {
            log.warning("Failed to open browser.", "cmdarray", cmdarray, e);
        }
    }

    @Override
    protected void exit(int exitCode) {
        // if we're running the app in the same JVM, don't call System.exit, but do
        // make double sure that the download window is closed.
        if (invokeDirect()) {
            disposeContainer();
        } else {
            System.exit(exitCode);
        }
    }

    @Override
    protected void fail(String message) {
        super.fail(message);
        // super.fail causes the UI to be created (if needed) on the next UI tick, so we
        // want to wait until that happens before we attempt to redecorate the window
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                // if the frame was set to be undecorated, make window decoration available
                // to allow the user to close the window
                if (_frame != null && _frame.isUndecorated()) {
                    _frame.dispose();
                    Color bg = _frame.getBackground();
                    if (bg != null && bg.getAlpha() < 255) {
                        // decorated windows do not allow alpha backgrounds
                        _frame.setBackground(
                                new Color(bg.getRed(), bg.getGreen(), bg.getBlue()));
                    }
                    _frame.setUndecorated(false);
                    showContainer();
                }
            }
        });
    }

    protected JFrame _frame;
}
