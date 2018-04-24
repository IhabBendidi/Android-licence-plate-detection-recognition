package org.tensorflow.demo;
import android.webkit.URLUtil;

public class openTokConfig {
    // *** Fill the following variables using your own Project info from the OpenTok dashboard  ***
    // ***                      https://dashboard.tokbox.com/projects                           ***

    // Replace with your OpenTok API key
    public static final String API_KEY = "46073712";
    // Replace with a generated Session ID
    public static final String SESSION_ID = "2_MX40NjA3MzcxMn5-MTUyMzY4MTE5MjAxOH5kYTZiRDJxcVJvcE5ERXJtWENqSEwzWDB-fg";
    // Replace with a generated token (from the dashboard or using an OpenTok server SDK)
    public static final String TOKEN = "T1==cGFydG5lcl9pZD00NjA3MzcxMiZzaWc9NmQwY2RhNmE3YTNmZjdjNjE1ZTc0NTE0MDU5ODhlZWI2OGQzZjQzYTpzZXNzaW9uX2lkPTJfTVg0ME5qQTNNemN4TW41LU1UVXlNelk0TVRFNU1qQXhPSDVrWVRaaVJESnhjVkp2Y0U1RVJYSnRXRU5xU0V3eldEQi1mZyZjcmVhdGVfdGltZT0xNTIzNjgxMjc5Jm5vbmNlPTAuMzg4NjYyOTM3MTc1NTc0MyZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNTI2MjczMjc4JmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9";

    /*                           ***** OPTIONAL *****
     If you have set up a server to provide session information replace the null value
     in CHAT_SERVER_URL with it.

     For example: "https://yoursubdomain.com"
    */
    public static final String CHAT_SERVER_URL = null;
    public static final String SESSION_INFO_ENDPOINT = CHAT_SERVER_URL + "/session";


    // *** The code below is to validate this configuration file. You do not need to modify it  ***

    public static String webServerConfigErrorMessage;
    public static String hardCodedConfigErrorMessage;

    public static boolean areHardCodedConfigsValid() {
        if (openTokConfig.API_KEY != null && !openTokConfig.API_KEY.isEmpty()
                && openTokConfig.SESSION_ID != null && !openTokConfig.SESSION_ID.isEmpty()
                && openTokConfig.TOKEN != null && !openTokConfig.TOKEN.isEmpty()) {
            return true;
        }
        else {
            hardCodedConfigErrorMessage = "API KEY, SESSION ID and TOKEN in OpenTokConfig.java cannot be null or empty.";
            return false;
        }
    }

    public static boolean isWebServerConfigUrlValid(){
        if (openTokConfig.CHAT_SERVER_URL == null || openTokConfig.CHAT_SERVER_URL.isEmpty()) {
            webServerConfigErrorMessage = "CHAT_SERVER_URL in OpenTokConfig.java must not be null or empty";
            return false;
        } else if ( !( URLUtil.isHttpsUrl(openTokConfig.CHAT_SERVER_URL) || URLUtil.isHttpUrl(openTokConfig.CHAT_SERVER_URL)) ) {
            webServerConfigErrorMessage = "CHAT_SERVER_URL in OpenTokConfig.java must be specified as either http or https";
            return false;
        } else if ( !URLUtil.isValidUrl(openTokConfig.CHAT_SERVER_URL) ) {
            webServerConfigErrorMessage = "CHAT_SERVER_URL in OpenTokConfig.java is not a valid URL";
            return false;
        } else {
            return true;
        }
    }
}
