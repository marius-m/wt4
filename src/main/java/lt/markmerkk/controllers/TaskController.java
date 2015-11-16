package lt.markmerkk.controllers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.ObservableList;
import lt.markmerkk.storage.entities.Project;
import lt.markmerkk.storage.entities.Task;
import lt.markmerkk.utils.Utils;

/**
 * Created by mariusm on 11/2/14.
 */
public class TaskController {

    public static final String SEPERATOR = "-";
    protected ResourceListener listener;

    public TaskController(ResourceListener listener) {
        this.listener = listener;
    }

    public Long handle(String message) {
        if (listener == null)
            return null;
        if (listener.getTasks() == null || listener.getProjects() == null)
            return null;
        if (Utils.isEmpty(message))
            return null;
        ObservableList<Task> tasks = listener.getTasks();
        ObservableList<Project> projects = listener.getProjects();
        String title = inspectAndFormTitle(message);
        String titleName = splitName(title);
        if (title == null || titleName == null)
            return null;
        Task oldTask = Task.getTaskWithTitle(tasks, title);
        if (oldTask == null) {
            Task newTask = null;
            Project oldProject = Project.getProjectWithTitle(projects, titleName);
            if (oldProject == null) {
                Long newProjectId = listener.onNewProject(new Project(titleName));
                if (newProjectId != null)
                    newTask = new Task(title);
            } else {
                newTask = new Task(title);
            }
            Long taskId = null;
            if (newTask != null)
                 taskId = listener.onNewTask(newTask);
            listener.onDataChange();
            return taskId;
        }
        return oldTask.getId();
    }

    public Long handle(Task remoteTask) {
        if (listener == null)
            return null;
        if (listener.getTasks() == null || listener.getProjects() == null)
            return null;
        ObservableList<Task> tasks = listener.getTasks();
        ObservableList<Project> projects = listener.getProjects();
        String title = inspectAndFormTitle(remoteTask.getTitle());
        String titleName = splitName(title);
        if (title == null || titleName == null)
            return null;
        Task oldTask = Task.getTaskWithTitle(tasks, title);
        if (oldTask == null) {
            Task newTask = null;
            Project oldProject = Project.getProjectWithTitle(projects, titleName);
            if (oldProject == null) {
                Long newProjectId = listener.onNewProject(new Project(titleName));
                if (newProjectId != null)
                    newTask = new Task(title);
            } else {
                newTask = new Task(title);
            }
            Long taskId = null;
            if (newTask != null) {
                newTask.setDetail(remoteTask.getDetail());
                newTask.setLink(remoteTask.getLink());
                taskId = listener.onNewTask(newTask);
            }
            listener.onDataChange();
            return taskId;
        } else {
            oldTask.setDetail(remoteTask.getDetail());
            oldTask.setLink(remoteTask.getLink());
            listener.onUpdateTask(oldTask);
            listener.onDataChange();
        }
        return oldTask.getId();
    }

    public static String splitName(String message) {
        if (Utils.isEmpty(message))
            return null;
        message = message.replaceAll("\\n", "");
        Pattern pattern =
                Pattern.compile("[a-zA-Z]+");
        Matcher matcher =
                pattern.matcher(message.trim());
        if (matcher.find()) {
            String found = matcher.group();
            found = found.toUpperCase();
            found = found.trim();
            if (found.length() == 0)
                return null;
            return found;
        }
        return null;
    }

    /**
     * Inspects id for a valid type
     * @param message
     */
    public static String inspectAndFormTitle(String message) {
        if (Utils.isEmpty(message))
            return null;
        message = message.replaceAll("\\n", "");
        Pattern pattern =
                Pattern.compile("[a-zA-Z]+(-)?[0-9]+");
        Matcher matcher =
                pattern.matcher(message.trim());
        if (matcher.find()) {
            String found = matcher.group();
            found = found.toUpperCase();
            found = found.trim();
            if (!found.contains(SEPERATOR))
                found = insertMissingSeperator(found);
            if (found.length() == 0)
                return null;
            return found;
        }
        return null;
    }

    /**
     * Insers a missing seperator if it is missing.
     * @param message message that should be altered
     * @return altered message with seperator attached to its proper spot.
     */
    public static String insertMissingSeperator(String message) {
        if (message == null)
            return null;
        Pattern pattern =
                Pattern.compile("[a-zA-Z]+[^0-9]");
        Matcher matcher =
                pattern.matcher(message.trim());
        if (matcher.find()) {
            message = message.substring(0, matcher.end())
                    +SEPERATOR
                    +message.substring(matcher.end(), message.length());
        }
        return message;
    }

    public interface ResourceListener {
        public ObservableList<Task> getTasks();
        public ObservableList<Project> getProjects();
        public Long onNewProject(Project newProject);
        public Long onNewTask(Task newTask);
        public Long onUpdateTask(Task updateTask);
        public void onDataChange();
    }

}
