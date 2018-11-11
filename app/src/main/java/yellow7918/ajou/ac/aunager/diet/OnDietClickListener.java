package yellow7918.ajou.ac.aunager.diet;


import android.support.annotation.NonNull;

public interface OnDietClickListener {
    void onClick(@NonNull Diet diet, String documentKey);

    void onDeleteClick(@NonNull Diet diet, @NonNull String documentKey);
}
