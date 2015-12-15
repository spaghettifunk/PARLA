package davideberdin.goofing.networking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.animation.DecelerateInterpolator;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import davideberdin.goofing.controllers.User;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.IOUtilities;
import davideberdin.goofing.utilities.Logger;
import davideberdin.goofing.utilities.Recording;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

public class ServerRequest {
    public boolean sendData = true;

    private ProgressDialog progressDialog;

    /**
     * Server request - Params in this order: params[0] = Title, params[1] = Message
     *
     * @param activity
     * @param params
     */
    public ServerRequest(Activity activity, String... params) {
        Activity currentActivity = activity;

        this.progressDialog = new ProgressDialog(currentActivity);
        this.progressDialog.setIndeterminateDrawable(new SmoothProgressDrawable.Builder(activity.getApplicationContext())
                .color(Color.BLUE)
                .interpolator(new DecelerateInterpolator())
                .sectionsCount(4)
                .separatorLength(8) //You should use Resources#getDimensionPixelSize
                .strokeWidth(8f)    //You should use Resources#getDimension
                .speed(2f)  //2 times faster
                .progressiveStartSpeed(2)
                .progressiveStopSpeed(3.4f)
                .reversed(false)
                .mirrorMode(false)
                .progressiveStart(true)
                .build());

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

    public void setProgressMessage(String message){
        this.progressDialog.setMessage(message);
    }

    public void fetchUserDataInBackground(User user, GetCallback callback) {
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

        this.progressDialog.setCancelable(true);
        this.progressDialog.setCanceledOnTouchOutside(true);

        final String audioFilename = "recorded_" + currentSentence; // overwrite the file for each sentence
        final String sentenceTV = currentSentence.replace("_", " ");

        Recording.startRecording(context, audioFilename);

        //region recording - accept current recording
        this.progressDialog.setButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    // add sentence when the user accept it
                    String sentence = Character.toString(sentenceTV.charAt(0)).toUpperCase() + sentenceTV.substring(1);
                    if (!IOUtilities.audioFilesName.contains(sentence))
                        IOUtilities.audioFilesName.add(sentence);

                    InputStream inStream;

                    Recording.stopRecording();
                    inStream = context.openFileInput(audioFilename);

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
                    FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                    fileOutputStream.write(recordedAudio);
                    fileOutputStream.flush();
                    fileOutputStream.close();

                    dismissProgress();

                    callback.done(recordedAudio);
                } catch (Exception e) {
                    Logger.Log(Constants.CONNECTION_ACTIVITY, "Error in recordingAudioIBackground()\n" + e.getMessage());
                }
//                  finally {
//                    File audioFile = context.getFileStreamPath(audioFilename);
//                    audioFile.delete();
//                }
            }
        });
        //endregion

        this.progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                File audioFile = context.getFileStreamPath(audioFilename);
                audioFile.delete();

                dismissProgress();
            }
        });

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

    // Send report
    public void sendReportDataInBackground(String username, final GetCallback callback){
        this.progressDialog.show();

        NetworkingTask networkingTask = new NetworkingTask(callback, this.progressDialog);
        networkingTask.execute(Constants.NETWORKING_SEND_REPORT, username);
    }
}
