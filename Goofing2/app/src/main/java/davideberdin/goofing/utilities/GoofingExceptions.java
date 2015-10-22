package davideberdin.goofing.utilities;
import davideberdin.goofing.utilities.Logger;

/**
 * Created by dado on 09/10/15.
 */
public class GoofingExceptions extends Exception {

    public GoofingExceptions() {
        Logger.getLogger().writeLog("Unknown", "Unhandled error");
    }

    public GoofingExceptions(String tagName, String message) {
        super(message);
        Logger.getLogger().writeLog(tagName, message);
    }

    public GoofingExceptions(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public GoofingExceptions(String tagName, String message, Throwable cause) {
        super(message, cause);
        Logger.getLogger().writeLog(tagName, message);
    }
}