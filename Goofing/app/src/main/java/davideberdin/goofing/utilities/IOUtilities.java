package davideberdin.goofing.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class IOUtilities {

    public static ArrayList<String> audioFiles = new ArrayList<>();
    public static ArrayList<String> audioFilesName = new ArrayList<>();

    private static SharedPreferences reportSP;
    private static SharedPreferences audioFilesSP;
    private static SharedPreferences audioFilesNameSP;

    public static void writeUserAudio(Context context) throws IOException {
        if (audioFilesSP == null)
            audioFilesSP = context.getSharedPreferences(Constants.SHARED_PREFERENCES_RECORDED_AUDIO_NAME, 0);

        if (audioFilesNameSP == null)
            audioFilesNameSP = context.getSharedPreferences(Constants.SHARED_PREFERENCES_RECORDED_AUDIO_NAME_LIST, 0);

        if (audioFiles.size() == 0 || audioFilesName.size() == 0)
            return;

        JSONArray files = new JSONArray();
        for (String val: audioFiles) {
            files.put(val);
        }

        JSONArray names = new JSONArray();
        for (String val: audioFilesName) {
            names.put(val);
        }

        SharedPreferences.Editor filesEditor = audioFilesSP.edit();
        filesEditor.putString("files", files.toString());

        SharedPreferences.Editor namesEditor = audioFilesNameSP.edit();
        namesEditor.putString("names", names.toString());

        filesEditor.apply();
        namesEditor.apply();
    }

    public static void readUserAudio(Context context) throws IOException, ClassNotFoundException, JSONException {
        if (audioFilesSP == null)
            audioFilesSP = context.getSharedPreferences(Constants.SHARED_PREFERENCES_RECORDED_AUDIO_NAME, 0);

        if (audioFilesNameSP == null)
            audioFilesNameSP = context.getSharedPreferences(Constants.SHARED_PREFERENCES_RECORDED_AUDIO_NAME_LIST, 0);

        String filesString = audioFilesSP.getString("files", "");
        JSONArray files = new JSONArray(filesString);
        for (int i = 0; i < files.length(); i++) {
            if (!audioFiles.contains((String) files.get(i)))
                audioFiles.add((String) files.get(i));
        }

        String namesString = audioFilesNameSP.getString("names", "");
        JSONArray names = new JSONArray(namesString);
        for (int i = 0; i < names.length(); i++) {
            if (!audioFilesName.contains((String) names.get(i)))
                audioFilesName.add((String) names.get(i));
        }
    }

    public static void writeReport(Context context) {
        if (reportSP == null)
            reportSP = context.getSharedPreferences(Constants.REPORT_SHARED_PREFERENCES, 0);

        SharedPreferences.Editor reportEditor = reportSP.edit();
        reportEditor.putString("report", Logger.GetReport());

        reportEditor.apply();
    }

    public static void readReport(Context context) {
        if (reportSP == null)
            reportSP = context.getSharedPreferences(Constants.REPORT_SHARED_PREFERENCES, 0);

        String loadedReport = reportSP.getString("report", "");
        Logger.SetReport(loadedReport);
    }
}
