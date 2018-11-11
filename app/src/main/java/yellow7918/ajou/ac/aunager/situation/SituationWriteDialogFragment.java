package yellow7918.ajou.ac.aunager.situation;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import yellow7918.ajou.ac.aunager.R;
import yellow7918.ajou.ac.aunager.social.SocialText;

public class SituationWriteDialogFragment extends DialogFragment {

    private String category;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_situation, null);

        TextView okButton = view.findViewById(R.id.dialog_ok_button);
        TextView cancelButton = view.findViewById(R.id.dialong_cancel_button);

        String email = getArguments().getString("EMAIL");

        final EditText titleEditText = view.findViewById(R.id.title);
        final EditText beforeEditText = view.findViewById(R.id.before);
        final EditText afterEditText = view.findViewById(R.id.after);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (titleEditText.getText().length() > 0 && beforeEditText.getText().length() > 0 && afterEditText.getText().length() > 0) {
                    String title = titleEditText.getText().toString();
                    String before = beforeEditText.getText().toString();
                    String after = afterEditText.getText().toString();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Situation text = new Situation(title, before, after, email);
                    db.collection("Situation").document(email)
                            .set(text)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Snackbar.make(view, "글 작성에 성공하였습니다.", Snackbar.LENGTH_SHORT).show();
                                } else {
                                    Snackbar.make(view, "글 작성에 실패하였습니다.", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                    Intent intent = new Intent();
                    intent.putExtra("SITUATION", text);
                    getTargetFragment().onActivityResult(
                            getTargetRequestCode(), Activity.RESULT_OK, intent);
                    dismiss();
                } else {
                    Snackbar.make(getView(), "빈칸을 작성해 주세요.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(v -> dismiss());


        return new AlertDialog.Builder(getActivity()).setView(view).create();
    }

    public static SituationWriteDialogFragment newInstance(String email) {
        SituationWriteDialogFragment f = new SituationWriteDialogFragment();

        Bundle args = new Bundle();
        args.putString("EMAIL", email);
        f.setArguments(args);

        return f;
    }
}
