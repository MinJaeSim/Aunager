package yellow7918.ajou.ac.aunager.routine;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.Calendar;
import java.util.Date;

import yellow7918.ajou.ac.aunager.R;

public class RoutineFragment extends Fragment {
    private MaterialCalendarView calendarView;

    private EventDecorator decorator;

    private int year;
    private int month;
    private int day;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routine, container, false);


        TextView routineDate = view.findViewById(R.id.routine_date);
        TextView routineDetail1 = view.findViewById(R.id.detail1);
        TextView routineDetail2 = view.findViewById(R.id.detail2);

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

        decorator = new EventDecorator(Color.GREEN, today, getContext());
        calendarView.addDecorator(decorator);

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView, @NonNull CalendarDay calendarDay, boolean b) {
//                Toast.makeText(getContext(), calendarDay.toString(), Toast.LENGTH_SHORT).show();
                year = calendarDay.getYear();
                month = calendarDay.getMonth() + 1;
                day = calendarDay.getDay();

                String shot_Day = year + "년 " + month + "월 " + day + "일";
                routineDate.setText(shot_Day);
                calendarView.clearSelection();
                calendarView.removeDecorator(decorator);

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

        routineDetail1.setText(" 날씨 : 흐림 \n 수면시간 : 6시간 \n 화장실 이용 여부 : O \n 약 복용 : X \n\n 전체적인 컨디션 : 상");
        routineDetail2.setText(" 전달 사항 : \n 아침을 조금 먹음");

        // 수면시간, 날씨, 화장실 이용 여부, 약 복용 여부, 특이 사항
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

}
