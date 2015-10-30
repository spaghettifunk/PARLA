package davideberdin.goofing;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
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

import davideberdin.goofing.controllers.User;
import davideberdin.goofing.fragments.ListenNative;
import davideberdin.goofing.fragments.ListenUser;
import davideberdin.goofing.utilities.UserLocalStore;

public class Listening extends AppCompatActivity implements View.OnClickListener
{
    public static String selectedSentence = "";
    public static String selectedPhonetic = "";

    private UserLocalStore userLocalStore = null;
    private User loggedUser = null;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listening);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fabListening = (FloatingActionButton) findViewById(R.id.fabListening);
        fabListening.setOnClickListener(this);

        FloatingActionButton fabTest = (FloatingActionButton) findViewById(R.id.fabTestPronunciation);
        fabTest.setOnClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_listening, menu);
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
            case R.id.fabListening:
                // play audio
                try {
                    // TODO: mapping between R.raw.xxx to sentence
                    int resID = this.getResources().getIdentifier("test_audio", "raw", this.getPackageName());

                    MediaPlayer mediaPlayer = MediaPlayer.create(this.getApplicationContext(), resID);
                    mediaPlayer.start();
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.fabTestPronunciation:
                this.userLocalStore = new UserLocalStore(this);
                this.loggedUser = this.userLocalStore.getLoggedUser();
                this.loggedUser.SetCurrentSentence(selectedSentence);
                this.loggedUser.SetCurrentPhonetic(selectedPhonetic);
                this.userLocalStore.storeUserData(this.loggedUser);

                Intent intent = new Intent(this, MenuActivity.class);
                startActivity(intent);

                break;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            Fragment f;
            switch (position){
                case 0:
                    f = new ListenNative();
                    break;
                case 1:
                    f = new ListenUser();
                break;
                default:
                    f = null;
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
                    return "Listen Native";
                case 1:
                    return "Listen Yourself";
            }
            return null;
        }
    }
}
