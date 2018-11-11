package yellow7918.ajou.ac.aunager;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import yellow7918.ajou.ac.aunager.routine.RoutineFragment;
import yellow7918.ajou.ac.aunager.situation.SituationFragment;
import yellow7918.ajou.ac.aunager.social.SocialFragment;

public class MainActivity extends BaseActivity {

    private BottomNavigationView bottomNavigation;
    private long pressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null)
            fm.beginTransaction().add(R.id.fragment_container, new MainFragment()).commit();

        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    Fragment mainFragment = new MainFragment();
                    fm.beginTransaction().replace(R.id.fragment_container, mainFragment).commit();
                    return true;
                case R.id.action_routine:
                    Fragment routineFragment = new RoutineFragment();
                    fm.beginTransaction().replace(R.id.fragment_container, routineFragment).commit();
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
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            super.onBackPressed();
            return;
        }
        if (pressedTime == 0) {
            Toast.makeText(MainActivity.this, getString(R.string.string_close_warning), Toast.LENGTH_LONG).show();
            pressedTime = System.currentTimeMillis();
        } else {
            int seconds = (int) (System.currentTimeMillis() - pressedTime);

            if (seconds > 2000) {
                Toast.makeText(MainActivity.this, getString(R.string.string_close_warning), Toast.LENGTH_LONG).show();
                pressedTime = 0;
            } else {
                super.onBackPressed();
            }
        }
    }
}


