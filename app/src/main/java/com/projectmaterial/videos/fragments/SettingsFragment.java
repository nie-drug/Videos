package com.projectmaterial.videos.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;
import com.google.android.material.snackbar.Snackbar;
import com.projectmaterial.videos.R;
import com.projectmaterial.preference.M3PreferenceFragmentCompat;
import com.projectmaterial.videos.activities.SettingsThumbnailsDirActivity;
import java.io.File;

public class SettingsFragment extends M3PreferenceFragmentCompat {
    private ListPreference mListPreference;
    private ListPreference mListPreferenceTheme;
    private Preference mPreference;
    private SharedPreferences mSharedPreferences;
    private SwitchPreferenceCompat mSwitchPreferenceCompat;
    private SwitchPreferenceCompat mSwitchPreferenceCompatHighContrast;
    private static final String PREF_KEY_COLOR_SCHEME = "pref_key_color_scheme";
    private static final String PREF_KEY_THEME = "pref_key_theme";
    private static final String PREF_KEY_HIGH_CONTRAST = "pref_key_high_contrast";
    private static final String PREF_KEY_LANGUAGE = "pref_key_language";
    private static final String PREF_KEY_THUMBNAILS_DIR = "pref_key_thumbnails_dir";
    private static final String PREF_KEY_CLEAR_CACHED_THUMBNAILS = "pref_key_clear_cached_thumbnails";
   
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        mListPreferenceTheme = findPreference(PREF_KEY_THEME);
        mListPreferenceTheme.setOnPreferenceChangeListener(
            (preference, newValue) -> {
                PreferenceManager.getDefaultSharedPreferences(getContext())
                    .edit()
                    .putString(PREF_KEY_THEME, (String) newValue)
                    .apply();
                updateHighContrastPreferenceEnabled((String) newValue);
                return true;
            }
        );
        {
            mSwitchPreferenceCompat = findPreference(PREF_KEY_COLOR_SCHEME);
            mSwitchPreferenceCompat.setOnPreferenceChangeListener(
                (preference, newValue) -> {
                    boolean dynamicColorsEnabled = (boolean) newValue;
                    PreferenceManager.getDefaultSharedPreferences(getContext())
                        .edit()
                        .putBoolean(PREF_KEY_COLOR_SCHEME, dynamicColorsEnabled)
                        .apply();
                    return true;
                }
            );
        }
        mSwitchPreferenceCompatHighContrast = findPreference(PREF_KEY_HIGH_CONTRAST);
        mSwitchPreferenceCompatHighContrast.setOnPreferenceChangeListener(
            (preference, newValue) -> {
                boolean highContrastEnabled = (boolean) newValue;
                PreferenceManager.getDefaultSharedPreferences(getContext())
                    .edit()
                    .putBoolean(PREF_KEY_HIGH_CONTRAST, highContrastEnabled)
                    .apply();
                return true;
            }
        );
            updateHighContrastPreferenceEnabled(mListPreferenceTheme.getValue());
        
        {
            mListPreference = findPreference(PREF_KEY_LANGUAGE);
            mListPreference.setOnPreferenceChangeListener(
                (preference, newValue) -> {
                    PreferenceManager.getDefaultSharedPreferences(getContext())
                        .edit()
                        .putString(PREF_KEY_LANGUAGE, (String) newValue)
                        .apply();
                    return true;
                }
            );
        }
        {
            mPreference = findPreference(PREF_KEY_THUMBNAILS_DIR);
            if (mPreference != null) {
                mPreference.setOnPreferenceClickListener(
                    (preference) -> {
                        Intent intent = new Intent(getActivity(), SettingsThumbnailsDirActivity.class);
                        startActivity(intent);
                        return true;
                    }
                );
            }
        }
        {
            mPreference = findPreference(PREF_KEY_CLEAR_CACHED_THUMBNAILS);
            if (mPreference != null) {
                mPreference.setOnPreferenceClickListener(
                    (preference) -> {
                        clearCache();
                        return true;
                    }
                );
            }
        }
    }
    
    private void updateHighContrastPreferenceEnabled(String themeValue) {
        boolean highContrastEnabled = themeValue.equals("MODE_NIGHT_YES");
        mSwitchPreferenceCompatHighContrast.setEnabled(highContrastEnabled);
    }
    
    private void clearCache() {
        Context context = getContext();
        String messageError = getString(R.string.snackbar_message_clear_cached_thumbnails_result_error);
        String messageSuccess = getString(R.string.snackbar_message_clear_cached_thumbnails_result_success);
        if (context != null) {
            try {
                File cacheDir = context.getCacheDir();
                if (cacheDir != null && cacheDir.isDirectory()) {
                    deleteRecursive(cacheDir);
                }
                Snackbar.make(getView(), messageSuccess, Snackbar.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Snackbar.make(getView(), messageError, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }
}