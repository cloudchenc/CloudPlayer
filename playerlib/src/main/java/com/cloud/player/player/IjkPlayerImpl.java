package com.cloud.player.player;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;

import com.cloud.player.utils.DecodeUtil;

import java.io.IOException;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by cloud on 2020/4/16.
 */
public class IjkPlayerImpl extends BasePlayerImpl implements IMediaPlayer.OnPreparedListener, IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnErrorListener, IMediaPlayer.OnSeekCompleteListener,
        IMediaPlayer.OnInfoListener, IMediaPlayer.OnVideoSizeChangedListener {

    private final static String TAG = IjkPlayerImpl.class.getSimpleName();

    private IjkMediaPlayer mMediaPlayer;

//    private IMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener;
//    private IMediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener;
//    private IMediaPlayer.OnCompletionListener mOnCompletionListener;
//    private IMediaPlayer.OnPreparedListener mOnPreparedListener;
//    private IMediaPlayer.OnErrorListener mOnErrorListener;
//    private IMediaPlayer.OnInfoListener mOnInfoListener;

    @Override
    public synchronized void initPlayer(Context context) {
        release();
        mMediaPlayer = newPlayer();

        mMediaPlayer.setOnVideoSizeChangedListener(this);
        mMediaPlayer.setOnSeekCompleteListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnInfoListener(this);
    }

    private IjkMediaPlayer newPlayer() {
        IjkMediaPlayer mediaPlayer = new IjkMediaPlayer();

        if (DecodeUtil.isSupportHardwareDecode(2 * 1024)) {
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-all-videos", 1);
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);

            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);

            //跳帧处理（-1~120）。CPU处理慢时，进行跳帧处理，保证音视频同步
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 60);
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-fps", 0);
            //设置是否开启环路过滤: 0开启，画面质量高，解码开销大，48关闭，画面质量差点，解码开销小
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
        }
        return mediaPlayer;
    }

    @Override
    public IMediaPlayer getMediaPlayer() {
        return null;
    }

    @Override
    public void setDataSource(String url, Map<String, String> headers) {
        try {
            if (mMediaPlayer != null) {
                setPlayerOptions(mMediaPlayer, url);
                mMediaPlayer.setDataSource(url, headers);
                mMediaPlayer.setScreenOnWhilePlaying(true);
                mMediaPlayer.prepareAsync();
                mCurrentState = STATE_PREPARING;
            }
        } catch (IOException e) {
            mCurrentState = STATE_ERROR;
            Log.e(TAG, "setDataSource", e);
        }
    }

    private void setPlayerOptions(IjkMediaPlayer mediaPlayer, String url) {
        if (!TextUtils.isEmpty(url) && (url.startsWith("rtmp") || url.startsWith("rtsp"))) {
            // Param for living
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max_cached_duration", 2000);
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "infbuf", 1);
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0);
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "nobuffer");

            //network
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max_queue_size", 10 * 1024 * 1024);
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1);
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 3);
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "probsize", "4096");
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", "2000000");

            //waylens
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "probesize", 500 * 1024);
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 500 * 1024);
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "formatprobesize", 200 * 1024);
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fpsprobesize", 5);
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 500 * 1024);
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 1);
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "max_ts_probe", 200 * 1024);
        } else {
            // Param for playback
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max_cached_duration", 4000);
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "infbuf", 0);
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 1);

            //设置支持跳转非关键帧
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
        }
    }

    @Override
    public void setSurface(Surface surface) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setSurface(surface);
        }
    }

    @Override
    public void setVolume(float left, float right) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(left, right);
        }
    }

    @Override
    public void setSpeed(float speed) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setSpeed(speed);
        }
    }

    @Override
    public void start() {
        if (isInPlaybackState()) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
        }
    }

    private boolean isInPlaybackState() {
        return (mMediaPlayer != null &&
                mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING);
    }

    @Override
    public void pause() {
        if (isInPlaybackState() && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mCurrentState = STATE_PAUSED;
        }
    }

    @Override
    public synchronized void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
        }
    }

    @Override
    public synchronized void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
        }
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    public void seekTo(long time) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(time);
        }
    }

    @Override
    public long getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public long getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        mCurrentState = STATE_PLAYBACK_COMPLETED;
//        if (mMediaPlayer != null && mOnCompletionListener != null) {
//            mOnCompletionListener.onCompletion(iMediaPlayer);
//        }
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
        mCurrentState = STATE_ERROR;
//        if (mMediaPlayer != null && mOnErrorListener != null) {
//            mOnErrorListener.onError(iMediaPlayer, i, i1);
//        }
        return true;
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
//        if (mMediaPlayer != null && mOnInfoListener != null) {
//            mOnInfoListener.onInfo(iMediaPlayer, i, i1);
//        }
        return true;
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        mCurrentState = STATE_PREPARED;
//        if (mMediaPlayer != null && mOnPreparedListener != null) {
//            mOnPreparedListener.onPrepared(iMediaPlayer);
//        }
        start();
    }

    @Override
    public void onSeekComplete(IMediaPlayer iMediaPlayer) {
//        if (mMediaPlayer != null && mOnSeekCompleteListener != null) {
//            mOnSeekCompleteListener.onSeekComplete(iMediaPlayer);
//        }
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
//        if (mMediaPlayer != null && mOnVideoSizeChangedListener != null) {
//            mOnVideoSizeChangedListener.onVideoSizeChanged(iMediaPlayer, i, i1, i2, i3);
//        }
    }

//    public void setOnVideoSizeChangedListener(IMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener) {
//        this.mOnVideoSizeChangedListener = mOnVideoSizeChangedListener;
//    }
//
//    public void setOnSeekCompleteListener(IMediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener) {
//        this.mOnSeekCompleteListener = mOnSeekCompleteListener;
//    }
//
//    public void setOnCompletionListener(IMediaPlayer.OnCompletionListener mOnCompletionListener) {
//        this.mOnCompletionListener = mOnCompletionListener;
//    }
//
//    public void setOnPreparedListener(IMediaPlayer.OnPreparedListener mOnPreparedListener) {
//        this.mOnPreparedListener = mOnPreparedListener;
//    }
//
//    public void setOnErrorListener(IMediaPlayer.OnErrorListener mOnErrorListener) {
//        this.mOnErrorListener = mOnErrorListener;
//    }
//
//    public void setOnInfoListener(IMediaPlayer.OnInfoListener mOnInfoListener) {
//        this.mOnInfoListener = mOnInfoListener;
//    }
}
