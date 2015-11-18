package davideberdin.goofing.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

import davideberdin.goofing.R;
import davideberdin.goofing.utilities.IOUtilities;

public class NewWordsFragment extends Fragment {
    private View newWordsView = null;

    public NewWordsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.newWordsView = inflater.inflate(R.layout.new_words_layout, container, false);
        return this.newWordsView;
    }

    //region APP EVENTS
    @Override
    public void onResume() {
        super.onResume();

        try {
            IOUtilities.readUserAudio(getActivity());
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            IOUtilities.writeUserAudio(getActivity());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            IOUtilities.writeUserAudio(getActivity());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //endregion
}