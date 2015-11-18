package davideberdin.goofing;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import davideberdin.goofing.controllers.SentenceTuple;
import davideberdin.goofing.controllers.Tuple;
import davideberdin.goofing.fragments.NewWordsFragment;
import davideberdin.goofing.fragments.TestPronunciationFragment;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.IOUtilities;
import davideberdin.goofing.utilities.UserLocalStore;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static FragmentManager fragmentManager;
    private UserLocalStore userLocalStore = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Init structs
        if (Constants.nativeSentenceInfo.isEmpty())
            fillSentencesMap();

        // fill dictionary sentence-meaning-example
        if (Constants.meaningExampleMap.isEmpty())
            Constants.createMeaningExampleDictionary();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        this.userLocalStore = new UserLocalStore(this);

        TextView usernameMenuDrawer = (TextView) findViewById(R.id.usernameMenuDrawer);
        usernameMenuDrawer.setText("Hi " + this.userLocalStore.getLoggedUser().GetUsername());

        ImageView avatar = (ImageView) findViewById(R.id.avatarImageView);
        avatar.setImageResource(R.drawable.avatar);

        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_menu, new TestPronunciationFragment()).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authenticate() == false) {
            Intent newIntent = new Intent(MenuActivity.this, LoginActivity.class);
            startActivity(newIntent);
        }
    }

    private boolean authenticate() {
        return this.userLocalStore.getUserLoggedIn();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.test_pronunciation) {
            this.fragmentManager.beginTransaction().replace(R.id.content_menu, new TestPronunciationFragment()).commit();
        } else if (id == R.id.critical_listening) {
            Intent intent = new Intent(MenuActivity.this, Listening.class);
            startActivity(intent);
        } else if (id == R.id.new_words) {
            this.fragmentManager.beginTransaction().replace(R.id.content_menu, new NewWordsFragment()).commit();
        } else if (id == R.id.logout) {

            this.userLocalStore.clearUserData();
            this.userLocalStore.setUserLoggedIn(false);

            Intent newIntent = new Intent(MenuActivity.this, LoginActivity.class);
            startActivity(newIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //region APP EVENTS
    @Override
    public void onResume() {
        super.onResume();

        try {
            IOUtilities.readUserAudio(this);
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
            IOUtilities.writeUserAudio(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            IOUtilities.writeUserAudio(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            IOUtilities.writeUserAudio(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //endregion

    public static void fillSentencesMap() {
        for (int i = 0; i < Constants.nativeSentences.length; i++) {

            String stressPhonemes = Constants.nativeStressPhonemes[i];
            String stressPosition = Constants.nativeStressPosition[i];

            List<String> phonemes = Arrays.asList(stressPhonemes.split("\\s*,\\s*"));
            List<String> positions = Arrays.asList(stressPosition.split("\\s*,\\s*"));

            ArrayList<Tuple> stressNative = new ArrayList<Tuple>();
            for (int j = 0; j < phonemes.size(); j++) {
                Tuple stress = new Tuple(phonemes.get(j), positions.get(j));
                stressNative.add(stress);
            }

            SentenceTuple<String, String, ArrayList<Tuple>> tuple = new SentenceTuple<>(Constants.nativePhonetics[i], Constants.nativePhonemes[i], stressNative);
            Constants.nativeSentenceInfo.put(Constants.nativeSentences[i], tuple);
        }
    }
}
