package com.cloud.player.player;

import android.content.Context;

/**
 * Created by cloud on 2020/4/16.
 */
public abstract class BasePlayerImpl implements IPlayer {

    static final int STATE_ERROR = -1;
    static final int STATE_IDLE = 0;
    static final int STATE_PREPARING = 1;
    static final int STATE_PREPARED = 2;
    static final int STATE_PLAYING = 3;
    static final int STATE_PAUSED = 4;
    static final int STATE_PLAYBACK_COMPLETED = 5;

    int mCurrentState = STATE_IDLE;

}
