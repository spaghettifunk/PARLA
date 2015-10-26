package davideberdin.goofing.controllers;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import davideberdin.goofing.R;

/**
 * Created by dado on 25/10/15.
 */
public class NewWordsFragment extends Fragment
{
    private View newWordsView = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.newWordsView = inflater.inflate(R.layout.new_words_layout, container, false);
        this.newWordsView = inflater.inflate(R.layout.new_words_layout, container, false);
        return this.newWordsView;
    }
}