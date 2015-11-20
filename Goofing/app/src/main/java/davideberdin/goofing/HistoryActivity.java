package davideberdin.goofing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import davideberdin.goofing.controllers.CardTuple;
import davideberdin.goofing.controllers.HistoryCard;
import davideberdin.goofing.controllers.HistoryCardsAdapter;
import davideberdin.goofing.controllers.Tuple;
import davideberdin.goofing.controllers.User;
import davideberdin.goofing.networking.GetCallback;
import davideberdin.goofing.networking.NetworkingTask;
import davideberdin.goofing.networking.ServerRequest;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.UserLocalStore;

public class HistoryActivity extends AppCompatActivity {

    private ArrayList<HistoryCard> historyCardsAdapterList;
    private HistoryCardsAdapter historyCardsAdapter;
    private RecyclerView recList;
    private User loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        UserLocalStore localStore = new UserLocalStore(this);
        this.loggedUser = localStore.getLoggedUser();

        this.recList = (RecyclerView) findViewById(R.id.cardList);
        this.recList.setHasFixedSize(true);

        this.historyCardsAdapterList = new ArrayList<>();
        historyCardsAdapter = new HistoryCardsAdapter(this.historyCardsAdapterList);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        this.recList.setLayoutManager(llm);
        this.recList.setAdapter(historyCardsAdapter);

        // request for fetching history data
        ServerRequest historyRequest = new ServerRequest(this, Constants.FETCHING_HISTORY_TITLE, Constants.FETCHING_HISTORY);
        historyRequest.fetchHistoryDataInBackground(this.loggedUser.GetUsername(),
                this.loggedUser.GetCurrentSentence(),
                new GetCallback() {
                    @Override
                    public void done(Object... params) {
                        // handle historyData here
                        ArrayList<CardTuple> tempHistory = (ArrayList<CardTuple>) params[0];
                        ArrayList<CardTuple> historyCards = new ArrayList<CardTuple>();
                        for (CardTuple ct : tempHistory)
                            historyCards.add(ct);

                        // create listener for cards history
                        createList(historyCards);
                        historyCardsAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("deprecation")
    private void createList(ArrayList<CardTuple> historyData) {

        for (CardTuple data : historyData) {
            HistoryCard hc = new HistoryCard();

            hc.setCardDate(data.getDate());
            hc.setImageByteArray(data.getImage());

            historyCardsAdapterList.add(hc);
        }
    }
}
