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
public class LocalIssueBuilderExtractParentPrefixTest {

    IssueType fakeIssueType = mock(IssueType.class);
    Issue fakeParent = mock(Issue.class);
    LocalIssueBuilder builder = new LocalIssueBuilder();

    @Test
    public void nullParent_emptyDescription() throws Exception {
        // Arrange
        // Act
        String result = builder.extractParentPrefix(null);

        // Assert
        assertEquals("", result);
    }

    @Test
    public void emptyParent_validDescription() throws Exception {
        // Arrange
        // Act
        String result = builder.extractParentPrefix(fakeParent);

        // Assert
        assertEquals("(...):", result);
    }

    @Test
    public void onlyKey_validDescription() throws Exception {
        // Arrange
        when(fakeParent.getKey()).thenReturn("PARENT-1");

        // Act
        String result = builder.extractParentPrefix(fakeParent);

        // Assert
        assertEquals("(PARENT-1):", result);
    }

    @Test
    public void emptyKey_validDescription() throws Exception {
        // Arrange
        when(fakeParent.getKey()).thenReturn("");

        // Act
        String result = builder.extractParentPrefix(fakeParent);

        // Assert
        assertEquals("(...):", result);
    }

    @Test
    public void onlySummary_validDescription() throws Exception {
        // Arrange
        when(fakeParent.getSummary()).thenReturn("fake_summary");

        // Act
        String result = builder.extractParentPrefix(fakeParent);

        // Assert
        assertEquals("(fake_summary):", result);
    }

    @Test
    public void summaryEmpty_validDescription() throws Exception {
        // Arrange
        when(fakeParent.getSummary()).thenReturn("");

        // Act
        String result = builder.extractParentPrefix(fakeParent);

        // Assert
        assertEquals("(...):", result);
    }

    @Test
    public void validParent_validDescription() throws Exception {
        // Arrange
        when(fakeParent.getSummary()).thenReturn("fake_summary");
        when(fakeParent.getKey()).thenReturn("PARENT-1");

        // Act
        String result = builder.extractParentPrefix(fakeParent);

        // Assert
        assertEquals("(PARENT-1:fake_summary):", result);
    }

}