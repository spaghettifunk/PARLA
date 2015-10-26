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
public class CriticalListeningFragment extends Fragment
{
    private View criticalListeningView = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.criticalListeningView = inflater.inflate(R.layout.critical_listening_layout, container, false);
        return this.criticalListeningView;
    }
}
