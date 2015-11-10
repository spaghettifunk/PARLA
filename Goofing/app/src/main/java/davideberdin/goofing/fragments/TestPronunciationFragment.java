package davideberdin.goofing.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import davideberdin.goofing.FeedbacksActivity;
import davideberdin.goofing.R;
import davideberdin.goofing.controllers.User;
import davideberdin.goofing.networking.GetCallback;
import davideberdin.goofing.networking.ServerRequest;
import davideberdin.goofing.utilities.AppWindowManager;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.UserLocalStore;
import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Decoder;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.PocketSphinx;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;


public class TestPronunciationFragment extends Fragment implements View.OnClickListener, RecognitionListener
{
    //region VARIABLES
    public static SpeechRecognizer recognizer;

    private View testPronunciationView = null;
    private FloatingActionButton startButton = null;
    private ImageButton listenSentence = null;

    // variables related to the view
    private TextView tvSentence = null;
    private TextView tvPhonemes = null;

    private UserLocalStore userLocalStore = null;
    private User loggedUser = null;
    //endregion

    public TestPronunciationFragment() { }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.testPronunciationView = inflater.inflate(R.layout.test_pronunciation_layout, container, false);

        this.startButton = (FloatingActionButton) testPronunciationView.findViewById(R.id.fabStartRecording);
        this.listenSentence = (ImageButton) testPronunciationView.findViewById(R.id.listenSentence);

        // register listener
        this.startButton.setOnClickListener(this);
        this.listenSentence.setOnClickListener(this);

        this.userLocalStore = new UserLocalStore(this.getActivity());
        this.loggedUser = this.userLocalStore.getLoggedUser();

        this.tvSentence = (TextView) testPronunciationView.findViewById(R.id.tpSentence);
        String tmp = this.loggedUser.GetCurrentSentence();
        this.tvSentence.setText(tmp);

        // create phoneme sentence
        this.tvPhonemes = (TextView) testPronunciationView.findViewById(R.id.tpPhoneticSentence);
        tmp = this.loggedUser.GetCurrentPhonetic();
        this.tvPhonemes.setText("/ " + tmp + " /");

        // Setup listener for recognizing phonemes
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(getActivity());
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
                    AppWindowManager.showErrorMessage(getActivity(), "Failed to init recognizer " + result);
                }
            }
        }.execute();

        return this.testPronunciationView;
    }

//    @Override
//    public void onResume(){
//        if (recognizer != null) {
//            recognizer.cancel();
//            recognizer.stop();
//            recognizer.shutdown();
//            recognizer.removeListener(this);
//        }
//    }
//
//    @Override
//    public void onStop(){
//        if (recognizer != null) {
//            recognizer.cancel();
//            recognizer.stop();
//            recognizer.shutdown();
//            recognizer.removeListener(this);
//        }
//    }
//
    @Override
    public void onPause(){
        super.onPause();
        if (recognizer != null) {
            recognizer.shutdown();
            recognizer = null;
        }
    }

    @Override
    public void onClick(View v) {
        ServerRequest recordingRequest = new ServerRequest(this.getActivity(), "Recording", "Say the sentence");

        switch (v.getId())
        {
            case R.id.fabStartRecording:
                recordingRequest.recordingAudioInBackground(new GetCallback()
                {
                    @Override
                    public void done(Object... params)
                    {
                        try
                        {
                            // dismiss
                            // and send it to server
                            // ...

                            // obtained as result
                            Object fileAudio = null; // params[0];      // TODO: put the audiofile here
                            String currentSentence = loggedUser.GetCurrentSentence();

                            InputStream inStream = testPronunciationView.getContext().getResources().openRawResource(R.raw.test_audio); // TODO: edit this line
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            BufferedInputStream in = new BufferedInputStream(inStream);

                            int read;
                            byte[] buff = new byte[1024];
                            while ((read = in.read(buff)) > 0) {
                                out.write(buff, 0, read);
                            }
                            out.flush();
                            byte[] fileAudioByte = out.toByteArray();

                            ServerRequest request = new ServerRequest(getActivity() , "Analyzing audio", "Please wait...");
                            request.sendRecordedAudioToServer(loggedUser, fileAudioByte, currentSentence, new GetCallback()
                            {
                                @Override
                                public void done(Object... params)
                                {
                                    byte[] pitch_chart_byte = (byte[]) params[0];
                                    byte[] vowel_chart_byte = (byte[]) params[1];

                                    Intent intent = new Intent(getActivity(), FeedbacksActivity.class);
                                    intent.putExtra("pitchchart", pitch_chart_byte);
                                    intent.putExtra("vowelchart", vowel_chart_byte);

                                    startActivity(intent);
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case R.id.listenSentence:
                // play audio
                try {
                    // TODO: mapping between R.raw.xxx to sentence
                    int resID = getActivity().getResources().getIdentifier("test_audio", "raw", getActivity().getPackageName());

                    MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), resID);
                    mediaPlayer.start();
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {
        String result = recognizer.getSearchName();

        // TODO: check if result is correct
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {

    }

    @Override
    public void onResult(Hypothesis hypothesis) {

    }

    @Override
    public void onError(Exception e) {
        AppWindowManager.showErrorMessage(getActivity(), e.getMessage());
    }

    @Override
    public void onTimeout() {
        // what can I do here ?
        recognizer.cancel();
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        recognizer = defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))

                        // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                .setRawLogDir(assetsDir)

                        // Threshold to tune for keyphrase to balance between false alarms and misses
                .setKeywordThreshold(1e-45f)

                        // Use context-independent phonetic search, context-dependent is too slow for mobile
                .setBoolean("-allphone_ci", true)

                .getRecognizer();
        recognizer.addListener(this);

        File phoneticModel = new File(assetsDir, "en-phone.dmp");
        recognizer.addAllphoneSearch(Constants.PHONE_SEARCH, phoneticModel);
    }
}
