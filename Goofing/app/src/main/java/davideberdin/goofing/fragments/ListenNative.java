package davideberdin.goofing.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import davideberdin.goofing.Listening;
import davideberdin.goofing.R;
import davideberdin.goofing.utilities.Constants;


public class ListenNative extends Fragment
{
    private View previouslyNativeSelectedItem = null;
    private ListView nativeListView = null;
    private View listenNativeFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.listenNativeFragment = inflater.inflate(R.layout.fragment_listen_native, container, false);

        this.nativeListView = (ListView) this.listenNativeFragment.findViewById(R.id.nativeListView);

        // Fill list view native sentences
        ArrayList<String> nativeSentences = fillNativeList();
        ArrayAdapter nativeAdapter = new ArrayAdapter(this.listenNativeFragment.getContext(), android.R.layout.simple_list_item_1 , nativeSentences);
        this.nativeListView.setAdapter(nativeAdapter);
        this.nativeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (previouslyNativeSelectedItem != null)
                    previouslyNativeSelectedItem.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                view.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                previouslyNativeSelectedItem = view;
                Listening.selectedSentence = (String)parent.getItemAtPosition(position);
                Listening.selectedPhonetic = Constants.nativePhonetics[position];
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