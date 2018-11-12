package yellow7918.ajou.ac.aunager.social;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import yellow7918.ajou.ac.aunager.R;

public class SocialFragment extends Fragment {

    private ProgressDialog progressDialog;
    private FirebaseUser user;

    private FloatingActionButton writeButton;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_social, container, false);
        progressDialog = new ProgressDialog(getContext());

        user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());

        writeButton = view.findViewById(R.id.write_button);
        writeButton.setOnClickListener(v -> {
            if (user != null) {
                FragmentManager fm = getFragmentManager();
                DialogFragment dialogFragment = SocialWriteDialogFragment.newInstance(user.getEmail());
                dialogFragment.setTargetFragment(SocialFragment.this, 100);
                dialogFragment.show(fm, "InputDialog");

            } else
                Snackbar.make(getView(), "로그인 해주세요.", Snackbar.LENGTH_SHORT).show();
        });

        linearLayout.setOrientation(LinearLayout.VERTICAL);
        recyclerView.setLayoutManager(linearLayout);

        Query query = db.collection("SocialBoard");
        SocialAdapter adapter = new SocialAdapter(query);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new SocialAdapter.OnItemClickListener<SocialText>() {
            @Override
            public void onItemClick(SocialText item, String key) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd, HH:mm", Locale.getDefault());
                Date resultdate = new Date(item.getDate());
                String date = sdf.format(resultdate);

                FragmentManager fm = getFragmentManager();
                DialogFragment dialogFragment = SocialDetailDialogFragment.newInstance(item.getEmail(), item.getCategory(), item.getTitle(), item.getText(), date);
                dialogFragment.setTargetFragment(SocialFragment.this, 100);
                dialogFragment.show(fm, "InputDialog");
            }
        });

        return view;
    }

    private void showProgressBar(String text) {
        progressDialog.setMessage(text);
        progressDialog.show();
        writeButton.setEnabled(false);
    }

    private void hideProgressBar() {
        progressDialog.dismiss();
        writeButton.setEnabled(true);
    }
}
