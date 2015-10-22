package davideberdin.goofing.utilities;

import android.util.Log;

public class Logger
{
    // Easy logger on termina
    public static void Log(String activityName, String message){
        Log.d("Goofing", activityName + ": " + message);
    }
}
