package com.projectmaterial.videos.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import com.projectmaterial.videos.R;

public class InfoItemView extends LinearLayoutCompat {

    private AppCompatTextView mItemTitleTextView;
    private AppCompatTextView mItemSubtitleTextView;

    public InfoItemView(Context context) {
        super(context);
        init(context, null);
    }

    public InfoItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public InfoItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.projectx_info_item_view, this);
        mItemTitleTextView = findViewById(R.id.item_title_view);
        mItemSubtitleTextView = findViewById(R.id.item_subtitle_view);

        // Set default values for title and subtitle
        setTitle(0); // No default title
        setSubtitle(null); // No default subtitle

        // Check if attributes are not null
        if (attrs != null) {
            
        }
    }
    
    public void setSubtitle(@StringRes int subtitleResId) {
        if (subtitleResId != 0) {
            mItemSubtitleTextView.setText(subtitleResId);
        } else {
            mItemSubtitleTextView.setText(""); // Clear the subtitle
        }
    }
    
    public void setTitle(@StringRes int titleResId) {
        if (titleResId != 0) {
            mItemTitleTextView.setText(titleResId);
        } else {
            mItemTitleTextView.setText(""); // Clear the subtitle
        }
    }

    public void setSubtitle(String subtitle) {
        if (subtitle != null) {
            mItemSubtitleTextView.setText(subtitle);
        } else {
            mItemSubtitleTextView.setText(""); // Clear the subtitle
        }
    }
}