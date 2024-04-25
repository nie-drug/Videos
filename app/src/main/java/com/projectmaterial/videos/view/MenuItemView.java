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

public class MenuItemView extends LinearLayoutCompat {

    private AppCompatTextView mItemTitleTextView;
    private AppCompatImageView mItemIconImageView;

    public MenuItemView(Context context) {
        super(context);
        init(context, null);
    }

    public MenuItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MenuItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.projectx_menu_item_view, this);
        mItemTitleTextView = findViewById(R.id.item_title_view);
        mItemIconImageView = findViewById(R.id.item_icon_view);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MenuItemView); // Use your custom attribute set
            int titleResId = a.getResourceId(R.styleable.MenuItemView_item_title, 0); // Use your custom title attribute
            int iconResId = a.getResourceId(R.styleable.MenuItemView_item_icon, 0); // Use your custom icon attribute
            a.recycle();

            setTitle(titleResId);
            setIcon(iconResId);
        }
    }

    public void setTitle(@StringRes int titleResId) {
        if (titleResId != 0) {
            mItemTitleTextView.setText(titleResId);
        }
    }
    
    public void setIcon(int iconResId) {
        if (iconResId != 0) {
            mItemIconImageView.setImageResource(iconResId);
        }
    }
}