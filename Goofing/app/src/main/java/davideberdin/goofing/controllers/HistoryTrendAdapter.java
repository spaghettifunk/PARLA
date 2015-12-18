package davideberdin.goofing.controllers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import davideberdin.goofing.R;
import davideberdin.goofing.utilities.AutoResizeTextView;

public class HistoryTrendAdapter extends RecyclerView.Adapter<HistoryTrendAdapter.TrendViewHolder> {

    private ArrayList<TrendCard> cardsList;

    public HistoryTrendAdapter(ArrayList<TrendCard> contactList) {
        this.cardsList = contactList;
    }

    @Override
    public int getItemCount() {
        return cardsList.size();
    }

    @Override
    public void onBindViewHolder(TrendViewHolder cardViewHolder, int i) {
        TrendCard cl = cardsList.get(i);

        float[] imageFloat = cl.getImageFloatArray();
        String[] time = cl.getImageTimeArray();

        LineData data = new LineData(getXAxisValues(time), getDataSet(imageFloat));

        cardViewHolder.chartTitle.setText(cl.getImageTitle() + " - (lower is better)");  // title

        // chart parameters
        cardViewHolder.lineChart.setMarkerView(new MyMarkerView(cardViewHolder.context, R.layout.my_marker_layout));
        cardViewHolder.lineChart.setAutoScaleMinMaxEnabled(true);

        cardViewHolder.lineChart.getAxisLeft().setDrawGridLines(false);
        cardViewHolder.lineChart.getAxisRight().setDrawGridLines(false);
        cardViewHolder.lineChart.getXAxis().setDrawGridLines(false);
        cardViewHolder.lineChart.getXAxis().setDrawAxisLine(true);
        cardViewHolder.lineChart.setDrawBorders(true);
        cardViewHolder.lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        cardViewHolder.lineChart.getXAxis().setAvoidFirstLastClipping(true);

        cardViewHolder.lineChart.getLegend().setEnabled(false);   // Hide the legend

        cardViewHolder.lineChart.setDescription("");
        cardViewHolder.lineChart.setData(data);
        cardViewHolder.lineChart.animateXY(1000, 1000);
        cardViewHolder.lineChart.invalidate();
    }

    private LineDataSet getDataSet(float[] imageFloat) {
        ArrayList<Entry> values = new ArrayList<>();

        int index = 0;
        for (float val : imageFloat) {
            Entry e = new Entry(val, index++);
            values.add(e);
        }

        LineDataSet dataSet = new LineDataSet(values, "");
        dataSet.setColor(ColorTemplate.LIBERTY_COLORS[2]);
        dataSet.setFillColor(ColorTemplate.LIBERTY_COLORS[4]);
        dataSet.setDrawFilled(true);
        dataSet.setDrawValues(false);

        return dataSet;
    }

    private ArrayList<String> getXAxisValues(String[] time) {
        ArrayList<String> xAxis = new ArrayList<>();
        for (String t: time)
            xAxis.add(t);

        return xAxis;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public TrendViewHolder onCreateViewHolder(ViewGroup parent, int i) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trend_card, parent, false);
        itemView.setMinimumWidth(parent.getMeasuredWidth());

        return new TrendViewHolder(itemView);
    }

    public static class TrendViewHolder extends RecyclerView.ViewHolder {

        protected LineChart lineChart;
        protected Context context;
        protected AutoResizeTextView chartTitle;

        public TrendViewHolder(View v) {
            super(v);
            lineChart = (LineChart) v.findViewById(R.id.trend_card_chart);
            chartTitle = (AutoResizeTextView) v.findViewById(R.id.trendChartTitle);
            chartTitle.setSingleLine(true);

            context = v.getContext();
        }
    }
}
