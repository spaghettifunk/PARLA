package davideberdin.goofing.utilities;

import android.app.Activity;

import java.io.IOException;

import davideberdin.goofing.ParlaApplication;

public class GithubHandler {

    public static void openIssue(Activity activity, String title, String message) throws IOException {
        try {

        } catch (Exception e){
            ParlaApplication.getInstance().trackException(e);
            e.printStackTrace();
        }
    }
}
