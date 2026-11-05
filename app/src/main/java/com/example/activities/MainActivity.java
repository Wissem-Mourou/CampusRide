package com.example.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.R;
import com.example.fragments.MyTripsFragment;
import com.example.fragments.ProfileFragment;
import com.example.fragments.PublishFragment;
import com.example.fragments.SearchFragment;
import com.example.fragments.TripDetailFragment;
import com.example.models.Trip;
import com.example.models.User;
import com.example.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView tvHeaderUser;
    private ImageButton btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Vérification de la session utilisateur
        SessionManager sessionManager = SessionManager.getInstance(this);
        if (!sessionManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        setContentView(R.layout.activity_main);

        tvHeaderUser = findViewById(R.id.tv_header_user);
        btnLogout = findViewById(R.id.btn_logout);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        User currentUser = sessionManager.getUser();
        if (currentUser != null) {
            tvHeaderUser.setText(currentUser.getPrenom() + " " + currentUser.getNom() + " (" + currentUser.getVille() + ")");
        }

        btnLogout.setOnClickListener(v -> {
            selectTab(R.id.nav_profile);
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_search) {
                loadFragment(new SearchFragment(), false);
                return true;
            } else if (itemId == R.id.nav_publish) {
                loadFragment(new PublishFragment(), false);
                return true;
            } else if (itemId == R.id.nav_my_trips) {
                loadFragment(new MyTripsFragment(), false);
                return true;
            } else if (itemId == R.id.nav_profile) {
                loadFragment(new ProfileFragment(), false);
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            loadFragment(new SearchFragment(), false);
        }

        // Demande de permission de notifications pour Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // Planification périodique avec WorkManager (toutes les 15 minutes)
        try {
            androidx.work.PeriodicWorkRequest syncWork = new androidx.work.PeriodicWorkRequest.Builder(
                    com.example.workers.RequestSyncWorker.class,
                    15,
                    java.util.concurrent.TimeUnit.MINUTES
            ).build();

            androidx.work.WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                    "CampusRideSyncWork",
                    androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                    syncWork
            );
        } catch (Exception ignored) {
        }
    }

    public void selectTab(int itemId) {
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(itemId);
        }
    }

    public void showTripDetail(Trip trip) {
        TripDetailFragment fragment = TripDetailFragment.newInstance(trip);
        loadFragment(fragment, true);
    }

    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );
        transaction.replace(R.id.nav_host_fragment, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
