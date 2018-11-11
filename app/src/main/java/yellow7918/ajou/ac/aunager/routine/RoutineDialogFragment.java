package yellow7918.ajou.ac.aunager.routine;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import yellow7918.ajou.ac.aunager.R;


public class RoutineDialogFragment extends DialogFragment {
    private int getHour;
    private int getMinute;

    private RadioButton weatherSunny;
    private RadioButton weatherCloudy;
    private RadioButton weatherRainy;
    private RadioButton weatherSnow;

    private RadioButton medicineTrue;
    private RadioButton medicineFalse;

    private RadioButton toiletTrue;
    private RadioButton toiletFalse;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_routine, container, false);

        TextView title = view.findViewById(R.id.routine_title);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String email = getArguments().getString("EMAIL");
        long time = getArguments().getLong("TIME");

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM월 dd일", Locale.getDefault());
        Date d = new Date(time);
        String s = dateFormat.format(d);
        title.setText(String.format("%s의 %s 일정 등록", user.getDisplayName(), s));

        TextView okButton = view.findViewById(R.id.dialog_ok_button);
        TextView cancelButton = view.findViewById(R.id.dialong_cancel_button);

        EditText extraInfo = view.findViewById(R.id.text_extra_info);

        weatherSunny = view.findViewById(R.id.weather_1);
        weatherCloudy = view.findViewById(R.id.weather_2);
        weatherRainy = view.findViewById(R.id.weather_3);
        weatherSnow = view.findViewById(R.id.weather_4);

        medicineTrue = view.findViewById(R.id.medicine_true);
        medicineFalse = view.findViewById(R.id.medicine_false);

        toiletTrue = view.findViewById(R.id.toilet_true);
        toiletFalse = view.findViewById(R.id.toilet_false);

        EditText hourEditText = view.findViewById(R.id.time_hour_edit_text);
        Calendar c = Calendar.getInstance();
        getHour = c.get(Calendar.HOUR_OF_DAY);

        hourEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0)
                    getHour = Integer.parseInt(charSequence.toString());
                else
                    getHour = -1;

                if (getHour > 23) {
                    getHour = 23;
                    hourEditText.setText("" + getHour);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        EditText minEditText = view.findViewById(R.id.time_min_edit_text);
        getMinute = c.get(Calendar.MINUTE);
        minEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0)
                    getMinute = Integer.parseInt(charSequence.toString());
                else
                    getMinute = -1;

                if (getMinute > 59) {
                    getMinute = 59;
                    minEditText.setText("" + getMinute);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getMinute != -1 && getHour != -1) {
                    String info = "";

                    if (extraInfo.getText().length() > 0)
                        info = extraInfo.getText().toString();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String weather = getWeather();
                    String t = String.format(Locale.getDefault(), "%2d%2d", getHour, getMinute);
                    boolean toilet = toiletTrue.isChecked();
                    boolean medicine = medicineTrue.isChecked();
                    Routine text = new Routine(time, email, weather, t, toilet, 0, medicine, info);

                    db.collection("Routine").document(email + "." + time)
                            .set(text)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Snackbar.make(view, "일정 등록에 성공하였습니다.", Snackbar.LENGTH_SHORT).show();
                                } else {
                                    Snackbar.make(view, "일정 등록에 실패하였습니다.", Snackbar.LENGTH_SHORT).show();
                                }
                            });

                    Intent intent = new Intent();
                    intent.putExtra("ROUTINE", text);
                    getTargetFragment().onActivityResult(
                            getTargetRequestCode(), Activity.RESULT_OK, intent);
                    dismiss();
                } else {
                    Snackbar.make(getView(), "빈칸을 작성해 주세요.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(v -> dismiss());

        return view;
    }

    private String getWeather() {
        if (weatherSunny.isChecked())
            return "맑음";
        else if (weatherCloudy.isChecked())
            return "흐림";
        else if (weatherRainy.isChecked())
            return "비";
        else
            return "눈";
    }

    public static RoutineDialogFragment newInstance(String email, long time) {
        RoutineDialogFragment f = new RoutineDialogFragment();

        Bundle args = new Bundle();
        args.putString("EMAIL", email);
        args.putLong("TIME", time);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();

        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
