package yellow7918.ajou.ac.aunager.diet;


import java.util.List;

public interface DietContract {
    interface View {
        void showEditDietDialog(Diet diet, String key);

        void showRemoveDietDialog(Diet diet, String key);
    }

    interface Presenter {
        void setView(DietContract.View view);

        void setAdapter(List<DietAdapter> adapters);

        void addDiet(Diet diet);

        void updateDiet(Diet diet, String key);

        void removeDiet(Diet diet, String key);
    }
}
