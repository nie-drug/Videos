package com.projectmaterial.videos.activities;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.activity.EdgeToEdge;
import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.MaterialToolbar;
import com.projectmaterial.videos.R;
import com.projectmaterial.videos.fragments.DirectoryVideosFragment;
import com.projectmaterial.videos.utils.DirectoryUtils;

public class DirectoryVideosActivity extends BaseActivity {
    private DirectoryUtils mDirectoryUtils;
    private DirectoryVideosFragment mDirectoryVideosFragment;
    private MaterialToolbar mMaterialToolbar;
    private String mDirectory;
    private static final String DIRECTORY = "directory";
    
    @MainThread
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory);
        mDirectory = getIntent().getStringExtra(DIRECTORY);
        mDirectoryUtils = new DirectoryUtils(this);
        mMaterialToolbar = findViewById(R.id.toolbar);
        mMaterialToolbar.setNavigationIcon(R.drawable.quantum_ic_arrow_back_vd_theme_24);
        if (mDirectory != null && !mDirectory.isEmpty()) {
            mMaterialToolbar.setTitle(mDirectoryUtils.getFormattedName(mDirectory));
        }
        setSupportActionBar(mMaterialToolbar);
        if (savedInstanceState == null) {
            mDirectoryVideosFragment = DirectoryVideosFragment.newInstance(mDirectory);
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, mDirectoryVideosFragment)
                .commit();
        }
    }
    
    @MainThread
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}