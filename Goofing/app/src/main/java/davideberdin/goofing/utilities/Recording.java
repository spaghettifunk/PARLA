package davideberdin.goofing.utilities;

import android.content.Context;
import android.media.AudioFormat;
import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;

public class Recording
{
    private static File currentFile = null;
    private static ExtAudioRecorder audioRecorder = null;

    @SuppressWarnings("deprecation")
    public static void startRecording(Context context, String filename)
    {
        try
        {
            currentFile = new File(context.getFilesDir(), filename);

            audioRecorder = ExtAudioRecorder.getInstanse(false);
            audioRecorder.setOutputFile(currentFile.getAbsolutePath());
            audioRecorder.prepare();
            audioRecorder.start();

        } catch (IllegalStateException e) {
            // start:it is called before prepare()
            // prepare: it is called after start() or before setOutputFormat()
            e.printStackTrace();
        }
    }

    public static void stopRecording()
    {
        try {
            audioRecorder.stop();
            audioRecorder.release();

        } catch (IllegalStateException e) {
            //  it is called before start()
            e.printStackTrace();
        }
    }
}
