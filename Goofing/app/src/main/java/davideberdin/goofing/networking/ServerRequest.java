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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import davideberdin.goofing.R;
import davideberdin.goofing.controllers.User;
import davideberdin.goofing.fragments.TestPronunciationFragment;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.Debug;
import davideberdin.goofing.utilities.IOUtilities;
import davideberdin.goofing.utilities.Logger;
import davideberdin.goofing.utilities.Recording;
import edu.cmu.pocketsphinx.Decoder;

public class ServerRequest {
    public boolean sendData = true;

    private ProgressDialog progressDialog;
    private Activity currentActivity;

    /**
     * Server request - Params in this order: params[0] = Title, params[1] = Message
     *
     * @param activity
     * @param params
     */
    public ServerRequest(Activity activity, String... params) {
        this.currentActivity = activity;

        this.progressDialog = new ProgressDialog(currentActivity);
        this.progressDialog.setIndeterminate(true);

        // For recording part
        if (params.length > 0) {
            String title = params[0];
            String message = params[1];
            this.progressDialog.setTitle(title);
            this.progressDialog.setMessage(message);

        } else {
            this.progressDialog.setTitle("Processing");
            this.progressDialog.setMessage("Please wait...");
        }

        this.progressDialog.setCancelable(false);
        this.progressDialog.setCanceledOnTouchOutside(false);
    }

    public void dismissProgress() {
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

    public void fetchPhonemesInBackground(User user, byte[] audioFile, GetCallback callback) {
        this.progressDialog.show();
        NetworkingTask networkingTask = new NetworkingTask(callback, progressDialog);
        networkingTask.execute(Constants.NETWORKING_FETCH_PHONEMES, user, audioFile);
    }

    @SuppressWarnings("deprecation")
    public void recordingAudioInBackground(final Context context, String currentSentence, final GetCallback callback) {
        final String audioFilename = "recorded_" + currentSentence; // overwrite the file for each sentence

        // add sentence when recording
        String sentenceTV = currentSentence.replace("_", " ");
        sentenceTV = Character.toString(sentenceTV.charAt(0)).toUpperCase() + sentenceTV.substring(1);
        if (!IOUtilities.audioFiles.contains(sentenceTV))
            IOUtilities.audioFiles.add(sentenceTV);

        if (Debug.debugging) {
            Recording.startRecording(context, audioFilename);
        }

        this.progressDialog.setButton("Stop", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    InputStream inStream;
                    if (Debug.debugging) {
                        Recording.stopRecording();
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

                    // save recorded audio here!
                    String directory = context.getFilesDir() + File.separator + Constants.RECORDED_AUDIO_PATH;
                    File file = new File(directory);
                    if (!file.exists())
                        file.mkdir();

                    String filePath = file.getAbsolutePath() + File.separator + audioFilename + ".wav";
                    FileOutputStream fileOutputStreamos = new FileOutputStream(filePath);
                    fileOutputStreamos.write(recordedAudio);
                    fileOutputStreamos.flush();
                    fileOutputStreamos.close();

                    dismissProgress();

                    callback.done(recordedAudio);
                } catch (Exception e) {
                    Logger.Log(Constants.CONNECTION_ACTIVITY, "Error in recordingAudioIBackground()\n" + e.getMessage());
                } finally {
                    if (Debug.debugging) {
                        File audioFile = context.getFileStreamPath(audioFilename);
                        audioFile.delete();
                    }
                }
            }
        });

        this.progressDialog.setCancelable(false);
        this.progressDialog.setCanceledOnTouchOutside(false);

        // Start Recording here
        this.progressDialog.show();
    }

    public void sendRecordedAudioToServer(User loggedUser, byte[] fileAudioByte, String predictedPhonemes, String currentSentence, GetCallback callback) {
        NetworkingTask networkingTask = new NetworkingTask(callback, this.progressDialog);
        networkingTask.execute(Constants.NETWORKING_HANDLE_RECORDED_VOICE, loggedUser, fileAudioByte, predictedPhonemes, currentSentence);
    }

    public void fetchHistoryDataInBackground(String username, String currentSentence, String vowels, final GetCallback callback) {
        this.progressDialog.show();

        NetworkingTask networkingTask = new NetworkingTask(callback, this.progressDialog);
        networkingTask.execute(Constants.NETWORKING_FETCH_HISTORY, username, currentSentence, vowels);
    }
}
