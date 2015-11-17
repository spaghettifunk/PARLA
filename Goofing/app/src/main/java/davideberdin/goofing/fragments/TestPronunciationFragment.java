package davideberdin.goofing.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import davideberdin.goofing.FeedbacksActivity;
import davideberdin.goofing.R;
import davideberdin.goofing.controllers.User;
import davideberdin.goofing.libraries.RecognitionTask;
import davideberdin.goofing.networking.GetCallback;
import davideberdin.goofing.networking.ServerRequest;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.UserLocalStore;

import edu.cmu.pocketsphinx.SpeechRecognizer;

public class TestPronunciationFragment extends Fragment implements View.OnClickListener {
    //region VARIABLES
    public static SpeechRecognizer recognizer;
    public static String recognizedPhonemes = "";
    private ServerRequest recordingRequest = null;
    private RecognitionTask recognitionTask;

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
        String sentence = this.loggedUser.GetCurrentSentence();
        this.tvSentence.setText(sentence);

        // create phoneme sentence
        this.tvPhonemes = (TextView) testPronunciationView.findViewById(R.id.tpPhoneticSentence);
        String phonetic = this.loggedUser.GetCurrentPhonetic();
        this.tvPhonemes.setText("/ " + phonetic + " /");

        // TODO: need lot of testing because of the events
        if (recognizer == null) {
            this.recognitionTask = new RecognitionTask(getActivity(), this.loggedUser);
            this.recognitionTask.execute();
        }

        return this.testPronunciationView;
    }

    //region APP EVENTS
    @Override
    public void onResume() {
        super.onResume();
        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }
    //endregion

    @Override
    public void onClick(View v) {
        this.recordingRequest = new ServerRequest(this.getActivity(), "Recording", "Say the sentence");
        this.recognitionTask.setServerRequest(this.recordingRequest);

        switch (v.getId()) {
            case R.id.fabStartRecording:
                recordingRequest.recordingAudioInBackground(testPronunciationView.getContext(), new GetCallback() {
                    @Override
                    public void done(Object... params) {
                        if (recordingRequest.sendData) {
                            //region SEND DATA
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
                            //endregion
                        }
                    }
                });
                break;
            case R.id.listenSentence:
                // play audio
                try {
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
}
