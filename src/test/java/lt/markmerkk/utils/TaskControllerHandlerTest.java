package lt.markmerkk.utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lt.mm.entities.Project;
import lt.mm.entities.Task;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TaskControllerHandlerTest {

    private TaskController taskController;

    @Before
    public void setUp() throws Exception {
        taskController = new TaskController(mock(TaskController.ResourceListener.class));
    }

//    @Test
//    public void testNull() throws Exception {
//        assertNull(taskController.handle(null));
//        verify(taskController.listener, never()).onDataChange();
//        verify(taskController.listener, never()).onNewProject(any(Project.class));
//        verify(taskController.listener, never()).onNewTask(any(Task.class));
//    }

    @Test
    public void testEmpty() throws Exception {
        assertNull(taskController.handle(""));
        verify(taskController.listener, never()).onDataChange();
        verify(taskController.listener, never()).onNewProject(any(Project.class));
        verify(taskController.listener, never()).onNewTask(any(Task.class));
    }

    @Test
    public void testListener() throws Exception {
        taskController.listener = null;
        assertNull(taskController.handle(""));
    }

    @Test
    public void testNullProjects() throws Exception {
        when(taskController.listener.getProjects()).thenReturn(null);
        assertNull(taskController.handle(""));
    }

    @Test
    public void testNullTasks() throws Exception {
        when(taskController.listener.getTasks()).thenReturn(null);
        assertNull(taskController.handle(""));
    }

    @Test
    public void testTaskAndProjectDoesNotExist() throws Exception {
        ObservableList<Project> projects = FXCollections.observableArrayList();
        when(taskController.listener.getProjects()).thenReturn(projects);
        ObservableList<Task> tasks = FXCollections.observableArrayList();
        when(taskController.listener.getTasks()).thenReturn(tasks);

        assertNotNull(taskController.handle("TT-11"));
        verify(taskController.listener, atLeastOnce()).onNewTask(any(Task.class));
        verify(taskController.listener, atLeastOnce()).onNewProject(any(Project.class));
        verify(taskController.listener, atLeastOnce()).onDataChange();
    }

    @Test
    public void testTaskDoesNotExist() throws Exception {
        ObservableList<Project> projects = FXCollections.observableArrayList();
        projects.add(new Project("TT"));
        when(taskController.listener.getProjects()).thenReturn(projects);
        ObservableList<Task> tasks = FXCollections.observableArrayList();
        when(taskController.listener.getTasks()).thenReturn(tasks);

        assertNotNull(taskController.handle("TT-11"));
        verify(taskController.listener, atLeastOnce()).onNewTask(any(Task.class));
        verify(taskController.listener, never()).onNewProject(any(Project.class));
        verify(taskController.listener, atLeastOnce()).onDataChange();
    }

    @Test
    public void testTaskAlreadyExist() throws Exception {
        ObservableList<Project> projects = FXCollections.observableArrayList();
        projects.add(new Project("TT"));
        when(taskController.listener.getProjects()).thenReturn(projects);
        ObservableList<Task> tasks = FXCollections.observableArrayList();
        tasks.add(new Task("TT-11"));
        when(taskController.listener.getTasks()).thenReturn(tasks);

        // This should not add any more tasks or projects
        taskController.handle("TT-11");
        verify(taskController.listener, never()).onNewTask(any(Task.class));
        verify(taskController.listener, never()).onNewProject(any(Project.class));
        verify(taskController.listener, never()).onDataChange();
    }

}