package davideberdin.goofing.libraries;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;

import davideberdin.goofing.controllers.User;
import davideberdin.goofing.fragments.TestPronunciationFragment;
import davideberdin.goofing.networking.ServerRequest;
import davideberdin.goofing.utilities.AppWindowManager;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.Logger;
import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

public class RecognitionTask extends AsyncTask<Void, Void, Exception> implements RecognitionListener
{
    private Activity currActivity;
    private ProgressDialog progress;
    private ServerRequest request;
    private User loggedUser;

    public RecognitionTask(Activity activity, User loggedUser){
        this.currActivity = activity;
        this.loggedUser = loggedUser;

        this.progress = new ProgressDialog(activity);
        this.progress.setMessage("Initializing dictionary...");
        this.progress.setCancelable(false);
        this.progress.setCanceledOnTouchOutside(false);
        this.progress.show();
    }

    public void setServerRequest(ServerRequest request) { this.request = request; }

    // Recognizer initialization is a time-consuming and it involves IO,
    // so we execute it in async task
    @Override
    protected Exception doInBackground(Void... params) {
        try {
            Assets assets = new Assets(this.currActivity);
            File assetDir = assets.syncAssets();
            setupRecognizer(assetDir);
        } catch (IOException e) {
            return e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Exception result) {
        if (result != null) {
            AppWindowManager.showErrorMessage(this.currActivity, "Failed to init recognizer " + result);
        } else
        {
            this.progress.cancel();
            this.progress.dismiss();
        }
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        if (TestPronunciationFragment.recognizer == null) {
            TestPronunciationFragment.recognizer = defaultSetup()
                    .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                            //.setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                    .setDictionary(new File(assetsDir, "1240.dic"))

                            // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                    .setRawLogDir(assetsDir)

                            // Threshold to tune for keyphrase to balance between false alarms and misses
                    .setKeywordThreshold(1e-45f)

                            // Use context-independent phonetic search, context-dependent is too slow for mobile
                    .setBoolean("-allphone_ci", true)

                    .getRecognizer();
            TestPronunciationFragment.recognizer.addListener(this);

            File mymodel = new File(assetsDir, "test.dmp");
            TestPronunciationFragment.recognizer.addNgramSearch(Constants.SENTENCES_SEARCH, mymodel);

            // Phonetic search
            // File phoneticModel = new File(assetsDir, "en-phone.dmp");
            // recognizer.addAllphoneSearch(Constants.PHONE_SEARCH, phoneticModel);
        }
    }

    //region CMU POCKETSPHINX
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;
    }

    //This callback is called when we stop the recognizer.
    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            TestPronunciationFragment.recognizedPhonemes = hypothesis.getHypstr();

            if (TestPronunciationFragment.recognizedPhonemes.equalsIgnoreCase(this.loggedUser.GetCurrentSentence()))
                return;
        }

        this.request.dismissProgress();
        this.request.sendData = false;
        AppWindowManager.showErrorMessage(this.currActivity, Constants.ERROR_WRONG_WORDS);
    }

    @Override
    public void onError(Exception error) {
        TestPronunciationFragment.recognizer.stop();
        TestPronunciationFragment.recognizer.cancel();

        if (this.request != null) {
            this.request.sendData = false;
            this.request.dismissProgress();
        }

        Logger.Log(this.currActivity.getLocalClassName(), "CMU Error: " + error.getMessage());
    }

    @Override
    public void onTimeout() {
        TestPronunciationFragment.recognizer.stop();
        TestPronunciationFragment.recognizer.cancel();

        if (this.request != null) {
            this.request.sendData = false;
            this.request.dismissProgress();
        }
    }

    //We stop recognizer here to get a final result
    @Override
    public void onEndOfSpeech() { }

    @Override
    public void onBeginningOfSpeech() { TestPronunciationFragment.recognizedPhonemes = ""; }
    //endregion
}
