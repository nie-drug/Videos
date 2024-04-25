package com.projectmaterial.videos.fragments;

import android.view.View;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.transition.MaterialFadeThrough;
import com.projectmaterial.videos.activities.BaseComponentActivity;

public abstract class BaseFragment extends Fragment {
    private static String TRANSITION_NAME = "shared_transition";
    
    public void setSupportActionBar(@Nullable MaterialToolbar materialToolbar) {
        ((BaseComponentActivity) requireActivity()).setSupportActionBar(materialToolbar);
    }
    
    public void setUpTransition(@Nullable View rootView) {
        MaterialFadeThrough materialFadeThrough = new MaterialFadeThrough();
        setEnterTransition(materialFadeThrough);
        setExitTransition(materialFadeThrough);
        rootView.setTransitionName(TRANSITION_NAME);
    }
}