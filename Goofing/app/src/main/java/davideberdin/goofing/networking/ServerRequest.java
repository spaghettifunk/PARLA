package davideberdin.goofing.networking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.UUID;

import davideberdin.goofing.R;
import davideberdin.goofing.controllers.User;
import davideberdin.goofing.fragments.TestPronunciationFragment;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.Debug;
import davideberdin.goofing.utilities.Logger;
import davideberdin.goofing.utilities.Recording;
import edu.cmu.pocketsphinx.Decoder;

public class ServerRequest
{
    public boolean sendData = true;

    private ProgressDialog progressDialog;
    private Activity currentActivity;

    /**
     * Server request - Params in this order: params[0] = Title, params[1] = Message
     * @param activity
     * @param params
     */
    public ServerRequest(Activity activity, String... params) {
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

    public void dismissProgress(){
        progressDialog.cancel();    // GRAZIE DI ESISTERE!!!!!!!!
        progressDialog.dismiss();
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
    public void recordingAudioInBackground(final Context context, final GetCallback callback) {
        final String audioFilename = "recorded_" + UUID.randomUUID().toString();
        if (Debug.debugging) {
            TestPronunciationFragment.recognizer.startListening(Constants.SENTENCES_SEARCH, 10000);
            Recording.startRecording(context, audioFilename);
        }

        this.progressDialog.setButton("Stop", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    InputStream inStream;
                    if (Debug.debugging) {
                        Recording.stopRecording();
                        TestPronunciationFragment.recognizer.stop();
                        inStream = context.openFileInput(audioFilename);
                    } else {
                        inStream = context.getResources().openRawResource(R.raw.test_audio);
                    }

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    BufferedInputStream in = new BufferedInputStream(inStream);

                    int read;
                    byte[] buff = new byte[1024];
                    while ((read = in.read(buff)) > 0) {
                        out.write(buff, 0, read);
                    }
                    out.flush();

                    byte[] recordedAudio = out.toByteArray();

                    dismissProgress();

                    callback.done(recordedAudio);
                } catch (Exception e) {
                    Logger.Log(Constants.CONNECTION_ACTIVITY, "Error in recordingAudioIBackground()\n" + e.getMessage());
                } finally
                {
                    if(Debug.debugging) {
                        File audioFile = context.getFileStreamPath(audioFilename);
                        audioFile.delete();
                    }
                }
            }
        });

        // Start Recording here
        this.progressDialog.show();
    }

    public void sendRecordedAudioToServer(User loggedUser, byte[] fileAudioByte, String currentSentence, GetCallback callback) {
        this.progressDialog.show();

        NetworkingTask networkingTask = new NetworkingTask(callback, this.progressDialog);
        networkingTask.execute(Constants.NETWORKING_HANDLE_RECORDED_VOICE, loggedUser, fileAudioByte, currentSentence);
    }
}
