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

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        // request here for history data
        ArrayList<CardTuple> historyData = new ArrayList<>();


        HistoryCardsAdapter historyCardsAdapter = new HistoryCardsAdapter(createList(historyData));
        recList.setAdapter(historyCardsAdapter);

        recList.setLayoutManager(llm);
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
