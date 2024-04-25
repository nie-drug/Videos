package com.projectmaterial.videos.activities;

import android.content.Intent;
import android.os.Bundle;
import com.projectmaterial.collapsingtoolbar.CollapsingToolbarActivity;
import com.projectmaterial.videos.R;
import com.projectmaterial.videos.fragments.SettingsFragment;

public class SettingsActivity extends CollapsingToolbarActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.settings);
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.content_frame, new SettingsFragment())
            .commit();
    }
    
    @Override
    public void recreate() {
        Intent intent = getIntent();
        finish();
        overrideTransition();
        startActivity(intent);
        overrideTransition();
    }
}