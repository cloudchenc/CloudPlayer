package com.cloud.player.player;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.Surface;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.video.VideoListener;

import java.io.IOException;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by cloud on 2020/4/16.
 */
public class ExoPlayerImpl extends BasePlayerImpl implements Player.EventListener, VideoListener {

    private final static String TAG = ExoPlayerImpl.class.getSimpleName();

    private ExoMediaPlayer mMediaPlayer;
    private Context mContext;

    @Override
    public void initPlayer(Context context) {
        this.mContext = context;

        release();
        mMediaPlayer = new ExoMediaPlayer(context);
    }

    @Override
    public IMediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    @Override
    public void setDataSource(String url, Map<String, String> headers) {
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.setDataSource(mContext, Uri.parse(url), headers);
                mMediaPlayer.prepareAsync();
                mCurrentState = STATE_PREPARING;
            }
        } catch (IOException e) {
            mCurrentState = STATE_ERROR;
            Log.e(TAG, "setDataSource", e);
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
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    @Override
    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    @Override
    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    @Override
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
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
        if (mMediaPlayer == null) {
            return 0;
        }
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        if (mMediaPlayer == null) {
            return 0;
        }
        return mMediaPlayer.getDuration();
    }
}
