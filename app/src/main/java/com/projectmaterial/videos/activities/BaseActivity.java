package com.projectmaterial.videos.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import com.projectmaterial.videos.R;

public class BaseActivity extends BaseComponentActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String PREF_KEY_THEME = "pref_key_theme";
    private static final String PREF_KEY_THEME_COLOR_SCHEME = "pref_key_color_scheme";
    private static final String PREF_KEY_THEME_HIGH_CONTRAST = "pref_key_high_contrast";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);
        String[] themeValues = getResources().getStringArray(R.array.pref_values_theme);
        String themePrefs = preferences.getString(PREF_KEY_THEME, "MODE_NIGHT_FOLLOW_SYSTEM");
        if (preferences.getBoolean(PREF_KEY_THEME_COLOR_SCHEME, false)) {
            setTheme(R.style.Theme_ProjectX_DynamicColors_Activity);
        } else {
            setTheme(R.style.Theme_ProjectX_StaticColors_Activity);
        }
        if (preferences.getBoolean(PREF_KEY_THEME_HIGH_CONTRAST, false) && themePrefs.equals(themeValues[2]) && preferences.getBoolean(PREF_KEY_THEME_COLOR_SCHEME, false)) {
            setTheme(R.style.Theme_ProjectX_DynamicColors_Activity_HighContrast);
        } else if (preferences.getBoolean(PREF_KEY_THEME_HIGH_CONTRAST, false) && themePrefs.equals(themeValues[2])) {
            setTheme(R.style.Theme_ProjectX_StaticColors_Activity_HighContrast);
        }
    }
}