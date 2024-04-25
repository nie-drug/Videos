package com.projectmaterial.videos.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.MenuItem;
import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.projectmaterial.videos.R;
import com.projectmaterial.videos.VideoApplication;
import com.projectmaterial.videos.fragments.DirectoriesFragment;
import com.projectmaterial.videos.fragments.FavoritesFragment;
import com.projectmaterial.videos.fragments.VideosFragment;

@SuppressWarnings("deprecation")
public class VideoLibraryActivity extends BaseComponentActivity {
    private NavigationBarView mNavigationBarView;
    private SharedPreferences mSharedPreferences;
    private static final int MANAGE_ALL_FILES_ACCESS_PERMISSION = 100;
    private static final String SELECTED_FRAGMENT_KEY = "selected_fragment";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavigationBarView = findViewById(R.id.navigation_bar_view);
        mNavigationBarView.setOnItemSelectedListener(this::onNavigationItemSelected);
        mSharedPreferences = getSharedPreferences("FragmentState", MODE_PRIVATE);
        if (checkManageAllFilesAccessPermission()) {
            setupInitialFragment();
        }
        getOnBackPressedDispatcher().addCallback(this,
            new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    finish();
                }
            }
        );
    }
    
    private boolean checkManageAllFilesAccessPermission() {
        if (!Environment.isExternalStorageManager()) {
            requestManageAllFilesAccessPermission();
            return false;
        }
        return true;
    }
    
    private void requestManageAllFilesAccessPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, MANAGE_ALL_FILES_ACCESS_PERMISSION);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MANAGE_ALL_FILES_ACCESS_PERMISSION) {
            if (checkManageAllFilesAccessPermission()) {
                setupInitialFragment();
                VideoApplication videoApplication = (VideoApplication) getApplication();
                videoApplication.initVideoList();
            }
        }
    }
    
    private void setupInitialFragment() {
        int selectedFragmentId = mSharedPreferences.getInt(SELECTED_FRAGMENT_KEY, R.id.menu_navigation_videos);
        Fragment fragment = getFragmentForMenuItem(selectedFragmentId);
        replaceFragment(fragment);
    }
    
    private Fragment getFragmentForMenuItem(int itemId) {
        if (itemId == R.id.menu_navigation_videos) {
            return new VideosFragment();
        } else if (itemId == R.id.menu_navigation_starred) {
            return new FavoritesFragment();
        } else if (itemId == R.id.menu_navigation_folders) {
            return new DirectoriesFragment();
        }
        return null;
    }
    
    private void replaceFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }
    
    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == mNavigationBarView.getSelectedItemId()) {
            return true;
        }

        Fragment fragment = getFragmentForMenuItem(item.getItemId());
        replaceFragment(fragment);
        saveSelectedFragmentId(item.getItemId());
        return true;
    }
    
    private void saveSelectedFragmentId(int itemId) {
        mSharedPreferences.edit().putInt(SELECTED_FRAGMENT_KEY, itemId).apply();
    }
    
    @Override
    public void onStop() {
        super.onStop();
        saveSelectedFragmentId(R.id.menu_navigation_videos);
    }
}