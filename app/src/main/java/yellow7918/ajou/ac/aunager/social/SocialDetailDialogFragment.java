package yellow7918.ajou.ac.aunager.social;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import yellow7918.ajou.ac.aunager.R;

public class SocialDetailDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_social_detail, null);


        String email = getArguments().getString("EMAIL");
        String category = getArguments().getString("CATEGORY");
        String title = getArguments().getString("TITLE");
        String text = getArguments().getString("TEXT");
        String date = getArguments().getString("DATE");

        TextView textTitle = view.findViewById(R.id.social_board_title);
        TextView textInfo = view.findViewById(R.id.text_title);
        TextView contents = view.findViewById(R.id.text_contents);

        textTitle.setText(String.format("[%s]%s", category, title));
        textInfo.setText(String.format("%s / %s", email, date));
        contents.setText(text);


        return new AlertDialog.Builder(getActivity()).setView(view).create();
    }

    public static SocialDetailDialogFragment newInstance(String email, String category, String title, String text, String date) {
        SocialDetailDialogFragment f = new SocialDetailDialogFragment();

        Bundle args = new Bundle();
        args.putString("EMAIL", email);
        args.putString("CATEGORY", category);
        args.putString("TITLE", title);
        args.putString("TEXT", text);
        args.putString("DATE", date);
        f.setArguments(args);

        return f;
    }
}
