package davideberdin.goofing.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.view.MotionEvent;
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

    public static void showInfoDialog(final Activity activity, String title, String message)
    {
        AlertDialog.Builder ad = new AlertDialog.Builder(activity);
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        ad.show();
    }
}
