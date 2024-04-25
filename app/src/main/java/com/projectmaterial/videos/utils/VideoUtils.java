package com.projectmaterial.videos.utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.View;
import androidx.core.content.FileProvider;
import com.google.android.material.snackbar.Snackbar;
import com.projectmaterial.videos.R;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import wseemann.media.FFmpegMediaMetadataRetriever;

public class VideoUtils {
    private Context mContext;
    private FFmpegMediaMetadataRetriever mFFmpegMediaMetadataRetriever;
    private MediaMetadataRetriever mMediaMetadataRetriever;
    
    public VideoUtils(Context context) {
        mContext = context;
    }
    
    public List<String> extractSubtitle(String name) {
        List<String> subtitle = new ArrayList<>();
        Pattern pattern = Pattern.compile("^\\s*\\[([^\\[\\]]+)\\]");
        Matcher matcher = pattern.matcher(name);
        while (matcher.find()) {
            subtitle.add(matcher.group(1).trim());
        }
        return subtitle;
    }
    
    public String extractTitle(String name) {
        return name.replaceAll("^\\[([^\\[\\]]+)\\]\\s*", "").trim();
    }
    
    public long getDate(Video video) {
        File videoFile = getVideoFile(video);
        long videoFileLastModified = videoFile.lastModified();
        return videoFileLastModified;
    }

    public String getFormattedBitrate(Video video) {
        String videoData = video.getData();
        String videoBitrate = "";
        mMediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            mMediaMetadataRetriever.setDataSource(videoData);
            videoBitrate = mMediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                mMediaMetadataRetriever.release();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        if (videoBitrate.isEmpty()) {
            return "";
        }
        
        int bitrate = Integer.parseInt(videoBitrate);
        String[] units = mContext.getResources().getStringArray(R.array.bitrate_units);
        
        if (bitrate < 1000) {
            return bitrate + " " + units[0];
        } else if (bitrate < 1000000) {
            double kbps = bitrate / 1000.0;
            return String.format("%.2f", kbps) + " " + units[1];
        } else if (bitrate < 1000000000) {
            double mbps = bitrate / 1000000.0;
            return String.format("%.2f", mbps) + " " + units[2];
        } else {
            double gbps = bitrate / 1000000000.0;
            return String.format("%.2f", gbps) + " " + units[3];
        }
    }
    
    public String getFormattedDirectory(Video video) {
        File videoFile = getVideoFile(video);
        File videoDirectory = videoFile.getParentFile();
        return videoDirectory.getAbsolutePath() + "/";
    }
    
    public String getFormattedDuration(Video video) {
        long duration = video.getDuration();
        
        long hours = duration / 3600;
        long minutes = (duration % 3600) / 60;
        long seconds = duration % 60;

        String[] formattedDuration = mContext.getResources().getStringArray(R.array.formatted_duration);
    
        if (hours > 0) {
            return String.format(formattedDuration[0], hours, minutes, seconds);
        } else if (minutes >= 10) {
            return String.format(formattedDuration[1], minutes, seconds);
        } else {
            return String.format(formattedDuration[2], minutes, seconds);
        }
    }
    
    public String getFormattedFrameRate(Video video) {
        String videoData = video.getData();
        String videoFrameRate = "";
        mFFmpegMediaMetadataRetriever = new FFmpegMediaMetadataRetriever();
        try {
            mFFmpegMediaMetadataRetriever.setDataSource(videoData);
            videoFrameRate = mFFmpegMediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_FRAMERATE);
            return (videoFrameRate != null) ? (videoFrameRate + " " + mContext.getString(R.string.bottom_sheet_video_info_frame_rate_fps)) : "";
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return "";
        } finally {
            mFFmpegMediaMetadataRetriever.release();
        }
    }
    
    public String getFormattedNameWithoutExtension(Video video) {
        String videoFileName = getName(video);
        int dotIndex = videoFileName.lastIndexOf('.');
        return videoFileName.substring(0, dotIndex);
    }
    
    public String getFormattedNameWithoutSquareBrackets(Video video) {
        String videoFileNameWithoutExtension = getFormattedNameWithoutExtension(video);
        List<String> videoSubtitle = extractSubtitle(videoFileNameWithoutExtension);
        String videoTitle = extractTitle(videoFileNameWithoutExtension);
        return videoSubtitle + " " + videoTitle;
    }
    
    public String getFormattedResolution(Video video) {
        String videoData = video.getData();
        String videoWidth = "";
        String videoHeight = "";
        MediaExtractor extractor = new MediaExtractor();
        try {
            extractor.setDataSource(videoData);
            MediaFormat format = null;
            for (int i = 0; i < extractor.getTrackCount(); i++) {
                format = extractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith("video/")) {
                    videoWidth = format.containsKey(MediaFormat.KEY_WIDTH) ? String.valueOf(format.getInteger(MediaFormat.KEY_WIDTH)) : "";
                    videoHeight = format.containsKey(MediaFormat.KEY_HEIGHT) ? String.valueOf(format.getInteger(MediaFormat.KEY_HEIGHT)) : "";
                    break;
                }
            }
            return (!videoWidth.isEmpty() && !videoHeight.isEmpty()) ? (videoWidth + "x" + videoHeight) : "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            extractor.release();
        }
    }
    
    public String getFormattedSize(Video video) {
        File videoFile = getVideoFile(video);
        long videoLength = videoFile.length();
        String[] units = mContext.getResources().getStringArray(R.array.size_units);
        int index = 0;
        float videoFileSize = videoLength;
        while (videoFileSize >= 1024 && index < units.length - 1) {
            videoFileSize /= 1024;
            index++;
        }
        return String.format("%.2f %s", videoFileSize, units[index]);
    }
    
    public String getFormattedVideoCodec(Video video) {
        String videoData = video.getData();
        String videoCodec = "";
        MediaExtractor extractor = new MediaExtractor();
        try {
            extractor.setDataSource(videoData);
            MediaFormat format = null;
            for (int i = 0; i < extractor.getTrackCount(); i++) {
                format = extractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith("video/")) {
                    videoCodec = format.containsKey(MediaFormat.KEY_MIME) ? format.getString(MediaFormat.KEY_MIME).substring(6) : "";
                    switch (videoCodec) {
                        case "av01":
                            return "AV1";
                        case "x-vnd.on2.vp8":
                            return "VP8";
                        case "x-vnd.on2.vp9":
                            return "VP9";
                        case "avc":
                            return "AVC/H.264";
                        case "hevc":
                            return "HEVC/H.265";
                        default:
                            return videoCodec;
                    }
                }
            }
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            extractor.release();
        }
    }
    
    public String getName(Video video) {
        File videoFile = getVideoFile(video);
        String videoFileName = videoFile.getName();
        return videoFileName;
    }
    
    public long getSize(Video video) {
        File videoFile = getVideoFile(video);
        return videoFile.length();
    }
    
    private File getVideoFile(Video video) {
        String videoData = video.getData();
        return new File(videoData);
    }
    
    public void openVideo(Video video) {
        String videoData = video.getData();
        if (videoData != null && !videoData.isEmpty()) {
            File videoFile = new File(videoData);
            if (videoFile.exists()) {
                Uri videoUri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".fileprovider", videoFile);
                Intent openIntent = new Intent(Intent.ACTION_VIEW);
                openIntent.setDataAndTypeAndNormalize(videoUri, "video/*");
                openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                mContext.startActivity(openIntent);
            }
        }
    }
    
    public boolean renameVideo(String path, String newPath) {
        File videoFile = new File(path);
        File renamedVideoFile = new File(newPath);
        if (videoFile.exists()) {
            return videoFile.renameTo(renamedVideoFile);
        } else {
            return false;
        }
    }
    
    public void shareVideo(Video video) {
        String videoData = video.getData();
        if (videoData != null && !videoData.isEmpty()) {
            File videoFile = new File(videoData);
            if (videoFile.exists()) {
                Uri videoUri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".fileprovider", videoFile);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("video/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, videoUri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                mContext.startActivity(Intent.createChooser(shareIntent, null));
            }
        }
    }
    
    public void showSnackbar(View fragment, String message) {
        Snackbar.make(fragment, message, Snackbar.LENGTH_SHORT).show();
    }
}