package davideberdin.goofing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import davideberdin.goofing.controllers.SentenceTuple;
import davideberdin.goofing.controllers.StressTuple;
import davideberdin.goofing.controllers.User;
import davideberdin.goofing.utilities.AutoResizeTextView;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.UserLocalStore;

public class FeedbacksActivity extends AppCompatActivity implements View.OnClickListener
{
    private byte[] pitchChartByte;
    private byte[] vowelChartByte;

    private AutoResizeTextView nativeFeedbacks;
    private AutoResizeTextView nativeFeedbacksSentence;

    private AutoResizeTextView userFeedbacks;

    private ImageView pitchChart;
    private ImageView vowelChart;
    private User loggedUser;
    private UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbacks);

        Bundle b = getIntent().getExtras();

        this.pitchChartByte = b.getByteArray("pitchchart");
        this.vowelChartByte = b.getByteArray("vowelchart");

        this.userLocalStore = new UserLocalStore(this);
        this.loggedUser = this.userLocalStore.getLoggedUser();

        // Feedbacks
        this.nativeFeedbacks = (AutoResizeTextView) findViewById(R.id.feedbacksNative);
        this.nativeFeedbacks.setMaxLines(1);

        this.nativeFeedbacksSentence = (AutoResizeTextView) findViewById(R.id.feedbacksNativeSentence);
        this.nativeFeedbacksSentence.setMaxLines(1);
        this.nativeFeedbacksSentence.setLineSpacing(2.0f, 5.0f);

        this.nativeFeedbacksSentence.setText(this.loggedUser.GetCurrentSentence());

        this.userFeedbacks = (AutoResizeTextView) findViewById(R.id.feedbacksUser);
        this.userFeedbacks.setMaxLines(1);

        // color strings
        fillNativeFeedbacks();

        this.pitchChart = (ImageView) findViewById(R.id.pitchChartImageView);
        Bitmap pitchBitmap = BitmapFactory.decodeByteArray(this.pitchChartByte, 0, this.pitchChartByte.length);
        this.pitchChart.setImageBitmap(pitchBitmap);
        this.pitchChart.setOnClickListener(this);

        // Build feedbacks image
        this.vowelChart = (ImageView) findViewById(R.id.vowelChartImageView);
        Bitmap vowelBitmap = BitmapFactory.decodeByteArray(this.vowelChartByte, 0, this.vowelChartByte.length);
        this.vowelChart.setImageBitmap(vowelBitmap);
        this.vowelChart.setOnClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.pitchChartImageView:
                Intent pitchIntent = new Intent(FeedbacksActivity.this, FullscreenImageActivity.class);
                pitchIntent.putExtra("isPitch", true);
                pitchIntent.putExtra("pitchchart", this.pitchChartByte);
                startActivity(pitchIntent);
            case R.id.vowelChartImageView:
                Intent vowelIntent = new Intent(FeedbacksActivity.this, FullscreenImageActivity.class);
                vowelIntent.putExtra("isPitch", false);
                vowelIntent.putExtra("vowelchart", this.vowelChartByte);
                startActivity(vowelIntent);
        break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
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

    private void fillNativeFeedbacks()
    {
        String currentSentence = this.loggedUser.GetCurrentSentence();
        SentenceTuple<String, String, ArrayList<StressTuple<String, String>>> sentenceNativeFeedbacks = Constants.nativeSentenceInfo.get(currentSentence);

        this.nativeFeedbacks.setText(sentenceNativeFeedbacks.getPhonemes());

        // change color based on stress position
        String currentSentenceNoDigits = sentenceNativeFeedbacks.getPhonemes().replaceAll("\\d", "");
        
        List<String> allPhonemes = Arrays.asList(currentSentenceNoDigits.split("\\s+"));
        ArrayList<StressTuple<String, String>> stress = sentenceNativeFeedbacks.getStress();

        String pronunciationRepresentation = "";
        for (String p: allPhonemes) {
            boolean changed = false;
            for (StressTuple<String, String> st : new ArrayList<StressTuple<String, String>>(stress)) {
                if (p.contains(st.getPhoneme()) && st.getIsStress().equals("1")) {
                    stress.remove(st);

                    int subPosition = p.indexOf(st.getPhoneme());
                    String temp = p.replace(st.getPhoneme(), "");

                    String coloredPhoneme = Constants.getColoredSpanned(st.getPhoneme(), Color.RED) + " ";

                    StringBuffer buff = new StringBuffer(temp);
                    buff.insert(subPosition, coloredPhoneme);

                    pronunciationRepresentation += "<u>" + buff.toString() + "</u> ";

                    changed = true;
                    break;
                }
            }
            if (changed == false)
                pronunciationRepresentation +=  "<u>" + p + "</u> ";
        }

        this.nativeFeedbacks.setText(Html.fromHtml(pronunciationRepresentation));
    }
}
