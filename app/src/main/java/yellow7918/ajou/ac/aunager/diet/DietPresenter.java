package yellow7918.ajou.ac.aunager.diet;


import android.support.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class DietPresenter implements DietContract.Presenter, OnDietClickListener {
    private FirebaseFirestore firebaseFirestore;
    private DietContract.View view;
    private List<DietAdapter> dietAdapters;

    public DietPresenter() {
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void onClick(@NonNull Diet diet, String documentKey) {
        view.showEditDietDialog(diet, documentKey);
    }

    @Override
    public void onDeleteClick(@NonNull Diet diet, @NonNull String documentKey) {
        view.showRemoveDietDialog(diet, documentKey);
    }

    @Override
    public void setView(DietContract.View view) {
        this.view = view;
    }

    @Override
    public void setAdapter(List<DietAdapter> adapters) {
        this.dietAdapters = adapters;

        for (DietAdapter adapter : dietAdapters) {
            adapter.setOnDietClickListener(this);
        }
    }

    @Override
    public void addDiet(Diet diet) {
        String type = diet.getType() == 1 ? "Breakfast" : diet.getType() == 2 ? "Lunch" : "Dinner";

        Query query = firebaseFirestore.collection(type).whereEqualTo("date", diet.getDate());
        query.get().addOnSuccessListener(documentSnapshots -> {
            if (documentSnapshots.isEmpty()) {
                firebaseFirestore.collection(type).add(diet);
            } else {
                String documentKey = documentSnapshots.getDocuments().get(0).getId();
                updateDiet(diet, documentKey);
            }
        });
    }

    @Override
    public void updateDiet(Diet diet, String key) {
        diet.setType(diet.getType());
        diet.setProfileImageUrl(diet.getProfileImageUrl());

        String type = diet.getType() == 1 ? "Breakfast" : diet.getType() == 2 ? "Lunch" : "Dinner";
        firebaseFirestore.collection(type).document(key).set(diet);
    }

    @Override
    public void removeDiet(Diet diet, String key) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        String type = diet.getType() == 1 ? "Breakfast" : diet.getType() == 2 ? "Lunch" : "Dinner";
        firebaseFirestore.collection(type).document(key).delete();
    }
}
