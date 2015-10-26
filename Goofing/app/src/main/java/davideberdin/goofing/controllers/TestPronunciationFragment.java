package davideberdin.goofing.controllers;

import android.app.Fragment;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;

import davideberdin.goofing.R;


public class TestPronunciationFragment extends Fragment
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

    public TestPronunciationFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.testPronunciationView = inflater.inflate(R.layout.test_pronunciation_layout, container, false);

        try
        {
            this.mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
            this.mFileName += "/test.wav";

            // Compute the minimum required audio buffer size and allocate the buffer.
            // mBufferSize = AudioRecord.getMinBufferSize(SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            // mAudioBuffer = new short[mBufferSize / 2];

            SurfaceView waveForm = (SurfaceView) testPronunciationView.findViewById(R.id.waveformView);

            this.startButton = (Button) testPronunciationView.findViewById(R.id.tpStartButton);
            this.startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecord(true);

                }
            });

            this.stopButton = (Button) testPronunciationView.findViewById(R.id.tpStopButton);
            this.stopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecord(false);

                    // draw waveform
                    try {
                        FileInputStream audioFile = testPronunciationView.getContext().openFileInput(mFileName);


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception ex){
            int x = 0;
        }

        return this.testPronunciationView;
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);

            byte[] byteInput = {0};
            short[] input = {0};

            FileInputStream fis = new FileInputStream(mFileName);

            fis.read(byteInput, 44, byteInput.length - 45);
            ByteBuffer.wrap(byteInput).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(input);

        } catch (IOException e) {
            int x = 0;
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
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
