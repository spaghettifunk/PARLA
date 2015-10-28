package davideberdin.goofing.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import davideberdin.goofing.R;


public class NativeGeneralOverviewFragment extends Fragment
{
    //region VARIABLES
    private View nativeGeneralOverviewFragment;

    private TextView sentenceTextView = null;
    private TextView phoneticTextView = null;
    private SurfaceView overviewSurfaceView = null;

    private String currentSentence = "";
    private String currentPhonetic = "";
    //endregion

    public NativeGeneralOverviewFragment() { }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.nativeGeneralOverviewFragment = inflater.inflate(R.layout.fragment_native_overview, container, false);

        this.overviewSurfaceView = (SurfaceView) getActivity().findViewById(R.id.nativeOverviewSurfaceView);

        this.currentSentence = getArguments().getString("Sentence");
        this.currentPhonetic = getArguments().getString("Phonetic");

        this.sentenceTextView = (TextView) this.nativeGeneralOverviewFragment.findViewById(R.id.listenNativeSentence);
        this.sentenceTextView.setText(this.currentSentence);

        this.phoneticTextView = (TextView) this.nativeGeneralOverviewFragment.findViewById(R.id.listenNativePhonetic);
        this.phoneticTextView.setText(this.currentPhonetic);

        return this.nativeGeneralOverviewFragment;
    }
}
