package davideberdin.goofing.controllers;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class Recorder
{
    // The number of buffer frames to keep around (for a nice fade-out visualization).
    private static final int HISTORY_SIZE = 6;
    // To make quieter sounds still show up well on the display, we use +/- 8192 as the amplitude
    // that reaches the top/bottom of the view instead of +/- 32767. Any samples that have
    // magnitude higher than this limit will simply be clipped during drawing.
    private static final float MAX_AMPLITUDE_TO_DRAW = 8192.0f;

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;

    // The sampling rate for the audio recorder.
    private final int SAMPLING_RATE = 11025;
    private String mFileName = null;

    private WaveformView mWaveformView;
    private Paint mPaint;

    private View currentView = null;

    public Recorder(View view){
        this.currentView = view;
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
            InputStream inStream = this.currentView.getContext().openFileInput(mFileName);
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
