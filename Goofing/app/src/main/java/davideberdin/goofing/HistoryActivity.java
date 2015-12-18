package davideberdin.goofing;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.MotionEvent;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import davideberdin.goofing.controllers.CardTuple;
import davideberdin.goofing.controllers.HistoryCard;
import davideberdin.goofing.controllers.HistoryCardsAdapter;
import davideberdin.goofing.controllers.HistoryTrendAdapter;
import davideberdin.goofing.controllers.TrendCard;
import davideberdin.goofing.controllers.TrendTuple;
import davideberdin.goofing.controllers.User;
import davideberdin.goofing.networking.GetCallback;
import davideberdin.goofing.networking.ServerRequest;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.IOUtilities;
import davideberdin.goofing.utilities.Logger;
import davideberdin.goofing.utilities.UserLocalStore;

public class HistoryActivity extends AppCompatActivity {

    // vowels history
    private ArrayList<HistoryCard> historyCardsAdapterList;
    private HistoryCardsAdapter historyCardsAdapter;
    private RecyclerView recCardsList;

    // vowels trend
    private ArrayList<TrendCard> historyTrendAdapterList;
    private HistoryTrendAdapter historyTrendAdapter;
    private RecyclerView recTrendList;

    private User loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        UserLocalStore localStore = new UserLocalStore(this);
        this.loggedUser = localStore.getLoggedUser();

        // vowels history
        this.recCardsList = (RecyclerView) findViewById(R.id.cardList);
        this.recCardsList.setHasFixedSize(true);

        this.historyCardsAdapterList = new ArrayList<>();
        historyCardsAdapter = new HistoryCardsAdapter(this.historyCardsAdapterList);

        LinearLayoutManager cardsLlm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        this.recCardsList.setLayoutManager(cardsLlm);
        this.recCardsList.setAdapter(historyCardsAdapter);

        // vowels trend
        this.recTrendList = (RecyclerView) findViewById(R.id.cardListTrend);
        this.recTrendList.setHasFixedSize(true);

        this.historyTrendAdapterList = new ArrayList<>();
        historyTrendAdapter = new HistoryTrendAdapter(this.historyTrendAdapterList);

        LinearLayoutManager trendLlm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        this.recTrendList.setLayoutManager(trendLlm);
        this.recTrendList.setAdapter(historyTrendAdapter);

        // request for fetching history data
        int indexSentence = Arrays.asList(Constants.nativeSentences).indexOf(this.loggedUser.GetCurrentSentence());
        String vowels = Constants.nativeStressPhonemes[indexSentence];

        ServerRequest historyRequest = new ServerRequest(this, Constants.FETCHING_HISTORY_TITLE, Constants.FETCHING_HISTORY);
        historyRequest.fetchHistoryDataInBackground(this.loggedUser.GetUsername(),
                this.loggedUser.GetCurrentSentence(), vowels,
                new GetCallback() {
                    @Override
                    public void done(Object... params) {
                        //region vowels history here
                        ArrayList<CardTuple> tempHistory = (ArrayList<CardTuple>) params[0];
                        ArrayList<CardTuple> historyCards = new ArrayList<CardTuple>();
                        for (CardTuple ct : tempHistory)
                            historyCards.add(ct);

                        // create listener for cards history
                        createList(historyCards);
                        historyCardsAdapter.notifyDataSetChanged();
                        //endregion
                        //region vowels trend here
                        ArrayList<TrendTuple> tempTrend = (ArrayList<TrendTuple>) params[1];
                        ArrayList<TrendTuple> historyTrend = new ArrayList<TrendTuple>();
                        for (TrendTuple tc : tempTrend)
                            historyTrend.add(tc);

                        // create listener for cards trend
                        createListTrend(historyTrend);
                        historyTrendAdapter.notifyDataSetChanged();
                        //endregion
                    }
                });

        // back arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                Logger.WriteOnReport("HistoryActivity", "Clicked on back BUTTON");
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("deprecation")
    private void createList(ArrayList<CardTuple> historyData) {

        for (CardTuple data : historyData) {
            HistoryCard hc = new HistoryCard();

            hc.setCardId(data.getId());
            hc.setCardDate(data.getDate());
            hc.setImageByteArray(data.getImage());

            historyCardsAdapterList.add(hc);
        }
    }

    @SuppressWarnings("deprecation")
    private void createListTrend(ArrayList<TrendTuple> historyData) {

        for (TrendTuple data : historyData) {
            TrendCard hc = new TrendCard();

            hc.setImageFloatArray(data.getImageYValues());
            hc.setImageTimeArray(data.getImageXValues());
            hc.setImageTitle(data.getTitle());

            historyTrendAdapterList.add(hc);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                Logger.WriteOnReport("HistoryActivity", "Action was DOWN");
                return true;
            case (MotionEvent.ACTION_MOVE):
                Logger.WriteOnReport("HistoryActivity", "Action was MOVE");
                return true;
            case (MotionEvent.ACTION_UP):
                Logger.WriteOnReport("HistoryActivity", "Action was UP");
                return true;
            case (MotionEvent.ACTION_CANCEL):
                Logger.WriteOnReport("HistoryActivity", "Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE):
                Logger.WriteOnReport("HistoryActivity", "Movement occurred outside bounds of current screen element");
                return true;
            case (MotionEvent.ACTION_SCROLL):
                Logger.WriteOnReport("HistoryActivity", "Action was SCROLL");
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    //region APP EVENTS
    @Override
    public void onResume() {
        super.onResume();
        try {
            IOUtilities.readUserAudio(this);
            IOUtilities.readReport(this);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            IOUtilities.writeUserAudio(this);
            IOUtilities.writeReport(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            IOUtilities.writeUserAudio(this);
            IOUtilities.writeReport(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            IOUtilities.writeUserAudio(this);
            IOUtilities.writeReport(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //endregion
}
