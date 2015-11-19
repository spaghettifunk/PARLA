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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import davideberdin.goofing.controllers.SentenceTuple;
import davideberdin.goofing.controllers.Tuple;
import davideberdin.goofing.controllers.User;
import davideberdin.goofing.utilities.AppWindowManager;
import davideberdin.goofing.utilities.AutoResizeTextView;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.IOUtilities;
import davideberdin.goofing.utilities.UserLocalStore;

public class FeedbacksActivity extends AppCompatActivity implements View.OnClickListener {
    //region VARIABLES
    private ArrayList<String> phonemes;
    private ArrayList<Tuple> vowelStress;
    private String resultWER;
    private byte[] pitchChartByte;
    private byte[] vowelChartByte;

    private AutoResizeTextView nativeFeedbacks;
    private AutoResizeTextView nativeFeedbacksSentence;

    private AutoResizeTextView userFeedbacks;
    private AutoResizeTextView userFeedbacksSentence;
    private AutoResizeTextView resultWERFeedbacks;

    private ImageView pitchChart;
    private ImageView vowelChart;
    private User loggedUser;
    private UserLocalStore userLocalStore;

    private ImageButton infoFeedbacksButton;
    private RelativeLayout feedbacksRelativeLayout;

    private Button historyButton;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbacks);

        this.feedbacksRelativeLayout = (RelativeLayout) findViewById(R.id.feedbacksRelativeLayout);
        this.feedbacksRelativeLayout.setVisibility(View.VISIBLE);

        Bundle b = getIntent().getExtras();

        this.resultWER = b.getString(Constants.GET_WER_POST);
        this.phonemes = b.getStringArrayList(Constants.GET_PHONEMES_POST);
        this.vowelStress = b.getParcelableArrayList(Constants.GET_VOWELSTRESS_POST);
        this.pitchChartByte = b.getByteArray(Constants.GET_PITCH_CHART_POST);
        this.vowelChartByte = b.getByteArray(Constants.GET_VOWEL_CHART_POST);

        this.userLocalStore = new UserLocalStore(this);
        this.loggedUser = this.userLocalStore.getLoggedUser();

        // Feedbacks
        this.infoFeedbacksButton = (ImageButton) findViewById(R.id.infoImageButton);
        this.infoFeedbacksButton.setOnClickListener(this);

        this.nativeFeedbacks = (AutoResizeTextView) findViewById(R.id.feedbacksNative);
        this.nativeFeedbacks.setMaxLines(1);

        this.nativeFeedbacksSentence = (AutoResizeTextView) findViewById(R.id.feedbacksNativeSentence);
        this.nativeFeedbacksSentence.setMaxLines(1);
        this.nativeFeedbacksSentence.setLineSpacing(2.0f, 5.0f);

        this.nativeFeedbacksSentence.setText(this.loggedUser.GetCurrentSentence());

        this.userFeedbacks = (AutoResizeTextView) findViewById(R.id.feedbacksUser);
        this.userFeedbacks.setMaxLines(1);

        this.userFeedbacksSentence = (AutoResizeTextView) findViewById(R.id.feedbacksUserSentence);
        this.userFeedbacksSentence.setMaxLines(1);

        this.resultWERFeedbacks = (AutoResizeTextView) findViewById(R.id.infoWERTextView);
        this.resultWERFeedbacks.setMaxLines(1);
        this.resultWERFeedbacks.setText(this.resultWER);

        // color strings
        fillNativeFeedbacks();
        fillUserFeedbacks();

        this.pitchChart = (ImageView) findViewById(R.id.pitchChartImageView);
        Bitmap pitchBitmap = BitmapFactory.decodeByteArray(this.pitchChartByte, 0, this.pitchChartByte.length);
        this.pitchChart.setImageBitmap(pitchBitmap);
        this.pitchChart.setOnClickListener(this);

        // Build feedbacks image
        this.vowelChart = (ImageView) findViewById(R.id.vowelChartImageView);
        Bitmap vowelBitmap = BitmapFactory.decodeByteArray(this.vowelChartByte, 0, this.vowelChartByte.length);
        this.vowelChart.setImageBitmap(vowelBitmap);
        this.vowelChart.setOnClickListener(this);

        // History Button
        this.historyButton = (Button) findViewById(R.id.historyButton);
        this.historyButton.setOnClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pitchChartImageView:
                Intent pitchIntent = new Intent(FeedbacksActivity.this, FullscreenImageActivity.class);
                pitchIntent.putExtra("isPitch", true);
                pitchIntent.putExtra("pitchchart", this.pitchChartByte);
                startActivity(pitchIntent);
                break;

            case R.id.vowelChartImageView:
                Intent vowelIntent = new Intent(FeedbacksActivity.this, FullscreenImageActivity.class);
                vowelIntent.putExtra("isPitch", false);
                vowelIntent.putExtra("vowelchart", this.vowelChartByte);
                startActivity(vowelIntent);
                break;

            case R.id.infoImageButton:

                AppWindowManager.showInfoFeedbacksDialog(this, this.loggedUser.GetCurrentSentence());

                break;
            case R.id.historyButton:
                Intent historyIntent = new Intent(FeedbacksActivity.this, HistoryActivity.class);
                historyIntent.putExtra("sentence", this.loggedUser.GetCurrentSentence());
                startActivity(historyIntent);

                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_listening, menu);
        return true;
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
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    private void fillNativeFeedbacks() {
        String currentSentence = this.loggedUser.GetCurrentSentence();
        SentenceTuple<String, String, ArrayList<Tuple>> sentenceNativeFeedbacks = Constants.nativeSentenceInfo.get(currentSentence);

        this.nativeFeedbacks.setText(sentenceNativeFeedbacks.getPhonemes());

        // change color based on stress position
        String currentSentenceNoDigits = sentenceNativeFeedbacks.getPhonemes().replaceAll("\\d", "");

        List<String> allPhonemes = Arrays.asList(currentSentenceNoDigits.split("\\s+"));
        ArrayList<Tuple> stress = sentenceNativeFeedbacks.getStress();

        String pronunciationRepresentation = "";
        for (String p : allPhonemes) {
            boolean changed = false;
            for (Tuple st : new ArrayList<Tuple>(stress)) {
                if (p.contains(st.getFirst()) && st.getSecond().equals("1")) {
                    stress.remove(st);

                    int subPosition = p.indexOf(st.getFirst());
                    String temp = p.replace(st.getFirst(), "");

                    String coloredPhoneme = "<big>" + Constants.getColoredSpanned(st.getFirst(), Color.RED) + "</big>";

                    StringBuffer buff = new StringBuffer(temp);
                    buff.insert(subPosition, coloredPhoneme);

                    pronunciationRepresentation += "<u>" + buff.toString() + "</u>&#160";

                    changed = true;
                    break;
                }
            }
            if (!changed)
                pronunciationRepresentation += "<u>" + p + "</u>&#160";
        }

        this.nativeFeedbacks.setText(Html.fromHtml(pronunciationRepresentation));
    }

    private void fillUserFeedbacks() {
        String currentSentence = this.loggedUser.GetCurrentSentence();

        String userPhonemes = "";
        for (String p : phonemes)
            userPhonemes += p.replace(" ", "") + " ";
        userPhonemes = userPhonemes.replaceAll("\\d", "");
        this.userFeedbacks.setText(userPhonemes);

        // change color based on stress position
        List<String> allPhonemes = Arrays.asList(userPhonemes.split("\\s+"));
        ArrayList<Tuple> stress = this.vowelStress;

        String pronunciationRepresentation = "";
        for (String p : allPhonemes) {
            boolean changed = false;
            for (Tuple st : new ArrayList<Tuple>(stress)) {
                if (p.contains(st.getFirst()) && st.getSecond().equals("1")) {
                    stress.remove(st);

                    int subPosition = p.indexOf(st.getFirst());
                    String temp = p.replace(st.getFirst(), "");

                    String coloredPhoneme = "<big>" + Constants.getColoredSpanned(st.getFirst(), Color.RED) + "</big>";

                    StringBuffer buff = new StringBuffer(temp);
                    buff.insert(subPosition, coloredPhoneme);

                    pronunciationRepresentation += "<u>" + buff.toString() + "</u>&#160";

                    changed = true;
                    break;
                }
            }
            if (changed == false)
                pronunciationRepresentation += "<u>" + p + "</u>&#160";
        }

        this.userFeedbacks.setText(Html.fromHtml(pronunciationRepresentation));
        this.userFeedbacksSentence.setText(currentSentence);
    }
}
