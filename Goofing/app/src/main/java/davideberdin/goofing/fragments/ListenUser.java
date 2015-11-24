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
import davideberdin.goofing.utilities.IOUtilities;
import davideberdin.goofing.utilities.UserLocalStore;


public class ListenUser extends Fragment {
    private View listenUserFragment;
    private ListView userListView = null;
    private View previouslyUserSelectedItem = null;

    private UserLocalStore userLocalStore = null;
    private User loggedUser = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.listenUserFragment = inflater.inflate(R.layout.fragment_listen_user, container, false);

        this.userListView = (ListView) this.listenUserFragment.findViewById(R.id.userListView);
        this.userLocalStore = new UserLocalStore(this.getActivity());

        // Fill list view user sentences
        ArrayAdapter<String> userAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, IOUtilities.audioFiles);
        userAdapter.notifyDataSetChanged();

        this.userListView.setAdapter(userAdapter);
        this.userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (previouslyUserSelectedItem != null)
                    previouslyUserSelectedItem.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                view.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
                previouslyUserSelectedItem = view;

                loggedUser = userLocalStore.getLoggedUser();
                loggedUser.SetCurrentSentence((String) parent.getItemAtPosition(position));
                loggedUser.SetCurrentPhonetic(Constants.nativePhonetics[position]);
                userLocalStore.storeUserData(loggedUser);

                Listening.listeningNative = false;
            }
        });

        return this.listenUserFragment;
    }
}
