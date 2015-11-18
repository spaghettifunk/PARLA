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

import java.util.ArrayList;

import davideberdin.goofing.Listening;
import davideberdin.goofing.R;
import davideberdin.goofing.controllers.User;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.UserLocalStore;


public class ListenNative extends Fragment
{
    private View previouslyNativeSelectedItem = null;
    private ListView nativeListView = null;
    private View listenNativeFragment;

    private UserLocalStore userLocalStore = null;
    private User loggedUser = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.listenNativeFragment = inflater.inflate(R.layout.fragment_listen_native, container, false);

        this.nativeListView = (ListView) this.listenNativeFragment.findViewById(R.id.nativeListView);
        this.userLocalStore = new UserLocalStore(this.getActivity());

        // Fill list view native sentences
        ArrayList<String> nativeSentences = fillNativeList();
        ArrayAdapter<String> nativeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1 , nativeSentences);
        this.nativeListView.setAdapter(nativeAdapter);
        this.nativeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (previouslyNativeSelectedItem != null)
                    previouslyNativeSelectedItem.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                view.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
                previouslyNativeSelectedItem = view;

                loggedUser = userLocalStore.getLoggedUser();
                loggedUser.SetCurrentSentence((String) parent.getItemAtPosition(position));
                loggedUser.SetCurrentPhonetic(Constants.nativePhonetics[position]);
                userLocalStore.storeUserData(loggedUser);

                Listening.listeningNative = true;
            }
        });

        return this.listenNativeFragment;
    }

    private ArrayList<String> fillNativeList()
    {
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < Constants.nativeSentences.length; ++i) {
            list.add(Constants.nativeSentences[i]);
        }

        return list;
    }
}
