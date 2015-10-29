package davideberdin.goofing.networking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import davideberdin.goofing.controllers.Recorder;
import davideberdin.goofing.controllers.User;
import davideberdin.goofing.utilities.Constants;

public class ServerRequest
{
    private ProgressDialog progressDialog;
    private Activity currentActivity;

    /**
     * Server request - Params in this order: params[0] = Title, params[1] = Message
     * @param activity
     * @param params
     */
    public ServerRequest(Activity activity, String... params)
    {
        this.currentActivity = activity;

        this.progressDialog = new ProgressDialog(currentActivity);
        this.progressDialog.setIndeterminate(true);

        // For recording part
        if (params.length > 0)
        {
            String title = params[0];
            String message = params[1];
            this.progressDialog.setTitle(title);
            this.progressDialog.setMessage(message);
            this.progressDialog.setCancelable(true);

        } else {
            this.progressDialog.setTitle("Processing");
            this.progressDialog.setMessage("Please wait...");
            this.progressDialog.setCancelable(false);
        }
    }

    public void fetchUserDataInBackgroud(User user, GetCallback callback) {
        this.progressDialog.show();
        NetworkingTask networkingTask = new NetworkingTask(user, callback, progressDialog);
        networkingTask.execute(Constants.NETWORKING_LOGIN_STATE, user);
    }

    public void storeUserDataInBackground(User user, GetCallback callback) {
        this.progressDialog.show();
        NetworkingTask networkingTask = new NetworkingTask(user, callback, progressDialog);
        networkingTask.execute(Constants.NETWORKING_REGISTER_STATE, user);
    }

    @SuppressWarnings("deprecation")
    public void recordingAudioInBackground(Recorder recorder, final GetCallback callback) {
        this.progressDialog.setButton("Stop", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Stop Recording
                // ...

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
                callback.done(null);    // TODO: put the wav file here
            }
        });

        // Start Recording
        // ...

        this.progressDialog.show();
    }

    public void sendRecordedAudioToServer(Object fileAudio, String currentSentence, GetCallback callback)
    {
        this.progressDialog.show();

        //NetworkingTask networkingTask = new NetworkingTask(callback, this.progressDialog);
        //networkingTask.execute(fileAudio, currentSentence);

        this.progressDialog.dismiss();
        callback.done();
    }
}
