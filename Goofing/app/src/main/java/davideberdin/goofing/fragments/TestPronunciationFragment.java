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
import davideberdin.goofing.networking.GetCallback;
import davideberdin.goofing.networking.ServerRequest;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.Debug;
import davideberdin.goofing.utilities.UserLocalStore;


public class TestPronunciationFragment extends Fragment implements View.OnClickListener {
    //region VARIABLES
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

        return this.testPronunciationView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        ServerRequest recordingRequest = new ServerRequest(this.getActivity(), "Recording", "Say the sentence");

        switch (v.getId()) {
            case R.id.fabStartRecording:
                recordingRequest.recordingAudioInBackground(testPronunciationView.getContext(), new GetCallback() {
                    @Override
                    public void done(Object... params) {
                        // obtained as result
                        byte[] fileAudioByte = (byte[]) params[0];      // TODO: put the audiofile here
                        String currentSentence = loggedUser.GetCurrentSentence();

                        if (Debug.debugging == false) {
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
}
