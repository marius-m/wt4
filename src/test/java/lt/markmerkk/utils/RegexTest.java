package lt.markmerkk.utils;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * Created by mariusm on 10/27/14.
 */
public class RegexTest {
    @Test
    public void testSimple() throws Exception {
        assertEquals("Nick", parseRegex("[Nn]ick", "zero Nick smart"));
        assertEquals("nick", parseRegex("[Nn]ick", "zero nick smart"));
    }

    private String parseRegex(String regex, String message) {
        Pattern pattern =
                Pattern.compile(regex);
        Matcher matcher =
                pattern.matcher(message.trim());
        if (matcher.find())
            return matcher.group();
        return null;
    }
}
