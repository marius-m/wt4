package lt.markmerkk.utils;

import org.junit.Test;

/**
 * Created by mariusm on 10/27/14.
 */
public class SimpleTest {
    @Test
    public void testSimple() throws Exception {
        String testString = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n" +
                "\"http://www.w3.org/TR/html4/loose.dtd\">\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "<head>\n" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
                "<title>Android.1.1</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <script type=\"text/javascript\" src=\"https://track.adform.net/serving/scripts/trackpoint/\"></script>\n" +
                "    <script type=\"text/javascript\">\n" +
                "        var adfJson = {\n" +
                "            \"version\": \"1.0\",\n" +
                "            \"tracker\": \"Android.1.1\",\n" +
                "            \"event\": \"MOBILE_APP_UPDATE\",\n" +
                "            \"data\": {\n" +
                "    \"extinfo\": [\n" +
                "        \"a1\",\n" +
                "        \"com.adform.CreativeSpace\",\n" +
                "        1,\n" +
                "        \"1.0\"\n" +
                "    ],\n"+
                "    \"application_package_name\": \"com.adform.CreativeSpace\",\n" +
                "    \"application_tracking_enabled\": true,\n" +
                "    \"format\": \"json\",\n" +
                "    \"bundle_short_version\": \"1\",\n" +
                "    \"auto_publish\": false,\n" +
                "    \"advertiser_id\": \"8A982B48-D062-44A6-A605-F1CA54C2C830\",\n" +
                "    \"bundle_version\": \"1.0\",\n" +
                "    \"facebook_api\": \"2.2\",\n" +
                "    \"advertiser_tracking_enabled\": true,\n" +
                "    \"attribution\": \"F1CA54C2C830\",\n" +
                "    \"sdk\": \"android\",\n" +
                "    \"event\": \"CUSTOM_APP_EVENTS\"\n" +
                "}\n" +
                "        };\n" +
                "        adf.post(255982,'AppName|Update', adfJson);\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
        System.out.println(testString);
    }

}
