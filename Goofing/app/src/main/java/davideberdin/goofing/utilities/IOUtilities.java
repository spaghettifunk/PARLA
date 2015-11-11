package davideberdin.goofing.utilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class IOUtilities
{
    public static byte[] convertStreamToByteArray(InputStream is) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[10240];
        int i = Integer.MAX_VALUE;
        while ((i = is.read(buff, 0, buff.length)) > 0) {
            baos.write(buff, 0, i);
        }

        return baos.toByteArray(); // be sure to close InputStream in calling function
    }

    public static InputStream convertByteToInputStream(byte[] buffer){
        try{
            return new ByteArrayInputStream(buffer);
        } catch (Exception e){
            return null;
        }
    }

    public static byte[] ShortToByte_Twiddle_Method(short[] input)
    {
        int short_index, byte_index;
        int iterations = input.length;

        byte[] buffer = new byte[input.length * 2];

        short_index = byte_index = 0;

        for(/*NOP*/; short_index != iterations; /*NOP*/)
        {
            buffer[byte_index] = (byte) (input[short_index] & 0x00FF);
            buffer[byte_index + 1] = (byte) ((input[short_index] & 0xFF00) >> 8);

            ++short_index; byte_index += 2;
        }

        return buffer;
    }
}
