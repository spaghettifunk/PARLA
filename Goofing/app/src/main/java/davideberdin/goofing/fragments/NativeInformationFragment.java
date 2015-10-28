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
public class NativeInformationFragment extends Fragment
{
    private View nativeInformationFragment;

    public NativeInformationFragment(){ }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.nativeInformationFragment = inflater.inflate(R.layout.fragment_native_information, container, false);

        return this.nativeInformationFragment;
    }
}
