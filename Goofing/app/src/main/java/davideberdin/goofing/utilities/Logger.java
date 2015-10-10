package davideberdin.goofing.utilities;

/**
 * Created by dado on 09/10/15.
 */

import android.util.Log;

public class Logger {
    private static Logger singletonLogger = getLogger();

    private Logger() { }

    // static methods
    public static Logger getLogger() {
        return singletonLogger;
    }

    public static void writeLog(String tagName, String message){
        Log.d(tagName, message);
    }
}
