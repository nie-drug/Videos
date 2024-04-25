package com.projectmaterial.videos.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatCheckedTextView;
import com.projectmaterial.videos.R;

public class XCheckedTextView extends AppCompatCheckedTextView {
    private Drawable doneIcon;

    public XCheckedTextView(Context context) {
        super(context);
        init(context, null);
    }

    public XCheckedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public XCheckedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        doneIcon = context.getDrawable(R.drawable.quantum_ic_check_vd_theme_24);
        updateDrawable();
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        updateDrawable();
    }

    private void updateDrawable() {
        if (isChecked()) {
            if (getLayoutDirection() == LAYOUT_DIRECTION_RTL) {
                setCompoundDrawablesWithIntrinsicBounds(null, null, doneIcon, null);
            } else {
                setCompoundDrawablesWithIntrinsicBounds(doneIcon, null, null, null);
            }
        } else {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }
}