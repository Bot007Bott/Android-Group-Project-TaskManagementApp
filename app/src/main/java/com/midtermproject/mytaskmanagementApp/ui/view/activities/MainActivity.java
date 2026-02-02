package com.midtermproject.mytaskmanagementApp.ui.view.activities;

import android.Manifest;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.midtermproject.mytaskmanagementApp.R;
import com.midtermproject.mytaskmanagementApp.ui.view.fragments.CategoryListFragment;
import com.midtermproject.mytaskmanagementApp.ui.view.fragments.TaskListFragment;
import com.midtermproject.mytaskmanagementApp.ui.view.fragments.CompletedTasksFragment;
import com.midtermproject.mytaskmanagementApp.util.NotificationHelper;

public class MainActivity extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // In MainActivity.onCreate(), before creating channel:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.deleteNotificationChannel("task_reminders");
            }
        }
        NotificationHelper.createNotificationChannel(this);

        requestNotificationPermission();

        setupBottomNavigation();

        findViewById(R.id.btn_search).setOnClickListener(v -> {
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (current instanceof TaskListFragment) {
                ((TaskListFragment) current).toggleSearchBar();
            } else if (current instanceof CategoryListFragment) {
                ((CategoryListFragment) current).toggleSearchBar();
            }
        });

        findViewById(R.id.btn_menu).setOnClickListener(v -> {
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (current instanceof MenuCallback) {
                ((MenuCallback) current).showMenu(v);
            }
        });

        // Load Home fragment (TaskListFragment) on start
        if (savedInstanceState == null) {
            loadFragment(new TaskListFragment());
        }
    }

    private void requestNotificationPermission() {
        // For Android 13+ (API 33+), we need to request notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                // Request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        View btnSearch = findViewById(R.id.btn_search);
        View btnMenu = findViewById(R.id.btn_menu);

        bottomNav.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new TaskListFragment();
                btnSearch.setVisibility(View.VISIBLE);
                btnMenu.setVisibility(View.VISIBLE);
            } else if (item.getItemId() == R.id.nav_categories) {
                selectedFragment = new CategoryListFragment();
                btnSearch.setVisibility(View.VISIBLE);
                btnMenu.setVisibility(View.VISIBLE);
            } else if (item.getItemId() == R.id.nav_completed) {
                selectedFragment = new CompletedTasksFragment();
                btnSearch.setVisibility(View.GONE);
                btnMenu.setVisibility(View.GONE);
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public interface MenuCallback {
        void showMenu(View anchorView);
    }
}