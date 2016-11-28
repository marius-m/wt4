package lt.markmerkk.entities;

import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.IssueType;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author mariusmerkevicius
 * @since 2016-11-28
 */
public class LocalIssueBuilderExtractDescriptionTest {

    IssueType fakeIssueType = mock(IssueType.class);
    Issue fakeParent = mock(Issue.class);
    Issue fakeIssue = mock(Issue.class);
    LocalIssueBuilder builder = new LocalIssueBuilder();

    @Test
    public void null_emptyDescription() throws Exception {
        // Arrange
        // Act
        String result = builder.extractDescription(null);

        // Assert
        assertEquals("", result);
    }

    @Test
    public void valid_notSubtask_validDescription() throws Exception {
        // Arrange
        when(fakeIssueType.isSubtask()).thenReturn(false);
        when(fakeIssue.getIssueType()).thenReturn(fakeIssueType);
        when(fakeIssue.getSummary()).thenReturn("fake_description");

        // Act
        String result = builder.extractDescription(fakeIssue);

        // Assert
        assertEquals("fake_description", result);
    }

    @Test
    public void valid_isSubtaskNotAvailable_validDescription() throws Exception {
        // Arrange
        when(fakeIssue.getSummary()).thenReturn("fake_description");

        // Act
        String result = builder.extractDescription(fakeIssue);

        // Assert
        assertEquals("fake_description", result);
    }

    @Test
    public void valid_subtask_parentValid_validDescription() throws Exception {
        // Arrange
        when(fakeIssueType.isSubtask()).thenReturn(true);
        when(fakeIssue.getIssueType()).thenReturn(fakeIssueType);
        when(fakeIssue.getSummary()).thenReturn("fake_description");
        when(fakeIssue.getParent()).thenReturn(fakeParent);
        when(fakeParent.getKey()).thenReturn("PARENT-1");
        when(fakeParent.getSummary()).thenReturn("fake_parent_summary");

        // Act
        String result = builder.extractDescription(fakeIssue);

        // Assert
        assertEquals("(PARENT-1:fake_parent_summary):fake_description", result);
    }

    // todo : check if there is no subtype entity

}