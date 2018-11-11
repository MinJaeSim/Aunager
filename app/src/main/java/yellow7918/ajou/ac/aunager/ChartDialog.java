package yellow7918.ajou.ac.aunager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import yellow7918.ajou.ac.aunager.routine.Routine;


public class ChartDialog extends DialogFragment {

    private ProgressDialog progressDialog;
    private BarChart chart;

    private List<Routine> routineList;
    private List<Integer> scoreList;
    private ArrayList<BarEntry> secondEntryList;
    private ArrayList<String> xVal;

    private int ave;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_chart, null);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        progressDialog = new ProgressDialog(getContext());

        routineList = new ArrayList<>();
        scoreList = new ArrayList<>();
        secondEntryList = new ArrayList<>();
        xVal = new ArrayList<>();
        chart = view.findViewById(R.id.bar_graph);
        showProgressDialog("잠시만 기다려 주세요.");

        TextView textView = view.findViewById(R.id.comment);

        Query secondQuery = db.collection("Routine").whereEqualTo("email", user.getEmail());
        Task<QuerySnapshot> task = secondQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (documentSnapshots.isEmpty()) {
                    Routine data = new Routine();
                    data.setDate(System.currentTimeMillis());
                    routineList.add(data);

                    return;
                }

                int sum = 0;
                for (DocumentSnapshot d : documentSnapshots) {
                    Routine r = d.toObject(Routine.class);
                    routineList.add(r);
                    int h = Integer.parseInt(r.getSleepHour().substring(0, 2).trim()) * 60;
                    int m = Integer.parseInt(r.getSleepHour().substring(2, 4).trim());

                    sum += h + m;
                }
                ave = sum / routineList.size();
                Collections.sort(routineList, (o1, o2) -> Long.compare(o1.getDate(), o2.getDate()));
            }
        });

        Task<Void> t = Tasks.whenAll(task);
        t.addOnCompleteListener(task1 -> {

            for (Routine r : routineList) {
                int score = 0;
                if (r.getWeather().equals("맑음"))
                    score += 3;
                else if (r.getWeather().equals("흐림"))
                    score += 2;
                else if (r.getWeather().equals("눈"))
                    score += 1;
                else if (r.getWeather().equals("비"))
                    score += 0;

                if (r.isToilet())
                    score += 2;

                if (r.isMedicine())
                    score += 1;

                int h = Integer.parseInt(r.getSleepHour().substring(0, 2).trim()) * 60;
                int m = Integer.parseInt(r.getSleepHour().substring(2, 4).trim());

                if (ave > h + m)
                    score -= 1;
                else
                    score += 1;

                score = score > 10 ? 10 : score;
                scoreList.add(score);
            }


            initBarChart(chart);
            dismissProgressDialog();
        });

        return new AlertDialog.Builder(getActivity()).setView(view).create();
    }

    private void initBarChart(BarChart chart) {
        chart.setScaleEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(Color.WHITE);
        chart.setTouchEnabled(true);

        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        chart.setMaxHighlightDistance(10);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);

        xAxis.setDrawAxisLine(false);
        xAxis.setAxisLineColor(Color.rgb(0, 0, 0));
        xAxis.setAxisLineWidth(2f);
        xAxis.setDrawGridLines(true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setEnabled(false);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        Legend l = chart.getLegend();
        l.setEnabled(false);

        setBarData(scoreList.size(), chart);

        if (scoreList.size() < 6)
            chart.setVisibleXRange(0, scoreList.size() + 1);
        else
            chart.setVisibleXRange(0, 6);

        chart.moveViewToX(scoreList.size() - 2);
    }

    private void setBarData(int count, BarChart mChart) {
        secondEntryList.clear();
        xVal.clear();

        SimpleDateFormat dayTime = new SimpleDateFormat("MM월 dd일", Locale.KOREA);
        for (int i = 0; i < count; i++) {
            xVal.add(dayTime.format(new Date(routineList.get(i).getDate())));
        }

        for (int i = 0; i < count; i++) {
            int val = scoreList.get(i);
            secondEntryList.add(new BarEntry(i, val, xVal.get(i)));
        }

        XAxis x = mChart.getXAxis();
        x.setValueFormatter(new IndexAxisValueFormatter(xVal));

//        Routine lastScoreData = scoreList.get(count - 1);
//        int lastScore = lastScoreData.getCore() + lastScoreData.getLungCapacity() + lastScoreData.getStrengthDown() + lastScoreData.getStrengthUp() + lastScoreData.getStrengthEndurance();
//        setComment(lastScore);

        BarDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);

            set1.setValues(secondEntryList);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {

            set1 = new BarDataSet(secondEntryList, "DataSet 1");
            set1.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return (int) value + " 점";
                }
            });

            // 바 색갈
            set1.setColors(Color.rgb(198, 255, 141), Color.rgb(255, 248, 140), Color.rgb(255, 211, 141), Color.rgb(140, 235, 255), Color.rgb(255, 142, 156));

            set1.setHighLightColor(Color.rgb(0, 0, 0));
//            set1.setDrawHorizontalHighlightIndicator(false);
//            set1.setFillFormatter(new IFillFormatter() {
//                @Override
//                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
//                    return -10;
//                }
//            });

            // create a data object with the datasets
            BarData data = new BarData(set1);
            data.setValueTextSize(9f);
            data.setDrawValues(true);
            data.setBarWidth(0.4f);

            // set data
            mChart.setData(data);

            dismissProgressDialog();
        }
    }

    public void showProgressDialog(String text) {
        progressDialog.setMessage(text);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }
}