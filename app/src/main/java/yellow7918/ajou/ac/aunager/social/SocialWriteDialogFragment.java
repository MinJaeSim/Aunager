package yellow7918.ajou.ac.aunager.social;

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

public class SocialWriteDialogFragment extends DialogFragment {

    private String category;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_social, null);

        TextView okButton = view.findViewById(R.id.dialog_ok_button);
        TextView cancelButton = view.findViewById(R.id.dialong_cancel_button);

        String email = getArguments().getString("EMAIL");

        List<String> list = new ArrayList<>();
        list.add("잡담");
        list.add("정보");
        list.add("질문");
        list.add("기타");

        Spinner spinner = view.findViewById(R.id.category);
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, list);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = spinner.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final EditText titleEditText = view.findViewById(R.id.text_title);
        final EditText contentsEditText = view.findViewById(R.id.text_contents);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (titleEditText.getText().length() > 0 && contentsEditText.getText().length() > 0) {
                    String title = titleEditText.getText().toString();
                    String contents = contentsEditText.getText().toString();
                    long time = System.currentTimeMillis();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    SocialText text = new SocialText(category, email, title, contents, time);
                    db.collection("SocialBoard").document(email +"."+title+"."+time)
                            .set(text)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Snackbar.make(view, "글 작성에 성공하였습니다.", Snackbar.LENGTH_SHORT).show();
                                } else {
                                    Snackbar.make(view, "글 작성에 실패하였습니다.", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                    Intent intent = new Intent();
                    intent.putExtra("SOCIAL", text);
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

    public static SocialWriteDialogFragment newInstance(String email) {
        SocialWriteDialogFragment f = new SocialWriteDialogFragment();

        Bundle args = new Bundle();
        args.putString("EMAIL", email);
        f.setArguments(args);

        return f;
    }
}
