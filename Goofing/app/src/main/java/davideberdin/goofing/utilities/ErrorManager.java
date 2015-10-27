package davideberdin.goofing.utilities;

import android.app.Activity;
import android.app.AlertDialog;

public class ErrorManager
{
    private static ErrorManager ourInstance = new ErrorManager();

    public static ErrorManager getInstance() {
        return ourInstance;
    }

    private ErrorManager() { }

    public static void showErrorMessage(Activity activity, String message)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }
}
