package com.projectmaterial.collapsingtoolbar;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.text.LineBreakConfig;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.MenuItem;
import android.widget.FrameLayout;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.projectmaterial.videos.R;
import com.projectmaterial.videos.activities.BaseComponentActivity;

public class CollapsingToolbarActivity extends BaseComponentActivity {
    private AppBarLayout mAppBarLayout;
    private AppBarLayout.Behavior mAppBarLayoutBehavior;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private CoordinatorLayout.LayoutParams mCoordinatorLayoutLayoutParams;
    private FrameLayout mContentFrameLayout;
    private RecyclerView mRecyclerView;
    private Toolbar mToolbar;
    private SharedPreferences mSharedPreferences;
    private static final String PREF_KEY_THEME = "pref_key_theme";
    private static final String PREF_KEY_COLOR_SCHEME = "pref_key_color_scheme";
    private static final String PREF_KEY_HIGH_CONTRAST = "pref_key_high_contrast";
    private static final float LINE_SPACING_MULTIPLIER = 1.1f;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        String[] themeValues = getResources().getStringArray(R.array.pref_values_theme);
        String themePrefs = mSharedPreferences.getString(PREF_KEY_THEME, "MODE_NIGHT_FOLLOW_SYSTEM");
        if (mSharedPreferences.getBoolean(PREF_KEY_COLOR_SCHEME, false)) {
            setTheme(R.style.Theme_ProjectX_DynamicColors_Settings);
        } else {
            setTheme(R.style.Theme_ProjectX_StaticColors_Settings);
        }
        if (mSharedPreferences.getBoolean(PREF_KEY_HIGH_CONTRAST, false) && themePrefs.equals(themeValues[2]) && mSharedPreferences.getBoolean(PREF_KEY_COLOR_SCHEME, false)) {
            setTheme(R.style.Theme_ProjectX_DynamicColors_Settings_HighContrast);
        } else if (mSharedPreferences.getBoolean(PREF_KEY_HIGH_CONTRAST, false) && themePrefs.equals(themeValues[2])) {
            setTheme(R.style.Theme_ProjectX_StaticColors_Settings_HighContrast);
        }
        setContentView(R.layout.collapsing_toolbar_base_layout);
        mAppBarLayout = findViewById(R.id.app_bar);
        mCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        mCollapsingToolbarLayout.setLineSpacingMultiplier(LINE_SPACING_MULTIPLIER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mCollapsingToolbarLayout.setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_NORMAL_FAST);
            mCollapsingToolbarLayout.setStaticLayoutBuilderConfigurer(
                builder -> {
                    builder.setLineBreakConfig(
                        new LineBreakConfig.Builder()
                            .setLineBreakWordStyle(LineBreakConfig.LINE_BREAK_WORD_STYLE_PHRASE)
                            .build()
                    );
                }
            );
        }
        mCoordinatorLayoutLayoutParams = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
        mAppBarLayoutBehavior = new AppBarLayout.Behavior();
        mAppBarLayoutBehavior.setDragCallback(
            new AppBarLayout.Behavior.DragCallback() {
                @Override
                public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                    return appBarLayout.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
                }
            }
        );
        mCoordinatorLayoutLayoutParams.setBehavior(mAppBarLayoutBehavior);
        mContentFrameLayout = findViewById(R.id.content_frame);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.quantum_ic_arrow_back_vd_theme_24);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}