package davideberdin.goofing.utilities;

import android.support.annotation.Nullable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger
{
    private static String Report = "";

    // Easy logger on terminal
    public static void Log(String activityName, String message){
        Log.d("Goofing", activityName + ": " + message);
    }

    // Create system to log every single action
    public static void WriteOnReport(String activity, String message){
        Report += "[" + getCurrentTimeStamp() + "] ";
        Report += activity + " - " + message + "\n";
    }

    public static String GetReport(){
        return Report;
    }

    public static void SetReport(String loadedReport){
        Report = loadedReport;
    }

    /**
     * @return dd-MM-yyyy HH:mm:ss format date as string
     */
    @Nullable
    private static String getCurrentTimeStamp(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String currentTimeStamp = dateFormat.format(new Date()); // Find todays date

            return currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }
}
