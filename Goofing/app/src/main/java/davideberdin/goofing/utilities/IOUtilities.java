package davideberdin.goofing.utilities;

import android.content.Context;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;


public class IOUtilities {

    public static TinyDB tinydb = null;
    public static ArrayList<String> audioFiles = new ArrayList<>();

    public static byte[] convertStreamToByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[10240];
        int i = Integer.MAX_VALUE;
        while ((i = is.read(buff, 0, buff.length)) > 0) {
            baos.write(buff, 0, i);
        }

        return baos.toByteArray(); // be sure to close InputStream in calling function
    }

    public static InputStream convertByteToInputStream(byte[] buffer) {
        try {
            return new ByteArrayInputStream(buffer);
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] ShortToByte_Twiddle_Method(short[] input) {
        int short_index, byte_index;
        int iterations = input.length;

        byte[] buffer = new byte[input.length * 2];

        short_index = byte_index = 0;

        for (/*NOP*/; short_index != iterations; /*NOP*/) {
            buffer[byte_index] = (byte) (input[short_index] & 0x00FF);
            buffer[byte_index + 1] = (byte) ((input[short_index] & 0xFF00) >> 8);

            ++short_index;
            byte_index += 2;
        }

        return buffer;
    }

    public static void writeUserAudio(Context context) throws IOException {
        if (tinydb == null)
            tinydb = new TinyDB(context);

        tinydb.putListString(Constants.SHARED_PREFERENCES_RECORDED_AUDIO_NAME, audioFiles);
    }

    public static void readUserAudio(Context context) throws IOException, ClassNotFoundException {
        if (tinydb == null)
            tinydb = new TinyDB(context);

        audioFiles = tinydb.getListString(Constants.SHARED_PREFERENCES_RECORDED_AUDIO_NAME);
    }
}
