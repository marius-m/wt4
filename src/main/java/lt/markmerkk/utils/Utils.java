package lt.markmerkk.utils;

import java.util.ArrayList;

/**
 * Created by mariusm on 10/27/14.
 */
public class Utils {

    public static boolean isEmpty(String string) {
        return !(string != null && string.length() > 0);
    }

    public static boolean isArrayEmpty(ArrayList<String> array) {
        if (array == null)
            return true;
        if (array.size() == 0)
            return true;
        return false;
    }
}
