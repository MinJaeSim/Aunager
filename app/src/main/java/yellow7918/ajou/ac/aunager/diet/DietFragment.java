package yellow7918.ajou.ac.aunager.diet;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import yellow7918.ajou.ac.aunager.R;

public class DietFragment extends Fragment implements DietContract.View, DietDialogFragment.OnDietDialogEventListener {

    private DietContract.Presenter dietPresenter;

    private RadioButton dietRadioButton;
    private RadioButton othersRadioButton;

    private DietAdapter breakfastAdapter;
    private DietAdapter lunchAdapter;
    private DietAdapter dinnerAdapter;
    private DietAdapter othersAdapter;

    private RadioButton mon;
    private RadioButton tue;
    private RadioButton wed;
    private RadioButton thu;
    private RadioButton fri;
    private RadioButton sat;
    private RadioButton sun;

    private ArrayList<RadioButton> days;
    private int day;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diet, container, false);

        dietPresenter = new DietPresenter();
        dietPresenter.setView(this);

        dietRadioButton = view.findViewById(R.id.radioButton_meal);
        othersRadioButton = view.findViewById(R.id.radioButton_others);

        TextView textViewBreakfast = view.findViewById(R.id.text_view_breakfast);
        TextView textViewLunch = view.findViewById(R.id.text_view_lunch);
        TextView textViewDinner = view.findViewById(R.id.text_view_dinner);

        RecyclerView recyclerViewBreakfast = view.findViewById(R.id.recyclerView_breakfast);
        LinearLayoutManager breakfastLinearLayoutManager = new LinearLayoutManager(getContext());
        breakfastLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewBreakfast.setLayoutManager(breakfastLinearLayoutManager);

        RecyclerView recyclerViewLunch = view.findViewById(R.id.recyclerView_lunch);
        LinearLayoutManager lunchLinearLayoutManager = new LinearLayoutManager(getContext());
        lunchLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewLunch.setLayoutManager(lunchLinearLayoutManager);

        RecyclerView recyclerViewDinner = view.findViewById(R.id.recyclerView_dinner);
        LinearLayoutManager dinnerLinearLayoutManager = new LinearLayoutManager(getContext());
        dinnerLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewDinner.setLayoutManager(dinnerLinearLayoutManager);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mon = view.findViewById(R.id.mon);
        tue = view.findViewById(R.id.tue);
        wed = view.findViewById(R.id.wed);
        thu = view.findViewById(R.id.thu);
        fri = view.findViewById(R.id.fri);
        sat = view.findViewById(R.id.sat);
        sun = view.findViewById(R.id.sun);

        days = new ArrayList<>();
        days.add(mon);
        days.add(tue);
        days.add(wed);
        days.add(thu);
        days.add(fri);
        days.add(sat);
        days.add(sun);

        for (int i = 0; i < 7; i++) {
            int finalI = i;
            days.get(i).setOnCheckedChangeListener((compoundButton, b) -> {
                if (b) {
                    day = finalI;
                    breakfastAdapter = new DietAdapter(FirebaseFirestore.getInstance().collection("Breakfast").whereEqualTo("uid", uid).whereEqualTo("day", day));
                    recyclerViewBreakfast.setAdapter(breakfastAdapter);

                    lunchAdapter = new DietAdapter(FirebaseFirestore.getInstance().collection("Lunch").whereEqualTo("uid", uid).whereEqualTo("day", day));
                    recyclerViewLunch.setAdapter(lunchAdapter);


                    dinnerAdapter = new DietAdapter(FirebaseFirestore.getInstance().collection("Dinner").whereEqualTo("uid", uid).whereEqualTo("day", day));
                    recyclerViewDinner.setAdapter(dinnerAdapter);

                    List<DietAdapter> adapters = new ArrayList<>();
                    adapters.add(breakfastAdapter);
                    adapters.add(lunchAdapter);
                    adapters.add(dinnerAdapter);

                    dietPresenter.setAdapter(adapters);

                }
            });
        }

        day = 0;

        dietRadioButton.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                textViewBreakfast.setText("아침");

                textViewLunch.setVisibility(View.VISIBLE);
                textViewDinner.setVisibility(View.VISIBLE);

                recyclerViewLunch.setVisibility(View.VISIBLE);
                recyclerViewDinner.setVisibility(View.VISIBLE);


                breakfastAdapter = new DietAdapter(FirebaseFirestore.getInstance().collection("Breakfast").whereEqualTo("uid", uid).whereEqualTo("day", day));
                recyclerViewBreakfast.setAdapter(breakfastAdapter);

                lunchAdapter = new DietAdapter(FirebaseFirestore.getInstance().collection("Lunch").whereEqualTo("uid", uid).whereEqualTo("day", day));
                recyclerViewLunch.setAdapter(lunchAdapter);


                dinnerAdapter = new DietAdapter(FirebaseFirestore.getInstance().collection("Dinner").whereEqualTo("uid", uid).whereEqualTo("day", day));
                recyclerViewDinner.setAdapter(dinnerAdapter);

                List<DietAdapter> adapters = new ArrayList<>();
                adapters.add(breakfastAdapter);
                adapters.add(lunchAdapter);
                adapters.add(dinnerAdapter);

                dietPresenter.setAdapter(adapters);
            }
        });

        othersRadioButton.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                textViewLunch.setVisibility(View.GONE);
                textViewDinner.setVisibility(View.GONE);

                recyclerViewLunch.setVisibility(View.GONE);
                recyclerViewDinner.setVisibility(View.GONE);
                textViewBreakfast.setText("선호");

                othersAdapter = new DietAdapter(FirebaseFirestore.getInstance().collection("Others").whereEqualTo("uid", uid));
                recyclerViewBreakfast.setAdapter(othersAdapter);
                List<DietAdapter> adapters = new ArrayList<>();
                adapters.add(othersAdapter);
                dietPresenter.setAdapter(adapters);
            }
        });

        breakfastAdapter = new DietAdapter(FirebaseFirestore.getInstance().collection("Breakfast").whereEqualTo("uid", uid).whereEqualTo("day", day));
        recyclerViewBreakfast.setAdapter(breakfastAdapter);

        lunchAdapter = new DietAdapter(FirebaseFirestore.getInstance().collection("Lunch").whereEqualTo("uid", uid).whereEqualTo("day", day));
        recyclerViewLunch.setAdapter(lunchAdapter);


        dinnerAdapter = new DietAdapter(FirebaseFirestore.getInstance().collection("Dinner").whereEqualTo("uid", uid).whereEqualTo("day", day));
        recyclerViewDinner.setAdapter(dinnerAdapter);


        FloatingActionButton addButton = view.findViewById(R.id.add_button);
        addButton.setOnClickListener(v -> {
            showEditDietDialog(null, "");
        });

        List<DietAdapter> adapters = new ArrayList<>();
        adapters.add(breakfastAdapter);
        adapters.add(lunchAdapter);
        adapters.add(dinnerAdapter);
        dietPresenter.setAdapter(adapters);

        return view;
    }


    @Override
    public void onConfirm(Diet diet) {
        dietPresenter.addDiet(diet);
        breakfastAdapter.notifyDataSetChanged();
        lunchAdapter.notifyDataSetChanged();
        dinnerAdapter.notifyDataSetChanged();
    }


    @Override
    public void showEditDietDialog(Diet diet, String key) {
        DietDialogFragment dialogFragment = new DietDialogFragment();
        if (diet != null)
            dialogFragment.setDiet(diet);
        dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        dialogFragment.setOnDietEventListener(this);
        dialogFragment.show(getFragmentManager(), "test");
    }

    @Override
    public void showRemoveDietDialog(Diet diet, String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Do you want to remove routine?");
        builder.setPositiveButton("Yes",
                (dialog, which) -> {
                    dietPresenter.removeDiet(diet, key);
                    breakfastAdapter.notifyDataSetChanged();
                    lunchAdapter.notifyDataSetChanged();
                    dinnerAdapter.notifyDataSetChanged();
                });
        builder.setNegativeButton("No",
                (dialog, which) -> {

                });
        builder.show();
    }
}
