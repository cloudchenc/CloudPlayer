package com.cloud.player.player;

import android.content.Context;
import android.view.Surface;

import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by cloud on 2020/4/16.
 */
public interface IPlayer {

    void initPlayer(Context context);

    IMediaPlayer getMediaPlayer();

    void setDataSource(String url, Map<String, String> headers);

    void setSurface(Surface surface);

    void setVolume(float left, float right);

    void setSpeed(float speed);

    void start();

    void pause();

    void stop();

    void release();

    boolean isPlaying();

    void seekTo(long time);

    long getCurrentPosition();

    long getDuration();
}
