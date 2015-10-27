package davideberdin.goofing.networking;

import android.app.Activity;
import android.app.ProgressDialog;

import davideberdin.goofing.controllers.User;
import davideberdin.goofing.utilities.Constants;

public class ServerRequest
{
    private ProgressDialog progressDialog;
    private Activity currentActivity;

    public ServerRequest(Activity activity)
    {
        this.currentActivity = activity;

        this.progressDialog = new ProgressDialog(currentActivity);
        this.progressDialog.setCancelable(false);
        this.progressDialog.setTitle("Processing");
        this.progressDialog.setMessage("Please wait...");
    }

    public void fetchUserDataInBackgroud(User user, GetCallback callback)
    {
        this.progressDialog.show();
        NetworkingTask networkingTask = new NetworkingTask(user, callback, progressDialog);
        networkingTask.execute(Constants.NETWORKING_LOGIN_STATE, user);
    }

    public void storeUserDataInBackground(User user, GetCallback callback)
    {
        this.progressDialog.show();
        NetworkingTask networkingTask = new NetworkingTask(user, callback, progressDialog);
        networkingTask.execute(Constants.NETWORKING_REGISTER_STATE, user);
    }

    // all the other request here
}
