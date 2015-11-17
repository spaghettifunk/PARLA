package davideberdin.goofing.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import davideberdin.goofing.R;
import davideberdin.goofing.controllers.Tuple;

public class AppWindowManager {
    private static AppWindowManager ourInstance = new AppWindowManager();

    public static AppWindowManager getInstance() {
        return ourInstance;
    }

    private AppWindowManager() {
    }

    public static void showErrorMessage(final Activity activity, String message) {
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

    public static void showInfoDialog(final Activity activity, String title, String message) {
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

    public static void showInfoFeedbacksDialog(final Context context, String sentence) {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.info_feedbacks_dialog);

        Tuple tuple = Constants.meaningExampleMap.get(sentence);
        TextView meaning = (TextView) dialog.findViewById(R.id.infoMeaningTextView);
        meaning.setText(tuple.getFirst());

        TextView example = (TextView) dialog.findViewById(R.id.infoExampleTextView);
        example.setText(tuple.getSecond());

        // if button is clicked, close the custom dialog
        Button dialogButton = (Button) dialog.findViewById(R.id.infoOKButton);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
