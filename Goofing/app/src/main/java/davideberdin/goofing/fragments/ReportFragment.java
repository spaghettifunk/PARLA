package davideberdin.goofing.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;

import davideberdin.goofing.R;
import davideberdin.goofing.controllers.User;
import davideberdin.goofing.networking.GetCallback;
import davideberdin.goofing.networking.ServerRequest;
import davideberdin.goofing.utilities.IOUtilities;
import davideberdin.goofing.utilities.Logger;
import davideberdin.goofing.utilities.UserLocalStore;

public class ReportFragment extends Fragment {

    public ReportFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View reportView = inflater.inflate(R.layout.report_layout, container, false);

        Button reportButton = (Button) reportView.findViewById(R.id.sendReportButton);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Logger.WriteOnReport("ReportActivity", "Clicked on Report BUTTON");

                UserLocalStore localStore = new UserLocalStore(getActivity());
                User user = localStore.getLoggedUser();

                ServerRequest request = new ServerRequest(getActivity());
                request.sendReportDataInBackground(user.GetUsername(), new GetCallback() {
                    @Override
                    public void done(Object... params) {
                        boolean isOk = (boolean) params[0];
                        if (!isOk) {
                            // something happened
                            // log and create an issue in case
                            int x = 0;
                        }
                    }
                });

            }
        });

        return reportView;
    }

    //region APP EVENTS
    @Override
    public void onResume() {
        super.onResume();
        try {
            IOUtilities.readUserAudio(getActivity());
            IOUtilities.readReport(getActivity());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            IOUtilities.writeUserAudio(getActivity());
            IOUtilities.writeReport(getActivity());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            IOUtilities.writeUserAudio(getActivity());
            IOUtilities.writeReport(getActivity());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            IOUtilities.writeUserAudio(getActivity());
            IOUtilities.writeReport(getActivity());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //endregion
}