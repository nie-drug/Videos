package com.projectmaterial.videos.activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.color.MaterialColors;
import com.projectmaterial.videos.R;
import java.util.Locale;

public class BaseComponentActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SharedPreferences mSharedPreferences;
    private static final String PREF_KEY_COLOR_SCHEME = "pref_key_color_scheme";
    private static final String PREF_KEY_THEME = "pref_key_theme";
    private static final String PREF_KEY_HIGH_CONTRAST = "pref_key_high_contrast";
    private static final String PREF_KEY_LANGUAGE = "pref_key_language";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        String[] themeValues = getResources().getStringArray(R.array.pref_values_theme);
        String themePrefs = mSharedPreferences.getString(PREF_KEY_THEME, "MODE_NIGHT_FOLLOW_SYSTEM");
        if (themePrefs.equals(themeValues[0])) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if (themePrefs.equals(themeValues[1])) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (themePrefs.equals(themeValues[2])) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        if (mSharedPreferences.getBoolean(PREF_KEY_COLOR_SCHEME, false)) {
            setTheme(R.style.Theme_ProjectX_DynamicColors);
        } else {
            setTheme(R.style.Theme_ProjectX_StaticColors);
        }
        if (mSharedPreferences.getBoolean(PREF_KEY_HIGH_CONTRAST, false) && themePrefs.equals(themeValues[2]) && mSharedPreferences.getBoolean(PREF_KEY_COLOR_SCHEME, false)) {
            setTheme(R.style.Theme_ProjectX_DynamicColors_HighContrast);
        } else if (mSharedPreferences.getBoolean(PREF_KEY_HIGH_CONTRAST, false) && themePrefs.equals(themeValues[2])) {
            setTheme(R.style.Theme_ProjectX_StaticColors_HighContrast);
        }
        String languagePrefs = mSharedPreferences.getString(PREF_KEY_LANGUAGE, Locale.getDefault().getLanguage());
        updateLocale(languagePrefs);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
    
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key != null && key.equals(PREF_KEY_THEME)) {
            recreate();
        } else if (key != null && key.equals(PREF_KEY_COLOR_SCHEME)) {
            recreateWithDelay();
        } else if (key != null && key.equals(PREF_KEY_HIGH_CONTRAST)) {
            recreateWithDelay();
        } else if (key != null && key.equals(PREF_KEY_LANGUAGE)) {
            recreate();
        }
    }
    
    private void recreateWithDelay() {
        new Handler(Looper.getMainLooper())
            .postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        recreate();
                    }
                }, 325
            );
    }
    
    @SuppressWarnings("deprecation")
    public void overrideTransition() {
        int fadeIn = android.R.anim.fade_in;
        int fadeOut = android.R.anim.fade_out;
        overridePendingTransition(fadeIn, fadeOut);
    }
    
    @SuppressWarnings("deprecation")
    private void updateLocale(String language) {
        String[] languageParts = language.split("-");
        String languageCode = languageParts[0];
        String region = languageParts.length > 1 ? languageParts[1] : "";
        Locale newLocale = new Locale(languageCode, region);
        Locale.setDefault(newLocale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(newLocale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}