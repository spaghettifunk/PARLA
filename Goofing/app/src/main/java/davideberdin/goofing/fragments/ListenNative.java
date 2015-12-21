package davideberdin.goofing.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import davideberdin.goofing.Listening;
import davideberdin.goofing.ParlaApplication;
import davideberdin.goofing.R;
import davideberdin.goofing.controllers.User;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.IOUtilities;
import davideberdin.goofing.utilities.UserLocalStore;


public class ListenNative extends Fragment
{
    private View previouslyNativeSelectedItem = null;
    private View previouslyUserSelectedItem = null;

    private UserLocalStore userLocalStore = null;
    private User loggedUser = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View listenNativeFragment = inflater.inflate(R.layout.fragment_listen_native, container, false);

        ListView nativeListView = (ListView) listenNativeFragment.findViewById(R.id.nativeListView);

        this.previouslyUserSelectedItem = inflater.inflate(R.layout.fragment_listen_user, container, false);
        this.userLocalStore = new UserLocalStore(this.getActivity());

        // Fill list view native sentences
        ArrayList<String> nativeSentences = fillNativeList();
        ArrayAdapter<String> nativeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1 , nativeSentences);
        nativeListView.setAdapter(nativeAdapter);
        nativeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (previouslyNativeSelectedItem != null)
                    previouslyNativeSelectedItem.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                if (previouslyUserSelectedItem != null)
                    previouslyUserSelectedItem.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                view.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
                previouslyNativeSelectedItem = view;

                loggedUser = userLocalStore.getLoggedUser();
                loggedUser.SetCurrentSentence((String) parent.getItemAtPosition(position));
                loggedUser.SetCurrentPhonetic(Constants.nativePhonetics[position]);
                userLocalStore.storeUserData(loggedUser);

                Listening.listeningNative = true;
            }
        });

        return listenNativeFragment;
    }

    private ArrayList<String> fillNativeList()
    {
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < Constants.nativeSentences.length; ++i) {
            list.add(Constants.nativeSentences[i]);
        }

        return list;
    }

    //region APP EVENTS
    @Override
    public void onResume() {
        super.onResume();
        try {
            IOUtilities.readUserAudio(getActivity());
            IOUtilities.readReport(getActivity());
        } catch (IOException e) {
            ParlaApplication.getInstance().trackException(e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            ParlaApplication.getInstance().trackException(e);
            e.printStackTrace();
        } catch (JSONException e) {
            ParlaApplication.getInstance().trackException(e);
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
            ParlaApplication.getInstance().trackException(e);
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
            ParlaApplication.getInstance().trackException(e);
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
            ParlaApplication.getInstance().trackException(e);
            e.printStackTrace();
        }
    }
    //endregion
}
