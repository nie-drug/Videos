package com.projectmaterial.videos.fragments;

import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import com.projectmaterial.preference.M3EditTextPreference;
import com.projectmaterial.preference.M3PreferenceFragmentCompat;
import com.projectmaterial.videos.common.VideoThumbnailManager;
import java.io.File;
import com.projectmaterial.videos.R;
import com.projectmaterial.videos.utils.ConstantUtils;

public class SettingsThumbnailsDirFragment extends M3PreferenceFragmentCompat {
    private M3EditTextPreference mEditTextPreference;
    private VideoThumbnailManager mVideoThumbnailManager;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVideoThumbnailManager = new VideoThumbnailManager(requireContext());
    }
    
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_thumbnails_dir, rootKey);
        {
            mEditTextPreference = findPreference("pref_key_thumbnails_dir_path");
            if (mEditTextPreference != null) {
                String defaultValue = "/storage/emulated/0/Movies/.thumbnails/";
                String messageInvalid = getString(R.string.snackbar_message_thumbnails_dir_error);
                String messageSuccess = getString(R.string.snackbar_message_thumbnails_dir_success);
                String negativeButtonText = getString(R.string.dialog_negative_button_cancel);
                String positiveButtonText = getString(R.string.dialog_positive_button_save);
                mEditTextPreference.setNegativeButtonText(negativeButtonText);
                mEditTextPreference.setPositiveButtonText(positiveButtonText);
                mEditTextPreference.setOnPreferenceChangeListener(
                    (preference, newValue) -> {
                        String newThumbnailPath = (String) newValue;
                        String currentThumbnailPath = mEditTextPreference.getText();
                        if (newThumbnailPath == null || newThumbnailPath.trim().equals("")) {
                            if (newThumbnailPath.equals(currentThumbnailPath)) {
                                return true;
                            } else {
                                ConstantUtils.setThumbnailsDirectoryPath(requireContext(), defaultValue);
                                Snackbar.make(getView(), messageSuccess, Snackbar.LENGTH_SHORT).show();
                                return true;
                            }
                        } else {
                            if (newThumbnailPath.equals(currentThumbnailPath)) {
                                return true;
                            }
                            if (isValidDirectory(newThumbnailPath)) {
                                ConstantUtils.setThumbnailsDirectoryPath(requireContext(), newThumbnailPath);
                                if (!hasNomediaFile(newThumbnailPath)) {
                                    createNomediaFile(newThumbnailPath);
                                }
                                Snackbar.make(getListView(), messageSuccess, Snackbar.LENGTH_SHORT).show();
                                return true;
                            } else {
                                Snackbar.make(getListView(), messageInvalid, Snackbar.LENGTH_SHORT).show();
                                return false;
                            }
                        }
                    }
                );
            }
        }
    }
    
    private boolean isValidDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        return directory.exists() && directory.isDirectory();
    }
    
    private boolean hasNomediaFile(String directoryPath) {
        File nomediaFile = new File(directoryPath, ".nomedia");
        return nomediaFile.exists();
    }
    
    private void createNomediaFile(String directoryPath) {
        try {
            File nomediaFile = new File(directoryPath, ".nomedia");
            nomediaFile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}