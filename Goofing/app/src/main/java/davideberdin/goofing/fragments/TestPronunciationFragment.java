package davideberdin.goofing.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import davideberdin.goofing.FeedbacksActivity;
import davideberdin.goofing.R;
import davideberdin.goofing.controllers.User;
import davideberdin.goofing.networking.GetCallback;
import davideberdin.goofing.networking.ServerRequest;
import davideberdin.goofing.utilities.AppWindowManager;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.Logger;
import davideberdin.goofing.utilities.UserLocalStore;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;
import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Decoder;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;


public class TestPronunciationFragment extends Fragment implements View.OnClickListener, RecognitionListener {
    //region VARIABLES
    public static SpeechRecognizer recognizer;
    private String recognizedPhonemes = "";
    private ServerRequest recordingRequest = null;

    private View testPronunciationView = null;
    private FloatingActionButton startButton = null;
    private ImageButton listenSentence = null;

    // variables related to the view
    private TextView tvSentence = null;
    private TextView tvPhonemes = null;

    private UserLocalStore userLocalStore = null;
    private User loggedUser = null;
    //endregion

    public TestPronunciationFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

        // TODO: need lot of testing because of the events
        if(recognizer == null)
        {
            final ProgressDialog progress = new ProgressDialog(getActivity());
            progress.setMessage("Initializing app...");
            progress.show();

            // Recognizer initialization is a time-consuming and it involves IO,
            // so we execute it in async task
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
                    } else
                    {
                        progress.cancel();
                        progress.dismiss();
                    }
                }
            }.execute();
        }

        return this.testPronunciationView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    @Override
    public void onClick(View v) {
        recordingRequest = new ServerRequest(this.getActivity(), "Recording", "Say the sentence");

        switch (v.getId()) {
            case R.id.fabStartRecording:
                recordingRequest.recordingAudioInBackground(testPronunciationView.getContext(), new GetCallback() {
                    @Override
                    public void done(Object... params) {
                        // obtained as result
                        byte[] fileAudioByte = (byte[]) params[0];
                        String currentSentence = loggedUser.GetCurrentSentence();

                        ServerRequest request = new ServerRequest(getActivity(), "Analyzing audio", "Please wait...");
                        request.sendRecordedAudioToServer(loggedUser, fileAudioByte, currentSentence, new GetCallback() {
                            @Override
                            public void done(Object... params) {
                                ArrayList<String> phonemes = (ArrayList<String>) params[0];
                                ArrayList<ArrayList<String>> vowelStress = (ArrayList<ArrayList<String>>) params[1];

                                String resultWER = (String) params[2];

                                byte[] pitch_chart_byte = (byte[]) params[3];
                                byte[] vowel_chart_byte = (byte[]) params[4];

                                Intent intent = new Intent(getActivity(), FeedbacksActivity.class);
                                intent.putExtra(Constants.GET_PHONEMES_POST, phonemes);
                                intent.putExtra(Constants.GET_VOWELSTRESS_POST, vowelStress);
                                intent.putExtra(Constants.GET_WER_POST, resultWER);
                                intent.putExtra(Constants.GET_PITCH_CHART_POST, pitch_chart_byte);
                                intent.putExtra(Constants.GET_VOWEL_CHART_POST, vowel_chart_byte);

                                startActivity(intent);
                            }
                        });
                    }
                });
                break;
            case R.id.listenSentence:
                // play audio
                try
                {
                    String fileAudio = ((this.loggedUser.GetCurrentSentence()).toLowerCase()).replace(" ", "_");
                    if (this.loggedUser.GetGender() == "Male")
                        fileAudio = "m_" + fileAudio;
                    else
                        fileAudio = "f_" + fileAudio;

                    int resID = getActivity().getResources().getIdentifier(fileAudio, "raw", getActivity().getPackageName());
                    MediaPlayer mediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), resID);
                    mediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;
    }

    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            this.recognizedPhonemes = hypothesis.getHypstr();
            int score = hypothesis.getBestScore();
        }
    }

    @Override
    public void onBeginningOfSpeech() {
        this.recognizedPhonemes = "";
    }

    /**
     * We stop recognizer here to get a final result
     */
    @Override
    public void onEndOfSpeech() {

    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        recognizer = defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                //.setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                .setDictionary(new File(assetsDir, "1240.dic"))
                // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                .setRawLogDir(assetsDir)

                // Threshold to tune for keyphrase to balance between false alarms and misses
                .setKeywordThreshold(1e-10f)

                // Use context-independent phonetic search, context-dependent is too slow for mobile
                .setBoolean("-allphone_ci", true)

                .getRecognizer();
        recognizer.addListener(this);

        // sentences grammar
//        File sentencesModel = new File(assetsDir, "sentences.gram");
//        recognizer.addGrammarSearch(Constants.SENTENCES_SEARCH, sentencesModel);

        File mymodel = new File(assetsDir, "test.dmp");
        recognizer.addNgramSearch(Constants.SENTENCES_SEARCH, mymodel);

        // Phonetic search
        File phoneticModel = new File(assetsDir, "en-phone.dmp");
        recognizer.addAllphoneSearch(Constants.PHONE_SEARCH, phoneticModel);
    }

    @Override
    public void onError(Exception error) {
        recognizer.stop();
        recognizer.cancel();

        if (recordingRequest != null)
            recordingRequest.dismissProgress();
    }

    @Override
    public void onTimeout() {
        recognizer.stop();
        recognizer.cancel();

        if (recordingRequest != null)
            recordingRequest.dismissProgress();
    }
}
