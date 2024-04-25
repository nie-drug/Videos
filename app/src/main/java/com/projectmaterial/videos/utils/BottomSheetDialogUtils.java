package com.projectmaterial.videos.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.projectmaterial.videos.R;
import com.projectmaterial.videos.common.VideoThumbnailManager;
import com.projectmaterial.videos.view.InfoItemView;
import com.projectmaterial.videos.view.MenuItemView;
import java.util.List;

public class BottomSheetDialogUtils {
    private Button mPositiveButton;
    private ClipboardManager mClipboardManager;
    private ClipData mClipData;
    private Context mContext;
    private FavoriteStateToggleListener mListener;
    private View mFragmentView;
    private VideoThumbnailManager mVideoThumbnailManager;
    private VideoOptionUtils mVideoOptionUtils;
    private VideoUtils mVideoUtils;
    
    public BottomSheetDialogUtils(Context context, View fragmentView, FavoriteStateToggleListener listener) {
        mContext = context;
        mFragmentView = fragmentView;
        mListener = listener;
        mVideoOptionUtils = new VideoOptionUtils(context);
        mVideoThumbnailManager = new VideoThumbnailManager(context);
        mVideoUtils = new VideoUtils(context);
    }
    
    public interface FavoriteStateToggleListener {
        void toggleFavoriteState(Video video);
    }
    
    public void showVideoOptionsBottomSheetDialog(@NonNull Video video) {
        BottomSheetDialog dialog = new BottomSheetDialog(mContext);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_video_options, null);
        String videoData = video.getData();
        
        ImageView thumbnailImageView = contentView.findViewById(R.id.thumbnail);
        if (videoData != null && !videoData.isEmpty()) {
            mVideoThumbnailManager.getThumbnailBitmap(thumbnailImageView, video);
        } else {
            thumbnailImageView.setImageDrawable(null);
        }
        
        String formattedNameWithoutExtension = mVideoUtils.getFormattedNameWithoutExtension(video);
        TextView titleTextView = contentView.findViewById(R.id.title);
        titleTextView.setText(mVideoUtils.extractTitle(formattedNameWithoutExtension));
        
        List<String> subtitles = mVideoUtils.extractSubtitle(formattedNameWithoutExtension);
        TextView subtitleTextView = contentView.findViewById(R.id.subtitle);
        if (!subtitles.isEmpty()) {
            StringBuilder subtitleText = new StringBuilder();
            for (String subtitle : subtitles) {
                subtitleText.append(subtitle).append(" ");
            }
            String subtitleString = subtitleText.toString().trim();
            subtitleTextView.setText(subtitleString);
            subtitleTextView.setVisibility(View.VISIBLE);
        } else {
            subtitleTextView.setVisibility(View.GONE);
        }
        
        String formattedDuration = mVideoUtils.getFormattedDuration(video);
        TextView durationTextView = contentView.findViewById(R.id.duration);
        durationTextView.setText(formattedDuration);
        
        MenuItemView menuItemFavorite = contentView.findViewById(R.id.menu_item_favorite);
        boolean isFavorite = video.getFavoriteState(mContext);
        video.setFavorite(isFavorite);
        if (video.isFavorite()) {
            menuItemFavorite.setTitle(R.string.bottom_sheet_video_options_unstar);
            menuItemFavorite.setIcon(R.drawable.quantum_ic_star_filled_vd_theme_24);
            menuItemFavorite.setOnClickListener(
                view -> {
                    mListener.toggleFavoriteState(video);
                    String messageUnstarred = mContext.getString(R.string.snackbar_message_favorite_result_unstarred, mVideoUtils.extractTitle(formattedNameWithoutExtension));
                    mVideoUtils.showSnackbar(mFragmentView, messageUnstarred);
                    dialog.dismiss();
                }
            );
        } else {
            menuItemFavorite.setTitle(R.string.bottom_sheet_video_options_star);
            menuItemFavorite.setIcon(R.drawable.quantum_ic_star_vd_theme_24);
            menuItemFavorite.setOnClickListener(
                view -> {
                    mListener.toggleFavoriteState(video);
                    String messageStarred = mContext.getString(R.string.snackbar_message_favorite_result_starred, mVideoUtils.extractTitle(formattedNameWithoutExtension));
                    mVideoUtils.showSnackbar(mFragmentView, messageStarred);
                    dialog.dismiss();
                }
            );
        }
        
        MenuItemView menuItemRename = contentView.findViewById(R.id.menu_item_rename);
        menuItemRename.setTitle(R.string.bottom_sheet_video_options_rename);
        menuItemRename.setIcon(R.drawable.quantum_ic_edit_vd_theme_24);
        menuItemRename.setOnClickListener(
            view -> {
                showRenameVideoDialog(video);
                dialog.dismiss();
            }
        );
        
        MenuItemView menuItemShare = contentView.findViewById(R.id.menu_item_share);
        menuItemShare.setTitle(R.string.bottom_sheet_video_options_share);
        menuItemShare.setIcon(R.drawable.quantum_ic_share_vd_theme_24);
        menuItemShare.setOnClickListener(
            view -> {
                mVideoUtils.shareVideo(video);
                dialog.dismiss();
            }
        );
        
        MenuItemView menuItemInfo = contentView.findViewById(R.id.menu_item_info);
        menuItemInfo.setTitle(R.string.bottom_sheet_video_options_info);
        menuItemInfo.setIcon(R.drawable.quantum_ic_info_vd_theme_24);
        menuItemInfo.setOnClickListener(
            view -> {
                showVideoInfoBottomSheetDialog(video);
                dialog.dismiss();
            }
        );
        
        MenuItemView menuItemDelete = contentView.findViewById(R.id.menu_item_delete);
        menuItemDelete.setTitle(R.string.bottom_sheet_video_options_delete);
        menuItemDelete.setIcon(R.drawable.quantum_ic_delete_vd_theme_24);
        menuItemDelete.setOnClickListener(
            view -> {
                showDeleteConfirmationDialog(video);
                dialog.dismiss();
            }
        );
        
        dialog.getWindow().setNavigationBarColor(Color.TRANSPARENT);
        dialog.setContentView(contentView);
        dialog.show();
    }
    
    private void showRenameVideoDialog(Video video) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext, R.style.ThemeOverlay_SettingsLib_MaterialAlertDialog_Centered);
        builder.setTitle(R.string.dialog_rename_title);
        View viewInflated = LayoutInflater.from(mContext).inflate(R.layout.dialog_rename, null);
        TextInputEditText input = viewInflated.findViewById(R.id.text_input_edit_text);
        TextInputLayout inputLayout = viewInflated.findViewById(R.id.text_input_layout);
        input.setText(mVideoUtils.getFormattedNameWithoutExtension(video));
        builder.setView(viewInflated);
        builder.setNegativeButton(R.string.dialog_negative_button_cancel,
            (dialog, which) -> {
                dialog.cancel();
            }
        );
        builder.setPositiveButton(R.string.dialog_positive_button_rename, null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(
            (dialogInterface) -> {
                mPositiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                mPositiveButton.setEnabled(false);
                mPositiveButton.setOnClickListener(
                    (view) -> {
                        String newName = input.getText().toString().trim();
                        if (!newName.isEmpty() && isValidFileName(newName)) {
                            String newPath = video.getData().replace(mVideoUtils.getFormattedNameWithoutExtension(video), newName);
                            if (mVideoUtils.renameVideo(video.getData(), newPath)) {
                                dialog.dismiss();
                                String messageRenameSuccess = mContext.getString(R.string.snackbar_message_rename_result_success);
                                mVideoUtils.showSnackbar(mFragmentView, messageRenameSuccess);
                            } else {
                                String messageRenameError = mContext.getString(R.string.snackbar_message_rename_result_error);
                                mVideoUtils.showSnackbar(mFragmentView, messageRenameError);
                            }
                        } else {
                            if (newName.isEmpty()) {
                                inputLayout.setError(mContext.getString(R.string.dialog_rename_error_empty_input));
                            } else {
                                inputLayout.setError(mContext.getString(R.string.dialog_rename_error_invalid_characters));
                            }
                        }
                    }
                );
            }
        );
    
        input.addTextChangedListener(
            new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String newName = s.toString().trim();
                    String originalName = mVideoUtils.getFormattedNameWithoutExtension(video);
                    boolean nameChanged = !newName.equals(originalName);
                    mPositiveButton.setEnabled(nameChanged);
                }
                @Override
                public void afterTextChanged(Editable s) {

                }
            }
        );
        
        dialog.show();
    }
    
    private boolean isValidFileName(String fileName) {
        String[] invalidChars = {"?", ":", "*", "|", "/", "\\", "<", ">"};
        for (String invalidChar : invalidChars) {
            if (fileName.contains(invalidChar)) {
                return false;
            }
        }
        return true;
    }
    
    private void showDeleteConfirmationDialog(Video video) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
        builder.setTitle(R.string.dialog_delete_title);
        builder.setMessage(R.string.dialog_delete_message);
        builder.setNegativeButton(R.string.dialog_negative_button_cancel,
            (dialog, which) -> {
                dialog.cancel();
            }
        );
        builder.setPositiveButton(R.string.dialog_positive_button_delete,
            (dialog, which) -> {
                mVideoOptionUtils.deleteVideo(mFragmentView, video);
                dialog.dismiss();
            }
        );
        builder.show();
    }
    
    private void showVideoInfoBottomSheetDialog(@NonNull Video video) {
        BottomSheetDialog dialog = new BottomSheetDialog(mContext);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_video_info, null);
        
        String name = mVideoUtils.getName(video);
        InfoItemView infoItemCompleteName = contentView.findViewById(R.id.info_item_complete_name);
        infoItemCompleteName.setTitle(R.string.bottom_sheet_video_info_file_name);
        infoItemCompleteName.setSubtitle(name);
        infoItemCompleteName.setOnLongClickListener(
            (view) -> {
                mClipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                mClipData = ClipData.newPlainText(mContext.getString(R.string.bottom_sheet_video_info_file_name), name);
                mClipboardManager.setPrimaryClip(mClipData);
                Toast.makeText(mContext, mContext.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show();
                return true;
            }
        );
        
        
        String formattedDirectory = mVideoUtils.getFormattedDirectory(video);
        InfoItemView infoItemLocation = contentView.findViewById(R.id.info_item_location);
        infoItemLocation.setTitle(R.string.bottom_sheet_video_info_location);
        infoItemLocation.setSubtitle(formattedDirectory);
        infoItemLocation.setOnLongClickListener(
            (view) -> {
                mClipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                mClipData = ClipData.newPlainText(mContext.getString(R.string.bottom_sheet_video_info_location), formattedDirectory);
                mClipboardManager.setPrimaryClip(mClipData);
                Toast.makeText(mContext, mContext.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show();
                return true;
            }
        );
        
        View infoSpaceVertical1 = contentView.findViewById(R.id.info_space_vertical_1);
        
        String formattedResolution = mVideoUtils.getFormattedResolution(video);
        InfoItemView infoItemResolution = contentView.findViewById(R.id.info_item_resolution);
        infoItemResolution.setTitle(R.string.bottom_sheet_video_info_resolution);
        if (!formattedResolution.isEmpty()) {
            infoItemResolution.setSubtitle(formattedResolution);
        } else {
            infoItemResolution.setVisibility(View.GONE);
            infoSpaceVertical1.setVisibility(View.GONE);
        }
        
        String formattedBitrate = mVideoUtils.getFormattedBitrate(video);
        InfoItemView infoItemBitrate = contentView.findViewById(R.id.info_item_bitrate);
        infoItemBitrate.setTitle(R.string.bottom_sheet_video_info_bitrate);
        if (!formattedBitrate.isEmpty()) {
            infoItemBitrate.setSubtitle(formattedBitrate);
        } else {
            infoItemBitrate.setVisibility(View.GONE);
            infoSpaceVertical1.setVisibility(View.GONE);
        }
        
        View infoSpaceHorizontal2 = contentView.findViewById(R.id.info_space_horizontal_2);
        ViewGroup infoItemContainer1 = contentView.findViewById(R.id.info_item_container_1);
        if (infoItemResolution.getVisibility() == View.GONE && infoItemBitrate.getVisibility() == View.GONE) {
            infoSpaceHorizontal2.setVisibility(View.GONE);
            infoItemContainer1.setVisibility(View.GONE);
        }
        
        View infoSpaceVertical2 = contentView.findViewById(R.id.info_space_vertical_2);
        
        String formattedFrameRate = mVideoUtils.getFormattedFrameRate(video);
        InfoItemView infoItemFrameRate = contentView.findViewById(R.id.info_item_frame_rate);
        infoItemFrameRate.setTitle(R.string.bottom_sheet_video_info_frame_rate);
        if (!formattedFrameRate.isEmpty()) {
            infoItemFrameRate.setSubtitle(formattedFrameRate);
        } else {
            infoItemFrameRate.setVisibility(View.GONE);
            infoSpaceVertical2.setVisibility(View.GONE);
        }
        
        String formattedVideoCodec = mVideoUtils.getFormattedVideoCodec(video);
        InfoItemView infoItemVideoCodec = contentView.findViewById(R.id.info_item_codec);
        infoItemVideoCodec.setTitle(R.string.bottom_sheet_video_info_codec);
        if (!formattedVideoCodec.isEmpty()) {
            infoItemVideoCodec.setSubtitle(formattedVideoCodec);
        } else {
            infoItemVideoCodec.setVisibility(View.GONE);
            infoSpaceVertical2.setVisibility(View.GONE);
        }
        
        View infoSpaceHorizontal3 = contentView.findViewById(R.id.info_space_horizontal_3);
        ViewGroup infoItemContainer2 = contentView.findViewById(R.id.info_item_container_2);
        if (infoItemFrameRate.getVisibility() == View.GONE && infoItemVideoCodec.getVisibility() == View.GONE) {
            infoSpaceHorizontal3.setVisibility(View.GONE);
            infoItemContainer2.setVisibility(View.GONE);
        }
        
        String formattedDuration = mVideoUtils.getFormattedDuration(video);
        InfoItemView infoItemDuration = contentView.findViewById(R.id.info_item_duration);
        infoItemDuration.setTitle(R.string.bottom_sheet_video_info_duration);
        infoItemDuration.setSubtitle(formattedDuration);
        
        String formattedSize = mVideoUtils.getFormattedSize(video);
        InfoItemView infoItemSize = contentView.findViewById(R.id.info_item_size);
        infoItemSize.setTitle(R.string.bottom_sheet_video_info_size);
        infoItemSize.setSubtitle(formattedSize);
        
        dialog.getWindow().setNavigationBarColor(Color.TRANSPARENT);
        dialog.setContentView(contentView);
        dialog.show();
    }
}