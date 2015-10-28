package davideberdin.goofing.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import davideberdin.goofing.ListeningNativeSpeaker;
import davideberdin.goofing.ListeningUser;
import davideberdin.goofing.R;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.ErrorManager;

public class CriticalListeningFragment extends Fragment implements View.OnClickListener
{
    //region VARIABLES
    private FragmentManager fragmentManager = null;

    private Button listenNSButton = null;
    private Button listenYourselfButton = null;

    private ListView nativeListView = null;
    private ListView userListView = null;

    private View previouslyNativeSelectedItem = null;
    private View previouslyUserSelectedItem = null;

    private View criticalListeningView = null;

    private String selectedNativeSentence = null;
    private String selectedNativePhonetic = null;

    private String selectedUserSentence = null;
    private String selectedUserPhonetic = null;
    //endregion

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.criticalListeningView = inflater.inflate(R.layout.critical_listening_layout, container, false);

        this.fragmentManager = getFragmentManager();

        this.listenNSButton = (Button) this.criticalListeningView.findViewById(R.id.listenNSButton);
        this.listenYourselfButton = (Button) this.criticalListeningView.findViewById(R.id.listenYourselfButton);
        this.nativeListView = (ListView) this.criticalListeningView.findViewById(R.id.nativeListView);
        this.userListView = (ListView) this.criticalListeningView.findViewById(R.id.userListView);

        this.listenNSButton.setOnClickListener(this);
        this.listenYourselfButton.setOnClickListener(this);

        // Fill list view native sentences
        ArrayList<String> nativeSentences = fillNativeList();
        ArrayAdapter nativeAdapter = new ArrayAdapter(this.criticalListeningView.getContext(), android.R.layout.simple_list_item_1 , nativeSentences);
        this.nativeListView.setAdapter(nativeAdapter);
        this.nativeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (previouslyNativeSelectedItem != null)
                    previouslyNativeSelectedItem.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                view.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                previouslyNativeSelectedItem = view;
                selectedNativeSentence = (String)parent.getItemAtPosition(position);
                selectedNativePhonetic = Constants.nativePhonetics[position];
            }
        });

        // Fill list view user sentences
        ArrayList<String> userSentences = fillUserList();
        ArrayAdapter userAdapter = new ArrayAdapter(this.criticalListeningView.getContext(), android.R.layout.simple_list_item_1, userSentences);
        this.userListView.setAdapter(userAdapter);
        this.userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (previouslyUserSelectedItem != null)
                    previouslyUserSelectedItem.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                view.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                previouslyUserSelectedItem = view;
                selectedUserSentence = (String)parent.getItemAtPosition(position);
            }
        });

        return this.criticalListeningView;
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.listenNSButton:
                if (selectedNativeSentence == null){
                    ErrorManager.showErrorMessage(this.getActivity(), Constants.ERROR_SELECTING_SENTENCE);
                }
                else {
                    Intent intent = new Intent(this.getActivity(), ListeningNativeSpeaker.class);
                    intent.putExtra("Sentence", selectedNativeSentence);
                    intent.putExtra("Phonetic", selectedNativePhonetic);
                    startActivity(intent);
                }
                break;
            case R.id.listenYourselfButton:
                if (selectedUserSentence == null){
                    ErrorManager.showErrorMessage(this.getActivity(), Constants.ERROR_SELECTING_SENTENCE);
                }
                else {
                    Intent intent = new Intent(this.getActivity(), ListeningUser.class);
                    intent.putExtra("Sentence", selectedUserSentence);
                    intent.putExtra("Phonetic", selectedUserPhonetic);
                    startActivity(intent);
                }
                break;
        }
    }

    private ArrayList<String> fillNativeList()
    {
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < Constants.nativeSentences.length; ++i) {
            list.add(Constants.nativeSentences[i]);
        }

        return list;
    }

    private ArrayList<String> fillUserList()
    {
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < Constants.userSentences.length; ++i) {
            list.add(Constants.nativeSentences[i]);
        }

        return list;
    }
}
