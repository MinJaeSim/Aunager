package yellow7918.ajou.ac.aunager;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import yellow7918.ajou.ac.aunager.routine.RoutineFragment;
import yellow7918.ajou.ac.aunager.situation.SituationFragment;
import yellow7918.ajou.ac.aunager.social.SocialFragment;

public class MainActivity extends BaseActivity {

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null)
            fm.beginTransaction().add(R.id.fragment_container, new RoutineFragment()).commit();

        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    Fragment routineFragment = new RoutineFragment();
                    fm.beginTransaction().replace(R.id.fragment_container, routineFragment).commit();
                    return true;
                case R.id.action_routine:
//                    Fragment routineFragment = new RoutineFragment();
//                    fm.beginTransaction().replace(R.id.fragment_container, routineFragment).commit();
                    return true;
                case R.id.action_situation:
                    Fragment situationFragment = new SituationFragment();
                    fm.beginTransaction().replace(R.id.fragment_container, situationFragment).commit();
                    return true;
                case R.id.action_social:
                    Fragment socialFragment = new SocialFragment();
                    fm.beginTransaction().replace(R.id.fragment_container, socialFragment).commit();
                    return true;
            }
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setMessage("앱을 종료하시겠습니까?")
                .setPositiveButton("OK", (d, which) -> finish())
                .setNegativeButton("Cancel", (d, which) -> {
                }).create();
        dialog.show();
    }
}


