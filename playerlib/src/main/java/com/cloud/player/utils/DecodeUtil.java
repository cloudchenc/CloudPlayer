package com.cloud.player.utils;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by cloud on 2020/4/17.
 */
public class DecodeUtil {

    private final static String TAG = DecodeUtil.class.getSimpleName();

    private final static String H264_TYPE = "video/avc";

    public static boolean isSupportHardwareDecode(int resolution) {
        boolean isSupported = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.REGULAR_CODECS);
            MediaCodecInfo[] codecInfos = mediaCodecList.getCodecInfos();

            if (codecInfos == null) {
                return false;
            }

            for (MediaCodecInfo codecInfo : codecInfos) {
                boolean isEncoder = codecInfo.isEncoder();
                String[] supportedTypes = codecInfo.getSupportedTypes();

                if (!isEncoder && supportedTypes != null
                        && Arrays.asList(supportedTypes).contains(H264_TYPE)) {
                    MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(H264_TYPE);
                    MediaCodecInfo.VideoCapabilities videoCapabilities = capabilities.getVideoCapabilities();
                    if (videoCapabilities != null) {
                        isSupported = videoCapabilities.getSupportedWidths().contains(resolution)
                                || videoCapabilities.getSupportedHeights().contains(resolution);
                        return isSupported;
                    }
                }
            }
        }
        return false;
    }
}
