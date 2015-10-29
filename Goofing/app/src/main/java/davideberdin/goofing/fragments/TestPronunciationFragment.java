package davideberdin.goofing.fragments;

import android.app.ActionBar;
import android.app.Fragment;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import davideberdin.goofing.R;
import davideberdin.goofing.controllers.Recorder;
import davideberdin.goofing.controllers.User;
import davideberdin.goofing.networking.GetCallback;
import davideberdin.goofing.networking.ServerRequest;
import davideberdin.goofing.utilities.IOUtilities;
import davideberdin.goofing.utilities.UserLocalStore;
import davideberdin.goofing.utilities.VisualizerView;


public class TestPronunciationFragment extends Fragment implements View.OnClickListener
{
    // related to the draw
    private byte[] mBytes;
    private float[] mPoints;
    private Rect mRect = new Rect();

    private Paint mForePaint = new Paint();
    private LinearLayout waveFormLayout = null;
    private Visualizer mVisualizer;
    private VisualizerView mVisualizerView;

    private View testPronunciationView = null;
    private FloatingActionButton startButton = null;

    // variables related to the view
    private TextView tvSentence = null;
    private TextView tvPhonemes = null;

    private UserLocalStore userLocalStore = null;
    private User loggedUser = null;

    public TestPronunciationFragment() { }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.testPronunciationView = inflater.inflate(R.layout.test_pronunciation_layout, container, false);

        this.waveFormLayout = (LinearLayout) testPronunciationView.findViewById(R.id.waveFormLayout);
        readFileAudioAndDrawWave();

        this.startButton = (FloatingActionButton) testPronunciationView.findViewById(R.id.fabStartRecording);

        // register listener
        this.startButton.setOnClickListener(this);

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
    public void onClick(View v)
    {
        ServerRequest recordingRequest = new ServerRequest(this.getActivity(), "Recording", "Say the sentence");

        switch (v.getId())
        {
            case R.id.fabStartRecording:
                Recorder recorder = new Recorder(this.getView());
                recordingRequest.recordingAudioInBackground(recorder, new GetCallback() {
                    @Override
                    public void done(Object... params) {
                        // dismiss
                        // and send it to server
                        // ...

                        // obtained as result
                        Object fileAudio = null; // params[0];      // TODO: put the audiofile here
                        String currentSentence = loggedUser.GetCurrentSentence();

                        ServerRequest request = new ServerRequest(getActivity() , "Analyzing audio", "Please wait...");
                        request.sendRecordedAudioToServer(fileAudio, currentSentence, new GetCallback() {
                            @Override
                            public void done(Object... params) {
                                // dismiss everything
                                // save the result
                                // start Feedback activity
                                // ...
                            }
                        });
                    }
                });
                break;
        }
    }


    private void readFileAudioAndDrawWave()
    {
        try
        {
            InputStream inStream = this.testPronunciationView.getContext().getResources().openRawResource(R.raw.test_audio);
            byte[] music = IOUtilities.convertStreamToByteArray(inStream);

            // Create a VisualizerView (defined below), which will render the simplified audio
            // wave form to a Canvas.
            mVisualizerView = new VisualizerView(this.testPronunciationView.getContext());
            ViewGroup.LayoutParams params = waveFormLayout.getLayoutParams();
            params.height = 100;

            waveFormLayout.addView(mVisualizerView);

            mVisualizerView.updateVisualizer(music);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
