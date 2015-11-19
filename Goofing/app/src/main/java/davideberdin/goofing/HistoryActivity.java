package davideberdin.goofing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

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

    private ArrayList<CardTuple> historyData;
    private HistoryCardsAdapter historyCardsAdapter;
    private User loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        UserLocalStore localStore = new UserLocalStore(this);
        this.loggedUser = localStore.getLoggedUser();

        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        // create listener for cards history
        this.historyData = new ArrayList<>();
        historyCardsAdapter = new HistoryCardsAdapter(createList(this.historyData));
        recList.setAdapter(historyCardsAdapter);

        recList.setLayoutManager(llm);
    }

    @Override
    public void onStart() {
        super.onStart();

        // request for fetching history data
        ServerRequest historyRequest = new ServerRequest(this, Constants.FETCHING_HISTORY_TITLE, Constants.FETCHING_HISTORY);
        historyRequest.fetchHistoryDataInBackground(this.loggedUser.GetUsername(),
                this.loggedUser.GetCurrentSentence(),
                new GetCallback() {
                    @Override
                    public void done(Object... params) {
                        // handle historyData here
                        ArrayList<CardTuple> tempHistory = (ArrayList<CardTuple>) params[0];
                        for(CardTuple ct : tempHistory)
                            historyData.add(ct);

                        historyCardsAdapter.notifyDataSetChanged(); // update the adapter
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
    private List<HistoryCard> createList(ArrayList<CardTuple> historyData) {

        List<HistoryCard> result = new ArrayList<>();
        for (CardTuple data : historyData) {
            HistoryCard hc = new HistoryCard();

            hc.setCardDate(data.getDate());
            hc.setImageByteArray(data.getImage());

            result.add(hc);
        }

        return result;
    }
}
