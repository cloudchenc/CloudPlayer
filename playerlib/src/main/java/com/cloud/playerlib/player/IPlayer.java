package com.cloud.playerlib.player;

import android.view.Surface;

/**
 * Created by cloud on 2020/4/16.
 */
public interface IPlayer {
    void initPlayer();

    void setSurface(Surface surface);

    void setVolume(float left, float right);

    void start();

    void pause();

    void stop();

    boolean isPlaying();

    void seekTo(long time);

    long getCurrentPosition();

    long getDuration();
}
