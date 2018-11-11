package yellow7918.ajou.ac.aunager.routine;


import android.support.annotation.NonNull;

public interface OnRoutineClickListener {
    void onClick(@NonNull Routine routine, String documentKey);

    void onDeleteClick(@NonNull String documentKey);
}
