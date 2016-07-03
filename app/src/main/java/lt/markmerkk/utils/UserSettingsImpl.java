package lt.markmerkk.utils;

import lt.markmerkk.JiraSearchJQL;
import lt.markmerkk.listeners.WorldEvents;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by mariusmerkevicius on 12/21/15.
 * Controller for holding persistent data
 */
public class UserSettingsImpl implements UserSettings, WorldEvents {
    public static final String HOST = "HOST";
    public static final String USER = "USER";
    public static final String PASS = "PASS";
    public static final String VERSION = "VERSION";
    public static final String ISSUE_JQL = "ISSUE_JQL";

    AdvHashSettings settings;

    // Used settings
    String host;
    String username;
    String password;
    String issueJql;
    int version = -1;

    public UserSettingsImpl() {
        settings = new AdvHashSettings();
    }

    //region Getters / Setters

    @Override
    public String getIssueJql() {
        if (issueJql == null) return JiraSearchJQL.DEFAULT_JQL_USER_ISSUES;
        return issueJql;
    }

    @Override
    public void setIssueJql(String issueJql) {
        this.issueJql = issueJql;
        settings.save();
    }

    @Override
    public String getHost() {
        if (host == null) return "";
        return host;
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String getUsername() {
        if (username == null) return "";
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        if (password == null) return "";
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void setCustom(String key, String value) {
        settings.set(key, value);
        settings.save();
    }

    @Override
    public String getCustom(String key) {
        return settings.get(key);
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public void setVersion(int version) {
        this.version = version;
        settings.save();
    }

    //endregion

    //region World events

    @PostConstruct
    @Override
    public void onStart() {
        settings.load();
        host = settings.get(HOST);
        username = settings.get(USER);
        password = settings.get(PASS);
        String versionString = settings.get(VERSION);
        version = (versionString == null) ? -1 : Integer.parseInt(versionString);
        issueJql = settings.get(ISSUE_JQL);
    }

    @PreDestroy
    @Override
    public void onStop() {
        settings.set(HOST, getHost());
        settings.set(USER, getUsername());
        settings.set(PASS, getPassword());
        settings.set(VERSION, String.valueOf(version));
        settings.set(ISSUE_JQL, getIssueJql());
        settings.save();
    }

    //endregion
}
