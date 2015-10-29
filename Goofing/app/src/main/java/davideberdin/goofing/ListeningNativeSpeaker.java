package davideberdin.goofing;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import davideberdin.goofing.fragments.NativeGeneralOverviewFragment;
import davideberdin.goofing.fragments.NativeInformationFragment;
import davideberdin.goofing.utilities.AppWindowManager;

public class ListeningNativeSpeaker extends AppCompatActivity implements View.OnClickListener
{
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private NativeSectionsPagerAdapter mNativeSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private String currentSentence = "";
    private String currentPhonetic = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listening_native_speaker);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mNativeSectionsPagerAdapter = new NativeSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mNativeSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton listenButton = (FloatingActionButton) findViewById(R.id.fabListenNSButton);
        listenButton.setOnClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get selected sentence
        this.currentSentence = getIntent().getExtras().getString("Sentence");
        this.currentPhonetic = getIntent().getExtras().getString("Phonetic");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_listening_native_speaker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()){
            case R.id.fabListenNSButton:
                AppWindowManager.showErrorMessage(this, "Such a beautiful voice: " + this.currentSentence);
                break;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class NativeSectionsPagerAdapter extends FragmentPagerAdapter
    {
        public NativeSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            Fragment f;
            Bundle args = new Bundle();
            switch (position){
                case 0:
                    f = new NativeGeneralOverviewFragment();
                    args.putString("Sentence", currentSentence);
                    args.putString("Phonetic", currentPhonetic);
                    f.setArguments(args);

                    break;
                case 1:
                    f = new NativeInformationFragment();
                    break;
                default:
                    f = null;
                    break;
            }

            return f;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "General overview";
                case 1:
                    return "Information";
            }
            return null;
        }
    }
}
