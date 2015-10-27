package davideberdin.goofing.controllers;

import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import davideberdin.goofing.R;
import davideberdin.goofing.networking.NetworkingTask;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.UserLocalStore;


public class TestPronunciationFragment extends Fragment implements View.OnClickListener
{
    // The number of buffer frames to keep around (for a nice fade-out visualization).
    private static final int HISTORY_SIZE = 6;
    // To make quieter sounds still show up well on the display, we use +/- 8192 as the amplitude
    // that reaches the top/bottom of the view instead of +/- 32767. Any samples that have
    // magnitude higher than this limit will simply be clipped during drawing.
    private static final float MAX_AMPLITUDE_TO_DRAW = 8192.0f;

    private View testPronunciationView = null;
    private Button startButton = null;
    private Button stopButton = null;

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;

    // The sampling rate for the audio recorder.
    private static final int SAMPLING_RATE = 11025;
    private static String mFileName = null;
    private WaveformView mWaveformView;
    private Paint mPaint;

    // variables related to the view
    private TextView tvSentence = null;
    private TextView tvPhonemes = null;

    private UserLocalStore userLocalStore = null;

    public TestPronunciationFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.testPronunciationView = inflater.inflate(R.layout.test_pronunciation_layout, container, false);

        try
        {
            //this.mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
            File dirs = this.getActivity().getFilesDir();
            this.mFileName += dirs.getAbsolutePath() + "test.wav";

            SurfaceView waveForm = (SurfaceView) testPronunciationView.findViewById(R.id.waveformView);

            this.startButton = (Button) testPronunciationView.findViewById(R.id.tpStartButton);
            this.stopButton = (Button) testPronunciationView.findViewById(R.id.tpStopButton);

            // register listener
            this.startButton.setOnClickListener(this);
            this.stopButton.setOnClickListener(this);

            this.userLocalStore = new UserLocalStore(this.getActivity());
            User loggedUser = this.userLocalStore.getLoggedUser();

            this.tvSentence = (TextView) testPronunciationView.findViewById(R.id.tpSentence);
            String tmp = loggedUser.GetCurrentSentence();
            this.tvSentence.setText(tmp);

            // create phoneme sentence
            this.tvPhonemes = (TextView) testPronunciationView.findViewById(R.id.tpPhoneticSentence);
            tmp = loggedUser.GetCurrentPhonetic();
            this.tvPhonemes.setText("/ " + tmp + " /");

        } catch (Exception ex){
            int x = 0;
        }

        return this.testPronunciationView;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()){
            case R.id.tpStartButton:
                onRecord(true);
                break;
            case R.id.tpStopButton:
                onRecord(false);
                analyzeAudioFile();
                break;
        }
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable()
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    private boolean isExternalStorageReadable()
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private void analyzeAudioFile()
    {
        // draw waveform
        try {
            InputStream inStream = testPronunciationView.getContext().openFileInput(mFileName);
            byte[] stream = convertStreamToByteArray(inStream);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] convertStreamToByteArray(InputStream is) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[11025];
        int i = Integer.MAX_VALUE;
        while ((i = is.read(buff, 0, buff.length)) > 0) {
            baos.write(buff, 0, i);
        }

        return baos.toByteArray(); // be sure to close InputStream in calling function
    }

    private void onRecord(boolean start)
    {
        try {
            if (start) {
                startRecording();
            } else {
                stopRecording();
            }
        } catch (IOException e){
            int x = 0;
        }
    }

    private void startRecording() throws IOException {
        try {
            //if (isExternalStorageReadable() == false || isExternalStorageWritable() == false)
            //    throw new IOException();

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(mFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            mRecorder.prepare();
        } catch (IOException e) {
            int x = 0;
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    private void drawWaveform(View v, Canvas canvas, short[] buffer)
    {
        // Clear the screen each time because SurfaceView won't do this for us.
        canvas.drawColor(Color.BLACK);

        float width = v.getWidth();
        float height = v.getHeight();
        float centerY = height / 2;

        // We draw the history from oldest to newest so that the older audio data is further back
        // and darker than the most recent data.
        int colorDelta = 255 / (HISTORY_SIZE + 1);
        int brightness = colorDelta;

        mPaint.setColor(Color.argb(brightness, 128, 255, 192));

        float lastX = -1;
        float lastY = -1;

        // For efficiency, we don't draw all of the samples in the buffer, but only the ones
        // that align with pixel boundaries.
        for (int x = 0; x < width; x++) {
            int index = (int) ((x / width) * buffer.length);
            short sample = buffer[index];
            float y = (sample / MAX_AMPLITUDE_TO_DRAW) * centerY + centerY;

            if (lastX != -1) {
                canvas.drawLine(lastX, lastY, x, y, mPaint);
            }

            lastX = x;
            lastY = y;
        }

        brightness += colorDelta;
    }
}
