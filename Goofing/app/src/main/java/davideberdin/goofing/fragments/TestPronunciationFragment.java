package davideberdin.goofing.fragments;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import davideberdin.goofing.FeedbacksActivity;
import davideberdin.goofing.R;
import davideberdin.goofing.controllers.AlertReceiver;
import davideberdin.goofing.controllers.User;
import davideberdin.goofing.networking.GetCallback;
import davideberdin.goofing.networking.ServerRequest;
import davideberdin.goofing.utilities.AppWindowManager;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.IOUtilities;
import davideberdin.goofing.utilities.UserLocalStore;

public class TestPronunciationFragment extends Fragment implements View.OnClickListener {
    //region VARIABLES
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
        String sentence = this.loggedUser.GetCurrentSentence();
        this.tvSentence.setText(sentence);

        // create phoneme sentence
        this.tvPhonemes = (TextView) testPronunciationView.findViewById(R.id.tpPhoneticSentence);
        String phonetic = this.loggedUser.GetCurrentPhonetic();
        this.tvPhonemes.setText("/ " + phonetic + " /");

        // set notification
        setAlarm();

        return this.testPronunciationView;
    }

    //region APP EVENTS
    @Override
    public void onResume() {
        super.onResume();
        try {
            IOUtilities.readUserAudio(getActivity());
            IOUtilities.readUsageTimestamp(getActivity());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            IOUtilities.writeUserAudio(getActivity());
            IOUtilities.writeUsageTimestamp(getActivity());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            IOUtilities.writeUserAudio(getActivity());
            IOUtilities.writeUsageTimestamp(getActivity());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            IOUtilities.writeUserAudio(getActivity());
            IOUtilities.writeUsageTimestamp(getActivity());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //endregion

    @Override
    public void onClick(View v) {
        this.recordingRequest = new ServerRequest(this.getActivity(), "Recording", "Say the sentence");

        switch (v.getId()) {
            case R.id.fabStartRecording:
                String currentSentence = ((loggedUser.GetCurrentSentence()).toLowerCase()).replace(" ", "_");
                recordingRequest.recordingAudioInBackground(testPronunciationView.getContext(), currentSentence, new GetCallback() {
                    @Override
                    public void done(Object... params) {

                        // TODO: TESTING REQUIRED
                        //region ERROR
                        if (params[0] instanceof String){
                            String error = (String) params[0];

                            if (error.equals(Constants.FAILED_POST)){
                                recordingRequest.dismissProgress();
                                AppWindowManager.showErrorMessage(getActivity(), Constants.FUNNY_ERROR_MESSAGE);
                                return;
                            }
                        }
                        //endregion

                        if (recordingRequest.sendData) {
                            //region SEND DATA
                            final byte[] fileAudioByte = (byte[]) params[0];
                            final String currentSentence = loggedUser.GetCurrentSentence();

                            final ServerRequest request = new ServerRequest(getActivity(), "Analyzing audio", "Please wait...");

                            // Java Service
                            request.fetchPhonemesInBackground(loggedUser, fileAudioByte, new GetCallback() {
                                @Override
                                public void done(Object... params) {

                                    // TODO: TESTING REQUIRED
                                    //region ERROR
                                    if (params[0] instanceof String){
                                        String error = (String) params[0];

                                        if (error.equals(Constants.FAILED_POST)){
                                            recordingRequest.dismissProgress();
                                            AppWindowManager.showErrorMessage(getActivity(), Constants.FUNNY_ERROR_MESSAGE);
                                            return;
                                        }
                                    }
                                    //endregion

                                    // Django request
                                    request.sendRecordedAudioToServer(loggedUser, fileAudioByte, (String) params[0], currentSentence, new GetCallback() {
                                        @Override
                                        public void done(Object... params) {

                                            // TODO: TESTING REQUIRED
                                            //region ERROR
                                            if (params[0] instanceof String){
                                                String error = (String) params[0];

                                                if (error.equals(Constants.FAILED_POST)){
                                                    recordingRequest.dismissProgress();
                                                    AppWindowManager.showErrorMessage(getActivity(), Constants.FUNNY_ERROR_MESSAGE);
                                                    return;
                                                }
                                            }
                                            //endregion

                                            ArrayList<String> phonemes = (ArrayList<String>) params[0];
                                            ArrayList<ArrayList<String>> vowelStress = (ArrayList<ArrayList<String>>) params[1];

                                            String resultWER = (String) params[2];

                                            byte[] pitch_chart_byte = (byte[]) params[3];
                                            byte[] vowel_chart_byte = (byte[]) params[4];

                                            Intent intent = new Intent(getActivity(), FeedbacksActivity.class);
                                            intent.putExtra(Constants.GET_PHONEMES_POST, phonemes);
                                            intent.putExtra(Constants.GET_VOWEL_STRESS_POST, vowelStress);
                                            intent.putExtra(Constants.GET_WER_POST, resultWER);
                                            intent.putExtra(Constants.GET_PITCH_CHART_POST, pitch_chart_byte);
                                            intent.putExtra(Constants.GET_VOWEL_CHART_POST, vowel_chart_byte);

                                            startActivity(intent);
                                        }
                                    });
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
                    if (this.loggedUser.GetGender().equals("Male"))
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

    // set user reminder
    @SuppressWarnings("deprecation")
    private void setAlarm() {

        String lastLogin = IOUtilities.readUsageTimestamp(this.getActivity());
        Date lastTimestamp = new Date(Long.parseLong(lastLogin));

        int difference = ((int)((lastTimestamp.getTime()/(10 * 60 * 60 * 1000)) - (int)(new Date().getTime()/(10 * 60 * 60 * 1000))));
        if (difference > 0){

        }

        Long alertTimer = new GregorianCalendar().getTimeInMillis() + 10 * 60 * 60 * 1000;
        Intent alertIntent = new Intent(this.getActivity(), AlertReceiver.class);

        AlarmManager alarmManager = (AlarmManager) this.getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alertTimer, PendingIntent.getBroadcast(this.getActivity(), 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));
    }
}
