package davideberdin.goofing;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import davideberdin.goofing.controllers.MyMarkerView;
import davideberdin.goofing.utilities.IOUtilities;
import davideberdin.goofing.utilities.Logger;

public class FullscreenImageActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final String TAG = "Touch";
    @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f, MAX_ZOOM = 1f;

    // These matrices will be used to scale points of the lineChart
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    // The 3 states (events) which the user is trying to perform
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // these PointF objects are used to record the point(s) the user is touching
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    private ImageView chart = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_fullscreen_image);

        Bundle b = getIntent().getExtras();

        if (b.getBoolean("isPitch")) {
            ArrayList<String> YValuesNative = b.getStringArrayList("YValuesNative");
            ArrayList<String> YValuesUser = b.getStringArrayList("YValuesUser");

            this.generatePitchChart(YValuesNative, YValuesUser);
        }
        else {

            byte[] chartByte = b.getByteArray("vowelchart");

            // Build feedbacks lineChart
            this.chart = (ImageView) findViewById(R.id.fullscreen_image);
            Bitmap bmp = BitmapFactory.decodeByteArray(chartByte, 0, chartByte.length);
            this.chart.setImageBitmap(bmp);
            this.chart.setOnTouchListener(this);
            this.chart.setVisibility(View.VISIBLE);
        }

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

        ScatterChart chart = (ScatterChart) findViewById(R.id.pitchChartFS);
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

        chart.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Matrix matrix = new Matrix();

        // Checks the orientation of the screen
        if (this.chart != null && this.chart.getVisibility() == View.VISIBLE) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                this.chart.setScaleType(ImageView.ScaleType.MATRIX);   //required
                matrix.postRotate(90f, this.chart.getDrawable().getBounds().width() / 2, this.chart.getDrawable().getBounds().height() / 2);
                this.chart.setImageMatrix(matrix);

                // Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                // Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();

                this.chart.setScaleType(ImageView.ScaleType.MATRIX);   //required
                matrix.postRotate(-90f, this.chart.getDrawable().getBounds().width() / 2, this.chart.getDrawable().getBounds().height() / 2);
                this.chart.setImageMatrix(matrix);
            }
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
                Logger.WriteOnReport("FullscreenImageActivity", "Clicked on back BUTTON");
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        dumpEvent(event);
        // Handle touch events here...

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:   // first finger down only
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG"); // write to LogCat
                mode = DRAG;
                break;

            case MotionEvent.ACTION_UP: // first finger lifted

            case MotionEvent.ACTION_POINTER_UP: // second finger lifted

                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;

            case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");

                    Logger.WriteOnReport("FullscreenImageActivity", "pinch to ZOOM");
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); // create the transformation in the matrix  of points
                } else if (mode == ZOOM) {
                    // pinch zooming
                    float newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 5f) {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist; // setting the scaling of the
                        // matrix...if scale > 1 means
                        // zoom in...if scale < 1 means
                        // zoom out
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix); // display the transformation on screen

        return true; // indicate event was handled
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                Logger.WriteOnReport("FullscreenActivity", "Action was DOWN");
                return true;
            case (MotionEvent.ACTION_MOVE):
                Logger.WriteOnReport("FullscreenActivity", "Action was MOVE");
                return true;
            case (MotionEvent.ACTION_UP):
                Logger.WriteOnReport("FullscreenActivity", "Action was UP");
                return true;
            case (MotionEvent.ACTION_CANCEL):
                Logger.WriteOnReport("FullscreenActivity", "Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE):
                Logger.WriteOnReport("FullscreenActivity", "Movement occurred outside bounds of current screen element");
                return true;
            case (MotionEvent.ACTION_SCROLL):
                Logger.WriteOnReport("FullscreenActivity", "Action was SCROLL");
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
            ParlaApplication.getInstance().trackException(e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            ParlaApplication.getInstance().trackException(e);
            e.printStackTrace();
        } catch (JSONException e) {
            ParlaApplication.getInstance().trackException(e);
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
            ParlaApplication.getInstance().trackException(e);
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
            ParlaApplication.getInstance().trackException(e);
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
            ParlaApplication.getInstance().trackException(e);
            e.printStackTrace();
        }
    }
    //endregion

    /*
     * --------------------------------------------------------------------------
     * Method: spacing Parameters: MotionEvent Returns: float Description:
     * checks the spacing between the two fingers on touch
     * ----------------------------------------------------
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /*
     * --------------------------------------------------------------------------
     * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
     * Description: calculates the midpoint between the two fingers
     * ------------------------------------------------------------
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * Show an event in the LogCat view, for debugging
     */
    private void dumpEvent(MotionEvent event) {
        String names[] = {"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"};
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);

        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }

        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }

        sb.append("]");
        Log.d("Touch Events ---------", sb.toString());
    }
}
