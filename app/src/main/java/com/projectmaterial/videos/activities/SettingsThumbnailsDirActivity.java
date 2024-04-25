package com.projectmaterial.videos.activities;

import android.os.Bundle;
import com.projectmaterial.collapsingtoolbar.CollapsingToolbarActivity;
import com.projectmaterial.videos.R;
import com.projectmaterial.videos.fragments.SettingsThumbnailsDirFragment;

public class SettingsThumbnailsDirActivity extends CollapsingToolbarActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.pref_title_thumbnails_dir);
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.content_frame, new SettingsThumbnailsDirFragment())
            .commit();
    }
}