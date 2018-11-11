package yellow7918.ajou.ac.aunager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import yellow7918.ajou.ac.aunager.data.User;
import yellow7918.ajou.ac.aunager.routine.Routine;

public class MainFragment extends Fragment {

    private ProgressDialog progressDialog;

    private Date resultDate;
    private String temp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(getContext());
        showProgressDialog("잠시만 기다려 주세요");


        CircleImageView imageView = view.findViewById(R.id.profile_image_view);
        db.collection("User")
                .document(user.getEmail())
                .get().addOnSuccessListener(documentSnapshot -> {
            User u = documentSnapshot.toObject(User.class);
            if (u == null)
                return;
            Glide.with(imageView)
                    .load(u.getProfileImage())
                    .apply(new RequestOptions()
                            .centerInside()
                    )
                    .into(imageView);
            dismissProgressDialog();
        });

        TextView title = view.findViewById(R.id.text_title);
        TextView condition = view.findViewById(R.id.condition);
        TextView conditionDetail = view.findViewById(R.id.condition_detail);

        Button toilet = view.findViewById(R.id.toilet);
        Button medicine = view.findViewById(R.id.medicine);

        title.setText(String.format("%s의 어니저", user.getDisplayName()));
        condition.setText(String.format("%s의 오늘 상태", user.getDisplayName()));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        resultDate = new Date(System.currentTimeMillis());
        temp = sdf.format(resultDate);
        try {
            Date d = sdf.parse(temp);

            DocumentReference ref = db.collection("Routine").document(user.getEmail() + "." + d.getTime());

            Task<DocumentSnapshot> task = ref.get().addOnSuccessListener(doc -> {
                if (!doc.exists()) {
                    conditionDetail.setText("오늘의 상태를 입력해 주세요.");
                    return;
                }


                Routine r = doc.toObject(Routine.class);

                SimpleDateFormat hourMin = new SimpleDateFormat("hh:mm");
                Date tempDate = new Date(r.getToiletTime());
                String time = hourMin.format(tempDate);

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

                conditionDetail.setText(String.format(Locale.getDefault(), " 오늘 %s는 약 %s시간 %s분 정도 잠을 잤어요." +
                                "\n %s 는 화장실을 %s " +
                                "\n %s 는 약을 %s " +
                                "\n %s 의 전체적인 컨디션은 아마 %s",
                        user.getDisplayName(), r.getSleepHour().substring(0, 2), r.getSleepHour().substring(2, 4),
                        user.getDisplayName(), r.isToilet() ? "갔다왔고, " + time + "에 갔다왔어요." : "아직 갔다오지 않았어요.",
                        user.getDisplayName(), r.isMedicine() ? "먹었어요." : "아직 먹지 않았어요.",
                        user.getDisplayName(), score > 7 ? "좋을 것 같아요." : score > 4 ? "중간 정도 일거에요." : "좋지 않을거에요."));
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }

        toilet.setOnClickListener(v -> {
            resultDate = new Date(System.currentTimeMillis());
            temp = sdf.format(resultDate);
            try {
                Date d = sdf.parse(temp);

                Toast.makeText(getContext(), d.getTime() + "", Toast.LENGTH_SHORT).show();
                db.collection("Routine").document(user.getEmail() + "." + d.getTime())
                        .update("toiletTime", System.currentTimeMillis(), "toilet", true)
                        .addOnCompleteListener(t -> {
                            if (t.isSuccessful()) {
                                Snackbar.make(view, "등록에 성공하였습니다.", Snackbar.LENGTH_SHORT).show();
                            } else {
                                Snackbar.make(view, "등록에 실패하였습니다.", Snackbar.LENGTH_SHORT).show();
                            }
                        });
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        medicine.setOnClickListener(v -> {
            resultDate = new Date(System.currentTimeMillis());
            temp = sdf.format(resultDate);
            try {
                Date d = sdf.parse(temp);
                db.collection("Routine").document(user.getEmail() + "." + d.getTime())
                        .update("medicine", true)
                        .addOnCompleteListener(t -> {
                            if (t.isSuccessful()) {
                                Snackbar.make(view, "등록에 성공하였습니다.", Snackbar.LENGTH_SHORT).show();
                            } else {
                                Snackbar.make(view, "등록에 실패하였습니다.", Snackbar.LENGTH_SHORT).show();
                            }
                        });
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        return view;
    }


    public void showProgressDialog(String text) {
        progressDialog.setMessage(text);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }
}
