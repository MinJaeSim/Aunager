package yellow7918.ajou.ac.aunager.situation;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import yellow7918.ajou.ac.aunager.R;

public class SituationFragment extends Fragment {

    private SituationAdapter adapter;
    private ProgressDialog progressDialog;
    private FloatingActionButton writeButton;
    private FirebaseFirestore db;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_situation, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        String email = user.getEmail();

        progressDialog = new ProgressDialog(getContext());
        writeButton = view.findViewById(R.id.write_button);

        writeButton = view.findViewById(R.id.write_button);
        writeButton.setOnClickListener(v -> {
            if (user != null) {
                FragmentManager fm = getFragmentManager();
                DialogFragment dialogFragment = SituationWriteDialogFragment.newInstance(user.getEmail());
                dialogFragment.setTargetFragment(SituationFragment.this, 100);
                dialogFragment.show(fm, "InputDialog");

            } else
                Snackbar.make(getView(), "로그인 해주세요.", Snackbar.LENGTH_SHORT).show();
        });

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);


        Query query = db.collection("Situation").whereEqualTo("email", email);
        adapter = new SituationAdapter(query);

        adapter.setOnItemClickListener(new SituationAdapter.OnItemClickListener<Situation>() {
            @Override
            public void onItemClick(Situation item, String key) {
                DetailSituation f = DetailSituation.newInstance(item);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, f)
                        .addToBackStack(null)
                        .commit();
            }
        });

        adapter.setOnItemLongClickListener((item, key) -> showRemoveDialog(key));

        recyclerView.setAdapter(adapter);

        return view;
    }

    public void showRemoveDialog(final String documentKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("상황을 삭제 하시겠습니까?");
        builder.setPositiveButton("예",
                (dialog, which) -> db.collection("Situation").document(documentKey).delete());
        builder.setNegativeButton("아니오",
                (dialog, which) -> {

                });
        builder.show();
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
