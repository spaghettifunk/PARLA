package davideberdin.goofing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import davideberdin.goofing.controllers.MyMarkerView;
import davideberdin.goofing.controllers.SentenceTuple;
import davideberdin.goofing.controllers.Tuple;
import davideberdin.goofing.controllers.User;
import davideberdin.goofing.utilities.AppWindowManager;
import davideberdin.goofing.utilities.AutoResizeTextView;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.IOUtilities;
import davideberdin.goofing.utilities.Logger;
import davideberdin.goofing.utilities.UserLocalStore;

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener {

    //region VARIABLES
    private ArrayList<String> phonemes;
    private ArrayList<Tuple> vowelStress;

    // interactive chart
    private ArrayList<String> YValuesNative;
    private ArrayList<String> YValuesUser;

    private byte[] vowelChartByte;

    private AutoResizeTextView nativeFeedback;

    private AutoResizeTextView userFeedback;
    private AutoResizeTextView userFeedbackSentence;

    private ImageView pitchChart;
    private User loggedUser;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbacks);

        RelativeLayout feedbackRelativeLayout = (RelativeLayout) findViewById(R.id.feedbacksRelativeLayout);
        feedbackRelativeLayout.setVisibility(View.VISIBLE);

        Bundle b = getIntent().getExtras();

        String resultWER = b.getString(Constants.GET_WER_POST);
        this.phonemes = b.getStringArrayList(Constants.GET_PHONEMES_POST);
        this.vowelStress = b.getParcelableArrayList(Constants.GET_VOWEL_STRESS_POST);
        this.vowelChartByte = b.getByteArray(Constants.GET_VOWEL_CHART_POST);

        this.YValuesNative = b.getStringArrayList("YValuesNative");
        this.YValuesUser = b.getStringArrayList("YValuesUser");

        UserLocalStore userLocalStore = new UserLocalStore(this);
        this.loggedUser = userLocalStore.getLoggedUser();

        // Feedback
        ImageButton infoFeedbackButton = (ImageButton) findViewById(R.id.infoImageButton);
        infoFeedbackButton.setOnClickListener(this);

        ImageButton fullscreenButton = (ImageButton) findViewById(R.id.fullScreenButton);
        fullscreenButton.setOnClickListener(this);

        this.nativeFeedback = (AutoResizeTextView) findViewById(R.id.feedbacksNative);
        this.nativeFeedback.setMaxLines(1);

        AutoResizeTextView nativeFeedbackSentence = (AutoResizeTextView) findViewById(R.id.feedbacksNativeSentence);
        nativeFeedbackSentence.setMaxLines(1);
        nativeFeedbackSentence.setLineSpacing(2.0f, 5.0f);

        nativeFeedbackSentence.setText(this.loggedUser.GetCurrentSentence());

        this.userFeedback = (AutoResizeTextView) findViewById(R.id.feedbacksUser);
        this.userFeedback.setMaxLines(1);

        this.userFeedbackSentence = (AutoResizeTextView) findViewById(R.id.feedbacksUserSentence);
        this.userFeedbackSentence.setMaxLines(1);

        AutoResizeTextView resultWERFeedback = (AutoResizeTextView) findViewById(R.id.infoWERTextView);
        resultWERFeedback.setMaxLines(1);
        resultWERFeedback.setText(resultWER);

        // color strings
        fillNativeFeedback();
        fillUserFeedback();

        // create interactive chart here
        generatePitchChart(YValuesNative, YValuesUser);

        // Build feedback image
        ImageView vowelChart = (ImageView) findViewById(R.id.vowelChartImageView);
        Bitmap vowelBitmap = BitmapFactory.decodeByteArray(this.vowelChartByte, 0, this.vowelChartByte.length);
        vowelChart.setImageBitmap(vowelBitmap);
        vowelChart.setOnClickListener(this);

        // History Button
        Button historyButton = (Button) findViewById(R.id.historyButton);
        historyButton.setOnClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void generatePitchChart(ArrayList<String> YValuesNative, ArrayList<String> YValuesUser) {

        // Y values Native
        int index = 0;
        ArrayList<ScatterDataSet> sets = new ArrayList<>();

        ArrayList<Entry> entriesNative = new ArrayList<>();
        for (Object yval : YValuesNative) {
            Double val = Double.parseDouble(yval.toString());
            Entry e = new Entry(val.floatValue(), index++);
            entriesNative.add(e);
        }

        ScatterDataSet setNative = new ScatterDataSet(entriesNative, "Native");
        setNative.setColor(ColorTemplate.JOYFUL_COLORS[0]);
        setNative.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        setNative.setScatterShapeSize(5.0f);
        setNative.setDrawValues(false);

        ArrayList<String> XLabels = new ArrayList<String>();
        for (int i = 0; i < YValuesNative.size(); i++) {
            XLabels.add("");
        }

        // Y values User
        index = 0;
        ArrayList<Entry> entriesUser = new ArrayList<>();
        for (Object yval : YValuesUser) {
            Double val = Double.parseDouble(yval.toString());
            Entry e = new Entry(val.floatValue(), index++);
            entriesUser.add(e);
        }

        ScatterDataSet setUser = new ScatterDataSet(entriesUser, "User");
        setUser.setColor(ColorTemplate.JOYFUL_COLORS[1]);
        setUser.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        setUser.setScatterShapeSize(5.0f);
        setUser.setDrawValues(false);

        // add datasets
        sets.add(setNative);
        sets.add(setUser);

        ScatterData scatterData = new ScatterData(XLabels, sets);

        ScatterChart chart = (ScatterChart) findViewById(R.id.pitchScatterChart);
        MyMarkerView mv = new MyMarkerView(this.getApplicationContext(), R.layout.my_marker_layout);

        // chart settings
        chart.setMarkerView(mv);
        chart.setDescription("");   // empty description -> use an external TextView
        chart.setAutoScaleMinMaxEnabled(true);

        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.setData(scatterData);

        chart.notifyDataSetChanged();   // notify that the dataset is changed
        chart.invalidate(); // refresh the chart
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fullScreenButton: {
                Logger.WriteOnReport("FeedbackActivity", "Clicked on pitch CHART");

                Intent pitchIntent = new Intent(FeedbackActivity.this, FullscreenImageActivity.class);
                pitchIntent.putExtra("isPitch", true);
                pitchIntent.putExtra("YValuesNative", this.YValuesNative);
                pitchIntent.putExtra("YValuesUser", this.YValuesUser);
                startActivity(pitchIntent);

                break;
            }
            case R.id.vowelChartImageView: {
                Logger.WriteOnReport("FeedbackActivity", "Clicked on vocal chart BUTTON");

                Intent vowelIntent = new Intent(FeedbackActivity.this, FullscreenImageActivity.class);
                vowelIntent.putExtra("isPitch", false);
                vowelIntent.putExtra("vowelchart", this.vowelChartByte);
                startActivity(vowelIntent);

                break;
            }
            case R.id.infoImageButton: {
                Logger.WriteOnReport("FeedbackActivity", "Clicked on info BUTTON");
                AppWindowManager.showInfoFeedbacksDialog(this, this.loggedUser.GetCurrentSentence());

                break;
            }
            case R.id.historyButton: {
                Logger.WriteOnReport("FeedbackActivity", "Clicked on history BUTTON");

                Intent historyIntent = new Intent(FeedbackActivity.this, HistoryActivity.class);
                historyIntent.putExtra("sentence", this.loggedUser.GetCurrentSentence());
                startActivity(historyIntent);

                break;
            }
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
                Logger.WriteOnReport("FeedbackActivity", "Clicked on back BUTTON");
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                Logger.WriteOnReport("FeedbackActivity", "Action was DOWN");
                return true;
            case (MotionEvent.ACTION_MOVE):
                Logger.WriteOnReport("FeedbackActivity", "Action was MOVE");
                return true;
            case (MotionEvent.ACTION_UP):
                Logger.WriteOnReport("FeedbackActivity", "Action was UP");
                return true;
            case (MotionEvent.ACTION_CANCEL):
                Logger.WriteOnReport("FeedbackActivity", "Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE):
                Logger.WriteOnReport("FeedbackActivity", "Movement occurred outside bounds of current screen element");
                return true;
            case (MotionEvent.ACTION_SCROLL):
                Logger.WriteOnReport("FeedbackActivity", "Action was SCROLL");
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    private void fillNativeFeedback() {
        String currentSentence = this.loggedUser.GetCurrentSentence();
        SentenceTuple<String, String, ArrayList<Tuple>> sentenceNativeFeedback = this.loggedUser.getNativeSentenceInfo().get(currentSentence);

        this.nativeFeedback.setText("");
        this.nativeFeedback.setText(sentenceNativeFeedback.getPhonemes());

        // change color based on stress position
        String currentSentenceNoDigits = sentenceNativeFeedback.getPhonemes().replaceAll("\\d", "");

        List<String> allPhonemes = Arrays.asList(currentSentenceNoDigits.split("\\s+"));
        ArrayList<Tuple> stress = sentenceNativeFeedback.getStress();

        String pronunciationRepresentation = "";
        for (String p : allPhonemes) {
            boolean changed = false;
            for (Tuple st : new ArrayList<>(stress)) {
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

        this.nativeFeedback.setText(Html.fromHtml(pronunciationRepresentation));
    }

    private void fillUserFeedback() {
        String currentSentence = this.loggedUser.GetCurrentSentence();

        String userPhonemes = "";
        for (String p : phonemes)
            userPhonemes += p.replace(" ", "") + " ";
        userPhonemes = userPhonemes.replaceAll("\\d", "");

        this.userFeedback.setText("");
        this.userFeedback.setText(userPhonemes);

        // change color based on stress position
        List<String> allPhonemes = Arrays.asList(userPhonemes.split("\\s+"));
        ArrayList<Tuple> stress = this.vowelStress;

        String pronunciationRepresentation = "";
        for (String p : allPhonemes) {
            boolean changed = false;
            for (Tuple st : new ArrayList<>(stress)) {
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

        this.userFeedback.setText(Html.fromHtml(pronunciationRepresentation));
        this.userFeedbackSentence.setText(currentSentence);
    }
}
