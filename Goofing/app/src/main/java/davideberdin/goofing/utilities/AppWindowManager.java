package davideberdin.goofing.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.view.View;

import java.util.concurrent.Executor;

import davideberdin.goofing.R;
import davideberdin.goofing.networking.GetCallback;
import davideberdin.goofing.networking.NetworkingTask;


public class AppWindowManager
{
    private static AppWindowManager ourInstance = new AppWindowManager();

    public static AppWindowManager getInstance() {
        return ourInstance;
    }

    private AppWindowManager() { }

    public static void showErrorMessage(final Activity activity, String message)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finishAffinity();
            }
        });
        dialogBuilder.show();
    }
}
