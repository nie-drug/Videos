package com.projectmaterial.videos.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textview.MaterialTextView;
import com.projectmaterial.videos.R;
import com.projectmaterial.videos.view.InfoItemView;

public class DirectoryBottomSheetDialogUtils {
    private BottomSheetDialog mBottomSheetDialog;
    private ClipboardManager mClipboardManager;
    private ClipData mClipData;
    private Context mContext;
    private DirectoryUtils mDirectoryUtils;
    private InfoItemView mInfoItemView;
    private MaterialTextView mDirectoryName;
    private View mContentView;
    
    public DirectoryBottomSheetDialogUtils(Context context) {
        mContext = context;
        mDirectoryUtils = new DirectoryUtils(context);
    }
    
    public void showDirectoryInfoBottomSheetDialog(@NonNull String directory) {
        mBottomSheetDialog = new BottomSheetDialog(mContext);
        mContentView = LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_directory_info, null);
        
        mDirectoryName = mContentView.findViewById(R.id.directory_name);
        mDirectoryName.setText(mDirectoryUtils.getFormattedName(directory));
        
        {
            mInfoItemView = mContentView.findViewById(R.id.info_item_location);
            mInfoItemView.setTitle(R.string.bottom_sheet_video_info_location);
            mInfoItemView.setSubtitle(mDirectoryUtils.getFormattedPath(directory));
            mInfoItemView.setOnLongClickListener(
                (view) -> {
                    mClipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    mClipData = ClipData.newPlainText(mContext.getString(R.string.bottom_sheet_video_info_location), mDirectoryUtils.getFormattedPath(directory));
                    mClipboardManager.setPrimaryClip(mClipData);
                    Toast.makeText(mContext, mContext.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show();
                    return true;
                }
            );
        }
        
        mBottomSheetDialog.getWindow().setNavigationBarColor(Color.TRANSPARENT);
        mBottomSheetDialog.setContentView(mContentView);
        mBottomSheetDialog.show();
    }
}