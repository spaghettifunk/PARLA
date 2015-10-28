package davideberdin.goofing.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import davideberdin.goofing.R;

/**
 * Created by dado on 28/10/15.
 */
public class UserGeneralOverviewFragment extends Fragment
{
    private View userGeneralOverviewFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.userGeneralOverviewFragment = inflater.inflate(R.layout.fragment_user_overview, container, false);

        return this.userGeneralOverviewFragment;
    }
}
