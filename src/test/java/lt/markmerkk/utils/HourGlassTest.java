package lt.markmerkk.utils;

import org.joda.time.DateTimeUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HourGlassTest {
    @Test
    public void testName() throws Exception {
        HourGlass hourGlass = new HourGlass();
        DateTimeUtils.setCurrentMillisFixed(111);
        hourGlass.setListener(new HourGlass.Listener() {

            @Override
            public void onStart(long start, long end, long delay) {
                assertEquals(111, start);
                assertEquals(111, end);
                assertEquals(0, delay);
            }

            @Override
            public void onStop(long start, long end, long delay) {
                assertEquals(111, start);
                assertEquals(333, end);
                assertEquals(222, delay);
            }

            @Override
            public void onTick(long start, long end, long delay) {
                assertEquals(111, start);
                assertEquals(333, end);
                assertEquals(222, delay);
            }
        });
        hourGlass.start();
        DateTimeUtils.setCurrentMillisFixed(333);
        hourGlass.stop();
    }
}