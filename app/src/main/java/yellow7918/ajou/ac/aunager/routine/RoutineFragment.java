package yellow7918.ajou.ac.aunager.routine;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import yellow7918.ajou.ac.aunager.ChartDialog;
import yellow7918.ajou.ac.aunager.R;

public class RoutineFragment extends Fragment {
    private MaterialCalendarView calendarView;

    private ProgressDialog progressDialog;

    private EventDecorator decorator;

    private TextView routineDate;
    private TextView routineDetail1;
    private TextView routineDetail2;

    private int year;
    private int month;
    private int day;

    private HashMap<Long, Routine> hashMap;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routine, container, false);

        progressDialog = new ProgressDialog(getContext());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();

        routineDate = view.findViewById(R.id.routine_date);
        routineDetail1 = view.findViewById(R.id.detail1);
        routineDetail2 = view.findViewById(R.id.detail2);
        LinearLayout routineDetail = view.findViewById(R.id.chart_button);

        routineDetail.setOnClickListener(v -> {
            FragmentManager fm = getFragmentManager();
            DialogFragment dialogFragment = new ChartDialog();
            dialogFragment.setTargetFragment(RoutineFragment.this, 100);
            dialogFragment.show(fm, "InputDialog");
        });

        calendarView = view.findViewById(R.id.calendarView);
        calendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                new OneDayDecorator()
        );

        CalendarDay today = CalendarDay.today();
        year = today.getYear();
        month = today.getMonth() + 1;
        day = today.getDay();

        String shot_Day = year + "년 " + month + "월 " + day + "일";
        routineDate.setText(shot_Day);

        hashMap = new HashMap<>();


        decorator = new EventDecorator(Color.GREEN, today, getContext());
        calendarView.addDecorator(decorator);

        showProgressBar("잠시만 기다려 주세요.");

        db.collection("Routine").whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Routine r = document.toObject(Routine.class);
                            hashMap.put(r.getDate(), r);
                        }
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                        Date d = new Date(System.currentTimeMillis());
                        String s = dateFormat.format(d);
                        Date date;
                        try {
                            date = dateFormat.parse(s);
                            if (hashMap.containsKey(date.getTime())) {
                                Routine r = hashMap.get(date.getTime());

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

                                if (7 * 60 > h + m)
                                    score -= 1;
                                else
                                    score += 1;


                                routineDate.setTextColor(Color.rgb(10, 230, 10));
                                routineDetail1.setText(String.format(Locale.getDefault(), " 날씨 : %s \n 수면시간 : %s시간 %s분 \n 화장실 이용 여부 : %s \n 약 복용 : %s \n\n 전체적인 컨디션 : %s",
                                        r.getWeather(), r.getSleepHour().substring(0, 2), r.getSleepHour().substring(2, 4),
                                        r.isToilet() ? "O" : "X", r.isMedicine() ? "O" : "X", score > 7 ? "상" : score > 4 ? "중" : "하"));
                                routineDetail2.setText(String.format(Locale.getDefault(), "전달 사항 : \n %s", r.getExtraInfo()));
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        hideProgressBar();
                    } else {
                        Snackbar.make(getView(), "정보를 읽어오는데 실패하였습니다.", Snackbar.LENGTH_SHORT).show();
                    }
                });

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView, @NonNull CalendarDay calendarDay, boolean b) {
                year = calendarDay.getYear();
                month = calendarDay.getMonth() + 1;
                day = calendarDay.getDay();

                String shot_Day = year + "년 " + month + "월 " + day + "일";
                routineDate.setText(shot_Day);
                calendarView.clearSelection();
                calendarView.removeDecorator(decorator);

                String d = String.format(Locale.getDefault(), "%d%d%d", year, month, day);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

                try {
                    Date date = dateFormat.parse(d);
//                    Toast.makeText(getContext(), date.getTime() + "", Toast.LENGTH_SHORT).show();

                    if (hashMap.containsKey(date.getTime())) {
                        Routine r = hashMap.get(date.getTime());

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

                        if (7 * 60 > h + m)
                            score -= 1;
                        else
                            score += 1;

                        routineDate.setTextColor(Color.rgb(10, 230, 10));
                        routineDetail1.setText(String.format(Locale.getDefault(), " 날씨 : %s \n 수면시간 : %s시간 %s분 \n 화장실 이용 여부 : %s \n 약 복용 : %s \n\n 전체적인 컨디션 : %s",
                                r.getWeather(), r.getSleepHour().substring(0, 2), r.getSleepHour().substring(2, 4),
                                r.isToilet() ? "O" : "X", r.isMedicine() ? "O" : "X", score > 7 ? "상" : score > 4 ? "중" : "하"));
                        routineDetail2.setText(String.format(Locale.getDefault(), "전달 사항 : \n %s", r.getExtraInfo()));

                    } else {
                        routineDate.setTextColor(Color.rgb(230, 10, 10));
                        routineDetail1.setText(" 일상을 등록해 주세요.");
                        routineDetail2.setText("");

                        routineDetail1.setOnClickListener(v -> {
                            FragmentManager fm = getFragmentManager();
                            DialogFragment dialogFragment = RoutineDialogFragment.newInstance(user.getEmail(), date.getTime());
                            dialogFragment.setTargetFragment(RoutineFragment.this, 100);
                            dialogFragment.show(fm, "InputDialog");
                        });
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                decorator = new EventDecorator(Color.GREEN, calendarDay, getContext());
                calendarView.addDecorator(decorator);
            }
        });

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView materialCalendarView, CalendarDay calendarDay) {
//                Toast.makeText(getContext(), calendarDay.toString() + "MMMMM", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    static class OneDayDecorator implements DayViewDecorator {

        private CalendarDay date;

        public OneDayDecorator() {
            date = CalendarDay.today();
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return date != null && day.equals(date);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new StyleSpan(Typeface.BOLD));
            view.addSpan(new RelativeSizeSpan(1.4f));
        }

        public void setDate(Date date) {
            this.date = CalendarDay.from(Calendar.getInstance().getTimeInMillis());
        }
    }

    static class SundayDecorator implements DayViewDecorator {

        private final Calendar calendar = Calendar.getInstance();

        public SundayDecorator() {
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            day.copyTo(calendar);
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            return weekDay == Calendar.SUNDAY;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.RED));
        }
    }

    static class SaturdayDecorator implements DayViewDecorator {

        private final Calendar calendar = Calendar.getInstance();

        public SaturdayDecorator() {
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            day.copyTo(calendar);
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            return weekDay == Calendar.SATURDAY;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.BLUE));
        }
    }

    static class EventDecorator implements DayViewDecorator {

        private final Drawable drawable;
        private int color;
        private CalendarDay dates;

        public EventDecorator(int color, CalendarDay dates, Context context) {
            drawable = context.getResources().getDrawable(R.drawable.more, null);
            this.color = color;
            this.dates = dates;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return day.equals(dates);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setSelectionDrawable(drawable);
            view.addSpan(new ForegroundColorSpan(Color.rgb(150, 220, 72)));
        }
    }

    private void showProgressBar(String text) {
        progressDialog.setMessage(text);
        progressDialog.show();
    }

    private void hideProgressBar() {
        progressDialog.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 100:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        Toast.makeText(getContext(), "완료", Toast.LENGTH_SHORT).show();
                        Routine r = ((Routine) data.getSerializableExtra("ROUTINE"));
                        hashMap.put(r.getDate(), r);

                        routineDate.setTextColor(Color.rgb(10, 230, 10));
                        routineDetail1.setText(String.format(Locale.getDefault(), " 날씨 : %s \n 수면시간 : %s시간 %s분 \n 화장실 이용 여부 : %s \n 약 복용 : %s \n\n 전체적인 컨디션 : %s",
                                r.getWeather(), r.getSleepHour().substring(0, 2), r.getSleepHour().substring(2, 4),
                                r.isToilet() ? "O" : "X", r.isMedicine() ? "O" : "X", "계산중"));
                        routineDetail2.setText(String.format(Locale.getDefault(), "전달 사항 : \n %s", r.getExtraInfo()));
                    }
                }
                break;
        }
    }

}
